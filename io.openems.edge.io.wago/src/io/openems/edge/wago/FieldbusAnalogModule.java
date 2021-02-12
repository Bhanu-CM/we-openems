package io.openems.edge.wago;

import io.openems.edge.bridge.modbus.api.element.AbstractModbusElement;
import io.openems.edge.common.channel.StringReadChannel;

public abstract class FieldbusAnalogModule {

	public abstract String getName();

	public abstract AbstractModbusElement<?>[] getInputElements();

	public abstract AbstractModbusElement<?>[] getOutputElements();

	public abstract int getOutputCoils();

	public abstract int getInputCoils();

	public abstract StringReadChannel[] getChannels();

}
