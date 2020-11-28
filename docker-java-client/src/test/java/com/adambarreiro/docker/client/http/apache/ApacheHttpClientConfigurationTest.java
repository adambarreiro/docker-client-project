package com.adambarreiro.docker.client.http.apache;

import com.adambarreiro.docker.client.http.client.apache.ApacheHttpClientConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApacheHttpClientConfigurationTest {

	private ApacheHttpClientConfiguration apacheHttpClientConfiguration;

	@BeforeEach
	public void setup() {
		Environment environment = mock(Environment.class);
		when(environment.getProperty(eq("docker.socket.uri"), anyString())).thenReturn("/var/run/docker.sock");
		this.apacheHttpClientConfiguration = new ApacheHttpClientConfiguration(environment);
	}

	@Test
	@DisplayName("The created client is an Apache HTTP client")
	public void createdClientIsApacheHttpClientTest() {
		assertNotNull(this.apacheHttpClientConfiguration.httpClient());
	}
}
