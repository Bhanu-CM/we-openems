package io.openems.edge.energy.optimizer;

import static io.jenetics.engine.EvolutionResult.toBestGenotype;
import static io.openems.edge.energy.optimizer.QuickSchedules.fromExistingSimulationResult;
import static java.lang.Double.isNaN;
import static java.lang.Thread.currentThread;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionStream;
import io.openems.edge.energy.api.EnergyScheduleHandler;
import io.openems.edge.energy.api.simulation.EnergyFlow;
import io.openems.edge.energy.api.simulation.GlobalSimulationsContext;
import io.openems.edge.energy.api.simulation.OneSimulationContext;

public class Simulator {

	/** Used to incorporate charge/discharge efficiency. */
	public static final double EFFICIENCY_FACTOR = 1.17;

	private static final Logger LOG = LoggerFactory.getLogger(Simulator.class);

	/**
	 * Simulates a Schedule and calculates the cost.
	 * 
	 * @param cache the {@link GenotypeCache}
	 * @param gsc   the {@link GlobalSimulationsContext}
	 * @param gt    the Schedule as a {@link Genotype}
	 * @return the cost, lower is better, always positive; {@link Double#NaN} on
	 *         error
	 */
	protected static double calculateCost(GenotypeCache cache, GlobalSimulationsContext gsc, Genotype<IntegerGene> gt) {
		var cost = cache.query(gt);
		if (!isNaN(cost)) {
			return cost;
		}

		// Not in cache
		cost = simulate(gsc, gt, null);
		cache.add(gt, cost);
		return cost;
	}

	/**
	 * Simulates a Schedule and calculates the cost.
	 * 
	 * @param gsc                   the {@link GlobalSimulationsContext}
	 * @param gt                    the simulated {@link Genotype}
	 * @param bestScheduleCollector the {@link BestScheduleCollector}; or null
	 * @return the cost, lower is better, always positive;
	 *         {@link Double#POSITIVE_INFINITY} on error
	 */
	public static double simulate(GlobalSimulationsContext gsc, Genotype<IntegerGene> gt,
			BestScheduleCollector bestScheduleCollector) {
		final var osc = OneSimulationContext.from(gsc);
		final var noOfPeriods = gsc.periods().size();

		var sum = 0.;
		for (var period = 0; period < noOfPeriods; period++) {
			sum += simulatePeriod(osc, gt, period, bestScheduleCollector);
		}
		return sum;
	}

	/**
	 * Calculates the cost of one Period under the given Schedule.
	 * 
	 * @param simulation            the {@link OneSimulationContext}
	 * @param gt                    the simulated {@link Genotype}
	 * @param periodIndex           the index of the simulated period
	 * @param bestScheduleCollector the {@link BestScheduleCollector}; or null
	 * @return the cost, lower is better, always positive;
	 *         {@link Double#POSITIVE_INFINITY} on error
	 */
	public static double simulatePeriod(OneSimulationContext simulation, Genotype<IntegerGene> gt, int periodIndex,
			BestScheduleCollector bestScheduleCollector) {
		final var period = simulation.global.periods().get(periodIndex);
		final var handlers = simulation.global.handlers();
		final var model = EnergyFlow.Model.from(simulation, period);

		var eshIndex = 0;
		for (var esh : handlers) {
			if (esh instanceof EnergyScheduleHandler.WithDifferentStates<?, ?> e) {
				// Simulate with state given by Genotype
				e.simulatePeriod(simulation, period, model, gt.get(eshIndex++).get(periodIndex).intValue());
			} else if (esh instanceof EnergyScheduleHandler.WithOnlyOneState<?> e) {
				e.simulatePeriod(simulation, period, model);
			}
		}

		final EnergyFlow energyFlow = model.solve();

		if (energyFlow == null) {
			LOG.error("Error while simulating period [" + periodIndex + "]");
			// TODO add configurable debug logging
			// LOG.info(simulation.toString());
			// model.logConstraints();
			// model.logMinMaxValues();
			return Double.POSITIVE_INFINITY;
		}

		// Calculate Cost
		// TODO should be done also by ESH to enable this use-case:
		// https://community.openems.io/t/limitierung-bei-negativen-preisen-und-lastgang-einkauf/2713/2
		double cost;
		if (energyFlow.getGrid() > 0) {
			// Filter negative prices
			var price = Math.max(0, period.price());

			cost = // Cost for direct Consumption
					energyFlow.getGridToCons() * price
							// Cost for future Consumption after storage
							+ energyFlow.getGridToEss() * price * EFFICIENCY_FACTOR;

		} else {
			// Sell-to-Grid
			cost = 0.;
		}
		if (bestScheduleCollector != null) {
			final var srp = SimulationResult.Period.from(period, energyFlow, simulation.getEssInitial());
			bestScheduleCollector.allPeriods.accept(srp);
			eshIndex = 0;
			for (var esh : handlers) {
				if (esh instanceof EnergyScheduleHandler.WithDifferentStates<?, ?> e) {
					bestScheduleCollector.eshStates.accept(new EshToState(e, srp, //
							e.postProcessPeriod(period, simulation, energyFlow,
									gt.get(eshIndex++).get(periodIndex).intValue())));
				}
			}
		}

		// Prepare for next period
		simulation.calculateEssInitial(energyFlow.getEss());

		return cost;
	}

