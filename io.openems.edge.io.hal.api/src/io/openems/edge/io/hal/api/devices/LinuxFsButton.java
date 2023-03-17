package io.openems.edge.io.hal.devices;

import io.openems.edge.io.hal.api.PressButton;
import io.openems.edge.io.hal.linuxfs.HardwareFactory;
import io.openems.edge.io.hal.linuxfs.LinuxFsDigitalIn;

public class LinuxFsButton implements PressButton {

	private final LinuxFsDigitalIn din;
	
	public LinuxFsButton(HardwareFactory context, int pinNumber) {
		this.din = context.fabricateIn(pinNumber);
	}

	@Override
	public boolean isPressed() {
		return this.din.getValue();
	}
	

}
