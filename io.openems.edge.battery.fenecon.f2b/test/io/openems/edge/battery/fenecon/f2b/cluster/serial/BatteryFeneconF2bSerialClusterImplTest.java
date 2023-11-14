package io.openems.edge.battery.fenecon.f2b.cluster.serial;

import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.Test;

import io.openems.common.test.TimeLeapClock;
import io.openems.common.types.ChannelAddress;
import io.openems.edge.battery.fenecon.f2b.cluster.common.statemachine.StateMachine;
import io.openems.edge.battery.fenecon.f2b.dummy.BatteryFeneconF2bDummyImpl;
import io.openems.edge.battery.fenecon.f2b.dummy.statemachine.StateMachine.State;
import io.openems.edge.common.startstop.StartStopConfig;
import io.openems.edge.common.test.AbstractComponentTest.TestCase;
import io.openems.edge.common.test.ComponentTest;
import io.openems.edge.common.test.DummyComponentManager;
import io.openems.edge.common.test.DummyConfigurationAdmin;

public class BatteryFeneconF2bSerialClusterImplTest {

	private static final String CLUSTER_ID = "battery0";
	private static final String BATTERY1_ID = "battery1";
	private static final String BATTERY2_ID = "battery2";

	private static final ChannelAddress BATTERY1_SOC = new ChannelAddress(BATTERY1_ID, "Soc");
	private static final ChannelAddress BATTERY2_SOC = new ChannelAddress(BATTERY2_ID, "Soc");

	private static final ChannelAddress BATTERY1_DISCHARGE_MIN_VOLTAGE = new ChannelAddress(BATTERY1_ID,
			"DischargeMinVoltage");
	private static final ChannelAddress BATTERY2_DISCHARGE_MIN_VOLTAGE = new ChannelAddress(BATTERY2_ID,
			"DischargeMinVoltage");

	private static final ChannelAddress BATTERY1_VOLTAGE = new ChannelAddress(BATTERY1_ID, "Voltage");
	private static final ChannelAddress BATTERY2_VOLTAGE = new ChannelAddress(BATTERY2_ID, "Voltage");

	private static final ChannelAddress BATTERY1_CHARGE_MAX_VOLTAGE = new ChannelAddress(BATTERY1_ID,
			"ChargeMaxVoltage");
	private static final ChannelAddress BATTERY2_CHARGE_MAX_VOLTAGE = new ChannelAddress(BATTERY2_ID,
			"ChargeMaxVoltage");

	private static final ChannelAddress CLUSTER_STATE = new ChannelAddress(CLUSTER_ID, "StateMachine");
	private static final ChannelAddress BATTERY1_STATE = new ChannelAddress(BATTERY1_ID, "StateMachine");
	private static final ChannelAddress BATTERY2_STATE = new ChannelAddress(BATTERY2_ID, "StateMachine");

