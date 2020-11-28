package com.adambarreiro.docker.client.http.client.apache;

import jnr.unixsocket.UnixSocket;
import jnr.unixsocket.UnixSocketAddress;
import jnr.unixsocket.UnixSocketChannel;
import org.apache.http.HttpHost;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Allows the Apache HttpClient to work with Unix sockets, as it's the only way to interact with the Docker engine.
 */
public final class UnixConnectionSocketFactory implements ConnectionSocketFactory {

	private final File unixSocket;

	public UnixConnectionSocketFactory(final String unixSocketPath) {
		this.unixSocket = new File(unixSocketPath);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Socket createSocket(final HttpContext context) throws IOException {
		return UnixSocketChannel.open().socket();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Socket connectSocket(final int connectTimeout, final Socket socket,
								final HttpHost host, final InetSocketAddress remoteAddress,
								final InetSocketAddress localAddress, final HttpContext context)
			throws IOException {
		Assert.isTrue(socket instanceof UnixSocket, "Unexpected socket type: " + socket.getClass().toString());
		socket.setSoTimeout(connectTimeout);
		socket.getChannel().connect(
				new UnixSocketAddress(this.unixSocket)
		);
		return socket;
	}

}
