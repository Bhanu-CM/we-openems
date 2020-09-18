package io.openems.edge.ess.test;

import java.util.function.Consumer;

import io.openems.edge.common.channel.Channel;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.ess.api.SymmetricEss;
import io.openems.edge.ess.power.api.Power;

/**
 * Provides a simple, simulated ManagedSymmetricEss component that can be used
 * together with the OpenEMS Component test framework.
 */
public class DummyManagedSymmetricEss extends AbstractOpenemsComponent
		implements ManagedSymmetricEss, SymmetricEss, OpenemsComponent {

	public static final int MAX_APPARENT_POWER = Integer.MAX_VALUE;

	private final Power power;

	private int powerPrecision = 1;
	private Consumer<SymmetricApplyPowerRecord> symmetricApplyPowerCallback = null;

	protected DummyManagedSymmetricEss(String id, Power power,
			io.openems.edge.common.channel.ChannelId[] firstInitialChannelIds,
			io.openems.edge.common.channel.ChannelId[]... furtherInitialChannelIds) {
		super(firstInitialChannelIds, furtherInitialChannelIds);
		this.power = power;
		for (Channel<?> channel : this.channels()) {
			channel.nextProcessImage();
		}
		super.activate(null, id, "", true);
	}

	public DummyManagedSymmetricEss(String id, Power power) {
		this(id, power, //
				OpenemsComponent.ChannelId.values(), //
				ManagedSymmetricEss.ChannelId.values(), //
				SymmetricEss.ChannelId.values() //
		);
	}

	public DummyManagedSymmetricEss(String id) {
		this(id, new DummyPower(MAX_APPARENT_POWER));
	}

	@Override
	public Power getPower() {
		return this.power;
	}

	@Override
	public int getPowerPrecision() {
		return this.powerPrecision;
	}

	public DummyManagedSymmetricEss withSoc(int value) {
		this._setSoc(value);
		this.getSocChannel().nextProcessImage();
		return this;
	}

	public DummyManagedSymmetricEss withMaxApparentPower(int value) {
		this._setMaxApparentPower(value);
		this.getMaxApparentPowerChannel().nextProcessImage();
		return this;
	}

	public DummyManagedSymmetricEss withAllowedChargePower(int value) {
		this._setAllowedChargePower(value);
		this.getAllowedChargePowerChannel().nextProcessImage();
		return this;
	}

	public DummyManagedSymmetricEss withAllowedDischargePower(int value) {
		this._setAllowedDischargePower(value);
		this.getAllowedDischargePowerChannel().nextProcessImage();
		return this;
	}

	public DummyManagedSymmetricEss withPowerPrecision(int value) {
		this.powerPrecision = value;
		return this;
	}

	@Override
	public void applyPower(int activePower, int reactivePower) {
		if (this.symmetricApplyPowerCallback != null) {
			this.symmetricApplyPowerCallback.accept(new SymmetricApplyPowerRecord(activePower, reactivePower));
		}
	}

	public void withSymmetricApplyPowerCallback(Consumer<SymmetricApplyPowerRecord> callback) {
		this.symmetricApplyPowerCallback = callback;
	}

	public static class SymmetricApplyPowerRecord {
		public final int activePower;
		public final int reactivePower;

		public SymmetricApplyPowerRecord(int activePower, int reactivePower) {
			this.activePower = activePower;
			this.reactivePower = reactivePower;
		}
	}
}
