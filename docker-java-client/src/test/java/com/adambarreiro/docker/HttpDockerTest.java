package com.adambarreiro.docker;

import com.adambarreiro.docker.client.api.http.HttpDocker;
import com.adambarreiro.docker.client.exception.DockerException;
import com.adambarreiro.docker.client.http.client.apache.ApacheDockerHttpClient;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;

public class HttpDockerTest {

	private HttpDocker httpDocker;

	@BeforeEach
	public void setup() {
		this.httpDocker = new HttpDocker(mock(ApacheDockerHttpClient.class));
	}

	public void dockerTest() throws DockerException {
		this.httpDocker.images().pull("ubuntu","latest");
	}

}
