package io.openems.edge.meter.algo2.uem1p5_4ds_e;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.metatype.annotations.Designate;

import io.openems.common.channel.PersistencePriority;
import io.openems.common.channel.Unit;
import io.openems.common.types.OpenemsType;
import io.openems.edge.bridge.modbus.api.element.SignedDoublewordElement;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.common.modbusslave.ModbusSlave;
import io.openems.edge.meter.api.AsymmetricMeter;
import io.openems.edge.meter.api.SymmetricMeter;

public interface MeterAlgo2UEM1P5_4DS_E extends SymmetricMeter, AsymmetricMeter, OpenemsComponent, ModbusSlave   {
	
	public static final int MODBUSREG_SET0_SYSIMPORTEDACTIVEENERGY_l1 = 0x0100; 
	public static final int MODBUSREG_SET0_SYSIMPORTEDACTIVEENERGY_L2 = 0x0103; 
	public static final int MODBUSREG_SET0_SYSIMPORTEDACTIVEENERGY_L3 = 0x0106; 
	public static final int MODBUSREG_SET0_SYSIMPORTEDACTIVEENERGY = 0x0109; 
	//
	public static final int MODBUSREG_SET0_SYSEXPORTEDACTIVEENERGY_L1 = 0x010C; 
	public static final int MODBUSREG_SET0_SYSEXPORTEDACTIVEENERGY_L2 = 0x010F; 
	public static final int MODBUSREG_SET0_SYSEXPORTEDACTIVEENERGY_L3 = 0x0112; 
	public static final int MODBUSREG_SET0_SYSEXPORTEDACTIVEENERGY = 0x0115; 

	
	
	
	public static final int MODBUSREG_SET1_SYSIMPORTEDACTIVEENERGY = 0x010C;
	public static final int MODBUSREG_SET1_SYSEXPORTEDACTIVEENERGY = 0x011C; 
	
	public static final int MODBUSREG_SET0_REGSETINUSE = 0x0523; 
	public static final int MODBUSREG_SET1_REGSETINUSE = 0x0538; 
	
	
	
	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
				
		
		
