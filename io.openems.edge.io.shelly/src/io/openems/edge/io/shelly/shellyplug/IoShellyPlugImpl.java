package io.openems.edge.io.shelly.shellyplug;

import static io.openems.common.utils.JsonUtils.getAsBoolean;
import static io.openems.common.utils.JsonUtils.getAsFloat;
import static io.openems.common.utils.JsonUtils.getAsJsonArray;
import static io.openems.common.utils.JsonUtils.getAsJsonObject;
import static io.openems.common.utils.JsonUtils.getAsLong;
import static io.openems.edge.io.api.ShellyUtils.generateDebugLog;
import static java.lang.Math.round;

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
import io.openems.edge.bridge.http.api.BridgeHttp;
import io.openems.edge.bridge.http.api.BridgeHttpFactory;
import io.openems.edge.bridge.http.api.HttpResponse;
import io.openems.edge.common.channel.BooleanWriteChannel;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.io.api.DigitalOutput;
import io.openems.edge.io.api.ShellyUtils;
import io.openems.edge.meter.api.ElectricityMeter;
import io.openems.edge.meter.api.MeterType;
import io.openems.edge.meter.api.SinglePhase;
import io.openems.edge.meter.api.SinglePhaseMeter;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "IO.Shelly.Plug", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE//
)
@EventTopics({ //
		EdgeEventConstants.TOPIC_CYCLE_EXECUTE_WRITE //
})
public class IoShellyPlugImpl extends AbstractOpenemsComponent
		implements IoShellyPlug, DigitalOutput, SinglePhaseMeter, ElectricityMeter, OpenemsComponent, EventHandler {

	private final Logger log = LoggerFactory.getLogger(IoShellyPlugImpl.class);
	private final BooleanWriteChannel[] digitalOutputChannels;

	private MeterType meterType = null;
	private SinglePhase phase = null;
	private String baseUrl;

	private BridgeHttp httpBridge;

	@Reference
	private BridgeHttpFactory httpBridgeFactory;

	public IoShellyPlugImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				ElectricityMeter.ChannelId.values(), //
				DigitalOutput.ChannelId.values(), //
				IoShellyPlug.ChannelId.values() //
		);
		this.digitalOutputChannels = new BooleanWriteChannel[] { //
				this.channel(IoShellyPlug.ChannelId.RELAY) //
		};

		SinglePhaseMeter.calculateSinglePhaseFromActivePower(this);
	}

	@Activate
	private void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.meterType = config.type();
		this.phase = config.phase();
		this.baseUrl = "http://" + config.ip();
		this.httpBridge = this.httpBridgeFactory.get();

		if (!this.isEnabled()) {
			return;
		}

		this.httpBridge.subscribeJsonEveryCycle(this.baseUrl + "/status", this::processHttpResult);
	}

	@Override
	@Deactivate
	protected void deactivate() {
		super.deactivate();
		this.httpBridgeFactory.unget(this.httpBridge);
		this.httpBridge = null;
	}

	@Override
	public BooleanWriteChannel[] digitalOutputChannels() {
		return this.digitalOutputChannels;
	}

	@Override
	public String debugLog() {
		return generateDebugLog(this.digitalOutputChannels, this.getActivePowerChannel());
	}

	@Override
	public void handleEvent(Event event) {
		if (!this.isEnabled()) {
			return;
		}

		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_EXECUTE_WRITE -> {
			ShellyUtils.executeWrite(this.getRelayChannel(), this.baseUrl, this.httpBridge, 0);
		}
		}
	}

	private void processHttpResult(HttpResponse<JsonElement> result, Throwable error) {
		this._setSlaveCommunicationFailed(result == null);

		Boolean relayIson = null;
		Integer power = null;
		Long energy = null;

		if (error != null) {
			this._setRelay(null);
			this._setActivePower(null);
			this._setActiveProductionEnergy(null);
			this._setSlaveCommunicationFailed(true);
			this.logDebug(this.log, error.getMessage());
			return;
		} else {
			try {

				var response = getAsJsonObject(result.data());

				var relays = getAsJsonArray(response, "relays");
				var relay1 = getAsJsonObject(relays.get(0));
				relayIson = getAsBoolean(relay1, "ison");

				var meters = getAsJsonArray(response, "meters");
				var meter1 = getAsJsonObject(meters.get(0));
				power = round(getAsFloat(meter1, "power"));
				energy = getAsLong(meter1, "total") /* Unit: Wm */ / 60 /* Wh */;

				this._setRelay(relayIson);
				this._setActivePower(power);
				this._setActiveProductionEnergy(energy);

			} catch (OpenemsNamedException e) {
				this._setRelay(null);
				this._setActivePower(null);
				this._setActiveProductionEnergy(0L);
				this._setSlaveCommunicationFailed(true);
				this.logDebug(this.log, e.getMessage());
			}
		}
	}

	@Override
	public MeterType getMeterType() {
		return this.meterType;
	}

	@Override
	public SinglePhase getPhase() {
		return this.phase;
	}

}