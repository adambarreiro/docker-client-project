package com.adambarreiro.docker;

import com.adambarreiro.docker.client.Docker;
import com.adambarreiro.docker.client.http.DockerHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.mockito.Mockito.mock;

public class DockerTest {

	private Environment environment;
	private Docker docker;

	@BeforeEach
	public void setup() {
		this.environment = mock(Environment.class);
		this.docker = new Docker(mock(DockerHttpClient.class), environment);
	}

	public void dockerTest() throws IOException, URISyntaxException {
		this.docker.images().pull("ubuntu","latest");

	}

}
