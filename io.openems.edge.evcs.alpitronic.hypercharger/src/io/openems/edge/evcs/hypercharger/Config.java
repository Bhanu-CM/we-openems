package io.openems.edge.evcs.hypercharger;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(//
		name = "EVCS Alpitronic Hypercharger", //
		description = "Implements the Alpitronic Hypercharger - Salia electric vehicle charging station.")
@interface Config {

	@AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
	String id() default "evcs0";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;

	@AttributeDefinition(name = "Modbus-ID", description = "ID of Modbus bridge")
	String modbus_id() default "modbus0";

	@AttributeDefinition(name = "Modbus Unit-ID", description = "The Unit-ID of the Modbus device.")
	int modbusUnitId() default 1;

	@AttributeDefinition(name = "Connector", description = "Slot of physical charging connector")
	Hypercharger.Connector connector() default Hypercharger.Connector.SLOT_0;

	@AttributeDefinition(name = "Minimum hardware power", description = "Minimum charging power of the Charger in W.", required = true)
	int minHwPower() default 5000;

	@AttributeDefinition(name = "Maximum hardware power", description = "Maximum charging power of the Charger in W.", required = true)
	int maxHwPower() default 75000;

	@AttributeDefinition(name = "Debug Mode", description = "Activates the debug mode")
	boolean debugMode() default false;

	@AttributeDefinition(name = "Modbus target filter", description = "This is auto-generated by 'Modbus-ID'.")
	String Modbus_target() default "(enabled=true)";

	String webconsole_configurationFactory_nameHint() default "EVCS Alpitronic Hypercharger [{id}]";

}