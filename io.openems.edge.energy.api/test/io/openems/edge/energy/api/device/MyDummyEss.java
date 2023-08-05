package io.openems.edge.energy.api.device;

import io.openems.edge.common.channel.Channel;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.energy.api.simulatable.SimpleEssSimulator;
import io.openems.edge.energy.api.simulatable.Simulatable;
import io.openems.edge.energy.api.simulatable.Simulator;

public class MyDummyEss extends AbstractOpenemsComponent implements OpenemsComponent, Simulatable {

	protected MyDummyEss(String id, io.openems.edge.common.channel.ChannelId[] firstInitialChannelIds,
			io.openems.edge.common.channel.ChannelId[]... furtherInitialChannelIds) {
		super(firstInitialChannelIds, furtherInitialChannelIds);
		for (Channel<?> channel : this.channels()) {
			channel.nextProcessImage();
		}
		super.activate(null, id, "", true);
	}

	public MyDummyEss(String id) {
		this(id, //
				OpenemsComponent.ChannelId.values() //
		);
	}

	private int getSoc() {
		return 50;
	}

	private int getCapacity() {
		return 10000;
	}

	@Override
	public Simulator getSimulator() {
		return new SimpleEssSimulator(this.id(), this.getCapacity(), this.getSoc());
	}
}
