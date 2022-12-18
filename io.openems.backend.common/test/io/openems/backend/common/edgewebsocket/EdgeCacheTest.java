package io.openems.backend.common.edgewebsocket;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.types.ChannelAddress;

public class EdgeCacheTest {

	private static final String CHANNEL1 = new ChannelAddress("foo", "bar1").toString();
	private static final String CHANNEL2 = new ChannelAddress("foo", "bar2").toString();
	private static final String CHANNEL3 = new ChannelAddress("foo", "bar3").toString();

	private static final BiConsumer<Instant, Instant> NO_OP = (ignore1, ignore2) -> {
	};

	@Test
	public void test() throws OpenemsNamedException {
		var cache = new EdgeCache();
		var timestamp = 0L;

		var data1 = buildData(timestamp, CHANNEL1, "value1");
		cache.complementDataFromCache(data1, NO_OP);
		assertEquals("value1", cache.getChannelValue(CHANNEL1).getAsString());

		// older than cache
		var data2 = buildData(timestamp - 1, CHANNEL1, "ignore");
		cache.complementDataFromCache(data2, NO_OP);
		assertEquals("value1", cache.getChannelValue(CHANNEL1).getAsString());

		// normal operation
		var data3 = buildData(timestamp += 2 * 60 * 1000, CHANNEL2, "value2");
		cache.complementDataFromCache(data3, NO_OP);
		assertEquals("value1", cache.getChannelValue(CHANNEL1).getAsString());
		assertEquals("value2", cache.getChannelValue(CHANNEL2).getAsString());

		// invalidate cache
		var data4 = buildData(timestamp += 5 * 60 * 1000 + 1, CHANNEL3, "value3");
		cache.complementDataFromCache(data4, NO_OP);
		assertEquals(JsonNull.INSTANCE, cache.getChannelValue(CHANNEL1));
		assertEquals(JsonNull.INSTANCE, cache.getChannelValue(CHANNEL2));
		assertEquals("value3", cache.getChannelValue(CHANNEL3).getAsString());
	}

	private static SortedMap<Long, Map<String, JsonElement>> buildData(long timestamp, String channel, String value)
			throws OpenemsNamedException {
		var data = new TreeMap<Long, Map<String, JsonElement>>();
		var map = new HashMap<String, JsonElement>();
		map.put(channel, (JsonElement) new JsonPrimitive(value));
		data.put(timestamp, map);
		return data;
	}
}
