package io.openems.edge.batteryinverter.refu88k.statemachine;

import io.openems.edge.battery.api.Battery;
import io.openems.edge.batteryinverter.refu88k.RefuStore88k;
import io.openems.edge.batteryinverter.refu88k.Config;


public class Context {
	protected final RefuStore88k component;
	protected final Battery battery;
	protected final Config config;
	protected final int setActivePower;
	protected final int setReactivePower;

	public Context(RefuStore88k component, Battery battery, Config config, int setActivePower,
			int setReactivePower) {
		this.component = component;
		this.battery = battery;
		this.config = config;
		this.setActivePower = setActivePower;
		this.setReactivePower = setReactivePower;
	}
}