	@Test
	public void startTest() throws Exception {
		final var clock = new TimeLeapClock(Instant.parse("2000-01-01T01:00:00.00Z"), ZoneOffset.UTC);
		var battery1 = new BatteryFeneconF2bDummyImpl();
		var dummyBattery1 = new ComponentTest(battery1) //
				.addReference("componentManager", new DummyComponentManager(clock)) //
				.activate(io.openems.edge.battery.fenecon.f2b.dummy.MyConfig.create() //
						.setId(BATTERY1_ID) //
						.setStartStop(StartStopConfig.AUTO) //
						.build());
		var battery2 = new BatteryFeneconF2bDummyImpl();
		var dummyBattery2 = new ComponentTest(battery2) //
				.addReference("componentManager", new DummyComponentManager(clock)) //
				.activate(io.openems.edge.battery.fenecon.f2b.dummy.MyConfig.create() //
						.setId(BATTERY2_ID) //
						.setStartStop(StartStopConfig.AUTO) //
						.build());
		var batteryCluster = new BatteryFeneconF2bClusterSerialImpl();
		new ComponentTest(batteryCluster) //
				.addReference("cm", new DummyConfigurationAdmin()) //
				.addReference("componentManager", new DummyComponentManager(clock)) //
				.addReference("addBattery", battery1) //
				.addReference("addBattery", battery2) //
				.activate(MyConfig.create() //
						.setId(CLUSTER_ID) //
						.setStartStop(StartStopConfig.START) //
						.setBatteryIds(BATTERY1_ID, BATTERY2_ID) //
						.build())
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.input(BATTERY1_STATE, State.UNDEFINED) //
						.input(BATTERY2_STATE, State.UNDEFINED) //
						.input(CLUSTER_STATE, StateMachine.State.UNDEFINED))//
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase())))//
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.STOPPED) //
						.output(BATTERY2_STATE, State.STOPPED) //
						.output(CLUSTER_STATE, StateMachine.State.UNDEFINED))//
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.STOPPED) //
						.output(BATTERY2_STATE, State.STOPPED) //
						.output(CLUSTER_STATE, StateMachine.State.UNDEFINED))//
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.input(BATTERY1_VOLTAGE, 400)//
						.output(BATTERY1_STATE, State.STOPPED) //
						.output(BATTERY2_STATE, State.STOPPED) //
						.output(CLUSTER_STATE, StateMachine.State.UNDEFINED))//
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.STOPPED) //
						.output(BATTERY2_STATE, State.STOPPED) //
						.output(CLUSTER_STATE, StateMachine.State.STOPPED))//
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.STOPPED) //
						.output(BATTERY2_STATE, State.STOPPED) //
						.output(CLUSTER_STATE, StateMachine.State.GO_RUNNING))//
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.GO_RUNNING) //
						.output(BATTERY2_STATE, State.GO_RUNNING) //
						.output(CLUSTER_STATE, StateMachine.State.GO_RUNNING))
				.next(new TestCase()//
						.input(BATTERY1_VOLTAGE, 344) //
						.input(BATTERY1_SOC, 50) //
						.input(BATTERY1_CHARGE_MAX_VOLTAGE, 403) //
						.input(BATTERY1_DISCHARGE_MIN_VOLTAGE, 259)) //
				.next(new TestCase("Time leap") //
						.onAfterProcessImage(() -> battery1.setHvContactorUnlocked(true))//
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.GO_RUNNING) //
						.output(BATTERY2_STATE, State.GO_RUNNING) //
						.output(CLUSTER_STATE, StateMachine.State.GO_RUNNING))
				.next(new TestCase()//
						.input(BATTERY2_VOLTAGE, 344) //
						.input(BATTERY2_SOC, 50) //
						.input(BATTERY2_CHARGE_MAX_VOLTAGE, 403) //
						.input(BATTERY2_DISCHARGE_MIN_VOLTAGE, 259)) //
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> battery2.setHvContactorUnlocked(true))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.RUNNING) //
						.output(BATTERY2_STATE, State.GO_RUNNING) //
						.output(CLUSTER_STATE, StateMachine.State.GO_RUNNING))//
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.RUNNING) //
						.output(BATTERY2_STATE, State.RUNNING) //
						.output(CLUSTER_STATE, StateMachine.State.GO_RUNNING))//
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.RUNNING) //
						.output(BATTERY2_STATE, State.RUNNING) //
						.output(CLUSTER_STATE, StateMachine.State.GO_RUNNING))//
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.RUNNING) //
						.output(BATTERY2_STATE, State.RUNNING) //
						.output(CLUSTER_STATE, StateMachine.State.GO_RUNNING))//
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.RUNNING) //
						.output(BATTERY2_STATE, State.RUNNING) //
						.output(CLUSTER_STATE, StateMachine.State.RUNNING))//
		;
	}

	@Test
	public void stopTest() throws Exception {
		final var clock = new TimeLeapClock(Instant.parse("2000-01-01T01:00:00.00Z"), ZoneOffset.UTC);
		var battery1 = new BatteryFeneconF2bDummyImpl();
		var dummyBattery1 = new ComponentTest(battery1) //
				.addReference("componentManager", new DummyComponentManager(clock)) //
				.activate(io.openems.edge.battery.fenecon.f2b.dummy.MyConfig.create() //
						.setId(BATTERY1_ID) //
						.setStartStop(StartStopConfig.AUTO) //
						.build());
		var battery2 = new BatteryFeneconF2bDummyImpl();
		var dummyBattery2 = new ComponentTest(battery2) //
				.addReference("componentManager", new DummyComponentManager(clock)) //
				.activate(io.openems.edge.battery.fenecon.f2b.dummy.MyConfig.create() //
						.setId(BATTERY2_ID) //
						.setStartStop(StartStopConfig.AUTO) //
						.build());
		var batteryCluster = new BatteryFeneconF2bClusterSerialImpl();
		new ComponentTest(batteryCluster) //
				.addReference("cm", new DummyConfigurationAdmin()) //
				.addReference("componentManager", new DummyComponentManager(clock)) //
				.addReference("addBattery", battery1) //
				.addReference("addBattery", battery2) //
				.activate(MyConfig.create() //
						.setId(CLUSTER_ID) //
						.setStartStop(StartStopConfig.STOP) //
						.setBatteryIds(BATTERY1_ID, BATTERY2_ID) //
						.build())
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.input(BATTERY1_STATE, State.UNDEFINED) //
						.input(BATTERY2_STATE, State.UNDEFINED) //
						.input(CLUSTER_STATE, StateMachine.State.UNDEFINED))//
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.GO_STOPPED)//
						.output(BATTERY2_STATE, State.GO_STOPPED) //
						.output(CLUSTER_STATE, StateMachine.State.UNDEFINED))//
				.next(new TestCase()//
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.STOPPED) //
						.output(BATTERY2_STATE, State.STOPPED) //
						.output(CLUSTER_STATE, StateMachine.State.UNDEFINED))
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.STOPPED) //
						.output(BATTERY2_STATE, State.STOPPED) //
						.output(CLUSTER_STATE, StateMachine.State.UNDEFINED))//
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.STOPPED) //
						.output(BATTERY2_STATE, State.STOPPED) //
						.output(CLUSTER_STATE, StateMachine.State.UNDEFINED))//
				.next(new TestCase() //
						.onAfterProcessImage(() -> dummyBattery1.next(new TestCase()))//
						.onAfterProcessImage(() -> dummyBattery2.next(new TestCase()))//
						.output(BATTERY1_STATE, State.STOPPED) //
						.output(BATTERY2_STATE, State.STOPPED) //
						.output(CLUSTER_STATE, StateMachine.State.STOPPED))//
		;
	}
}
