package io.openems.edge.controller.symmetric.selfconsumption;

import io.openems.edge.common.test.AbstractComponentConfig;

@SuppressWarnings("all")
public class MyConfig extends AbstractComponentConfig implements Config {

	protected static class Builder {
		private String id = null;
		private String essId = null;
		private String meterId = null;
		private Integer maximumSellToGridPower = null;

		private Builder() {

		}

		public Builder setId(String id) {
			this.id = id;
			return this;
		}

		public Builder setEssId(String essId) {
			this.essId = essId;
			return this;
		}

		public Builder setMeterId(String meterId) {
			this.meterId = meterId;
			return this;
		}

		public Builder setMaximumSellToGridPower(int maximumSellToGridPower) {
			this.maximumSellToGridPower = maximumSellToGridPower;
			return this;
		}

		public MyConfig build() {
			return new MyConfig(this);
		}
	}

	public static Builder create() {
		return new Builder();
	}

	private final Builder builder;

	private MyConfig(Builder builder) {
		super(Config.class, builder.id);
		this.builder = builder;
	}

	@Override
	public String ess_id() {
		return this.builder.essId;
	}

	@Override
	public String ess_target() {
		return "(&(enabled=true)(!(service.pid=" + this.id() + "))(|(id=" + this.ess_id() + ")))";
	}

	@Override
	public String meter_id() {
		return this.builder.meterId;
	}

	@Override
	public String meter_target() {
		return "(&(enabled=true)(!(service.pid=" + this.id() + "))(|(id=" + this.meter_id() + ")))";
	}

	@Override
	public int maximumSellToGridPower() {
		return this.builder.maximumSellToGridPower;
	}

}