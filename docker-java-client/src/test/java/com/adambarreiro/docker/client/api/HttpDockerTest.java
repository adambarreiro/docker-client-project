package com.adambarreiro.docker.client.api;

import com.adambarreiro.docker.client.api.http.HttpDockerContainers;
import com.adambarreiro.docker.client.api.http.HttpDocker;
import com.adambarreiro.docker.client.api.http.HttpDockerExecs;
import com.adambarreiro.docker.client.api.http.HttpDockerImages;
import com.adambarreiro.docker.client.http.client.DockerHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class HttpDockerTest {

	private Docker docker;

	@BeforeEach
	public void setup() {
		this.docker = new HttpDocker(mock(DockerHttpClient.class));
	}

	@Test
	@DisplayName("Docker client has containers namespace")
	public void dockerClientHasContainersNamespaceTest() {
		assertTrue(docker.containers() instanceof HttpDockerContainers);
	}

	@Test
	@DisplayName("Docker client has images namespace")
	public void dockerClientHasImagesNamespaceTest() {
		assertTrue(docker.images() instanceof HttpDockerImages);
	}

	@Test
	@DisplayName("Docker client has execs namespace")
	public void dockerClientHasExecsNamespaceTest() {
		assertTrue(docker.execs() instanceof HttpDockerExecs);
	}

}
