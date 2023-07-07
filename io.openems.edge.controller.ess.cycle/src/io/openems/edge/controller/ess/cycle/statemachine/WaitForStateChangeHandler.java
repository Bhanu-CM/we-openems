package io.openems.edge.controller.ess.cycle.statemachine;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.edge.common.statemachine.StateHandler;
import io.openems.edge.controller.ess.cycle.statemachine.StateMachine.State;

public class WaitForStateChangeHandler extends StateHandler<State, Context> {

	private final Logger log = LoggerFactory.getLogger(WaitForStateChangeHandler.class);

	@Override
	public State runAndGetNextState(Context context) {
		var controller = context.getParent();
		var config = context.config;

		var currentState = controller.getCurrentState();
		var nextState = controller.getNextState();
		var now = LocalDateTime.now(context.clock);
		var standbyTimeInMinutes = Duration.ofMinutes(config.standbyTime());
		var lastStateChangeTime = controller.getLastStateChangeTime();
		context.logInfo(this.log, " CURRENT STATE : " + currentState + " | " //
				+ " NEXT STATE : " + nextState + " | " //
				+ " NOW : " + now + " | " //
				+ " LAST STATE CHANGE TIME : " + lastStateChangeTime + " | " //
		);
		if (lastStateChangeTime == null) {
			return State.WAIT_FOR_STATE_CHANGE;
		}
		var nowMinusStandby = (now.getSecond() - standbyTimeInMinutes.getSeconds());
		var remainingTime = lastStateChangeTime.minus(nowMinusStandby, ChronoUnit.SECONDS);
		if (now.minus(standbyTimeInMinutes.toSeconds(), ChronoUnit.SECONDS).isAfter(lastStateChangeTime)) {
			return nextState;
		}
		context.logInfo(this.log, "Awaiting hysteresis for changing from ["//
				+ currentState + "] to [" + nextState + "]" //
				+ " remainingTime : " + remainingTime //
		);
		return State.WAIT_FOR_STATE_CHANGE;
	}
}