package io.openems.edge.fenecon.dess.charger;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.bridge.modbus.api.AbstractOpenemsModbusComponent;
import io.openems.edge.bridge.modbus.api.ElementToChannelConverter;
import io.openems.edge.bridge.modbus.api.ModbusProtocol;
import io.openems.edge.bridge.modbus.api.element.DummyRegisterElement;
import io.openems.edge.bridge.modbus.api.element.UnsignedDoublewordElement;
import io.openems.edge.bridge.modbus.api.element.UnsignedWordElement;
import io.openems.edge.bridge.modbus.api.element.WordOrder;
import io.openems.edge.bridge.modbus.api.task.FC3ReadRegistersTask;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.common.taskmanager.Priority;
import io.openems.edge.ess.dccharger.api.EssDcCharger;
import io.openems.edge.timedata.api.TimedataProvider;
import io.openems.edge.timedata.api.utils.CalculateEnergyFromPower;

public abstract class AbstractFeneconDessCharger extends AbstractOpenemsModbusComponent
		implements FeneconDessCharger, EssDcCharger, OpenemsComponent, TimedataProvider, EventHandler {

	private final CalculateEnergyFromPower calculateActualEnergy = new CalculateEnergyFromPower(this,
			EssDcCharger.ChannelId.ACTUAL_ENERGY);

	public AbstractFeneconDessCharger() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				EssDcCharger.ChannelId.values(), //
				FeneconDessCharger.ChannelId.values() //
		);
	}

	@Override
	protected ModbusProtocol defineModbusProtocol() throws OpenemsException {
		final int offset = this.getOffset();
		return new ModbusProtocol(this, //
				new FC3ReadRegistersTask(offset + 2, Priority.LOW, //
						m(EssDcCharger.ChannelId.ACTUAL_POWER, new UnsignedWordElement(offset + 2)), //
						new DummyRegisterElement(offset + 3, offset + 4),
						m(FeneconDessCharger.ChannelId.ORIGINAL_ACTUAL_ENERGY,
								new UnsignedDoublewordElement(offset + 5).wordOrder(WordOrder.MSWLSW),
								ElementToChannelConverter.SCALE_FACTOR_2)) //
		);
	}

	@Override
	public String debugLog() {
		return "P:" + this.getActualPower().asString();
	}

	protected abstract int getOffset();

	@Override
	public void handleEvent(Event event) {
		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_AFTER_PROCESS_IMAGE:
			this.calculateEnergy();
			break;
		}
	}

	/**
	 * Calculate the Energy values from ActivePower.
	 */
	private void calculateEnergy() {
		Integer actualPower = this.getActualPower().get();
		if (actualPower == null) {
			// Not available
			this.calculateActualEnergy.update(null);
		} else if (actualPower > 0) {
			this.calculateActualEnergy.update(actualPower);
		} else {
			this.calculateActualEnergy.update(0);
		}
	}
}