package io.openems.edge.solaredge.hybrid.ess;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(//
		name = "SolarEdge Hybrid Inverter", //
		description = "SolarEdge Hybrid Inverter System - ESS")
@interface Config {

	@AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
	String id() default "ess0";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;

	//@AttributeDefinition(name = "Core-ID", description = "Component-ID of \"SolarEdge Hybrid Inverter System \" component ?")
	//String core_id() default "ess0";
	
	@AttributeDefinition(name = "Modbus-ID", description = "ID of Modbus bridge.")
	String modbus_id() default "modbus0";
	
	@AttributeDefinition(name = "Modbus Unit-ID", description = "The Unit-ID of the Modbus device. ")
	int modbusUnitId() default 14;	

	@AttributeDefinition(name = "Core target filter", description = "This is auto-generated by 'Core-ID'.")
	String core_target() default "(enabled=true)";

	String webconsole_configurationFactory_nameHint() default "SolarEdge Hybrid Inverter System [{id}]";
}