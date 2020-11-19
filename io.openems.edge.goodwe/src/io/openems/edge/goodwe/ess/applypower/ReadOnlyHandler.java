package io.openems.edge.goodwe.ess.applypower;

import io.openems.edge.common.statemachine.StateHandler;
import io.openems.edge.goodwe.ess.applypower.ApplyPowerStateMachine.State;
import io.openems.edge.goodwe.ess.enums.PowerModeEms;

public class ReadOnlyHandler extends StateHandler<State, Context> {

	@Override
	public State runAndGetNextState(Context context) {
		context.setMode(PowerModeEms.AUTO, 0);

		return State.READ_ONLY;
	}

}
