package io.openems.edge.controller.chp.soc;

import static org.junit.Assert.*;

import org.junit.Test;
import io.openems.common.types.ChannelAddress;
import io.openems.edge.common.test.AbstractComponentConfig;
import io.openems.edge.common.test.DummyComponentManager;
import io.openems.edge.common.test.AbstractComponentTest.TestCase;
import io.openems.edge.controller.test.ControllerTest;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.ess.test.DummyManagedSymmetricEss;
import io.openems.edge.io.test.DummyInputOutput;

public class ChpSocTest {
	
	@SuppressWarnings("all")
	private static class MyConfig extends AbstractComponentConfig implements Config {
		
		private final String inputChannelAddress;
		private final String outputChannelAddress;
		private final int lowThreshold;
		private final int highThreshold;
		
		public MyConfig(String id, String inputChannelAddress, String outputChannelAddress, int lowThreshold, int highThreshold) {
			super(Config.class, id);
			this.inputChannelAddress = inputChannelAddress;
			this.outputChannelAddress = outputChannelAddress;
			this.lowThreshold = lowThreshold;
			this.highThreshold = highThreshold;			
		}
		
		@Override
		public String inputChannelAddress() {
			return this.inputChannelAddress;
		}
		
		@Override
		public String outputChannelAddress() {
			return this.outputChannelAddress;
		}
		
		@Override
		public int lowThreshold() {
			return this.lowThreshold;
		}
		
		@Override
		public int highThreshold() {
			return this.highThreshold;
		}
	}

	/*
	 * @Test public void test() { fail("Not yet implemented"); }
	 */
	
	@Test
	public void test() throws Exception {
		//initialize the controller
		ControllerChpSoc controller = new ControllerChpSoc();
		// Add referenced services
		DummyComponentManager componentManager = new DummyComponentManager();
		controller.componentManager = componentManager;
		
		ChannelAddress ess0 = new ChannelAddress("ess0", "Soc");
		ChannelAddress output0 = new ChannelAddress("io0", "InputOutput0");
		
		MyConfig myconfig = new MyConfig("ctrl1", ess0.toString(), output0.toString(), 15, 85);
		controller.activate(null, myconfig);
		controller.activate(null, myconfig);
		
		ManagedSymmetricEss ess = new DummyManagedSymmetricEss("ess0");
		DummyInputOutput io = new DummyInputOutput("io0");
		// Build and run test			
		new ControllerTest(controller, componentManager, ess, io) 
		.next(new TestCase()
				.input(ess0, 14)
				.output(output0, true))
		.next(new TestCase()
				.input(ess0, 50)) 
		.next(new TestCase() 
				.input(ess0, 90) 
				.output(output0, false)) 
		.run();		 
		new ControllerTest(controller, componentManager, ess, io)
		.next(new TestCase()
				.input(ess0, 50))
		.next(new TestCase()
				.input(ess0, 15))
		.next(new TestCase()
				.input(ess0, 85))
		.run();
		new ControllerTest(controller, componentManager, ess, io)
		.next(new TestCase()
				.input(ess0, 86)
				.output(output0, false))
		.next(new TestCase()
				.input(ess0, 14)
				.output(output0, true))
		.next(new TestCase()
				.input(ess0, 45))
		.run();
	}

}