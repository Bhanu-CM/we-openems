package io.openems.edge.battery.fenecon.f2b.dummy;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.battery.api.Battery;
import io.openems.edge.battery.fenecon.f2b.BatteryFeneconF2b;
import io.openems.edge.battery.fenecon.f2b.DeviceSpecificOnSetNextValueHandler;
import io.openems.edge.battery.fenecon.f2b.dummy.statemachine.Context;
import io.openems.edge.battery.fenecon.f2b.dummy.statemachine.StateMachine;
import io.openems.edge.battery.fenecon.f2b.dummy.statemachine.StateMachine.State;
import io.openems.edge.battery.protection.BatteryProtection;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.common.startstop.StartStop;
import io.openems.edge.common.startstop.StartStoppable;

/**
 * Provides a simple, simulated {@link Battery} component that can be used
 * together with the OpenEMS Component test framework.
 */
public class BatteryFeneconF2bDummyImpl extends AbstractOpenemsComponent
		implements BatteryFeneconF2bDummy, BatteryFeneconF2b, Battery, OpenemsComponent, StartStoppable, EventHandler {

	private final Logger log = LoggerFactory.getLogger(BatteryFeneconF2bDummyImpl.class);
	private final StateMachine stateMachine = new StateMachine(State.UNDEFINED);
	private StartStop startStopTarget = StartStop.UNDEFINED;
	private boolean hvContactorUnlocked = false;

	private ComponentManager componentManager;

	private Config config = null;

	public BatteryFeneconF2bDummyImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				StartStoppable.ChannelId.values(), //
				Battery.ChannelId.values(), //
				BatteryProtection.ChannelId.values(), //
				BatteryFeneconF2b.ChannelId.values(), //
				BatteryFeneconF2bDummy.ChannelId.values()//
		);
		for (var channel : this.channels()) {
			channel.nextProcessImage();
		}
	}

	@SuppressWarnings("unused")
	private void activate(ComponentContext context, Config config) throws OpenemsException {
		this.config = config;
		super.activate(null, config.id(), "", true);
	}

	@Override
	protected void deactivate() {
		super.deactivate();
	}

	/**
	 * Sets and applies the {@link Battery.ChannelId#CAPACITY}.
	 *
	 * @param value the Capacity in [Wh]
	 * @return myself
	 */
	public BatteryFeneconF2bDummyImpl withCapacity(int value) {
		this._setCapacity(value);
		this.getCapacityChannel().nextProcessImage();
		return this;
	}

	/**
	 * Sets and applies the {@link Battery.ChannelId#VOLTAGE}.
	 *
	 * @param value the Capacity in [V]
	 * @return myself
	 */
	public BatteryFeneconF2bDummyImpl withVoltage(int value) {
		this._setVoltage(value);
		this.getVoltageChannel().nextProcessImage();
		return this;
	}

	/**
	 * Sets and applies the {@link Battery.ChannelId#DISCHARGE_MAX_CURRENT}.
	 *
	 * @param value the Discharge Max Current in [A]
	 * @return myself
	 */
	public BatteryFeneconF2bDummyImpl withDischargeMaxCurrent(int value) {
		this._setDischargeMaxCurrent(value);
		this.getDischargeMaxCurrentChannel().nextProcessImage();
		return this;
	}

	/**
	 * Sets and applies the {@link Battery.ChannelId#CHARGE_MAX_CURRENT}.
	 *
	 * @param value the Charge Max Current in [A]
	 * @return myself
	 */
	public BatteryFeneconF2bDummyImpl withChargeMaxCurrent(int value) {
		this._setChargeMaxCurrent(value);
		this.getChargeMaxCurrentChannel().nextProcessImage();
		return this;
	}

	/**
	 * Sets and applies the {@link Battery.ChannelId#MIN_CELL_VOLTAGE}.
	 *
	 * @param value the Min-Cell-Voltage in [mV]
	 * @return myself
	 */
	public BatteryFeneconF2bDummyImpl withMinCellVoltage(int value) {
		this._setMinCellVoltage(value);
		this.getMinCellVoltageChannel().nextProcessImage();
		return this;
	}

	/**
	 * Sets and applies the {@link Battery.ChannelId#MAX_CELL_VOLTAGE}.
	 *
	 * @param value the Max-Cell-Voltage in [mV]
	 * @return myself
	 */
	public BatteryFeneconF2bDummyImpl withMaxCellVoltage(int value) {
		this._setMaxCellVoltage(value);
		this.getMaxCellVoltageChannel().nextProcessImage();
		return this;
	}

	/**
	 * Sets and applies the {@link Battery.ChannelId#MIN_CELL_TEMPERATURE}.
	 *
	 * @param value the Min-Cell-Temperature in [degC]
	 * @return myself
	 */
	public BatteryFeneconF2bDummyImpl withMinCellTemperature(int value) {
		this._setMinCellTemperature(value);
		this.getMinCellTemperatureChannel().nextProcessImage();
		return this;
	}

	/**
	 * Sets and applies the {@link Battery.ChannelId#MAX_CELL_TEMPERATURE}.
	 *
	 * @param value the Max-Cell-Temperature in [degC]
	 * @return myself
	 */
	public BatteryFeneconF2bDummyImpl withMaxCellTemperature(int value) {
		this._setMaxCellTemperature(value);
		this.getMaxCellTemperatureChannel().nextProcessImage();
		return this;
	}

	@Override
	public void handleEvent(Event event) {
		if (!this.isEnabled()) {
			return;
		}
		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_AFTER_PROCESS_IMAGE:
			this.handleStateMachine();
			break;
		}
	}

	private void handleStateMachine() {

		// Store the current State
		this._setStateMachine(this.stateMachine.getCurrentState());

		// Prepare Context
		try {
			var context = new Context(this, this.componentManager, this.config);

			// Call the StateMachine
			this.stateMachine.run(context);

			this.channel(BatteryFeneconF2bDummy.ChannelId.RUN_FAILED).setNextValue(false);

		} catch (OpenemsNamedException e) {
			this.logError(this.log, "StateMachine failed: " + e.getMessage());
			this.channel(BatteryFeneconF2bDummy.ChannelId.RUN_FAILED).setNextValue(true);
		}
	}

	@Override
	public State getCurrentState() {
		return this.stateMachine.getCurrentState();
	}

	@Override
	public synchronized void setStartStop(StartStop value) {
		this.startStopTarget = value;
	}

	@Override
	public StartStop getStartStopTarget() {
		return switch (this.config.startStop()) {
		case AUTO -> this.startStopTarget;
		case START -> StartStop.START;
		case STOP -> StartStop.STOP;
		};
	}

	@Override
	public void setHvContactorUnlocked(boolean value) {
		this.hvContactorUnlocked = value;
	}

	@Override
	public boolean getHvContactorUnlocked() {
		return this.hvContactorUnlocked;
	}

	@Override
	public DeviceSpecificOnSetNextValueHandler<? extends BatteryFeneconF2b> getDeviceSpecificOnSetNextValueHandler() {
		return null;
	}
}
