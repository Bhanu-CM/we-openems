package io.openems.edge.meter.virtual.common;

import java.util.List;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.modbusslave.ModbusSlave;
import io.openems.edge.meter.api.SymmetricMeter;
import io.openems.edge.meter.api.VirtualMeter;
import io.openems.edge.meter.virtual.symmetric.add.SymmetricChannelManager;

public abstract class AbstractVirtualAddMeter<METER extends SymmetricMeter> extends AbstractOpenemsComponent
		implements VirtualMeter, SymmetricMeter, OpenemsComponent, ModbusSlave {

	protected abstract SymmetricChannelManager getChannelManager();

	protected abstract List<METER> getMeters();

	protected AbstractVirtualAddMeter(io.openems.edge.common.channel.ChannelId[] firstInitialChannelIds,
			io.openems.edge.common.channel.ChannelId[]... furtherInitialChannelIds) {
		super(firstInitialChannelIds, furtherInitialChannelIds);
	}

	@Override
	protected void activate(ComponentContext context, String id, String alias, boolean enabled) {
		throw new IllegalArgumentException("Use the other activate() method!");
	}

	protected void activate(ComponentContext context, String id, String alias, boolean enabled, ConfigurationAdmin cm,
			String... meterIds) {
		super.activate(context, id, alias, enabled);

		if (OpenemsComponent.updateReferenceFilter(cm, this.servicePid(), "Meter", meterIds)) {
			return;
		}

		this.getChannelManager().activate(this.getMeters());
	}

	@Override
	protected void deactivate() {
		this.getChannelManager().deactivate();
		super.deactivate();
	}

	@Override
	public String debugLog() {
		return "L:" + this.getActivePower().asString();
	}

}
