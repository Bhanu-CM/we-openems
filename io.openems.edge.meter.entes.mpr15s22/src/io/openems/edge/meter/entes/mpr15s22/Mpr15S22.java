package io.openems.edge.meter.entes.mpr15s22;

import org.osgi.service.event.EventHandler;

import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.component.OpenemsComponent;

public interface Mpr15S22 extends OpenemsComponent {

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
