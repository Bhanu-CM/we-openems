package io.openems.edge.common.channel;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.function.ThrowingConsumer;
import io.openems.edge.common.component.OpenemsComponent;

public class IntegerWriteChannel extends IntegerReadChannel implements WriteChannel<Integer> {

	public static class MirrorToDebugChannel implements Consumer<Channel<Integer>> {

		private final Logger log = LoggerFactory.getLogger(MirrorToDebugChannel.class);

		private final ChannelId targetChannelId;

		public MirrorToDebugChannel(ChannelId targetChannelId) {
			this.targetChannelId = targetChannelId;
		}

		@Override
		public void accept(Channel<Integer> channel) {
			if (!(channel instanceof IntegerWriteChannel)) {
				this.log.error("Channel [" + channel.address()
						+ "] is not an IntegerWriteChannel! Unable to register \"onSetNextWrite\"-Listener!");
				return;
			}

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
	public List<ThrowingConsumer<Integer, OpenemsNamedException>> getOnSetNextWrites() {
		return super.getOnSetNextWrites();
	}

	@Override
	public void onSetNextWrite(ThrowingConsumer<Integer, OpenemsNamedException> callback) {
		this.getOnSetNextWrites().add(callback);
	}

	/**
	 * An object that holds information about the write target of this Channel, i.e.
	 * a Modbus Register or REST-Api endpoint address. Defaults to null.
	 */
	private Object writeTarget = null;

	@Override
	public <WRITE_TARGET> void setWriteTarget(WRITE_TARGET writeTarget) throws IllegalArgumentException {
		if (this.writeTarget != null && writeTarget != null && !Objects.equals(this.writeTarget, writeTarget)) {
			throw new IllegalArgumentException("Unable to set write target [" + writeTarget.toString()
					+ "]. Channel already has a write target [" + this.writeTarget.toString() + "]");
		}
		this.writeTarget = writeTarget;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <WRITE_TARGET> WRITE_TARGET getWriteTarget() {
		return (WRITE_TARGET) this.writeTarget;
	}
}
