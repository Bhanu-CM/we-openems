package io.openems.edge.controller.ess.hybrid.surplusfeedtogrid;

import org.junit.Test;

import io.openems.common.types.ChannelAddress;
import io.openems.edge.common.test.AbstractComponentTest.TestCase;
import io.openems.edge.common.test.DummyConfigurationAdmin;
import io.openems.edge.controller.test.ControllerTest;
import io.openems.edge.ess.test.DummyHybridEss;

public class SurplusFeedToGridControllerImplTest {

	private final static String CTRL_ID = "ctrl0";

	private final static ChannelAddress CTRL_SURPLUS_FEED_TO_GRID_IS_LIMITED = new ChannelAddress(CTRL_ID,
			"SurplusFeedToGridIsLimited");

	private final static String ESS_ID = "ess0";

	private final static ChannelAddress ESS_SET_ACTIVE_POWER_GREATER_OR_EQUALS = new ChannelAddress(ESS_ID,
			"SetActivePowerGreaterOrEquals");

	@Test
	public void test() throws Exception {
		final DummyHybridEss ess = new DummyHybridEss(ESS_ID);
		final ControllerTest test = new ControllerTest(new SurplusFeedToGridControllerImpl()) //
				.addReference("cm", new DummyConfigurationAdmin()) //
				.addReference("ess", ess) //
				.activate(MyConfig.create() //
						.setId(CTRL_ID) //
						.setEssId(ESS_ID) //
						.build());

		ess.setDummySurplusPower(null);
		test.next(new TestCase() //
				.output(ESS_SET_ACTIVE_POWER_GREATER_OR_EQUALS, null));

		ess.setDummySurplusPower(5000);
		ess.setDummyMaxApparentPower(10000);
		test.next(new TestCase() //
				.output(CTRL_SURPLUS_FEED_TO_GRID_IS_LIMITED, false) //
				.output(ESS_SET_ACTIVE_POWER_GREATER_OR_EQUALS, 5000));

		ess.setDummySurplusPower(5000);
		ess.setDummyMaxApparentPower(2000);
		test.next(new TestCase() //
				.output(CTRL_SURPLUS_FEED_TO_GRID_IS_LIMITED, true) //
				.output(ESS_SET_ACTIVE_POWER_GREATER_OR_EQUALS, 2000));
	}
}