package io.openems.edge.sma;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import io.openems.edge.ess.api.Phase;

@ObjectClassDefinition( //
		name = "SMA SunnyIsland 6.0H", //
		description = "Implements the SMA SunnyIsland 6.0H energy storage system.")
@interface Config {

	String id() default "ess0";

	boolean enabled() default true;

	Phase Phase() default Phase.L1;

	@AttributeDefinition(name = "Modbus-ID", description = "ID of Modbus brige.")
	String modbus_id() default "modbus0";

	int modbusUnitId() default 3;

	@AttributeDefinition(name = "Modbus target filter", description = "This is auto-generated by 'Modbus-ID'.")
	String Modbus_target() default "";

	String webconsole_configurationFactory_nameHint() default "SMA SunnyIsland 6.0H [{id}]";

}