package io.openems.edge.io.hal.api.linuxfs.devices;

import io.openems.edge.io.hal.api.Led;
import io.openems.edge.io.hal.linuxfs.HardwareFactory;
import io.openems.edge.io.hal.linuxfs.LinuxFsDigitalOut;

public class LinuxFsLed implements Led {

	private final LinuxFsDigitalOut dout;
	
	public LinuxFsLed(HardwareFactory context, int pinNumber) {
		this.dout = context.fabricateOut(pinNumber);
	}
	
	@Override
	public void on() {
		this.dout.on();
	}

	@Override
	public void off() {
		this.dout.off();
	}

	@Override
	public void toggle() {
		this.dout.toggle();
		
	}

	@Override
	public boolean isOn() {
		return this.dout.isOn();
	}
	
	@Override
	public void release() {
		try {
			this.dout.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
