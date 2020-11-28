package com.adambarreiro.docker.client.http;

import jnr.unixsocket.UnixSocket;
import org.apache.http.HttpHost;
import org.apache.http.protocol.HttpContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnixConnectionSocketFactoryTest {

	private UnixConnectionSocketFactory unixConnectionSocketFactory;

	@BeforeEach
	public void setup() {
		unixConnectionSocketFactory = new UnixConnectionSocketFactory("/var/run/docker.sock");
	}

	@Test
	@DisplayName("When socket is created the returned socket is a Unix socket")
	public void whenSocketIsCreatedTheReturnedSocketIsUnixSocketTest() throws Exception {
		// Given:
		final Socket socket = unixConnectionSocketFactory.createSocket(mock(HttpContext.class));

		// Then:
		assertTrue(socket instanceof UnixSocket, "Created socket is from class " + socket.getClass());
	}

	@Test
	@DisplayName("When socket is connected the returned socket is a Unix socket")
	public void whenSocketIsConnectedTheReturnedSocketIsUnixSocketTest() throws Exception {
		// Given:
		Socket unixSocket = mock(UnixSocket.class);
		when(unixSocket.getChannel()).thenReturn(mock(SocketChannel.class));

		// When:
		final Socket socket = unixConnectionSocketFactory.connectSocket(
				10,
				unixSocket,
				HttpHost.create("https://www.adambarreiro.com"),
				mock(InetSocketAddress.class),
				mock(InetSocketAddress.class),
				mock(HttpContext.class));

		// Then:
		assertTrue(socket instanceof UnixSocket, "Created socket is from class " + socket.getClass());
	}

}
