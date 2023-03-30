package io.openems.edge.evcs.dezony;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.exceptions.OpenemsException;
import io.openems.common.utils.JsonUtils;

/**
 * Implements the dezony Api.
 *
 */
public class DezonyApi {
	private final String baseUrl;
	private final DezonyImpl dezonyImpl;

	public DezonyApi(String ip, int port, DezonyImpl dezonyImpl) {
		this.baseUrl = "http://" + ip + ":" + port;
		this.dezonyImpl = dezonyImpl;
	}

	/**
	 * Sends a get request.
	 *
	 * @param endpoint the REST Api endpoint
	 * @return a JsonObject or JsonArray
	 * @throws OpenemsNamedException on error
	 */
	public JsonElement sendGetRequest(String endpoint) throws OpenemsNamedException {
		var getRequestFailed = false;
		JsonObject result = null;

		try {
			var url = new URL(this.baseUrl + endpoint);
			var con = (HttpURLConnection) url.openConnection();
			String body;

			con.setRequestMethod("GET");
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);

			try (final var in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
				final var content = new StringBuilder();
				String line;

				while ((line = in.readLine()) != null) {
					content.append(line);
					content.append(System.lineSeparator());
				}

				body = content.toString();
			}

			// Get response code
			final var status = con.getResponseCode();
		
			if (status >= 300) {
				getRequestFailed = true;
				throw new OpenemsException(
						"Error while reading from dezony API. Response code: " + status + ". " + body);
			}
			
			result = JsonUtils.parseToJsonObject(body);
		} catch (OpenemsNamedException | IOException e) {
			getRequestFailed = true;
		}

		this
			.dezonyImpl
			._setChargingstationCommunicationFailed(getRequestFailed);

		return result;
	}

	/**
	 * Sends a post request to the dezony.
	 *
	 * @param endpoint the REST Api endpoint @return a JsonObject or
	 *                 JsonArray @throws OpenemsNamedException on error @throws
	 * @return A JsonObject
	 * @throws OpenemsNamedException on error
	 */
	public JsonObject sendPostRequest(String endpoint) throws OpenemsNamedException {
		var putRequestFailed = false;
		JsonObject result = null;

		try {
			final var url = new URL(this.baseUrl + endpoint);
			final var connection = (HttpURLConnection) url.openConnection();

			// Set general information
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);


			String body;
			try (var in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

				// Read HTTP response
				var content = new StringBuilder();
				String line;
				while ((line = in.readLine()) != null) {
					content.append(line);
					content.append(System.lineSeparator());
				}
				body = content.toString();
			}

			// Get response code
			var status = connection.getResponseCode();
			if ((status >= 300) && (status >= 0)) {
				// Respond error status-code
				putRequestFailed = true;
				throw new OpenemsException(
						"Error while reading from dezony API. Response code: " + status + ". " + body);
			}
			// Result OK
			result = JsonUtils.parseToJsonObject(body);
		} catch (IOException e) {
			putRequestFailed = true;
		}

		// Set state and return result
		this.dezonyImpl._setChargingstationCommunicationFailed(false);
		return result;
	}
}
