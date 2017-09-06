package io.openems.backend.openemswebsocket;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.StringJoiner;

import org.apache.xmlrpc.XmlRpcException;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abercap.odoo.OdooApiException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.openems.backend.core.ConnectionManager;
import io.openems.backend.exception.FemsException;
import io.openems.backend.influx.Influxdb;
import io.openems.backend.odoo.Odoo;
import io.openems.backend.odoo.fems.device.FemsDevice;
import io.openems.backend.utilities.JsonUtils;
import io.openems.backend.utilities.OpenemsException;
import io.openems.backend.utilities.StringUtils;
import io.openems.backend.utilities.WebSocketUtils;

/**
 * Handles connections to FEMS/OpenEMS-Devices.
 * Needs to be initialized before it can be used as a singleton.
 *
 * @author stefan.feilmeier
 *
 */
public class OpenemsWebsocket extends WebSocketServer {

	private static Logger log = LoggerFactory.getLogger(OpenemsWebsocket.class);

	private static OpenemsWebsocket instance;

	/**
	 * Initialize and start the Websocketserver
	 *
	 * @param port
	 * @throws Exception
	 */
	public static synchronized void initialize(int port) throws Exception {
		OpenemsWebsocket ws = new OpenemsWebsocket(port);
		ws.start();
	}

	/**
	 * Returns the singleton instance
	 *
	 * @return
	 */
	public static synchronized OpenemsWebsocket getInstance() {
		return OpenemsWebsocket.instance;
	}

	/**
	 * Holds a reference to InfluxDB server
	 */
	private final Influxdb influxdb;

	/**
	 * Holds a reference to the ConnectionManager singleton
	 */
	private final ConnectionManager connectionManager;

	/**
	 * Holds a reference to the Odoo singleton
	 */
	private final Odoo odoo;

	private OpenemsWebsocket(int port) throws Exception {
		super(new InetSocketAddress(port));
		this.influxdb = Influxdb.getInstance();
		this.connectionManager = ConnectionManager.getInstance();
		this.odoo = Odoo.getInstance();
	}

	/**
	 * Open event of websocket. Expects an "apikey" authentication. On success tells the ConnectionManager
	 * to keep the websocket. On failure closes the websocket.
	 */
	@Override
	public void onOpen(WebSocket websocket, ClientHandshake handshake) {
		try {
			if (handshake.hasFieldValue("apikey")) {
				String apikey = handshake.getFieldValue("apikey");
				try {
					List<FemsDevice> devices = this.odoo.getDevicesForApikey(apikey);
					if (devices.isEmpty()) {
						throw new FemsException("Unable to find device for apikey [" + apikey + "]");
					} else {
						// found devices. Add all of them to ConnectionManager
						this.connectionManager.addFemsWebsocket(websocket, devices);
					}
					log.info("Opened connection to [" + this.getFemsName(websocket) + "]");
				} catch (OdooApiException | XmlRpcException e) {
					throw new FemsException("Unable to query for apikey [" + apikey + "]: " + e.getMessage());
				}
			}
		} catch (FemsException e) {
			log.warn("Connection failed: " + e.getMessage());
			websocket.close();
		}
	}

	/**
	 * Close event of websocket. Tells the ConnectionManager to remove the websocket.
	 */
	@Override
	public void onClose(WebSocket websocket, int code, String reason, boolean remote) {
		log.info("Close connection to [" + this.getFemsName(websocket) + "]" //
				+ " Code [" + code + "] Reason [" + reason + "]");
		this.connectionManager.removeFemsWebsocket(websocket);
	}

	/**
	 * Error event of websocket. Logs the error.
	 */
	@Override
	public void onError(WebSocket websocket, Exception ex) {
		log.info("Error on connection to [" + this.getFemsName(websocket) + "]: " + ex.getMessage());
	}

	/**
	 * Message event of websocket. Handles a new message. At this point the FEMS device is already authenticated.
	 */
	@Override
	public void onMessage(WebSocket websocket, String message) {
		this.connectionManager.getFemsWebsocketDevices(websocket).forEach(device -> {
			/**
			 * set active in Odoo
			 */
			if (device.getState().equals("inactive")) {
				device.setState("active");
			}
			device.setLastMessage();
			JsonObject jMessage = (new JsonParser()).parse(message).getAsJsonObject();

			/*
			 * New timestamped data
			 */
			if (jMessage.has("timedata")) {
				timedata(device, jMessage.get("timedata"));
			}

			/*
			 * New currentdata data -> forward to browserWebsockets
			 */
			if (jMessage.has("currentdata")) {
				currentdata(websocket, jMessage.get("currentdata"));
			}

			/*
			 * New log -> forward to browserWebsockets
			 */
			if (jMessage.has("log")) {
				log(websocket, jMessage.get("log"));
			}

			/*
			 * New metadata
			 */
			if (jMessage.has("metadata")) {
				metadata(device, websocket, jMessage.get("metadata"));
			}

			// Save data to Odoo
			try {
				device.writeObject();
			} catch (OdooApiException | XmlRpcException e) {
				log.error(device.getName() + ": Updating Odoo failed: " + e.getMessage());
			}
		});
	}

