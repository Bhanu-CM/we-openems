package io.openems.edge.predictor.similardaymodel;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(//
		name = "Predictor Similarday-Model", //
		description = "Implements Similar day predictor model")
@interface Config {
	
	@AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
	String id() default "predictor0";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;
	
	@AttributeDefinition(name = "How many weaks?", description = "No of weaks the data is needed?")
	int numOfWeeks() default 4;
	
	@AttributeDefinition(name = "Channel-Addresses", description = "List of Channel-Addresses this Predictor is used for, e.g. '*/ActivePower', '*/ActualPower'")
	String[] channelAddresses() default { "_sum/ProductionActivePower", "_sum/ConsumptionActivePower" };
	
	String webconsole_configurationFactory_nameHint() default "Predictor Similarday-Model [{id}]";

}
