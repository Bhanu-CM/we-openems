package io.openems.edge.evcs.dezony;

import java.util.function.Function;

import io.openems.common.channel.Level;
import io.openems.common.channel.Unit;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.BooleanReadChannel;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.type.TypeUtils;

public interface Dezony {
	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		RAW_CHARGE_STATUS_CHARGEPOINT(Doc.of(OpenemsType.STRING), "state"),;

		private final Doc doc;
		private final String[] jsonPaths;

		protected final Function<Object, Object> converter;

		private ChannelId(Doc doc, String... jsonPaths) {
			this(doc, value -> value, jsonPaths);
		}

		private ChannelId(Doc doc, Function<Object, Object> converter, String... jsonPaths) {
			this.doc = doc;
			this.converter = converter;
			this.jsonPaths = jsonPaths;
		}

		@Override
		public Doc doc() {
			return this.doc;
		}

		/**
		 * Get the whole JSON path.
		 *
		 * @return Whole path.
		 */
		public String[] getJsonPaths() {
			return this.jsonPaths;
		}
	}
}