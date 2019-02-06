package io.openems.edge.bridge.modbus;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.ghgande.j2mod.modbus.Modbus;

@ObjectClassDefinition(//
		name = "Bridge Modbus/TCP", //
		description = "Provides a service for connecting to, querying and writing to a Modbus/TCP device.")
@interface ConfigTcp {
	String id() default "modbus0";

	boolean enabled() default true;

	@AttributeDefinition(name = "IP-Address", description = "The IP address of the Modbus/TCP device.")
	String ip();

	@AttributeDefinition(name = "Port", description = "The Port of the Modbus/TCP device (defaults to 502)")
	int port() default Modbus.DEFAULT_PORT;

	String webconsole_configurationFactory_nameHint() default "Bridge Modbus/TCP [{id}]";
}