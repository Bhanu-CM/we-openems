package io.openems.edge.common.channel;

import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.internal.AbstractDoc;
import io.openems.edge.common.component.OpenemsComponent;

public class StateChannelDoc extends AbstractDoc<Boolean> {

	private final Level level;

	public StateChannelDoc(Level level) {
		super(OpenemsType.BOOLEAN);
		this.level = level;
	}

	@Override
	protected StateChannelDoc self() {
		return this;
	}

	/**
	 * Creates an instance of {@link Channel} for the given Channel-ID using its
	 * Channel-{@link Doc}.
	 * 
	 * @param channelId the Channel-ID
	 * @return the Channel
	 */
	@SuppressWarnings("unchecked")
	@Override
	public StateChannel createChannelInstance(OpenemsComponent component,
			io.openems.edge.common.channel.ChannelId channelId) {
		return new StateChannel(component, channelId, this, this.level);
	}
}
