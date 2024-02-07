package io.openems.edge.io.shelly.shelly25;

import java.util.Objects;

import io.openems.edge.bridge.http.api.BridgeHttp;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.service.event.propertytypes.EventTopics;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.edge.common.channel.BooleanWriteChannel;
import io.openems.edge.common.channel.WriteChannel;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.io.api.DigitalOutput;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "IO.Shelly.25", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE//
)
@EventTopics({ //
		EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE, //
		EdgeEventConstants.TOPIC_CYCLE_EXECUTE_WRITE //
})
public class IoShelly25Impl extends AbstractOpenemsComponent
		implements IoShelly25, DigitalOutput, OpenemsComponent, EventHandler {

	private final Logger log = LoggerFactory.getLogger(IoShelly25Impl.class);
	private final BooleanWriteChannel[] digitalOutputChannels;
	private String baseUrl;

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	private BridgeHttp httpBridge;

	public IoShelly25Impl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				DigitalOutput.ChannelId.values(), //
				IoShelly25.ChannelId.values() //
		);
		this.digitalOutputChannels = new BooleanWriteChannel[] { //
				this.channel(IoShelly25.ChannelId.RELAY_1), //
				this.channel(IoShelly25.ChannelId.RELAY_2), //
		};
	}

	@Activate
	private void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.baseUrl = "http://" + config.ip();
	}

	@Override
	@Deactivate
	protected void deactivate() {
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
		for (WriteChannel<Boolean> channel : this.digitalOutputChannels) {
			String valueText;
			var valueOpt = channel.value().asOptional();
			if (valueOpt.isPresent()) {
				valueText = valueOpt.get() ? "x" : "-";
			} else {
				valueText = "?";
			}
			b.append(i + valueText);

			// add space for all but the last
			if (++i <= this.digitalOutputChannels.length) {
				b.append(" ");
			}
		}
		return b.toString();
	}

	@Override
	public void handleEvent(Event event) {
		if (!this.isEnabled()) {
			return;
		}

		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE:
			this.eventBeforeProcessImage();
			break;

		case EdgeEventConstants.TOPIC_CYCLE_EXECUTE_WRITE:
			this.eventExecuteWrite();
			break;
		}
	}

	/**
	 * Execute on Cycle Event "Before Process Image".
	 */
	private void eventBeforeProcessImage() {
		for (int i = 0; i < digitalOutputChannels.length; i++) {
			final int index = i;
			String url = baseUrl + "/relay/" + index;
			this.httpBridge.subscribeJsonEveryCycle(url, json -> {
				try {
					boolean isOn = json.getAsJsonObject().get("ison").getAsBoolean();
					this.digitalOutputChannels[index].setNextWriteValue(isOn);
					this._setSlaveCommunicationFailed(false);
				} catch (Exception e) {
					this.logError(this.log, "Error processing relay state: " + e.getMessage());
					this._setSlaveCommunicationFailed(true);
				}
			}, throwable -> {
				this.logError(this.log, "HTTP request failed for relay " + index + ": " + throwable.getMessage());
				this._setSlaveCommunicationFailed(true);
			});
		}
	}

	/**
	 * Execute on Cycle Event "Execute Write".
	 */
	private void eventExecuteWrite() {
		for (int i = 0; i < digitalOutputChannels.length; i++) {
			executeWrite(digitalOutputChannels[i], i);
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

		this.httpBridge.request(url).whenComplete((response, error) -> {
			if (error != null) {
				this.logError(this.log, "HTTP request failed: " + error.getMessage());
				this._setSlaveCommunicationFailed(true);
			}
		});
	}

}
