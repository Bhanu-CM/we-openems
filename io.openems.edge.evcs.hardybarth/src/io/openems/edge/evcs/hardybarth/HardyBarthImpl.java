package io.openems.edge.evcs.hardybarth;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.service.event.propertytypes.EventTopics;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.evcs.api.ChargingType;
import io.openems.edge.evcs.api.Evcs;
import io.openems.edge.evcs.api.EvcsPower;
import io.openems.edge.evcs.api.ManagedEvcs;

import java.util.Arrays;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Evcs.HardyBarth", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
@EventTopics({ //
		EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE //
})
public class HardyBarthImpl extends AbstractOpenemsComponent
		implements OpenemsComponent, EventHandler, HardyBarth, Evcs, ManagedEvcs {

	protected final Logger log = LoggerFactory.getLogger(HardyBarthImpl.class);
	protected Config config;

	// API for main REST API functions
	protected HardyBarthApi api;

	// ReadWorker and WriteHandler: Reading and sending data to the EVCS
	private final HardyBarthReadWorker readWorker = new HardyBarthReadWorker(this);
	private final HardyBarthWriteHandler writeHandler = new HardyBarthWriteHandler(this);

	// Master EVCS is responsible for RFID authentication (Not implemented for now)
	protected boolean masterEvcs = true;

	private int[] phaseOrder;

	@Reference
	private EvcsPower evcsPower;

	public HardyBarthImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				Evcs.ChannelId.values(), //
				ManagedEvcs.ChannelId.values(), //
				HardyBarth.ChannelId.values() //
		);
	}

	@Activate
	void activate(ComponentContext context, Config config) throws ConfigurationException {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.config = config;
		this._setChargingType(ChargingType.AC);
		this._setMinimumHardwarePower(config.minHwCurrent() / 1000 * 3 * 230);
		this._setMaximumHardwarePower(config.maxHwCurrent() / 1000 * 3 * 230);
		this._setPowerPrecision(230);
		this.phaseOrder = config.phases();
		if (!this.checkPhases()) {
			throw new ConfigurationException("Phase Configuration is not valid!", "Configuration must only contain 1,2 and 3.");
		}

		if (config.enabled()) {
			this.api = new HardyBarthApi(config.ip(), this);

			// Reading the given values
			this.readWorker.activate(config.id());
			this.readWorker.triggerNextRun();
		}
	}
	/**
	 * Checks if the configured Phase Array is valid.
	 * In order to be valid, it has to contain each of the numbers 1,2,3 once.
	 * (e.g [1,2,3]).
	 *
	 * @return true if the config is valid
	 */
	private boolean checkPhases() {
		String phases = Arrays.toString(this.phaseOrder);
		return phases.contains("1") && phases.contains("2") && phases.contains("3") && this.phaseOrder.length == 3;
	}

	@Override
	@Deactivate
	protected void deactivate() {
		super.deactivate();

		if (this.readWorker != null) {
			this.readWorker.deactivate();
		}
	}

	@Override
	public void handleEvent(Event event) {
		if (!this.isEnabled()) {
			return;
		}
		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE:

			this.readWorker.triggerNextRun();

			// Handle writes
			this.writeHandler.run();

			// TODO: intelligent firmware update
			break;
		}
	}

	/**
	 * Debug Log.
	 *
	 * <p>
	 * Logging only if the debug mode is enabled
	 *
	 * @param message text that should be logged
	 */
	public void debugLog(String message) {
		if (this.config.debugMode()) {
			this.logInfo(this.log, message);
		}
	}

	@Override
	protected void logError(Logger log, String message) {
		super.logError(log, message);
	}

	@Override
	public EvcsPower getEvcsPower() {
		return this.evcsPower;
	}

	@Override
	public int[] getPhaseConfiguration() {
		return this.phaseOrder;
	}

}
