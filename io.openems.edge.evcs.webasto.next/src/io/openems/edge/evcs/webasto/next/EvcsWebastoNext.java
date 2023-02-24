package io.openems.edge.evcs.webasto.next;

import io.openems.common.channel.AccessMode;
import io.openems.common.channel.Unit;
import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.channel.IntegerWriteChannel;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.evcs.webasto.next.enums.CableState;
import io.openems.edge.evcs.webasto.next.enums.ChargePointState;
import io.openems.edge.evcs.webasto.next.enums.EvseErrorCode;
import io.openems.edge.evcs.webasto.next.enums.EvseState;
import io.openems.edge.evcs.webasto.next.enums.StartCancelChargingSession;

public interface EvcsWebastoNext extends OpenemsComponent {

	/**
	 * 
	 * 
	 * @return
	 */
	public default IntegerWriteChannel getLifeBitChannel() {
		return this.channel(ChannelId.LIFE_BIT);
	}

	/**
	 * 
	 * @param value
	 * @throws OpenemsNamedException
	 */
	public default void setLifeBit(Integer value) throws OpenemsNamedException {
		this.getLifeBitChannel().setNextWriteValue(value);
	}

	/**
	 * 
	 * @return
	 */
	public default IntegerWriteChannel getEvSetChargePowerLimitChannel() {
		return this.channel(ChannelId.EV_SET_CHARGE_POWER_LIMIT);
	}

	/**
	 * 
	 * @return
	 */
	public default Value<Integer> getEvSetChargePowerLimit() {
		return this.getEvSetChargePowerLimitChannel().value();
	}

	/**
	 * 
	 * @param value
	 * @throws OpenemsNamedException
	 */
	public default void setEvSetChargePowerLimit(Integer value) throws OpenemsNamedException {
		this.getEvSetChargePowerLimitChannel().setNextWriteValue(value);
	}

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {

		EV_SET_CHARGE_POWER_LIMIT(Doc.of(OpenemsType.INTEGER)//
				.accessMode(AccessMode.READ_WRITE)//
				.unit(Unit.WATT)), //

		CHARGE_POINT_STATE(Doc.of(ChargePointState.values())), //

		EVSE_STATE(Doc.of(EvseState.values())), //

		CABLE_STATE(Doc.of(CableState.values())), //

		EVSE_ERROR_CODE(Doc.of(EvseErrorCode.values())), //

		CURRENT_L1(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIAMPERE) //
				.accessMode(AccessMode.READ_ONLY)), //

		CURRENT_L2(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIAMPERE) //
				.accessMode(AccessMode.READ_ONLY)), //

		CURRENT_L3(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIAMPERE)), //

		POWER_L1(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIAMPERE)), //

		POWER_L2(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIAMPERE)), //

		POWER_L3(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIAMPERE)), //

		MAX_HW_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE)), //

		MIN_HW_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE)), //

		MAX_EVSE_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE)), //

		MAX_CABLE_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE)), //

		MAX_EV_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE)), //
		
		LAST_ENERGY_SESSION(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.WATT_HOURS)), //

		START_TIME(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.NONE)), //

		CHARGE_SESSION_TIME(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.SECONDS)), //

		END_TIME(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.NONE)), //

		SMART_VEHICLE_DETECTED(Doc.of(OpenemsType.BOOLEAN) //
				.unit(Unit.NONE)), //
		SAFE_CURRENT(Doc.of(OpenemsType.INTEGER)//
				.unit(Unit.AMPERE)//
				.accessMode(AccessMode.READ_WRITE)), //
		COM_TIMEOUT(Doc.of(OpenemsType.INTEGER)//
				.unit(Unit.SECONDS)//
				.accessMode(AccessMode.READ_WRITE)), //
		CHARGE_CURRENT(Doc.of(OpenemsType.INTEGER)//
				.unit(Unit.AMPERE)//
				.accessMode(AccessMode.WRITE_ONLY)), //
		LIFE_BIT(Doc.of(OpenemsType.INTEGER)//
				.unit(Unit.NONE)//
				.accessMode(AccessMode.READ_WRITE)), //
		START_CANCEL_CHARGING_SESSION(Doc.of(StartCancelChargingSession.values())//
				.accessMode(AccessMode.WRITE_ONLY)), //

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
