package io.openems.edge.sma.enums;

import io.openems.edge.common.channel.doc.OptionsEnum;

public enum ManualEqualizationCharge implements OptionsEnum {
	UNDEFINED(-1, "Undefined"), //
	STOP(381, "Stop"), //
	WAITING(1466, "Waiting"), //
	START(1467, "Start");

	private final int value;
	private final String name;

	private ManualEqualizationCharge(int value, String name) {
		this.value = value;
		this.name = name;
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public OptionsEnum getUndefined() {
		return UNDEFINED;
	}
}