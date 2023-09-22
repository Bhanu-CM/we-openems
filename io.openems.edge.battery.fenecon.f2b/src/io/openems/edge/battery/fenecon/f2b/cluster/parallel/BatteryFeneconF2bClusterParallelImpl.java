package io.openems.edge.battery.fenecon.f2b.cluster.parallel;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.service.event.propertytypes.EventTopics;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.battery.api.Battery;
import io.openems.edge.battery.fenecon.f2b.BatteryFeneconF2b;
import io.openems.edge.battery.fenecon.f2b.DeviceSpecificOnChangeHandler;
import io.openems.edge.battery.fenecon.f2b.cluster.common.AbstractBatteryFeneconF2bCluster;
import io.openems.edge.battery.fenecon.f2b.cluster.common.BatteryFeneconF2bCluster;
import io.openems.edge.battery.fenecon.f2b.cluster.common.ChannelManager;
import io.openems.edge.battery.fenecon.f2b.cluster.common.statemachine.Context;
import io.openems.edge.battery.fenecon.f2b.cluster.common.statemachine.StateMachine;
import io.openems.edge.battery.fenecon.f2b.cluster.common.statemachine.StateMachine.State;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.common.modbusslave.ModbusSlave;
import io.openems.edge.common.startstop.StartStop;
import io.openems.edge.common.startstop.StartStoppable;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Battery.Fenecon.F2B.Cluster.Parallel", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
@EventTopics({ //
		EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE //
})
public class BatteryFeneconF2bClusterParallelImpl extends AbstractBatteryFeneconF2bCluster
		implements BatteryFeneconF2bClusterParallel, BatteryFeneconF2bCluster, BatteryFeneconF2b, OpenemsComponent,
		EventHandler, ModbusSlave, Battery, StartStoppable {

	private final Logger log = LoggerFactory.getLogger(BatteryFeneconF2bClusterParallelImpl.class);
	private final ParallelChannelManager channelManager = new ParallelChannelManager(this);
	private final StateMachine stateMachine = new StateMachine(State.UNDEFINED);
	private boolean hvContactorUnlocked = false;

	@Reference
	private ConfigurationAdmin cm;

	@Reference
	protected ComponentManager componentManager;

	@Reference(//
			policy = ReferencePolicy.DYNAMIC, //
			policyOption = ReferencePolicyOption.GREEDY, //
			cardinality = ReferenceCardinality.MULTIPLE, //
			target = "(&(enabled=true)(!(service.factoryPid=Battery.Fenecon.F2B.Cluster.Parallel)))")
	protected synchronized void addBattery(BatteryFeneconF2b battery) {
		super.addBattery(battery);
	}

	protected synchronized void removeBattery(BatteryFeneconF2b battery) {
		super.removeBattery(battery);
	}

	protected ChannelManager getChannelManager() {
		return this.channelManager;
	}

	public BatteryFeneconF2bClusterParallelImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				Battery.ChannelId.values(), //
				BatteryFeneconF2b.ChannelId.values(), //
				BatteryFeneconF2bCluster.ChannelId.values(), //
				BatteryFeneconF2bClusterParallel.ChannelId.values(), //
				StartStoppable.ChannelId.values() //
		);
	}

	@Activate
	protected void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled(), this.cm, config.startStop(),
				config.battery_ids());

		if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "Battery", config.battery_ids())) {
			return;
		}
	}

	@Override
	@Deactivate
	protected void deactivate() {
		this.channelManager.deactivate();
		super.deactivate();
	}

	@Override
	public void handleEvent(Event event) {
		super.handleEvent(event);
	}

	@Override
	public void startBatteries() throws OpenemsException {
		super.startBatteries();
		final var minVoltage = super.batteries.stream() //
				.filter(BatteryFeneconF2b.class::isInstance) //
				.map(BatteryFeneconF2b.class::cast) //
				.map(BatteryFeneconF2b::getInternalVoltage) //
				.filter(Value::isDefined) //
				.mapToInt(Value::get) //
				.min();
		final var maxVoltage = super.batteries.stream() //
				.filter(BatteryFeneconF2b.class::isInstance) //
				.map(BatteryFeneconF2b.class::cast) //
				.map(BatteryFeneconF2b::getInternalVoltage) //
				.filter(Value::isDefined) //
				.mapToInt(Value::get) //
				.max();
		if (minVoltage.isEmpty() || maxVoltage.isEmpty() || minVoltage.getAsInt() == 0 || maxVoltage.getAsInt() == 0) {
			return;
		}
		final var voltageDifference = maxVoltage.getAsInt() - minVoltage.getAsInt();
		if (voltageDifference <= 4) {
			this.setHvContactorUnlocked(true);
		} else {
			this.setHvContactorUnlocked(false);
			this._setVoltageDifferenceHigh(true);
			throw new OpenemsException("High voltage difference!");
		}
	}

	@Override
	public String debugLog() {
		return Battery.generateDebugLog(this, this.stateMachine);
	}

	@Override
	protected ComponentManager getComponentManager() {
		return this.componentManager;
	}

	@Override
	protected void handleStateMachine() {
		// Store the current State
		var state = this.stateMachine.getCurrentState();
		this._setStateMachine(state);

		// Initialize 'Start-Stop' Channel
		this._setStartStop(StartStop.UNDEFINED);

		// Prepare Context
		try {
			var context = new Context(this, this.componentManager.getClock(), this.batteries);

			// Call the StateMachine
			this.stateMachine.run(context);

			this._setRunFailed(false);
		} catch (OpenemsNamedException e) {
			this._setRunFailed(true);
			this.logError(this.log, "StateMachine failed: " + e.getMessage());
		}
	}

	@Override
	public void setStartStop(StartStop value) {
		this.startStopTarget.set(value);
	}

	@Override
	public void setHvContactorUnlocked(boolean value) {
		this.hvContactorUnlocked = value;
	}

	@Override
	public boolean isHvContactorUnlocked() {
		return this.hvContactorUnlocked;
	}

	@Override
	public BatteryFeneconF2bCluster getBatteryFeneconF2bCluster() {
		return this;
	}

	@Override
	public DeviceSpecificOnChangeHandler<? extends BatteryFeneconF2b> getDeviceSpecificOnChangeHandler() {
		return null;
	}
}
