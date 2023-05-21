package io.openems.edge.bridge.modbus.api.worker.internal;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import io.openems.edge.bridge.modbus.api.LogVerbosity;
import io.openems.edge.bridge.modbus.api.task.Task;

/**
 * Manages the Read-, Write- and Wait-Tasks for one Cycle.
 * 
 * <p>
 * <li>{@link #onBeforeProcessImage()} initialize the next Cycle if previous
 * Cycle had finished
 * <li>{@link #onExecuteWrite()} puts Write-Tasks as highest priority
 */
public class CycleTasksManager {

	private final Function<DefectiveComponents, CycleTasks> cycleTasksSupplier;
	private final Consumer<Boolean> cycleTimeIsTooShortCallback;
	private final AtomicReference<LogVerbosity> logVerbosity;
	private final WaitDelayHandler waitDelayHandler;
	private final WaitMutexTask waitMutexTask = new WaitMutexTask();
	private final DefectiveComponents defectiveComponents = new DefectiveComponents();

	private CycleTasks cycleTasks;

	public CycleTasksManager(AtomicReference<LogVerbosity> logVerbosity,
			Function<DefectiveComponents, CycleTasks> cycleTasksSupplier,
			Consumer<Boolean> cycleTimeIsTooShortCallback) {
		this.logVerbosity = logVerbosity;
		this.waitDelayHandler = new WaitDelayHandler(this.logVerbosity, () -> this.onWaitDelayTaskFinished());
		this.cycleTasksSupplier = cycleTasksSupplier;
		this.cycleTimeIsTooShortCallback = cycleTimeIsTooShortCallback;
	}

	protected CycleTasksManager(Function<DefectiveComponents, CycleTasks> cycleTasksSupplier,
			Consumer<Boolean> cycleTimeIsTooShortCallback) {
		this(new AtomicReference<>(LogVerbosity.NONE), cycleTasksSupplier, cycleTimeIsTooShortCallback);
	}

	private static enum StateMachine {
		INITIAL_WAIT, //
		READ_BEFORE_WRITE, //
		WAIT_FOR_WRITE, //
		WRITE, //
		WAIT_BEFORE_READ, //
		READ_AFTER_WRITE, //
		FINISHED
	}

	private StateMachine state = StateMachine.FINISHED;

	/**
	 * Called on BEFORE_PROCESS_IMAGE event.
	 */
	public synchronized void onBeforeProcessImage() {
		// Evaluate Cycle-Time-Is-Too-Short and stop early
		if (this.state != StateMachine.FINISHED) {
			this.cycleTimeIsTooShortCallback.accept(true);
			return;
		} else {
			this.cycleTimeIsTooShortCallback.accept(false);
		}

		// Fill queues for this Cycle
		this.cycleTasks = this.cycleTasksSupplier.apply(this.defectiveComponents);

		// Calculate Delay
		this.waitDelayHandler.onBeforeProcessImage(//
				this.cycleTasks.containsDefectiveComponent(this.defectiveComponents));

		// Initialize next Cycle
		this.state = StateMachine.INITIAL_WAIT;

		// Interrupt wait
		this.waitMutexTask.interrupt();
	}

	/**
	 * Called on EXECUTE_WRITE event.
	 * 
	 * @return
	 */
	public synchronized void onExecuteWrite() {
		this.state = StateMachine.WRITE;

		this.waitMutexTask.interrupt();
	}

	/**
	 * Gets the next {@link Task}. This is called in a separate Thread by
	 * {@link ModbusWorker}.
	 * 
	 * @return next {@link Task}
	 */
	public Task getNextTask() {
		if (this.cycleTasks == null) {
			return this.waitMutexTask;
		}

		return switch (this.state) {

		case INITIAL_WAIT ->
			// Waiting for planned waiting time to pass
			this.waitDelayHandler.getWaitDelayTask();

		case READ_BEFORE_WRITE -> {
			// Read-Task available?
			var task = this.cycleTasks.reads().poll();
			if (task != null) {
				yield task;
			}
			// Otherwise -> next state + recursive call
			this.state = StateMachine.WAIT_FOR_WRITE;
			yield this.getNextTask();
		}

		case WAIT_FOR_WRITE ->
			// Waiting for EXECUTE_WRITE event
			this.waitMutexTask;

		case WRITE -> {
			// Write-Task available?
			var task = this.cycleTasks.writes().poll();
			if (task != null) {
				yield task;
			}
			// Otherwise -> next state + recursive call
			this.state = StateMachine.WAIT_BEFORE_READ;
			yield this.getNextTask();
		}

		case WAIT_BEFORE_READ ->
			// Waiting for planned waiting time to pass
			this.waitDelayHandler.getWaitDelayTask();

		case READ_AFTER_WRITE -> {
			// Read-Task available?
			var task = this.cycleTasks.reads().poll();
			if (task != null) {
				yield task;
			}
			// Otherwise -> next state + recursive call
			this.state = StateMachine.FINISHED;
			yield this.getNextTask();
		}

		case FINISHED -> {
			this.waitDelayHandler.onFinished();
			// Waiting for BEFORE_PROCESS_IMAGE event
			yield this.waitMutexTask;
		}

		};
	}

	/**
	 * Waiting in INITIAL_WAIT or WAIT_BEFORE_READ finished
	 */
	private synchronized void onWaitDelayTaskFinished() {
		this.state = switch (this.state) {
		// Expected
		case INITIAL_WAIT -> StateMachine.READ_BEFORE_WRITE;
		case WAIT_BEFORE_READ -> StateMachine.READ_AFTER_WRITE;
		// Unexpected (the State has been unexpectedly changed in-between)
		default -> this.state;
		};
	}
}