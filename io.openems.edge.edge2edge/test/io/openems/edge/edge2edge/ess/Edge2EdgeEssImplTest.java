package io.openems.edge.edge2edge.ess;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.openems.edge.bridge.modbus.test.DummyModbusBridge;
import io.openems.edge.common.test.AbstractComponentTest.TestCase;
import io.openems.edge.common.test.ComponentTest;
import io.openems.edge.common.test.DummyConfigurationAdmin;

public class Edge2EdgeEssImplTest {

	private static final String COMPONENT_ID = "ess0";
	private static final String MODBUS_ID = "modbus0";

	@Test
	public void test() throws Exception {
		new ComponentTest(new Edge2EdgeEssImpl()) //
				.addReference("cm", new DummyConfigurationAdmin()) //
				.addReference("setModbus", new DummyModbusBridge(MODBUS_ID)) //
				.activate(MyConfig.create() //
						.setId(COMPONENT_ID) //
						.setModbusId(MODBUS_ID) //
						.setRemoteComponentId(COMPONENT_ID) //
						.build())
				.next(new TestCase());
	}

	@Test
	public void testIsHashEqual() {
		assertTrue(Edge2EdgeEssImpl.isHashEqual(0x6201, "OpenEMS"));
		assertTrue(Edge2EdgeEssImpl.isHashEqual(0xb3dc, "OpenemsComponent"));
		assertFalse(Edge2EdgeEssImpl.isHashEqual(null, "_sum"));
		assertFalse(Edge2EdgeEssImpl.isHashEqual(0x6201, "foobar"));
	}
}
