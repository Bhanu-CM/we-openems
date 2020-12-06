package io.openems.edge.fenecon.dess.charger;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(//
		name = "FENECON DESS Charger 2", //
		description = "The MPP tracker 2 implementation of a FENECON DESS (PRO Hybrid, PRO Compact,...)")
@interface Config2 {

	@AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
	String id() default "charger1";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;

	@AttributeDefinition(name = "FENECON DESS ESS", description = "ID of FENECON DESS ESS device.")
	String ess_id() default "ess0";

	@AttributeDefinition(name = "FENECON DESS ESS target filter", description = "This is auto-generated by 'FENECON DESS ESS-ID'.")
	String Ess_target() default "";

	@AttributeDefinition(name = "Modbus target filter", description = "This is auto-generated by 'Modbus-ID' from FENECON DESS ESS.")
	String Modbus_target() default "";

	String webconsole_configurationFactory_nameHint() default "FENECON DESS Charger 2 [{id}]";
}