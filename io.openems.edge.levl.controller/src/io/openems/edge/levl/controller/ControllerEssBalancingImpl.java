package io.openems.edge.levl.controller;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.service.event.propertytypes.EventTopics;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import io.openems.common.exceptions.InvalidValueException;
import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.jsonrpc.base.JsonrpcResponseSuccess;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.cycle.Cycle;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.common.jsonapi.ComponentJsonApi;
import io.openems.edge.common.jsonapi.JsonApiBuilder;
import io.openems.edge.controller.api.Controller;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.ess.power.api.Phase;
import io.openems.edge.ess.power.api.Pwr;
import io.openems.edge.meter.api.ElectricityMeter;
import io.openems.edge.levl.controller.common.Efficiency;

@Designate(ocd = Config.class, factory = true)
@Component(name = "Levl.Controller.Symmetric.Balancing", immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@EventTopics({ EdgeEventConstants.TOPIC_CYCLE_BEFORE_CONTROLLERS, EdgeEventConstants.TOPIC_CYCLE_AFTER_WRITE, })


public class ControllerEssBalancingImpl extends AbstractOpenemsComponent
		implements ControllerEssBalancing, ComponentJsonApi, EventHandler {

	private final Logger log = LoggerFactory.getLogger(ControllerEssBalancingImpl.class);

	@Reference
	private ConfigurationAdmin cm;

	@Reference
	private ManagedSymmetricEss ess;

	@Reference
	private ElectricityMeter meter;

	@Reference
	private Cycle cycle;

	private Config config;

	private static Clock clock = Clock.systemDefaultZone();

	private LevlControlRequest currentRequest;
	private LevlControlRequest nextRequest;

	private RequestHandler requestHandler;
	private long levlSocWs;
	private long realizedEnergyBatteryWs;
	private long realizedEnergyGridWs;

	private int pucBatteryPower;

	private static final String METHOD = "sendLevlControlRequest";

	public ControllerEssBalancingImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				Controller.ChannelId.values(), //
				ControllerEssBalancing.ChannelId.values() //
		);
	}

	@Activate
	private void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.config = config;
		if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "ess", config.ess_id())) {
			return;
		}
		if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "meter", config.meter_id())) {
			return;
		}
	}

	@Override
	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	public void run() throws OpenemsNamedException {
		/*
		 * Check that we are On-Grid (and warn on undefined Grid-Mode)
		 */
		var gridMode = this.ess.getGridMode();
		if (gridMode.isUndefined()) {
			this.logWarn(this.log, "Grid-Mode is [UNDEFINED]");
		}
		switch (gridMode) {
		case ON_GRID:
		case UNDEFINED:
			break;
		case OFF_GRID:
			return;
		}

		/*
		 * Calculates required charge/discharge power
		 */
		var calculatedPower = calculateRequiredPower();

		/*
		 * set result
		 */
		this.ess.setActivePowerEqualsWithPid(calculatedPower);
		this.ess.setReactivePowerEquals(0);
	}

	/**
	 * Calculates required charge/discharge power.
	 *
	 * @param essPower           the charge/discharge power of the
	 *                           {@link ManagedSymmetricEss}
	 * @param gridPower          the buy-from-grid/sell-to grid power
	 * @param targetGridSetpoint the configured targetGridSetpoint
	 * @return the required power
	 * @throws InvalidValueException
	 */
	protected int calculateRequiredPower() throws OpenemsNamedException {
		double cycleTimeS = this.cycle.getCycleTime() / 1000;

		// load physical values
		int physicalSoc = this.ess.getSoc().getOrError();
		int gridPower = this.meter.getActivePower().getOrError();
		int essPower = this.ess.getActivePower().getOrError();
		int essCapacity = this.ess.getCapacity().getOrError();
		int minEssPower = this.ess.getPower().getMinPower(this.ess, Phase.ALL, Pwr.ACTIVE);
		int maxEssPower = this.ess.getPower().getMaxPower(this.ess, Phase.ALL, Pwr.ACTIVE);

		long essCapacityWs = essCapacity * 3600;
		long physicalSocWs = physicalSoc / 100 * essCapacityWs;

		// primary use case (puc) calculation
		long pucSocWs = physicalSocWs - this.levlSocWs;
		this.pucBatteryPower = calculatePucBatteryPower(cycleTimeS, gridPower, essPower,
				essCapacityWs, pucSocWs, minEssPower, maxEssPower);
		int pucGridPower = gridPower - essPower + this.pucBatteryPower;
		long nextPucSocWs = pucSocWs + Math.round(this.pucBatteryPower * cycleTimeS);

		// levl calculation
		int levlPowerW = this.calculateLevlPowerW(this.pucBatteryPower, minEssPower, maxEssPower, pucGridPower,
				nextPucSocWs, essCapacityWs, cycleTimeS);

		// overall calculation
		long batteryPowerW = this.pucBatteryPower + levlPowerW;
		return (int) batteryPowerW;
	}

	/**
	 * Calculates the power of the primary use case (puc)
	 * 
	 * @param cycleTimeS
	 * @param gridPower
	 * @param essPower
	 * @param essCapacityWs
	 * @param pucSocWs
	 * @param minEssPower
	 * @param maxEssPower
	 * @return
	 */
	private int calculatePucBatteryPower(double cycleTimeS, int gridPower, int essPower,
			long essCapacityWs, long pucSocWs, int minEssPower, int maxEssPower) {
		// calculate pucPower without any limits
		int pucBatteryPower = gridPower + essPower;

		// apply ess power limits
		pucBatteryPower = Math.max(Math.min(pucBatteryPower, maxEssPower), minEssPower);

		// apply soc bounds
		pucBatteryPower = applyPucSocBounds(cycleTimeS, essCapacityWs, pucSocWs, pucBatteryPower);
		return pucBatteryPower;
	}

	/**
	 * Checks and corrects the pucPower if it would exceed the upper or lower limits
	 * of the SoC.
	 * 
	 * @param cycleTimeSec
	 * @param essCapacityWs
	 * @param pucSocWs
	 * @param pucPower
	 * @return the restricted pucPower
	 */
	private int applyPucSocBounds(double cycleTimeS, long essCapacityWs, long pucSocWs, int pucPower) {
		long dischargeEnergyLowerBoundWs = pucSocWs - essCapacityWs;
		long dischargeEnergyUpperBoundWs = pucSocWs;

		long powerLowerBound = Efficiency.apply(Math.round(dischargeEnergyLowerBoundWs / cycleTimeS),
				this.currentRequest.efficiencyPercent);
		long powerUpperBound = Efficiency.apply(Math.round(dischargeEnergyUpperBoundWs / cycleTimeS),
				this.currentRequest.efficiencyPercent);

		return (int) Math.max(Math.min(pucPower, powerUpperBound), powerLowerBound);
	}

	private int calculateLevlPowerW(int pucBatteryPower, int minEssPower, int maxEssPower, int pucGridPower,
			long nextPucSocWs, long essCapacityWs, double cycleTimeS) {
		long levlPower = Math.round((this.currentRequest.energyWs - this.realizedEnergyGridWs) / (double) cycleTimeS);

		levlPower = this.applyBatteryPowerLimitsToLevlPower(levlPower, pucBatteryPower, minEssPower, maxEssPower);
		levlPower = this.applySocBoundariesToLevlPower(levlPower, nextPucSocWs, essCapacityWs, cycleTimeS);
		levlPower = this.applyGridPowerLimitsToLevlPower(levlPower, pucGridPower);
		levlPower = this.applyInfluenceSellToGridConstraint(levlPower, pucGridPower);

		return (int) levlPower;
	}

	private long applyInfluenceSellToGridConstraint(long levlPower, int pucGridPower) {
		if (!this.currentRequest.influenceSellToGrid) {
			if (pucGridPower < 0) {
				// if primary use case sells to grid, levl isn't allowed to do anything
				levlPower = 0;
			} else {
				// if primary use case buys from grid, levl can sell maximum this amount to grid
				levlPower = Math.min(levlPower, pucGridPower);
			}
		}
		return levlPower;
	}

	private long applyBatteryPowerLimitsToLevlPower(long levlPower, int pucBatteryPower, int minEssPower,
			int maxEssPower) {
		int levlPowerLowerBound = minEssPower - pucBatteryPower;
		int levlPowerUpperBound = maxEssPower - pucBatteryPower;
		return Math.max(Math.min(levlPower, levlPowerUpperBound), levlPowerLowerBound);
	}

	private long applySocBoundariesToLevlPower(long levlPower, long nextPucSocWs, long essCapacityWs,
			double cycleTimeS) {
		long levlSocLowerBoundWs = this.currentRequest.socLowerBoundPercent / 100 * essCapacityWs - nextPucSocWs;
		long levlSocUpperBoundWs = this.currentRequest.socUpperBoundPercent / 100 * essCapacityWs - nextPucSocWs;
		if (levlSocLowerBoundWs > 0)
			levlSocLowerBoundWs = 0;
		if (levlSocUpperBoundWs < 0)
			levlSocUpperBoundWs = 0;

		long levlDischargeEnergyLowerBoundWs = -(levlSocUpperBoundWs - this.levlSocWs);
		long levlDischargeEnergyUpperBoundWs = -(levlSocLowerBoundWs - this.levlSocWs);

		long levlPowerLowerBound = Efficiency.apply(Math.round(levlDischargeEnergyLowerBoundWs / cycleTimeS),
				this.currentRequest.efficiencyPercent);
		long levlPowerUpperBound = Efficiency.apply(Math.round(levlDischargeEnergyUpperBoundWs / cycleTimeS),
				this.currentRequest.efficiencyPercent);

		return Math.max(Math.min(levlPower, levlPowerUpperBound), levlPowerLowerBound);
	}

	private long applyGridPowerLimitsToLevlPower(long levlPower, int pucGridPower) {
		long levlPowerLowerBound = -(this.currentRequest.buyFromGridLimitW - pucGridPower);
		long levlPowerUpperBound = -(this.currentRequest.sellToGridLimitW - pucGridPower);
		return Math.max(Math.min(levlPower, levlPowerUpperBound), levlPowerLowerBound);
	}

	@Override
	public void buildJsonApiRoutes(JsonApiBuilder builder) {
		builder.handleRequest(METHOD, call -> {
			var levlControlRequest = LevlControlRequest.from(call.getRequest());
			this.nextRequest = levlControlRequest;
			this.levlSocWs = levlControlRequest.levlSocWh * 3600 - this.realizedEnergyBatteryWs;
			return JsonrpcResponseSuccess
					.from(this.generateResponse(call.getRequest().getId(), levlControlRequest.getLevlRequestId()));
		});
	}

	private JsonObject generateResponse(UUID requestId, String levlRequestId) {
		JsonObject response = new JsonObject();
		var result = new JsonObject();
		result.addProperty("levlRequestId", levlRequestId);
		response.addProperty("id", requestId.toString());
		response.add("result", result);
		return response;
	}

	private static boolean isActive(LevlControlRequest request) {
		LocalDateTime now = LocalDateTime.now(clock);
		return !(request == null || now.isBefore(request.getStart()) || now.isAfter(request.getDeadline()));
	}

	@Override
	public void handleEvent(Event event) {
		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_BEFORE_CONTROLLERS -> {
			// test cases:
			// - currentRequest: aktiv; nextRequest: nicht aktiv
			// - currentRequest: aktiv; nextRequest: null
			// ...
			if (isActive(this.nextRequest)) {
				if (this.currentRequest != null) {
					this.finishRequest();
				}
				this.currentRequest = this.nextRequest;
				this.nextRequest = null;
			} else if (!isActive(this.currentRequest)) {
				this.finishRequest();
			}
		}
		case EdgeEventConstants.TOPIC_CYCLE_AFTER_WRITE -> {
			int levlPower = 0;
			if (this.ess.getActivePower().isDefined()) {
				levlPower = this.ess.getActivePower().get() - this.pucBatteryPower;
			}
			long levlDischargeEnergyWs = levlPower * this.cycle.getCycleTime() / 1000;
			this.realizedEnergyGridWs += levlDischargeEnergyWs;
			this.realizedEnergyBatteryWs += Efficiency.apply(realizedEnergyGridWs,
					this.currentRequest.efficiencyPercent);
			this.levlSocWs -= Efficiency.apply(realizedEnergyGridWs, this.currentRequest.efficiencyPercent);
		}
		}
	}

	private void finishRequest() {
		// Channel realizedEnergy und requestTimestamp schreiben
		this._setRealizedPowerW(this.realizedEnergyGridWs);
		this._setLastControlRequestTimestamp(this.currentRequest.getTimestamp());
		this.realizedEnergyGridWs = 0;
		this.realizedEnergyBatteryWs = 0;
		this.currentRequest = null;
	}
}
