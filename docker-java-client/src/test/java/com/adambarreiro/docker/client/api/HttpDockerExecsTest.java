package com.adambarreiro.docker.client.api;

import com.adambarreiro.docker.client.api.http.HttpDocker;
import com.adambarreiro.docker.client.exception.DockerException;
import com.adambarreiro.docker.client.http.DockerHttpDockerResponse;
import com.adambarreiro.docker.client.http.client.DockerHttpClient;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpDockerExecsTest {

	private DockerHttpDockerResponse response;

	@Test
	@DisplayName("Existent exec starts correctly")
	public void existentExecStartsCorrectlyTest() throws DockerException {
		// Given:
		String execId = "1gh234f345gfj3";
		DockerExecs httpDockerExecs = new HttpDocker(createMockClient(String.format("/exec/%s/start",execId), 200)).execs();

		// Then:
		assertNotNull(httpDockerExecs.startInteractive(execId));
		verify(response, times(0)).close(); // We don't close it because it's interactive
	}

	@Test
	@DisplayName("Non existent exec throws an exception")
	public void nonExistentExecThrowsAnExceptionTest() throws DockerException {
		// Given:
		String execId = "1gh234f345gfj3";
		DockerExecs httpDockerExecs = new HttpDocker(createMockClient(String.format("/exec/%s/start",execId), 404)).execs();

		// Then:
		assertThrows(DockerException.class, () -> httpDockerExecs.startInteractive(execId));
		verify(response, times(1)).close();
	}

	private DockerHttpClient createMockClient(String endpoint, int statusCode) throws DockerException {
		DockerHttpClient client = mock(DockerHttpClient.class);
		this.response = mock(DockerHttpDockerResponse.class);
		when(response.getStatusCode()).thenReturn(statusCode);
		when(client.post(eq(endpoint), any(ObjectNode.class))).thenReturn(response);
		return client;
	}


}