	/**
	 * Builds a string with all FEMS names: "fems7/fems8"
	 *
	 * @param websocket
	 * @return
	 */
	private String getFemsName(WebSocket websocket) {
		StringJoiner joiner = new StringJoiner("/");
		this.connectionManager.getFemsWebsocketDeviceNames(websocket).forEach(name -> {
			joiner.add(name);
		});
		return joiner.toString();
	}

	private void timedata(FemsDevice device, JsonElement jTimedataElement) {
		try {
			JsonObject jTimedata = JsonUtils.getAsJsonObject(jTimedataElement);
			// Write to InfluxDB
			try {
				influxdb.write(device.getNameNumber(), jTimedata);
				log.info(device.getName() + ": wrote " + jTimedata.entrySet().size() + " timestamps "
						+ StringUtils.toShortString(jTimedata, 120));
			} catch (Exception e) {
				log.error("No InfluxDB-connection: ", e);
			}
			// Write some data to Odoo
			// TODO: this is only to provide feedback for FENECON
			// Service-Team that the device is online. Replace with
			// something based on the actual websocket connection
			device.setLastUpdate();
			jTimedata.entrySet().forEach(entry -> {
				try {
					JsonObject jChannels = JsonUtils.getAsJsonObject(entry.getValue());
					if (jChannels.has("ess0/Soc")) {
						int soc = JsonUtils.getAsPrimitive(jChannels, "ess0/Soc").getAsInt();
						device.setSoc(soc);
					}
					if (jChannels.has("system0/PrimaryIpAddress")) {
						String ipv4 = JsonUtils.getAsPrimitive(jChannels, "system0/PrimaryIpAddress").getAsString();
						device.setIpV4(ipv4);
					}
				} catch (OpenemsException e) {
					log.error(e.getMessage());
				}
			});
		} catch (OpenemsException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Forward currentdata to browserWebsockets
	 */
	private void currentdata(WebSocket websocket, JsonElement jCurrentdataElement) {
		try {
			JsonObject jCurrentdata = JsonUtils.getAsJsonObject(jCurrentdataElement);
			JsonObject j = new JsonObject();
			j.add("currentdata", jCurrentdata);
			// log.info("FemsWS: " + websocket + ", " + websocket.isOpen());
			this.connectionManager.getFemsWebsocketDeviceNames(websocket).forEach(name -> {
				j.addProperty("device", name);
				this.connectionManager.getBrowserWebsockets(name).forEach(browserWebsocket -> {
					// log.info("BrowserWS: " + browserWebsocket + ", " + browserWebsocket.isOpen());
					log.info(name + ": forward currentdata to Browser: " + StringUtils.toShortString(j, 100));
					WebSocketUtils.send(browserWebsocket, j);
				});
			});
		} catch (OpenemsException e) {
			log.error(e.getMessage());
		}
	}

	private void log(WebSocket websocket, JsonElement jLogElement) {
		try {
			JsonObject jLog = JsonUtils.getAsJsonObject(jLogElement);
			JsonObject j = new JsonObject();
			j.add("log", jLog);
			this.connectionManager.getFemsWebsocketDeviceNames(websocket).forEach(name -> {
				j.addProperty("device", name);
				this.connectionManager.getBrowserWebsockets(name).forEach(browserWebsocket -> {
					log.info(name + ": forward log to Browser: " + StringUtils.toShortString(j, 100));
					WebSocketUtils.send(browserWebsocket, j);
				});
			});
		} catch (OpenemsException e) {
			log.error(e.getMessage());
		}
	}

	private void metadata(FemsDevice device, WebSocket websocket, JsonElement jMetadataElement) {
		try {
			JsonObject jMetadata = JsonUtils.getAsJsonObject(jMetadataElement);
			if (jMetadata.has("config")) {
				JsonObject jConfig = JsonUtils.getAsJsonObject(jMetadata, "config");
				log.info(getFemsName(websocket) + ": got config " + StringUtils.toShortString(jConfig, 120));
				device.setOpenemsConfig(jConfig);
			}
		} catch (OpenemsException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public void onStart() {

	}
}