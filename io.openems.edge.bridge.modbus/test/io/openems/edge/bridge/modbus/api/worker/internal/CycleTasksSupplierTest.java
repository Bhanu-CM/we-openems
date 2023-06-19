package io.openems.edge.bridge.modbus.api.worker.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Test;

import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.bridge.modbus.DummyModbusComponent;
import io.openems.edge.bridge.modbus.api.worker.DummyReadTask;
import io.openems.edge.bridge.modbus.api.worker.DummyWriteTask;
import io.openems.edge.bridge.modbus.test.DummyModbusBridge;
import io.openems.edge.common.taskmanager.Priority;
import io.openems.edge.common.test.TimeLeapClock;

public class CycleTasksSupplierTest {

	private static final String CMP = "foo";

	private static DummyReadTask RT_H_1;
	private static DummyReadTask RT_H_2;
	private static DummyReadTask RT_L_1;
	private static DummyReadTask RT_L_2;
	private static DummyWriteTask WT_1;

	@Before
	public void before() {
		RT_H_1 = new DummyReadTask("RT_H_1", 49, Priority.HIGH);
		RT_H_2 = new DummyReadTask("RT_H_2", 70, Priority.HIGH);
		RT_L_1 = new DummyReadTask("RT_L_1", 20, Priority.LOW);
		RT_L_2 = new DummyReadTask("RT_L_2", 30, Priority.LOW);
		WT_1 = new DummyWriteTask("WT_1", 90);
	}

	@Test
	public void test() throws OpenemsException {
		var clock = new TimeLeapClock();
		var defectiveComponents = new DefectiveComponents(clock);
		var sut = new CycleTasksSupplier();

		var bridge = new DummyModbusBridge("modbus0");
		var foo = new DummyModbusComponent(CMP, bridge);
		var protocol = foo.getModbusProtocol();
		protocol.addTasks(RT_H_1, RT_H_2, RT_L_1, RT_L_2, WT_1);
		sut.addProtocol(CMP, protocol);

		// 1st Cycle
		var tasks = sut.apply(defectiveComponents);
		assertEquals(4, tasks.reads().size() + tasks.writes().size());
		assertTrue(tasks.reads().contains(RT_H_1));
		assertTrue(tasks.reads().contains(RT_H_2));
		assertTrue(tasks.reads().contains(RT_L_1));
		assertFalse(tasks.reads().contains(RT_L_2)); // -> not
		assertTrue(tasks.writes().contains(WT_1));

		// 2nd Cycle
		tasks = sut.apply(defectiveComponents);
		assertEquals(4, tasks.reads().size() + tasks.writes().size());
		assertTrue(tasks.reads().contains(RT_H_1));
		assertTrue(tasks.reads().contains(RT_H_2));
		assertFalse(tasks.reads().contains(RT_L_1)); // -> not
		assertTrue(tasks.reads().contains(RT_L_2));
		assertTrue(tasks.writes().contains(WT_1));

		// Add to defective
		defectiveComponents.add(CMP);

		// 3rd Cycle -> not yet due
		tasks = sut.apply(defectiveComponents);
		assertEquals(0, tasks.reads().size() + tasks.writes().size());

		// 4th Cycle -> due: total one task
		clock.leap(30_001, ChronoUnit.MILLIS);
		tasks = sut.apply(defectiveComponents);
		assertEquals(1, tasks.reads().size() + tasks.writes().size());

		// Remove from defective
		defectiveComponents.remove(CMP);

		// 5th Cycle -> back to normal
		tasks = sut.apply(defectiveComponents);
		assertEquals(4, tasks.reads().size() + tasks.writes().size());
	}

}
