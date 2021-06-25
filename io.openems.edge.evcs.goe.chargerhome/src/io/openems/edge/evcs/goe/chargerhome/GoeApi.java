package io.openems.edge.evcs.goe.chargerhome;

import com.google.gson.JsonObject;
import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.exceptions.OpenemsException;
import io.openems.common.utils.JsonUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GoeApi {
  private final String ipAddress;
  private final int executeEveryCycle = 10;
  private int cycle;
  private JsonObject jsonStatus;
  private GoeChargerHomeImpl parent;

  public GoeApi(GoeChargerHomeImpl p) {
    this.ipAddress = p.config.ip();
    this.cycle = 0;
    this.jsonStatus = null;
    this.parent = p;
  }

  /**
   * Gets the status from go-e. 
   * See https://github.com/goecharger
   *
   * @return the boolean value
   * @throws OpenemsNamedException on error
   */
  public JsonObject getStatus() {
      
    try {
      // Execute every x-Cycle
      if (this.cycle == 0 || this.cycle % this.executeEveryCycle == 0) {
        JsonObject json = new JsonObject();
        String url = "http://" + this.ipAddress + "/status";
        json = this.sendRequest(url, "GET");                                      
        
        this.cycle = 1;
        this.jsonStatus = json;
        return json;    
      } else {
        this.cycle++;
        return this.jsonStatus;
      }

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  /** Sets the activation status for go-e
   * See https://github.com/goecharger.
   *
   * @return JsonObject with new settings
   * @throws OpenemsNamedException on error
   */
  public JsonObject setActive(boolean active) {
      
    try {
      if (active != this.parent.active) {
        JsonObject json = new JsonObject();
        Integer status = 0;
        if (active) {
          status = 1;
        }
        String url = "http://" + this.ipAddress + "/mqtt?payload=alw=" + Integer.toString(status);
        json = this.sendRequest(url, "PUT");
        this.parent.active = active;    
        this.jsonStatus = json;
        return json;
      } else {
        return this.jsonStatus;
      }      

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  /** Sets the Current in Ampere for go-e
   * See https://github.com/goecharger.
   *
   * @return JsonObject with new settings
   * @throws OpenemsNamedException on error
   */
  public JsonObject setCurrent(int current) {
      
    try {
      Integer currentAmpere = current / 1000;
      if (currentAmpere != this.parent.activeCurrent / 1000) {
        JsonObject json = new JsonObject();        
        String url = "http://" + this.ipAddress + "/mqtt?payload=amp=" + Integer.toString(currentAmpere);
        json = this.sendRequest(url, "PUT");
        this.parent.activeCurrent = currentAmpere * 1000;
        this.jsonStatus = json;
        return json;
      } else {
        return this.jsonStatus;
      }

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  /** Limit MaxEnergy for go-e
   * See https://github.com/goecharger.
   *
   * @return JsonObject with new settings
   * @throws OpenemsNamedException on error
   */
  public boolean limitMaxEnergy(boolean limit) {
      
    try {
      JsonObject json = new JsonObject();
      int stp = 0;
      if (limit) {
        stp = 2;
      }        
      String url = "http://" + this.ipAddress + "/mqtt?payload=stp=" + Integer.toString(stp);
      json = this.sendRequest(url, "PUT");
      if (json != null) {
        this.jsonStatus = json;
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
  /** Sets the MaxEnergy in 0.1 kWh for go-e
   * See https://github.com/goecharger.
   *
   * @return JsonObject with new settings
   * @throws OpenemsNamedException on error
   */
  public boolean setMaxEnergy(int maxEnergy) {
      
    try {
      JsonObject json = new JsonObject();
      if (maxEnergy > 0) {
        this.limitMaxEnergy(true);
      } else {
        this.limitMaxEnergy(false);
      }        
      String url = "http://" + this.ipAddress + "/mqtt?payload=dwo=" + Integer.toString(maxEnergy);
      json = this.sendRequest(url, "PUT");
      if (json != null) {
        this.jsonStatus = json;
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Sends a get or set request to the go-e API.
   *
   * @param endpoint the REST Api endpoint
   * @return a JsonObject or JsonArray
   * @throws OpenemsNamedException on error
   */
  private JsonObject sendRequest(String urlString, String requestMethod) 
      throws OpenemsNamedException {
    try {
      URL url = new URL(urlString);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod(requestMethod);
      con.setConnectTimeout(5000);
      con.setReadTimeout(5000);
      int status = con.getResponseCode();
      String body;
      try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
        // Read HTTP response
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
          content.append(line);
          content.append(System.lineSeparator());
        }
        body = content.toString();
      }
      if (status < 300) {
        // Parse response to JSON
        return JsonUtils.parseToJsonObject(body);
      } else {
        throw new OpenemsException(
            "Error while reading from go-e API. Response code: " + status + ". " + body);
      }
    } catch (OpenemsNamedException | IOException e) {
      throw new OpenemsException(
          "Unable to read from go-e API. " + e.getClass().getSimpleName() + ": " + e.getMessage());
    }
  }
  
}
