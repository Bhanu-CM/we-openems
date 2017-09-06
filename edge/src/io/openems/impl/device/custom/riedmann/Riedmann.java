package io.openems.impl.device.custom.riedmann;

import java.util.HashSet;
import java.util.Set;

import io.openems.api.bridge.Bridge;
import io.openems.api.channel.ConfigChannel;
import io.openems.api.device.nature.DeviceNature;
import io.openems.api.doc.ConfigInfo;
import io.openems.api.exception.OpenemsException;
import io.openems.impl.protocol.modbus.ModbusDevice;

public class Riedmann extends ModbusDevice {

	@ConfigInfo(title = "", type = RiedmannNature.class)
	public final ConfigChannel<RiedmannNature> device = new ConfigChannel<RiedmannNature>("device", this);

	public Riedmann(Bridge parent) throws OpenemsException {
		super(parent);
	}

	@Override
	protected Set<DeviceNature> getDeviceNatures() {
		Set<DeviceNature> natures = new HashSet<>();
		if (device.valueOptional().isPresent()) {
			natures.add(device.valueOptional().get());
		}
		return natures;
	}

}