	/**
	 * Runs the optimization and returns the "best" simulation result.
	 * 
	 * @param cache                      the {@link GenotypeCache}
	 * @param gsc                        the {@link GlobalSimulationsContext}
	 * @param previousResult             the {@link SimulationResult} of the
	 *                                   previous optimization run
	 * @param engineInterceptor          an interceptor for the
	 *                                   {@link Engine.Builder}
	 * @param evolutionStreamInterceptor an interceptor for the
	 *                                   {@link EvolutionStream}
	 * @return the best Schedule
	 */
	public static SimulationResult getBestSchedule(GenotypeCache cache, GlobalSimulationsContext gsc,
			SimulationResult previousResult,
			Function<Engine.Builder<IntegerGene, Double>, Engine.Builder<IntegerGene, Double>> engineInterceptor,
			Function<EvolutionStream<IntegerGene, Double>, EvolutionStream<IntegerGene, Double>> evolutionStreamInterceptor) {
		// Genotype:
		// - Separate IntegerChromosome per EnergyScheduleHandler WithDifferentStates
		// - Chromosome length = number of periods
		// - Integer-Genes represent the state
		final var chromosomes = gsc.handlers().stream() //
				.filter(EnergyScheduleHandler.WithDifferentStates.class::isInstance) //
				.map(EnergyScheduleHandler.WithDifferentStates.class::cast) //
				.map(esh -> IntegerChromosome.of(0, esh.getAvailableStates().length, gsc.periods().size())) //
				.toList();
		if (chromosomes.isEmpty()) {
			return SimulationResult.EMPTY;
		}
		final var gtf = Genotype.of(chromosomes);

		// Define the cost function
		var eval = (Function<Genotype<IntegerGene>, Double>) (gt) -> {
			gsc.simulationCounter().incrementAndGet();
			return calculateCost(cache, gsc, gt);
		};

		// Decide for single- or multi-threading
		final Executor executor;
		final var availableCores = Runtime.getRuntime().availableProcessors() - 1;
		if (availableCores > 1) {
			// Executor is a Thread-Pool with CPU-Cores minus one
			executor = new ForkJoinPool(availableCores);
			System.out.println("OPTIMIZER Executor runs on " + availableCores + " cores");
		} else {
			// Executor is the current thread
			executor = Runnable::run;
			System.out.println("OPTIMIZER Executor runs on current thread");
		}

		// Build the Jenetics Engine
		var engine = Engine //
				.builder(eval, gtf) //
				.executor(executor) //
				.minimizing();
		if (engineInterceptor != null) {
			engine = engineInterceptor.apply(engine);
		}

		// Start with previous simulation result as initial population if available
		var initialPopulation = fromExistingSimulationResult(gsc, previousResult);
		EvolutionStream<IntegerGene, Double> stream;
		if (previousResult != null) {
			stream = engine.build().stream(List.of(initialPopulation));
		} else {
			stream = engine.build().stream();
		}
		stream = stream.limit(result -> !currentThread().isInterrupted());
		if (evolutionStreamInterceptor != null) {
			stream = evolutionStreamInterceptor.apply(stream);
		}

		// Start the evaluation
		var bestGt = stream //
				.collect(toBestGenotype());

		return SimulationResult.fromQuarters(gsc, bestGt);
	}

	protected static record BestScheduleCollector(//
			Consumer<SimulationResult.Period> allPeriods, //
			Consumer<EshToState> eshStates) {
	}

	protected static record EshToState(//
			EnergyScheduleHandler.WithDifferentStates<?, ?> esh, //
			SimulationResult.Period period, //
			int postProcessedStateIndex) {
	}
}
