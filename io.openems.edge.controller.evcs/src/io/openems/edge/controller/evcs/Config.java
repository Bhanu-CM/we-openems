package io.openems.edge.controller.evcs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(//
		name = "Controller Electric Vehicle Charging Station", //
		description = "Limits the maximum charging power of an electric vehicle charging station.")
@interface Config {

	@AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
	String id() default "ctrlEvcs0";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;

	@AttributeDefinition(name = "Debug Mode", description = "Activates the debug mode")
	boolean debugMode() default false;

	@AttributeDefinition(name = "Evcs-ID", description = "ID of Evcs device (Has to be managed).", required = true)
	String evcs_id() default "evcs0";

	@AttributeDefinition(name = "Enabled charging", description = "Activates or deactivates the Charging.")
	boolean enabledCharging() default true;

	@AttributeDefinition(name = "Charge-Mode", description = "Set the charge-mode.")
	ChargeMode chargeMode() default ChargeMode.FORCE_CHARGE;

	@AttributeDefinition(name = "Force-charge minimum power [W] per Phase", description = "Set the minimum power for the force charge mode in Watt per Phase.")
	int forceChargeMinPower() default 7360;

	@AttributeDefinition(name = "Default-charge minimum power [W]", description = "Set the minimum power for the default charge mode in Watt.")
	int defaultChargeMinPower() default 0;

	@AttributeDefinition(name = "Priority of charging", description = "Decide which Component should be preferred.")
	Priority priority() default Priority.CAR;

	@AttributeDefinition(name = "Ess-ID", description = "ID of Ess device.")
	String ess_id() default "ess0";

	@AttributeDefinition(name = "Energy limit in this session in [Wh]", description = "Set the Energylimit in this Session in Wh. The charging station will only charge till this limit; '0' is no limit.")
	int energySessionLimit() default 0;

	String webconsole_configurationFactory_nameHint() default "Controller Electric Vehicle Charging Station [{id}]";

}