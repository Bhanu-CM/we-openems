package io.openems.edge.fenecon.mini.ess;

import io.openems.common.channel.AccessMode;
import io.openems.common.channel.Level;
import io.openems.common.channel.Unit;
import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.channel.IntegerReadChannel;
import io.openems.edge.common.channel.IntegerWriteChannel;
import io.openems.edge.common.channel.WriteChannel;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.modbusslave.ModbusSlave;
import io.openems.edge.ess.api.AsymmetricEss;
import io.openems.edge.ess.api.ManagedAsymmetricEss;
import io.openems.edge.ess.api.ManagedSinglePhaseEss;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.ess.api.SinglePhaseEss;
import io.openems.edge.ess.api.SymmetricEss;
import io.openems.edge.fenecon.mini.ess.statemachine.StateMachine.State;

public interface FeneconMiniEss extends ManagedSinglePhaseEss, ManagedAsymmetricEss, ManagedSymmetricEss,
		SinglePhaseEss, AsymmetricEss, SymmetricEss, OpenemsComponent, ModbusSlave {

	public static enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		STATE_MACHINE(Doc.of(State.values()) //
				.text("Current State of State-Machine")), //
		RUN_FAILED(Doc.of(Level.FAULT) //
				.text("Running the Logic failed")),
		GRID_MAX_CHARGE_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.accessMode(AccessMode.READ_WRITE) //
				.unit(Unit.MILLIAMPERE)), //
		GRID_MAX_DISCHARGE_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.accessMode(AccessMode.READ_WRITE) //
				.unit(Unit.MILLIAMPERE)), //

		// EnumReadChannels
		SYSTEM_STATE(Doc.of(SystemState.values())), //
		CONTROL_MODE(Doc.of(ControlMode.values())), //
		BATTERY_GROUP_STATE(Doc.of(BatteryGroupState.values())), //

		// EnumWriteChannels
		PCS_MODE(Doc.of(PcsMode.values()) //
				.accessMode(AccessMode.READ_WRITE)), //
		SETUP_MODE(Doc.of(SetupMode.values()) //
				.accessMode(AccessMode.READ_WRITE)), //
		SET_WORK_STATE(Doc.of(SetWorkState.values()) //
				.accessMode(AccessMode.WRITE_ONLY)),
		DEBUG_RUN_STATE(Doc.of(DebugRunState.values()) //
				.accessMode(AccessMode.READ_WRITE)),

		// IntegerWriteChannels
		RTC_YEAR(Doc.of(OpenemsType.INTEGER) //
				.accessMode(AccessMode.WRITE_ONLY) //
				.text("Year")), //
		RTC_MONTH(Doc.of(OpenemsType.INTEGER) //
				.accessMode(AccessMode.WRITE_ONLY) //
				.text("Month")), //
		RTC_DAY(Doc.of(OpenemsType.INTEGER) //
				.accessMode(AccessMode.WRITE_ONLY) //
				.text("Day")), //
		RTC_HOUR(Doc.of(OpenemsType.INTEGER) //
				.accessMode(AccessMode.WRITE_ONLY) //
				.text("Hour")), //
		RTC_MINUTE(Doc.of(OpenemsType.INTEGER) //
				.accessMode(AccessMode.WRITE_ONLY) //
				.text("Minute")), //
		RTC_SECOND(Doc.of(OpenemsType.INTEGER) //
				.accessMode(AccessMode.WRITE_ONLY) //
				.text("Second")), //

		// IntegerReadChannels
		BATTERY_VOLTAGE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIAMPERE)), //
		BATTERY_POWER(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.WATT)), //

		BECU1_ALLOWED_CHARGE_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIAMPERE)), //
		BECU1_ALLOWED_DISCHARGE_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIAMPERE)), //
		BECU1_TOTAL_VOLTAGE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BECU1_TOTAL_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE)), //
		BECU1_SOC(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.PERCENT)), //
		BECU1_VERSION(Doc.of(OpenemsType.INTEGER)), //
		BECU1_NOMINAL_CAPACITY(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE_HOURS)), //
		BECU1_CURRENT_CAPACITY(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE_HOURS)), //
		BECU1_MINIMUM_VOLTAGE_NO(Doc.of(OpenemsType.INTEGER)), //
		BECU1_MINIMUM_VOLTAGE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.VOLT)), //
		BECU1_MAXIMUM_VOLTAGE_NO(Doc.of(OpenemsType.INTEGER)), //
		BECU1_MAXIMUM_VOLTAGE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.VOLT)), //
		BECU1_MINIMUM_TEMPERATURE_NO(Doc.of(OpenemsType.INTEGER)), //
		BECU1_MINIMUM_TEMPERATURE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BECU1_MAXIMUM_TEMPERATURE_NO(Doc.of(OpenemsType.INTEGER)), //
		BECU1_MAXIMUM_TEMPERATURE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //

		BECU2_CHARGE_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE)), //
		BECU2_DISCHARGE_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE)), //
		BECU2_VOLT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.VOLT)), //
		BECU2_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE)), //
		BECU2_SOC(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.PERCENT)), //

		BECU2_VERSION(Doc.of(OpenemsType.INTEGER)), //
		BECU2_MIN_VOLT_NO(Doc.of(OpenemsType.INTEGER)), //
		BECU2_MIN_VOLT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.VOLT)), //
		BECU2_MAX_VOLT_NO(Doc.of(OpenemsType.INTEGER)), //
		BECU2_MAX_VOLT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.VOLT)), //
		BECU2_MIN_TEMP_NO(Doc.of(OpenemsType.INTEGER)), //
		BECU2_MIN_TEMP(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BECU2_MAX_TEMP_NO(Doc.of(OpenemsType.INTEGER)), //
		BECU2_MAX_TEMP(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //

		SYSTEM_WORK_MODE_STATE(Doc.of(OpenemsType.INTEGER)), //
		SYSTEM_WORK_STATE(Doc.of(OpenemsType.INTEGER)), //

		BECU_NUM(Doc.of(OpenemsType.INTEGER)), //
		BECU_WORK_STATE(Doc.of(OpenemsType.INTEGER)), //
		BECU_CHARGE_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE)), //
		BECU_DISCHARGE_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE)), //
		BECU_VOLT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.VOLT)), //
		BECU_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE)), //

		BATTERY_VOLTAGE_SECTION_1(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_VOLTAGE_SECTION_2(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_VOLTAGE_SECTION_3(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_VOLTAGE_SECTION_4(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_VOLTAGE_SECTION_5(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_VOLTAGE_SECTION_6(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_VOLTAGE_SECTION_7(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_VOLTAGE_SECTION_8(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_VOLTAGE_SECTION_9(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_VOLTAGE_SECTION_10(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_VOLTAGE_SECTION_11(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_VOLTAGE_SECTION_12(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_VOLTAGE_SECTION_13(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_VOLTAGE_SECTION_14(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_VOLTAGE_SECTION_15(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		BATTERY_VOLTAGE_SECTION_16(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT)), //
		// TODO add .delta(-40L)
		BATTERY_TEMPERATURE_SECTION_1(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_TEMPERATURE_SECTION_2(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_TEMPERATURE_SECTION_3(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_TEMPERATURE_SECTION_4(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_TEMPERATURE_SECTION_5(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_TEMPERATURE_SECTION_6(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_TEMPERATURE_SECTION_7(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_TEMPERATURE_SECTION_8(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_TEMPERATURE_SECTION_9(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_TEMPERATURE_SECTION_10(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_TEMPERATURE_SECTION_11(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_TEMPERATURE_SECTION_12(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_TEMPERATURE_SECTION_13(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_TEMPERATURE_SECTION_14(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_TEMPERATURE_SECTION_15(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_TEMPERATURE_SECTION_16(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS)), //

		// BooleanReadChannels
		STATE_143(Doc.of(OpenemsType.BOOLEAN) //
				.text("TheCommunicationWireToDredBreak")), //

		// StateChannels
		STATE_1(Doc.of(Level.INFO) //
				.text("BECU1GeneralChargeOverCurrentAlarm")), //
		STATE_2(Doc.of(Level.INFO) //
				.text("BECU1GeneralDischargeOverCurrentAlarm")), //
		STATE_3(Doc.of(Level.INFO) //
				.text("BECU1ChargeCurrentLimitAlarm")), //
		STATE_4(Doc.of(Level.INFO) //
				.text("BECU1DischargeCurrentLimitAlarm")), //
		STATE_5(Doc.of(Level.INFO) //
				.text("BECU1GeneralHighVoltageAlarm")), //
		STATE_6(Doc.of(Level.INFO) //
				.text("BECU1GeneralLowVoltageAlarm")), //
		STATE_7(Doc.of(Level.INFO) //
				.text("BECU1AbnormalVoltageChangeAlarm")), //
		STATE_8(Doc.of(Level.INFO) //
				.text("BECU1GeneralHighTemperatureAlarm")), //
		STATE_9(Doc.of(Level.INFO) //
				.text("BECU1GeneralLowTemperatureAlarm")), //
		STATE_10(Doc.of(Level.INFO) //
				.text("BECU1AbnormalTemperatureChangeAlarm")), //
		STATE_11(Doc.of(Level.INFO) //
				.text("BECU1SevereHighVoltageAlarm")), //
		STATE_12(Doc.of(Level.INFO) //
				.text("BECU1SevereLowVoltageAlarm")), //
		STATE_13(Doc.of(Level.INFO) //
				.text("BECU1SevereLowTemperatureAlarm")), //
		STATE_14(Doc.of(Level.INFO) //
				.text("BECU1SeverveChargeOverCurrentAlarm")), //
		STATE_15(Doc.of(Level.INFO) //
				.text("BECU1SeverveDischargeOverCurrentAlarm")), //
		STATE_16(Doc.of(Level.INFO) //
				.text("BECU1AbnormalCellCapacityAlarm")), //
		STATE_17(Doc.of(Level.INFO) //
				.text("BECU1BalancedSamplingAlarm")), //
		STATE_18(Doc.of(Level.INFO) //
				.text("BECU1BalancedControlAlarm")), //
		STATE_19(Doc.of(Level.INFO) //
				.text("BECU1HallSensorDoesNotWorkAccurately")), //
		STATE_20(Doc.of(Level.INFO) //
				.text("BECU1Generalleakage")), //
		STATE_21(Doc.of(Level.INFO) //
				.text("BECU1Severeleakage")), //
		STATE_22(Doc.of(Level.INFO) //
				.text("BECU1Contactor1TurnOnAbnormity")), //
		STATE_23(Doc.of(Level.INFO) //
				.text("BECU1Contactor1TurnOffAbnormity")), //
		STATE_24(Doc.of(Level.INFO) //
				.text("BECU1Contactor2TurnOnAbnormity")), //
		STATE_25(Doc.of(Level.INFO) //
				.text("BECU1Contactor2TurnOffAbnormity")), //
		STATE_26(Doc.of(Level.INFO) //
				.text("BECU1Contactor4CheckAbnormity")), //
		STATE_27(Doc.of(Level.INFO) //
				.text("BECU1ContactorCurrentUnsafe")), //
		STATE_28(Doc.of(Level.INFO) //
				.text("BECU1Contactor5CheckAbnormity")), //
		STATE_29(Doc.of(Level.INFO) //
				.text("BECU1HighVoltageOffset")), //
		STATE_30(Doc.of(Level.INFO) //
				.text("BECU1LowVoltageOffset")), //
		STATE_31(Doc.of(Level.INFO) //
				.text("BECU1HighTemperatureOffset")), //
		STATE_32(Doc.of(Level.INFO) //
				.text("BECU1DischargeSevereOvercurrent")), //
		STATE_33(Doc.of(Level.INFO) //
				.text("BECU1ChargeSevereOvercurrent")), //
		STATE_34(Doc.of(Level.INFO) //
				.text("BECU1GeneralUndervoltage")), //
		STATE_35(Doc.of(Level.INFO) //
				.text("BECU1SevereOvervoltage")), //
		STATE_36(Doc.of(Level.INFO) //
				.text("BECU1GeneralOvervoltage")), //
		STATE_37(Doc.of(Level.INFO) //
				.text("BECU1SevereUndervoltage")), //
		STATE_38(Doc.of(Level.INFO) //
				.text("BECU1InsideCANBroken")), //
		STATE_39(Doc.of(Level.INFO) //
				.text("BECU1GeneralUndervoltageHighCurrentDischarge")), //
		STATE_40(Doc.of(Level.INFO) //
				.text("BECU1BMUError")), //
		STATE_41(Doc.of(Level.INFO) //
				.text("BECU1CurrentSamplingInvalidation")), //
		STATE_42(Doc.of(Level.INFO) //
				.text("BECU1BatteryFail")), //
		STATE_43(Doc.of(Level.INFO) //
				.text("BECU1TemperatureSamplingBroken")), //
		STATE_44(Doc.of(Level.INFO) //
				.text("BECU1Contactor1TestBackIsAbnormalTurnOnAbnormity")), //
		STATE_45(Doc.of(Level.INFO) //
				.text("BECU1Contactor1TestBackIsAbnormalTurnOffAbnormity")), //
		STATE_46(Doc.of(Level.INFO) //
				.text("BECU1Contactor2TestBackIsAbnormalTurnOnAbnormity")), //
		STATE_47(Doc.of(Level.INFO) //
				.text("BECU1Contactor2TestBackIsAbnormalTurnOffAbnormity")), //
		STATE_48(Doc.of(Level.INFO) //
				.text("BECU1SevereHighTemperatureFault")), //
		STATE_49(Doc.of(Level.INFO) //
				.text("BECU1HallInvalidation")), //
		STATE_50(Doc.of(Level.INFO) //
				.text("BECU1ContactorInvalidation")), //
		STATE_51(Doc.of(Level.INFO) //
				.text("BECU1OutsideCANBroken")), //
		STATE_52(Doc.of(Level.INFO) //
				.text("BECU1CathodeContactorBroken")), //

		STATE_53(Doc.of(Level.INFO) //
				.text("BECU2GeneralChargeOverCurrentAlarm")), //
		STATE_54(Doc.of(Level.INFO) //
				.text("BECU2GeneralDischargeOverCurrentAlarm")), //
		STATE_55(Doc.of(Level.INFO) //
				.text("BECU2ChargeCurrentLimitAlarm")), //
		STATE_56(Doc.of(Level.INFO) //
				.text("BECU2DischargeCurrentLimitAlarm")), //
		STATE_57(Doc.of(Level.INFO) //
				.text("BECU2GeneralHighVoltageAlarm")), //
		STATE_58(Doc.of(Level.INFO) //
				.text("BECU2GeneralLowVoltageAlarm")), //
		STATE_59(Doc.of(Level.INFO) //
				.text("BECU2AbnormalVoltageChangeAlarm")), //
		STATE_60(Doc.of(Level.INFO) //
				.text("BECU2GeneralHighTemperatureAlarm")), //
		STATE_61(Doc.of(Level.INFO) //
				.text("BECU2GeneralLowTemperatureAlarm")), //
		STATE_62(Doc.of(Level.INFO) //
				.text("BECU2AbnormalTemperatureChangeAlarm")), //
		STATE_63(Doc.of(Level.INFO) //
				.text("BECU2SevereHighVoltageAlarm")), //
		STATE_64(Doc.of(Level.INFO) //
				.text("BECU2SevereLowVoltageAlarm")), //
		STATE_65(Doc.of(Level.INFO) //
				.text("BECU2SevereLowTemperatureAlarm")), //
		STATE_66(Doc.of(Level.INFO) //
				.text("BECU2SeverveChargeOverCurrentAlarm")), //
		STATE_67(Doc.of(Level.INFO) //
				.text("BECU2SeverveDischargeOverCurrentAlarm")), //
		STATE_68(Doc.of(Level.INFO) //
				.text("BECU2AbnormalCellCapacityAlarm")), //
		STATE_69(Doc.of(Level.INFO) //
				.text("BECU2BalancedSamplingAlarm")), //
		STATE_70(Doc.of(Level.INFO) //
				.text("BECU2BalancedControlAlarm")), //
		STATE_71(Doc.of(Level.INFO) //
				.text("BECU2HallSensorDoesNotWorkAccurately")), //
		STATE_72(Doc.of(Level.INFO) //
				.text("BECU2Generalleakage")), //
		STATE_73(Doc.of(Level.INFO) //
				.text("BECU2Severeleakage")), //
		STATE_74(Doc.of(Level.INFO) //
				.text("BECU2Contactor1TurnOnAbnormity")), //
		STATE_75(Doc.of(Level.INFO) //
				.text("BECU2Contactor1TurnOffAbnormity")), //
		STATE_76(Doc.of(Level.INFO) //
				.text("BECU2Contactor2TurnOnAbnormity")), //
		STATE_77(Doc.of(Level.INFO) //
				.text("BECU2Contactor2TurnOffAbnormity")), //
		STATE_78(Doc.of(Level.INFO) //
				.text("BECU2Contactor4CheckAbnormity")), //
		STATE_79(Doc.of(Level.INFO) //
				.text("BECU2ContactorCurrentUnsafe")), //
		STATE_80(Doc.of(Level.INFO) //
				.text("BECU2Contactor5CheckAbnormity")), //
		STATE_81(Doc.of(Level.INFO) //
				.text("BECU2HighVoltageOffset")), //
		STATE_82(Doc.of(Level.INFO) //
				.text("BECU2LowVoltageOffset")), //
		STATE_83(Doc.of(Level.INFO) //
				.text("BECU2HighTemperatureOffset")), //
		STATE_84(Doc.of(Level.INFO) //
				.text("BECU2DischargeSevereOvercurrent")), //
		STATE_85(Doc.of(Level.INFO) //
				.text("BECU2ChargeSevereOvercurrent")), //
		STATE_86(Doc.of(Level.INFO) //
				.text("BECU2GeneralUndervoltage")), //
		STATE_87(Doc.of(Level.INFO) //
				.text("BECU2SevereOvervoltage")), //
		STATE_88(Doc.of(Level.INFO) //
				.text("BECU2GeneralOvervoltage")), //
		STATE_89(Doc.of(Level.INFO) //
				.text("BECU2SevereUndervoltage")), //
		STATE_90(Doc.of(Level.INFO) //
				.text("BECU2InsideCANBroken")), //
		STATE_91(Doc.of(Level.INFO) //
				.text("BECU2GeneralUndervoltageHighCurrentDischarge")), //
		STATE_92(Doc.of(Level.INFO) //
				.text("BECU2BMUError")), //
		STATE_93(Doc.of(Level.INFO) //
				.text("BECU2CurrentSamplingInvalidation")), //
		STATE_94(Doc.of(Level.INFO) //
				.text("BECU2BatteryFail")), //
		STATE_95(Doc.of(Level.INFO) //
				.text("BECU2TemperatureSamplingBroken")), //
		STATE_96(Doc.of(Level.INFO) //
				.text("BECU2Contactor1TestBackIsAbnormalTurnOnAbnormity")), //
		STATE_97(Doc.of(Level.INFO) //
				.text("BECU2Contactor1TestBackIsAbnormalTurnOffAbnormity")), //
		STATE_98(Doc.of(Level.INFO) //
				.text("BECU2Contactor2TestBackIsAbnormalTurnOnAbnormity")), //
		STATE_99(Doc.of(Level.INFO) //
				.text("BECU2Contactor2TestBackIsAbnormalTurnOffAbnormity")), //
		STATE_100(Doc.of(Level.INFO) //
				.text("BECU2SevereHighTemperatureFault")), //
		STATE_101(Doc.of(Level.INFO) //
				.text("BECU2HallInvalidation")), //
		STATE_102(Doc.of(Level.INFO) //
				.text("BECU2ContactorInvalidation")), //
		STATE_103(Doc.of(Level.INFO) //
				.text("BECU2OutsideCANBroken")), //
		STATE_104(Doc.of(Level.INFO) //
				.text("BECU2CathodeContactorBroken")), //

		STATE_105(Doc.of(Level.INFO) //
				.text("NoAvailableBatteryGroup")), //
		STATE_106(Doc.of(Level.INFO) //
				.text("StackGeneralLeakage")), //
		STATE_107(Doc.of(Level.INFO) //
				.text("StackSevereLeakage")), //
		STATE_108(Doc.of(Level.INFO) //
				.text("StackStartingFail")), //
		STATE_109(Doc.of(Level.INFO) //
				.text("StackStoppingFail")), //
		STATE_110(Doc.of(Level.INFO) //
				.text("BatteryProtection")), //
		STATE_111(Doc.of(Level.INFO) //
				.text("StackAndGroup1CANCommunicationInterrupt")), //
		STATE_112(Doc.of(Level.INFO) //
				.text("StackAndGroup2CANCommunicationInterrupt")), //
		STATE_113(Doc.of(Level.INFO) //
				.text("GeneralOvercurrentAlarmAtCellStackCharge")), //
		STATE_114(Doc.of(Level.INFO) //
				.text("GeneralOvercurrentAlarmAtCellStackDischarge")), //
		STATE_115(Doc.of(Level.INFO) //
				.text("CurrentLimitAlarmAtCellStackCharge")), //
		STATE_116(Doc.of(Level.INFO) //
				.text("CurrentLimitAlarmAtCellStackDischarge")), //
		STATE_117(Doc.of(Level.INFO) //
				.text("GeneralCellStackHighVoltageAlarm")), //
		STATE_118(Doc.of(Level.INFO) //
				.text("GeneralCellStackLowVoltageAlarm")), //
		STATE_119(Doc.of(Level.INFO) //
				.text("AbnormalCellStackVoltageChangeAlarm")), //
		STATE_120(Doc.of(Level.INFO) //
				.text("GeneralCellStackHighTemperatureAlarm")), //
		STATE_121(Doc.of(Level.INFO) //
				.text("GeneralCellStackLowTemperatureAlarm")), //
		STATE_122(Doc.of(Level.INFO) //
				.text("AbnormalCellStackTemperatureChangeAlarm")), //
		STATE_123(Doc.of(Level.INFO) //
				.text("SevereCellStackHighVoltageAlarm")), //
		STATE_124(Doc.of(Level.INFO) //
				.text("SevereCellStackLowVoltageAlarm")), //
		STATE_125(Doc.of(Level.INFO) //
				.text("SevereCellStackLowTemperatureAlarm")), //
		STATE_126(Doc.of(Level.INFO) //
				.text("SeverveOverCurrentAlarmAtCellStackDharge")), //
		STATE_127(Doc.of(Level.INFO) //
				.text("SeverveOverCurrentAlarmAtCellStackDischarge")), //
		STATE_128(Doc.of(Level.INFO) //
				.text("AbnormalCellStackCapacityAlarm")), //
		STATE_129(Doc.of(Level.INFO) //
				.text("TheParameterOfEEPROMInCellStackLoseEffectiveness")), //
		STATE_130(Doc.of(Level.INFO) //
				.text("IsolatingSwitchInConfluenceArkBreak")), //
		STATE_131(Doc.of(Level.INFO) //
				.text("TheCommunicationBetweenCellStackAndTemperatureOfCollectorBreak")), //
		STATE_132(Doc.of(Level.INFO) //
				.text("TheTemperatureOfCollectorFail")), //
		STATE_133(Doc.of(Level.INFO) //
				.text("HallSensorDoNotWorkAccurately")), //
		STATE_134(Doc.of(Level.INFO) //
				.text("TheCommunicationOfPCSBreak")), //
		STATE_135(Doc.of(Level.INFO) //
				.text("AdvancedChargingOrMainContactorCloseAbnormally")), //
		STATE_136(Doc.of(Level.INFO) //
				.text("AbnormalSampledVoltage")), //
		STATE_137(Doc.of(Level.INFO) //
				.text("AbnormalAdvancedContactorOrAbnormalRS485GalleryOfPCS")), //
		STATE_138(Doc.of(Level.INFO) //
				.text("AbnormalMainContactor")), //
		STATE_139(Doc.of(Level.INFO) //
				.text("GeneralCellStackLeakage")), //
		STATE_140(Doc.of(Level.INFO) //
				.text("SevereCellStackLeakage")), //
		STATE_141(Doc.of(Level.INFO) //
				.text("SmokeAlarm")), //
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

	/**
	 * Gets the Channel for {@link ChannelId#SETUP_MODE}.
	 *
	 * @return the Channel
	 */
	public default WriteChannel<SetupMode> getSetupModeChannel() {
		return this.channel(ChannelId.SETUP_MODE);
	}

	/**
	 * Gets the Setup Mode. See {@link ChannelId#SETUP_MODE}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default SetupMode getSetupMode() {
		return this.getSetupModeChannel().value().asEnum();
	}

	/**
	 * Set the Setup Mode. See {@link ChannelId#SETUP_MODE}.
	 *
	 * @param value the next value
	 * @throws OpenemsNamedException on error
	 */
	public default void setSetupMode(SetupMode value) throws OpenemsNamedException {
		this.getSetupModeChannel().setNextWriteValue(value);
	}

	/**
	 * Gets the Channel for {@link ChannelId#PCS_MODE}.
	 *
	 * @return the Channel
	 */
	public default WriteChannel<PcsMode> getPcsModeChannel() {
		return this.channel(ChannelId.PCS_MODE);
	}

	/**
	 * Gets the PCS Mode. See {@link ChannelId#PCS_MODE}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default PcsMode getPcsMode() {
		return this.getPcsModeChannel().value().asEnum();
	}

	/**
	 * Set the PCS Mode. See {@link ChannelId#PCS_MODE}.
	 *
	 * @param value the next value
	 * @throws OpenemsNamedException on error
	 */
	public default void setPcsMode(PcsMode value) throws OpenemsNamedException {
		this.getPcsModeChannel().setNextWriteValue(value);
	}

	/**
	 * Gets the Channel for {@link ChannelId#DEBUG_RUN_STATE}.
	 *
	 * @return the Channel
	 */
	public default WriteChannel<DebugRunState> getDebugRunStateChannel() {
		return this.channel(ChannelId.DEBUG_RUN_STATE);
	}

	/**
	 * Gets the Debug Run-State. See {@link ChannelId#DEBUG_RUN_STATE}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default DebugRunState getDebugRunState() {
		return this.getDebugRunStateChannel().value().asEnum();
	}

	/**
	 * Set the Debug Run-State. See {@link ChannelId#DEBUG_RUN_STATE}.
	 *
	 * @param value the next value
	 * @throws OpenemsNamedException on error
	 */
	public default void setDebugRunState(DebugRunState value) throws OpenemsNamedException {
		this.getDebugRunStateChannel().setNextWriteValue(value);
	}

	/**
	 * Gets the Channel for {@link ChannelId#GRID_MAX_CHARGE_CURRENT}.
	 *
	 * @return the Channel
	 */
	public default IntegerWriteChannel getGridMaxChargeCurrentChannel() {
		return this.channel(ChannelId.GRID_MAX_CHARGE_CURRENT);
	}

	/**
	 * Gets the Grid Max-Charge-Current in [mA]. See
	 * {@link ChannelId#GRID_MAX_CHARGE_CURRENT}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default Value<Integer> getGridMaxChargeCurrent() {
		return this.getGridMaxChargeCurrentChannel().value();
	}

	/**
	 * Set the Grid Max-Charge-Current in [mA]. See
	 * {@link ChannelId#GRID_MAX_CHARGE_CURRENT}.
	 *
	 * @param value the next value
	 * @throws OpenemsNamedException on error
	 */
	public default void setGridMaxChargeCurrent(Integer value) throws OpenemsNamedException {
		this.getGridMaxChargeCurrentChannel().setNextWriteValue(value);
	}

	/**
	 * Gets the Channel for {@link ChannelId#GRID_MAX_DISCHARGE_CURRENT}.
	 *
	 * @return the Channel
	 */
	public default IntegerWriteChannel getGridMaxDischargeCurrentChannel() {
		return this.channel(ChannelId.GRID_MAX_DISCHARGE_CURRENT);
	}

	/**
	 * Gets the Grid Max-Discharge-Current in [mA]. See
	 * {@link ChannelId#GRID_MAX_DISCHARGE_CURRENT}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default Value<Integer> getGridMaxDischargeCurrent() {
		return this.getGridMaxDischargeCurrentChannel().value();
	}

	/**
	 * Set the Grid Max-Charge-Current in [mA]. See
	 * {@link ChannelId#GRID_MAX_CHARGE_CURRENT}.
	 *
	 * @param value the next value
	 * @throws OpenemsNamedException on error
	 */
	public default void setGridMaxDischargeCurrent(Integer value) throws OpenemsNamedException {
		this.getGridMaxDischargeCurrentChannel().setNextWriteValue(value);
	}

	/**
	 * Gets the Channel for {@link ChannelId#BECU1_TOTAL_VOLTAGE}.
	 *
	 * @return the Channel
	 */
	public default IntegerReadChannel getBecu1TotalVoltageChannel() {
		return this.channel(ChannelId.BECU1_TOTAL_VOLTAGE);
	}

	/**
	 * Gets the Becu1 Total Voltage [mV]. See {@link ChannelId#BECU1_TOTAL_VOLTAGE}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default Value<Integer> getBecu1TotalVoltage() {
		return this.getBecu1TotalVoltageChannel().value();
	}

	/**
	 * Gets the Channel for {@link ChannelId#BECU1_ALLOWED_CHARGE_CURRENT}.
	 *
	 * @return the Channel
	 */
	public default IntegerReadChannel getBecu1AllowedChargeCurrentChannel() {
		return this.channel(ChannelId.BECU1_ALLOWED_CHARGE_CURRENT);
	}

	/**
	 * Gets the Channel for {@link ChannelId#BECU1_ALLOWED_DISCHARGE_CURRENT}.
	 *
	 * @return the Channel
	 */
	public default IntegerReadChannel getBecu1AllowedDischargeCurrentChannel() {
		return this.channel(ChannelId.BECU1_ALLOWED_DISCHARGE_CURRENT);
	}

	@Override
	public default void applyPower(int activePowerL1, int reactivePowerL1, int activePowerL2, int reactivePowerL2,
			int activePowerL3, int reactivePowerL3) throws OpenemsNamedException {
		ManagedSinglePhaseEss.super.applyPower(activePowerL1, reactivePowerL1, activePowerL2, reactivePowerL2,
				activePowerL3, reactivePowerL3);
	}

}