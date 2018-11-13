package io.openems.backend.edgewebsocket.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import io.openems.backend.metadata.api.Edge;
import io.openems.common.exceptions.OpenemsException;
import io.openems.common.websocket_old.AbstractOnOpen;
import io.openems.common.websocket_old.DefaultMessages;
import io.openems.common.websocket_old.WebSocketUtils;

public class OnOpen extends AbstractOnOpen {

	private final Logger log = LoggerFactory.getLogger(OnOpen.class);
	private final EdgeWebsocketServer parent;

	public OnOpen(EdgeWebsocketServer parent, WebSocket websocket, ClientHandshake handshake) {
		super(websocket, handshake);
		this.parent = parent;
	}

	@Override
	protected void run(WebSocket websocket, ClientHandshake handshake) {
		String apikey = "";
		try {
			// create websocket attachment
			Attachment attachment = new Attachment();
			websocket.setAttachment(attachment);

			// get apikey from handshake
			Optional<String> apikeyOpt = OnOpen.parseApikeyFromHandshake(handshake);
			if (!apikeyOpt.isPresent()) {
				throw new OpenemsException("Apikey is missing in handshake");
			}
			apikey = apikeyOpt.get();
			attachment.setApikey(apikey);

			// get edgeId for apikey
			int[] edgeIds = this.parent.parent.metadataService.getEdgeIdsForApikey(apikey);

			// verify apikey (is also empty, when Odoo is not initialized)
			if (edgeIds.length == 0) {
				throw new OpenemsException("Unable to authenticate this Apikey.");
			}

			// get Edge object for edgeIds
			List<Edge> edges = new ArrayList<>();
			for (int edgeId : edgeIds) {
				edges.add(this.parent.parent.metadataService.getEdge(edgeId)); // throws Exception if Edge is not found
			}

			// add edgeIds to websocket attachment
			attachment.setEdgeIds(edgeIds);

			// if existing: close existing websocket for this apikey
			synchronized (this.parent.websocketsMap) {
				for (int edgeId : edgeIds) {
					if (this.parent.websocketsMap.containsKey(edgeId)) {
						WebSocket oldWebsocket = this.parent.websocketsMap.get(edgeId);
						oldWebsocket.closeConnection(CloseFrame.REFUSE,
								"Another Edge with this apikey [" + apikey + "] connected.");
					}
					// add websocket to local cache
					this.parent.websocketsMap.put(edgeId, websocket);
				}
			}

			// send successful reply to openems
			JsonObject jReply = DefaultMessages.openemsConnectionSuccessfulReply();
			WebSocketUtils.send(websocket, jReply);

			// announce Edge as online
			for (Edge edge : edges) {
				edge.setOnline(true);
			}

			// log
			for (Edge edge : edges) {
				log.info("Edge [" + edge.getName() + "]" //
						+ (edgeIds.length > 1 ? ", ID [" + edge.getId() + "]" : "") //
						+ " connected.");
				// set last update timestamps in MetadataService
				edge.setLastMessage();
			}
		} catch (OpenemsException e) {
			log.warn(e.getMessage());
			// send connection failed to OpenEMS
			JsonObject jReply = DefaultMessages.openemsConnectionFailedReply(e.getMessage());
			WebSocketUtils.sendOrLogError(websocket, jReply);
			// close websocket
			websocket.closeConnection(CloseFrame.REFUSE,
					"Connection to backend failed. Apikey [" + apikey + "]. Error: " + e.getMessage());
		}
	}

	/**
	 * Parses the apikey from websocket onOpen handshake
	 *
	 * @param handshake
	 * @return
	 */
	private static Optional<String> parseApikeyFromHandshake(ClientHandshake handshake) {
		if (handshake.hasFieldValue("apikey")) {
			String apikey = handshake.getFieldValue("apikey");
			return Optional.ofNullable(apikey);
		}
		return Optional.empty();
	}
}
