package io.openems.edge.battery.soltaro.controller.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.battery.api.Battery;
import io.openems.edge.battery.soltaro.controller.IState;
import io.openems.edge.battery.soltaro.controller.State;
import io.openems.edge.ess.api.ManagedSymmetricEss;

public class FullCharge  extends BaseState implements IState {

	private final Logger log = LoggerFactory.getLogger(FullCharge.class);
	
	private int criticalHighCellVoltage;
	private int chargePowerPercent;
	

	public FullCharge(ManagedSymmetricEss ess, Battery bms, int criticalHighCellVoltage, int chargePowerPercent) {
		super(ess, bms);
		this.criticalHighCellVoltage = criticalHighCellVoltage;
		this.chargePowerPercent = chargePowerPercent;
	}

	@Override
	public State getState() {
		return State.FULL_CHARGE;
	}

	@Override
	public State getNextState() {		
		// According to the state machine the next state is UNDEFINED, FULL_CHARGE or NORMAL
		if (isNextStateUndefined()) {
			return State.UNDEFINED;
		}
		
		if (getEssMaxCellVoltage() >= criticalHighCellVoltage) {
			return State.NORMAL;
		}
		
		return State.FULL_CHARGE;
	}

	@Override
	public void act() throws OpenemsNamedException {
		log.info("Set charge power to max");
		chargeEssWithPercentOfMaxPower(chargePowerPercent);
	}

	
	

}
