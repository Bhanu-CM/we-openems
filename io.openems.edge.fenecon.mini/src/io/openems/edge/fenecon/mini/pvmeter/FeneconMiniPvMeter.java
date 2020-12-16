package io.openems.edge.fenecon.mini.pvmeter;

import org.osgi.service.event.EventHandler;

import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.meter.api.SymmetricMeter;
import io.openems.edge.timedata.api.TimedataProvider;

public interface FeneconMiniPvMeter extends SymmetricMeter, OpenemsComponent, TimedataProvider, EventHandler {

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		;
		private final Doc doc;

		private ChannelId(Doc doc) {
			this.doc = doc;
		}

		@Override
		public Doc doc() {
			return this.doc;
		}
	}

}