		CUSTACTIVECONSUMPTION_ENERGY_L1(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		CUSTACTIVECONSUMPTION_ENERGY_L2(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		CUSTACTIVECONSUMPTION_ENERGY_L3(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		CUSTACTIVECONSUMPTION_ENERGY(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		CUSTACTIVEPRODUCTION_ENERGY_L1(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		CUSTACTIVEPRODUCTION_ENERGY_L2(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		CUSTACTIVEPRODUCTION_ENERGY_L3(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		CUSTACTIVEPRODUCTION_ENERGY(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		
		
		/**
		 * Active Power L1
		 *
		 * <ul>
		 * <li>Interface: Meter Asymmetric
		 * <li>Type: Integer
		 * <li>Unit: W
		 * <li>Range: negative values for Consumption (power that is 'leaving the
		 * system', e.g. feed-to-grid); positive for Production (power that is 'entering
		 * the system')
		 * </ul>
		 */
		CACTIVE_POWER_L1(Doc.of(OpenemsType.LONG) //
				.unit(Unit.WATT) //
				.persistencePriority(PersistencePriority.HIGH) //
				.text(POWER_DOC_TEXT)), //
		/**
		 * Active Power L2
		 *
		 * <ul>
		 * <li>Interface: Meter Asymmetric
		 * <li>Type: Integer
		 * <li>Unit: W
		 * <li>Range: negative values for Consumption (power that is 'leaving the
		 * system', e.g. feed-to-grid); positive for Production (power that is 'entering
		 * the system')
		 * </ul>
		 */
		CACTIVE_POWER_L2(Doc.of(OpenemsType.LONG) //
				.unit(Unit.WATT) //
				.persistencePriority(PersistencePriority.HIGH) //
				.text(POWER_DOC_TEXT)), //
		/**
		 * Active Power L3
		 *
		 * <ul>
		 * <li>Interface: Meter Asymmetric
		 * <li>Type: Integer
		 * <li>Unit: W
		 * <li>Range: negative values for Consumption (power that is 'leaving the
		 * system', e.g. feed-to-grid); positive for Production (power that is 'entering
		 * the system')
		 * </ul>
		 */
		CACTIVE_POWER_L3(Doc.of(OpenemsType.LONG) //
				.unit(Unit.WATT) //
				.persistencePriority(PersistencePriority.HIGH) //
				.text(POWER_DOC_TEXT)), //
		
		
		
		/**
		 * Voltage L12
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mV
		 * </ul>
		 */
		VOLTAGE_L12(Doc.of(OpenemsType.LONG) //
				.unit(Unit.MILLIVOLT) //
				.persistencePriority(PersistencePriority.HIGH)), //
		/**
		 * Voltage L23
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mV
		 * </ul>
		 */
		VOLTAGE_L23(Doc.of(OpenemsType.LONG) //
				.unit(Unit.MILLIVOLT) //
				.persistencePriority(PersistencePriority.HIGH)), //
		/**
		 * Voltage L31
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mV
		 * </ul>
		 */
		VOLTAGE_L31(Doc.of(OpenemsType.LONG) //
				.unit(Unit.MILLIVOLT) //
				.persistencePriority(PersistencePriority.HIGH)), //
		/**
		 * System Voltage
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mV
		 * </ul>
		 */
		VOLTAGE_SYS(Doc.of(OpenemsType.LONG) //
				.unit(Unit.MILLIVOLT) //
				.persistencePriority(PersistencePriority.HIGH)), //
		
		

		/**
		 * Ph1 Current.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mA
		 * </ul>
		 */
		CURRENT_A1(Doc.of(OpenemsType.LONG) //
				.unit(Unit.MILLIAMPERE) //
				.persistencePriority(PersistencePriority.HIGH)),
		/**
		 * Ph2 Current.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mA
		 * </ul>
		 */
		CURRENT_A2(Doc.of(OpenemsType.LONG) //
				.unit(Unit.MILLIAMPERE) //
				.persistencePriority(PersistencePriority.HIGH)),
		/**
		 * Ph3 Current.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mA
		 * </ul>
		 */
		CURRENT_A3(Doc.of(OpenemsType.LONG) //
				.unit(Unit.MILLIAMPERE) //
				.persistencePriority(PersistencePriority.HIGH)),
		/**
		 * Neutral Current.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mA
		 * </ul>
		 */
		CURRENT_N(Doc.of(OpenemsType.LONG) //
				.unit(Unit.MILLIAMPERE) //
				.persistencePriority(PersistencePriority.HIGH)),
		/**
		 * System Current.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mA
		 * </ul>
		 */
		CURRENT_SYS(Doc.of(OpenemsType.LONG) //
				.unit(Unit.MILLIAMPERE) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		
		/**
		 * Power factor L1.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mA
		 * </ul>
		 */
		POWER_FACTOR_L1(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		/**
		 * Power factor L2.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mA
		 * </ul>
		 */
		POWER_FACTOR_L2(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		POWER_FACTOR_L1And2(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		
		/**
		 * Power factor L3.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mA
		 * </ul>
		 */
		POWER_FACTOR_L3(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		/**
		 * Power factor Sys.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mA
		 * </ul>
		 */
		POWER_FACTOR_SYS(Doc.of(OpenemsType.LONG) //
				.unit(Unit.WATT_HOURS) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		
		POWER_FACTOR_L3AndSys(Doc.of(OpenemsType.LONG) //
				.unit(Unit.WATT_HOURS) //
				.persistencePriority(PersistencePriority.HIGH)),
		

		
		
		
		/**
		 * Power factor L1.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mA
		 * </ul>
		 */
		APPARENT_POWER_L1(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		/**
		 * Power factor L2.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mA
		 * </ul>
		 */
		APPARENT_POWER_L2(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		/**
		 * Power factor L3.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mA
		 * </ul>
		 */
		APPARENT_POWER_L3(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		/**
		 * Power factor Sys.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mA
		 * </ul>
		 */
		APPARENT_POWER_SYS(Doc.of(OpenemsType.LONG) //
				.unit(Unit.WATT_HOURS) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		/**
		 * Power factor L1.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: mA
		 * </ul>
		 */
		CUSTREACTIVE_POWER_L1(Doc.of(OpenemsType.LONG) //
				.unit(Unit.WATT_HOURS) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		CUSTREACTIVE_POWER_L2(Doc.of(OpenemsType.LONG) //
				.unit(Unit.WATT_HOURS) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		CUSTREACTIVE_POWER_L3(Doc.of(OpenemsType.LONG) //
				.unit(Unit.WATT_HOURS) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		CUSTREACTIVE_POWER(Doc.of(OpenemsType.LONG) //
				.unit(Unit.WATT_HOURS) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		

		
		
		/**
		 * Phase Sequence.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: -
		 * </ul>
		 */
		PHASE_SEQUENCE(Doc.of(OpenemsType.LONG) //
				.unit(Unit.WATT_HOURS) //
				.persistencePriority(PersistencePriority.HIGH)),
		

		/**
		 * Meter Type data and infos.
		 * 
		 * <ul>
		 * <li>Interface: Meter Algo2 UEM1P5_4DS_E
		 * <li>Type: Integer
		 * <li>Unit: -
		 * </ul>
		 */
		METAS_COUNTER_SN(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		METAS_COUNTER_MODEL(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		METAS_COUNTER_TYPE(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		METAS_COUNTER_FMW(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		METAS_COUNTER_HW(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		METAS_COUNTER_REGSET_IN_USE(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH)),
		
		METAS_COUNTER_FWREL2(Doc.of(OpenemsType.LONG) //
				.unit(Unit.NONE) //
				.persistencePriority(PersistencePriority.HIGH))
		
		
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
