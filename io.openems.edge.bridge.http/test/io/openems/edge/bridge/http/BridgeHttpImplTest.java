package io.openems.edge.bridge.http;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.osgi.service.event.Event;

import io.openems.common.utils.ReflectionUtils;
import io.openems.edge.bridge.http.api.BridgeHttp;
import io.openems.edge.common.event.EdgeEventConstants;

public class BridgeHttpImplTest {

	private CycleSubscriber cycleSubscriber;
	private BridgeHttp bridgeHttp;

	@Before
	public void before() throws Exception {
		this.cycleSubscriber = new CycleSubscriber();
		this.bridgeHttp = new BridgeHttpImpl();
		ReflectionUtils.setAttribute(BridgeHttpImpl.class, this.bridgeHttp, "cycleSubscriber", this.cycleSubscriber);

		final var fetcher = new DummyUrlFetcher();
		fetcher.addUrlHandler(url -> {
			return switch (url) {
			case "dummy" -> "success";
			default -> null;
			};
		});
		ReflectionUtils.setAttribute(BridgeHttpImpl.class, this.bridgeHttp, "urlFetcher", fetcher);

		((BridgeHttpImpl) this.bridgeHttp).activate();
	}

	@Test
	public void test() throws Exception {
		final var callCount = new AtomicInteger(0);
		this.bridgeHttp.subscribe(3, "dummy", t -> {
			assertEquals("success", t);
			callCount.incrementAndGet();
		});

		assertEquals(0, callCount.get());
		this.nextCycle();
		assertEquals(0, callCount.get());
		this.nextCycle();
		assertEquals(0, callCount.get());
		this.nextCycle();
		// TODO separate pool
		Thread.sleep(1000);
		assertEquals(1, callCount.get());
	}

	private void nextCycle() {
		this.cycleSubscriber
				.handleEvent(new Event(EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE, new HashMap<>()));
	}

}
