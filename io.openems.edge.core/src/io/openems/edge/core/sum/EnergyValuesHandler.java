package io.openems.edge.core.sum;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.openems.common.types.ChannelAddress;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.sum.Sum;
import io.openems.edge.common.type.TypeUtils;
import io.openems.edge.timedata.api.Timedata;

/**
 * This Handler class makes sure that Energy values are steadily rising.
 */
public class EnergyValuesHandler {

	/**
	 * Initial delay in [s] to give the OSGi framework some time to provide a
	 * {@link Timedata} service if one is configured.
	 */
	private final static int INITIAL_DELAY = 60;

	private final static Sum.ChannelId[] ENERGY_CHANNEL_IDS = { //
			Sum.ChannelId.CONSUMPTION_ACTIVE_ENERGY, //
			Sum.ChannelId.ESS_ACTIVE_CHARGE_ENERGY, Sum.ChannelId.ESS_ACTIVE_DISCHARGE_ENERGY, //
			Sum.ChannelId.GRID_BUY_ACTIVE_ENERGY, Sum.ChannelId.GRID_SELL_ACTIVE_ENERGY, //
			Sum.ChannelId.PRODUCTION_ACTIVE_ENERGY, //
			Sum.ChannelId.PRODUCTION_AC_ACTIVE_ENERGY, Sum.ChannelId.PRODUCTION_DC_ACTIVE_ENERGY };

	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> scheduledFuture = null;

	private final SumImpl parent;

	/**
	 * Keeps the last energy values. This map is initially filled using the Timedata
	 * service if it is available.
	 * 
	 * <ul>
	 * <li>no entry indicates that the value was not yet read
	 * <li>null value indicates that there does no historic value exist
	 * <li>value holds the last set value
	 * </ul>
	 */
	private final Map<Sum.ChannelId, Long> lastEnergyValues = new HashMap<>();

	public EnergyValuesHandler(SumImpl parent) {
		this.parent = parent;
	}

	public void activate() {
		this.scheduledFuture = this.executor.schedule(() -> {
			Timedata timedata = this.parent.timedata;
			if (timedata == null) {
				// no Timedata service available: fill lastEnergyValues map with nulls.
				for (Sum.ChannelId channelId : ENERGY_CHANNEL_IDS) {
					this.lastEnergyValues.put(channelId, null);
				}
			} else {
				// Fill lastEnergyValues map with values from Timedata service
				for (Sum.ChannelId channelId : ENERGY_CHANNEL_IDS) {
					ChannelAddress channelAddress = new ChannelAddress("_sum", channelId.id());
					Long value = null;
					try {
						Optional<Object> latestValueOpt = this.parent.timedata.getLatestValue(channelAddress).get();
						if (latestValueOpt.isPresent()) {
							value = TypeUtils.getAsType(OpenemsType.LONG, latestValueOpt.get());
						}
					} catch (Exception e) {
						// ignore
					}
					this.lastEnergyValues.put(channelId, value);
				}
			}
		}, INITIAL_DELAY, TimeUnit.SECONDS);
	}

	public void deactivate() {
		if (this.scheduledFuture != null) {
			this.scheduledFuture.cancel(true);
		}
	};

	/**
	 * Sets the value of the Channel if it is greater-or-equals the lastValue.
	 * 
	 * @param essActiveChargeEnergy
	 * @param essActiveChargeEnergySum
	 */
	public void setValue(Sum.ChannelId channelId, Long value) {
		if (!this.lastEnergyValues.containsKey(channelId)) {
			// lastValue was not initialized yet -> abort
			return;
		}
		Long lastValue = this.lastEnergyValues.get(channelId);
		if (value == null || (lastValue != null && lastValue > value)) {
			this.parent.channel(channelId).setNextValue(null);
		} else {
			this.parent.channel(channelId).setNextValue(value);
			this.lastEnergyValues.put(channelId, value);
		}
	}
}
