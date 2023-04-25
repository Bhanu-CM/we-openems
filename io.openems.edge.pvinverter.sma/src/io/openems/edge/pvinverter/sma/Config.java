package io.openems.edge.pvinverter.sma;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import io.openems.edge.pvinverter.sunspec.Phase;

@ObjectClassDefinition(name = "PV-Inverter SMA Sunny Tripower", //
		description = "Implements the SMA Sunny Tripower PV inverter.")
@interface Config {

	@AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
	String id() default "pvInverter0";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;

	@AttributeDefinition(name = "Read-Only mode", description = "In Read-Only mode no power-limitation commands are sent to the inverter")
	boolean readOnly() default true;

	@AttributeDefinition(name = "Modbus-ID", description = "ID of Modbus bridge.")
	String modbus_id() default "modbus0";

	@AttributeDefinition(name = "Modbus Unit-ID", description = "The Unit-ID of the Modbus device. "
			+ "Be aware, that according to the manual you need to add '123' to the value that you configured "
			+ "in the SMA web interface.")
	int modbusUnitId() default 126;

	@AttributeDefinition(name = "Phase", description = "On which phase is the inverter connected?")
	Phase phase() default Phase.ALL;

	@AttributeDefinition(name = "Modbus target filter", description = "This is auto-generated by 'Modbus-ID'.")
	String Modbus_target() default "(enabled=true)";

	String webconsole_configurationFactory_nameHint() default "PV-Inverter SMA Sunny Tripower [{id}]";

}
