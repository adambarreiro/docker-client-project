package com.adambarreiro.docker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * These tests require a real Docker engine running.
 * What we do here is to subscribe to the controller websocket and
 * test that the stats are being retrieved and the stop event also.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StatsWebsocketControllerTest {

	public static final String SOCKET_URL = "ws://localhost:%d/socket";

	@LocalServerPort
	private int port;

	private WebSocketStompClient client;

	@BeforeEach
	public void setup() {
		this.client = new WebSocketStompClient(new SockJsClient(
				List.of(new WebSocketTransport(new StandardWebSocketClient()))));
		this.client.setMessageConverter(new StringMessageConverter());
	}

	@Test
	@DisplayName("Container is created and stats are received when socket is connected, then it's destroyed when stopped")
	public void containerIsCreatedAndStatsAreReceivedWhenSocketIsConnectedThenItsDestroyedWhenStoppedTest() throws Exception {
		// Given:
		CountDownLatch statsLatch = new CountDownLatch(1);
		CountDownLatch stoppedLatch = new CountDownLatch(1);
		StompSession session = this.client
				.connect(String.format(SOCKET_URL, this.port), new StompSessionHandlerAdapter() {})
				.get();
		session.subscribe("/docker/stats", this.subscriber(statsLatch));
		session.subscribe("/docker/stopped", this.subscriber(stoppedLatch));

		// When:
		session.send("/start", "");

		// Then:
		if (!statsLatch.await(10, TimeUnit.SECONDS)) {
			fail("Stats not received");
		}

		// When:
		session.send("/stop", "");

		// Then:
		if (!stoppedLatch.await(10, TimeUnit.SECONDS)) {
			fail("Stopped notification not received");
		}
	}

	private StompFrameHandler subscriber(final CountDownLatch latch) {
		return new StompFrameHandler() {

			@Override
			public Type getPayloadType(final StompHeaders headers) {
				return String.class;
			}

			@Override
			public void handleFrame(final StompHeaders headers, final Object payload) {
				latch.countDown();
			}
		};
	}

}
