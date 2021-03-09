package io.openems.edge.goodwe.charger;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import io.openems.edge.goodwe.GoodWeConstants;

@ObjectClassDefinition(//
		name = "GoodWe Charger PV2", //
		description = "Implements the Goodwe-ET Charger 2.")

@interface ConfigPV2 {
	@AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
	String id() default "charger1";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;

	@AttributeDefinition(name = "GoodWe ESS", description = "ID of GoodWe ET Energy Storage System.")
	String ess_id() default "ess0";

	@AttributeDefinition(name = "GoodWe ESS target filter", description = "This is auto-generated by 'GoodWe ESS'.")
	String ess_target() default "";

	@AttributeDefinition(name = "Modbus-ID", description = "ID of Modbus bridge.")
	String modbus_id() default "modbus0";

	@AttributeDefinition(name = "Modbus Unit-ID", description = "The Unit-ID of the Modbus device.")
	int modbusUnitId() default GoodWeConstants.DEFAULT_UNIT_ID;

	@AttributeDefinition(name = "Modbus target filter", description = "This is auto-generated by 'Modbus-ID'.")
	String Modbus_target() default "";

	String webconsole_configurationFactory_nameHint() default "GoodWe Charger PV2 [{id}]";
}