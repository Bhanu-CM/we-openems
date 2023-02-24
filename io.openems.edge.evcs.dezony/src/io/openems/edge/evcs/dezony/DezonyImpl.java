package io.openems.edge.evcs.dezony;

import java.util.Optional;

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

import com.google.gson.JsonElement;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.exceptions.OpenemsException;
import io.openems.common.utils.JsonUtils;
import io.openems.edge.common.channel.StringReadChannel;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.evcs.api.AbstractManagedEvcsComponent;
import io.openems.edge.evcs.api.ChargingType;
import io.openems.edge.evcs.api.Evcs;
import io.openems.edge.evcs.api.EvcsPower;
import io.openems.edge.evcs.api.ManagedEvcs;
import io.openems.edge.evcs.api.Phases;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Evcs.Dezony", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
@EventTopics({ //
		EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE, //
		EdgeEventConstants.TOPIC_CYCLE_EXECUTE_WRITE, //
})
public class DezonyImpl extends AbstractManagedEvcsComponent
		implements OpenemsComponent, EventHandler, Dezony, Evcs, ManagedEvcs {

	protected final Logger log = LoggerFactory.getLogger(DezonyImpl.class);
	protected Config config;

	// API for main REST API functions
	protected DezonyApi api;

	// ReadWorker and WriteHandler: Reading and sending data to the EVCS
	private final DezonyReadWorker readWorker = new DezonyReadWorker(this);

	// Master EVCS is responsible for RFID authentication (Not implemented for now)
	protected boolean masterEvcs = true;

	@Reference
	private EvcsPower evcsPower;

	public DezonyImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				Evcs.ChannelId.values(), //
				ManagedEvcs.ChannelId.values(), //
				Dezony.ChannelId.values() //
		);
	}

	@Activate
	void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled());
		
		this.config = config;
		
		this._setChargingType(ChargingType.AC);
		
		this._setFixedMinimumHardwarePower(config.minHwCurrent() / 1000 * 3 * 230);
		this._setFixedMaximumHardwarePower(config.maxHwCurrent() / 1000 * 3 * 230);
		
		this._setPowerPrecision(230);

		if (config.enabled()) {
			this.api = new DezonyApi(config.ip(), config.port(), this);
			this.readWorker.activate(config.id());
			this.readWorker.triggerNextRun();
		}
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
		super.handleEvent(event);
		
		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE:
			this.readWorker.triggerNextRun();

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
	public boolean getConfiguredDebugMode() {
		return this.config.debugMode();
	}

	@Override
	public boolean applyChargePowerLimit(int power) throws OpenemsNamedException {
		// Convert it to ampere and apply hard limits		
		final var current = (int) Math.round(power / (double) this.getPhasesAsInt() / 230.0);

		return this.setTarget(current);
	}

	@Override
	public boolean pauseChargeProcess() throws OpenemsNamedException {
		return this.setTarget(0);
	}

	/**
	 * Set current target to the charger.
	 * 
	 * @param current current target in A
	 * @return boolean if the target was set
	 * @throws OpenemsNamedException on error
	 */
	private boolean setTarget(int current) throws OpenemsNamedException {
		// Send charge power limit
		JsonElement resultLimit = this.api.sendPostRequest("/api/v1/charging/current?value=" + current);

		Optional<String> resultLimitVal = JsonUtils.getAsOptionalString(resultLimit, "charging_current");

		return resultLimitVal.orElse("").equals("ok");
	}

	@Override
	public boolean applyDisplayText(String text) throws OpenemsException {
		return false;
	}

	@Override
	public int getMinimumTimeTillChargingLimitTaken() {
		return 30;
	}

	@Override
	public int getConfiguredMinimumHardwarePower() {
		return Math.round(this.config.minHwCurrent() / 1000f) * DEFAULT_VOLTAGE * Phases.THREE_PHASE.getValue();
	}

	@Override
	public int getConfiguredMaximumHardwarePower() {
		return Math.round(this.config.maxHwCurrent() / 1000f) * DEFAULT_VOLTAGE * Phases.THREE_PHASE.getValue();
	}

	@Override
	public EvcsPower getEvcsPower() {
		return this.evcsPower;
	}
}
