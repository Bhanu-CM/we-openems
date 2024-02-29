package io.openems.edge.io.shelly.shelly3pro;

import java.util.Objects;

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

import io.openems.edge.bridge.http.api.BridgeHttp;
import io.openems.edge.bridge.http.api.BridgeHttpFactory;
import io.openems.edge.common.channel.BooleanWriteChannel;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.io.api.DigitalOutput;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.utils.JsonUtils;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "IO.Shelly.3Pro", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
@EventTopics({ //
		EdgeEventConstants.TOPIC_CYCLE_EXECUTE_WRITE //
})

public class IoShelly3ProImpl extends AbstractOpenemsComponent
		implements IoShelly3Pro, DigitalOutput, OpenemsComponent, EventHandler {

	private final Logger log = LoggerFactory.getLogger(IoShelly3ProImpl.class);
	private final BooleanWriteChannel[] digitalOutputChannels;
	private String baseUrl;

	@Reference
	private BridgeHttpFactory httpBridgeFactory;
	private BridgeHttp httpBridge;

	public IoShelly3ProImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				DigitalOutput.ChannelId.values(), //
				IoShelly3Pro.ChannelId.values() //
		);
		this.digitalOutputChannels = new BooleanWriteChannel[] { //
				this.channel(IoShelly3Pro.ChannelId.RELAY_1), //
				this.channel(IoShelly3Pro.ChannelId.RELAY_2), //
				this.channel(IoShelly3Pro.ChannelId.RELAY_3), //
		};
	}

	@Activate
	protected void activate(ComponentContext context, Config config) {
	    super.activate(context, config.id(), config.alias(), config.enabled());
	    this.baseUrl = "http://" + config.ip();
	    this.httpBridge = this.httpBridgeFactory.get();

	    for (int i = 0; i < 3; i++) {
	        final int relayIndex = i;
	        String url = this.baseUrl + "/rpc/Switch.GetStatus?id=" + relayIndex;
	        this.httpBridge.subscribeJsonEveryCycle(url, result -> {
	            try {
	            	processHttpResult(result, relayIndex);
	            } catch (Exception e) {
	                logError(this.log, "Error processing HTTP response: " + e.getMessage());
	                // Handle any exception that occurs during result processing
	            }
	        }, error -> {
	            logError(this.log, "HTTP request failed: " + error.getMessage());
	            // Handle the error
	        });
	    }
	}


	@Deactivate
	protected void deactivate() {
		this.httpBridgeFactory.unget(this.httpBridge);
		this.httpBridge = null;
		super.deactivate();
	}

	@Override
	public BooleanWriteChannel[] digitalOutputChannels() {
		return this.digitalOutputChannels;
	}

	@Override
	public String debugLog() {
		var b = new StringBuilder();
		var i = 1;
		for (BooleanWriteChannel channel : this.digitalOutputChannels) {
			String valueText;
			var valueOpt = channel.value().asOptional();
			if (valueOpt.isPresent()) {
				valueText = valueOpt.get() ? "ON" : "OFF";
			} else {
				valueText = "Unknown";
			}
			b.append(valueText);
			if (i < this.digitalOutputChannels.length) {
				b.append("|");
			}
			i++;
		}
		return b.toString();
	}

	@Override
	public void handleEvent(Event event) {
		if (!this.isEnabled()) {
			return;
		}

		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_EXECUTE_WRITE //
			-> this.eventExecuteWrite();
		}
	}

	private void processHttpResult(JsonElement result, int relayIndex) throws OpenemsNamedException {
	    Integer id = null;
	    Boolean output = null;

	    try {
	        JsonObject switchStatus = JsonUtils.getAsJsonObject(result);
	        id = JsonUtils.getAsInt(switchStatus, "id");
	        output = JsonUtils.getAsBoolean(switchStatus, "output");

	        this.digitalOutputChannels[id].setNextWriteValue(output);

	    } catch (OpenemsNamedException e) {
	        this._setSlaveCommunicationFailed(true);
	        this.logError(this.log, "Error processing HTTP result: " + e.getMessage());
	        throw e;
	    }

	    // Update the state of the corresponding relay based on the ID
	    if (id != null && output != null) {
	        switch (id) {
	            case 0:
	                this._setRelay1(output);
	                break;
	            case 1:
	                this._setRelay2(output);
	                break;
	            case 2:
	                this._setRelay3(output);
	                break;
	            default:
	                this.logError(this.log, "Unexpected ID value: " + id);
	                break;
	        }
	    }
	}


	/**
	 * Execute on Cycle Event "Execute Write".
	 */
	private void eventExecuteWrite() {
		for (int i = 0; i < this.digitalOutputChannels.length; i++) {
			this.executeWrite(this.digitalOutputChannels[i], i);
		}
	}

	private void executeWrite(BooleanWriteChannel channel, int index) {
		var readValue = channel.value().get();
		var writeValue = channel.getNextWriteValueAndReset();
		if (writeValue.isEmpty()) {
			return;
		}
		if (Objects.equals(readValue, writeValue.get())) {
			return;
		}
		final String url = this.baseUrl + "/relay/" + index + "?turn=" + (writeValue.get() ? "on" : "off");
		this.httpBridge.get(url).whenComplete((t, e) -> {
			if (e != null) {
				this.logError(this.log, "HTTP request failed: " + e.getMessage());
				this._setSlaveCommunicationFailed(true);
			}
		});
	}

}
