package com.adambarreiro.docker.client.http;

import org.apache.http.client.HttpClient;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Basic Apache HTTP client bean configuration.
 */
@Configuration
class HttpClientConfiguration {

	private static final String DOCKER_SOCKET_URI_PROPERTY = "docker.socket.uri";
	private static final String DOCKER_SOCKET_URI_DEFAULT_VALUE = "/var/run/docker.sock";

	private final Environment environment;

	HttpClientConfiguration(final Environment environment) {
		this.environment = environment;
	}

	/**
	 * Basic Apache HTTP client with a configured pool of 10 connections and the Unix socket
	 * that the Docker API uses to communicate.
	 *
	 * @return the Apache HTTP client ready to consume the Docker API.
	 */
	@Bean
	public HttpClient httpClient() {
		final String dockerSocketUrl = environment.getProperty(DOCKER_SOCKET_URI_PROPERTY, DOCKER_SOCKET_URI_DEFAULT_VALUE);
		final PoolingHttpClientConnectionManager pool =
				new PoolingHttpClientConnectionManager(
						RegistryBuilder
								.<ConnectionSocketFactory>create()
								.register("unix", new UnixConnectionSocketFactory(dockerSocketUrl))
								.build()
				);
		pool.setDefaultMaxPerRoute(10);
		pool.setMaxTotal(10);
		return HttpClientBuilder.create()
				.setConnectionManager(pool)
				.build();
	}

}
