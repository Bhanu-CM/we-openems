package io.openems.edge.solaredge.charger;


import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import io.openems.common.channel.AccessMode;
import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.bridge.modbus.api.AbstractOpenemsModbusComponent;
import io.openems.edge.bridge.modbus.api.ElementToChannelConverter;
import io.openems.edge.bridge.modbus.api.ModbusComponent;
import io.openems.edge.bridge.modbus.api.ModbusProtocol;
import io.openems.edge.bridge.modbus.api.element.DummyRegisterElement;
import io.openems.edge.bridge.modbus.api.element.SignedWordElement;
import io.openems.edge.bridge.modbus.api.task.FC3ReadRegistersTask;
import io.openems.edge.common.component.OpenemsComponent;
//import io.openems.edge.common.component.OpenemsComponent.ChannelId;
import io.openems.edge.common.modbusslave.ModbusSlave;
import io.openems.edge.common.modbusslave.ModbusSlaveNatureTable;
import io.openems.edge.common.modbusslave.ModbusSlaveTable;
import io.openems.edge.common.taskmanager.Priority;
import io.openems.edge.ess.dccharger.api.EssDcCharger;
import io.openems.edge.timedata.api.TimedataProvider;
import io.openems.edge.timedata.api.utils.CalculateEnergyFromPower;
import io.openems.edge.common.event.EdgeEventConstants;



public abstract class AbstractSolaredgeDcCharger extends AbstractOpenemsModbusComponent//AbstractOpenemsSunSpecComponent 
	implements SolaredgeDcCharger, EssDcCharger, ModbusComponent, OpenemsComponent, TimedataProvider,
	EventHandler, ModbusSlave {

	private final CalculateEnergyFromPower calculateActualEnergy = new CalculateEnergyFromPower(this,EssDcCharger.ChannelId.ACTUAL_ENERGY);

	public AbstractSolaredgeDcCharger() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				ModbusComponent.ChannelId.values(), //
				EssDcCharger.ChannelId.values(), //
				SolaredgeDcCharger.ChannelId.values() //
		);

	}

	@Override
	protected ModbusProtocol defineModbusProtocol() throws OpenemsException {
		var protocol = new ModbusProtocol(this, //
					new FC3ReadRegistersTask(0x9ca0, Priority.LOW, //
							m(EssDcCharger.ChannelId.CURRENT,
									new SignedWordElement(0x9ca0), ElementToChannelConverter.DIRECT_1_TO_1), //
							new DummyRegisterElement(0x9ca1), //
							m(EssDcCharger.ChannelId.VOLTAGE,
									new SignedWordElement(0x9ca2), ElementToChannelConverter.DIRECT_1_TO_1), //
							new DummyRegisterElement(0x9ca3), //							
							m(EssDcCharger.ChannelId.ACTUAL_POWER,
									new SignedWordElement(0x9ca4), ElementToChannelConverter.DIRECT_1_TO_1))); //
		
		return protocol;
	}

	@Override
	public String debugLog() {
		return "P:" + this.getActualPower().asString();
	}

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
		var actualPower = this.getActualPower().get();
		if (actualPower == null) {
			// Not available
			this.calculateActualEnergy.update(null);
		} else if (actualPower > 0) {
			this.calculateActualEnergy.update(actualPower);
		} else {
			this.calculateActualEnergy.update(0);
		}
	}	

	@Override
	public ModbusSlaveTable getModbusSlaveTable(AccessMode accessMode) {
		return new ModbusSlaveTable(//
				OpenemsComponent.getModbusSlaveNatureTable(accessMode), //
				EssDcCharger.getModbusSlaveNatureTable(accessMode), //
				ModbusSlaveNatureTable.of(SolaredgeDcCharger.class, accessMode, 100) //
						.build());
	}
}
