package io.openems.edge.common.channel;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.channel.ChannelId;

public class IntegerWriteChannel extends IntegerReadChannel implements WriteChannel<Integer> {

	public static class MirrorToDebugChannel implements Consumer<Channel<Integer>> {

		private final ChannelId targetChannelId;

		public MirrorToDebugChannel(ChannelId targetChannelId) {
			this.targetChannelId = targetChannelId;
		}

		@Override
		public void accept(Channel<Integer> channel) {
			// on each setNextWrite to the channel -> store the value in the DEBUG-channel
			((IntegerWriteChannel) channel).onSetNextWrite(value -> {
				channel.getComponent().channel(this.targetChannelId).setNextValue(value);
			});
		}
	}

	protected IntegerWriteChannel(OpenemsComponent component, ChannelId channelId, IntegerDoc channelDoc) {
		super(component, channelId, channelDoc);
	}

	private Optional<Integer> nextWriteValueOpt = Optional.empty();

	/**
	 * Internal method. Do not call directly.
	 * 
	 * @param value
	 */
	@Deprecated
	@Override
	public void _setNextWriteValue(Integer value) {
		this.nextWriteValueOpt = Optional.ofNullable(value);
	}

	@Override
	public Optional<Integer> getNextWriteValue() {
		return this.nextWriteValueOpt;
	}

	/*
	 * onSetNextWrite
	 */
	@Override
	public List<Consumer<Integer>> getOnSetNextWrites() {
		return super.getOnSetNextWrites();
	}

	@Override
	public void onSetNextWrite(Consumer<Integer> callback) {
		this.getOnSetNextWrites().add(callback);
	}

}
