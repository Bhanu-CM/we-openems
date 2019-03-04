package io.openems.edge.common.channel;

import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.doc.ChannelId;
import io.openems.edge.common.component.OpenemsComponent;

public class EnumReadChannel extends AbstractReadChannel<Integer> {

	protected EnumReadChannel(OpenemsComponent component, ChannelId channelId) {
		super(OpenemsType.ENUM, component, channelId);
	}

	public EnumReadChannel(OpenemsComponent component, ChannelId channelId, Enum<?> initialValueEnum) {
		super(OpenemsType.ENUM, component, channelId, initialValueEnum.ordinal());
	}

}
