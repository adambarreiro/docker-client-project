package com.adambarreiro.docker.client.api;

import com.adambarreiro.docker.client.api.http.HttpDocker;
import com.adambarreiro.docker.client.exception.DockerException;
import com.adambarreiro.docker.client.http.DockerHttpDockerResponse;
import com.adambarreiro.docker.client.http.client.DockerHttpClient;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpDockerContainersTest {

	private DockerHttpDockerResponse response;

	@Test
	@DisplayName("A valid container creation returns its ID")
	public void validContainerCreationReturnsItsIdTest() throws DockerException {
		// Given:
		DockerContainers httpDockerContainers = new HttpDocker(createMockClient("/containers/create", 201)).containers();
		// Then:
		assertEquals("123", httpDockerContainers.create("ubuntu1","ubuntu:latest"));
		verify(response, times(1)).close();
	}

	@Test
	@DisplayName("A non valid container creation returns its ID")
	public void nonValidContainerCreationReturnsItsIdTest() throws DockerException {
		// Given:
		DockerContainers httpDockerContainers = new HttpDocker(createMockClient("/containers/create", 400)).containers();
		// Then:
		assertThrows(DockerException.class, () -> httpDockerContainers.create("ubuntu1","ubuntu:latest"));
		verify(response, times(1)).close();
	}

	@Test
	@DisplayName("A valid container ID starts a valid container")
	public void validContainerIdStartsValidContainer() throws DockerException {
		// Given:
		String containerId = "123";
		DockerContainers httpDockerContainers = new HttpDocker(createMockClient(
				String.format("/containers/%s/start", containerId), 204)).containers();
		// Then:
		httpDockerContainers.start(containerId);
		verify(response, times(1)).close();
	}

	@Test
	@DisplayName("A non valid container ID starts throws an exception")
	public void nonValidContainerIdStartsValidContainer() throws DockerException {
		// Given:
		String containerId = "123";
		DockerContainers httpDockerContainers = new HttpDocker(createMockClient(
				String.format("/containers/%s/start", containerId), 404)).containers();
		// Then:
		assertThrows(DockerException.class, () -> httpDockerContainers.start("123"));
		verify(response, times(1)).close();
	}

	@Test
	@DisplayName("A valid container ID is stopped")
	public void stoppingValidContainerIdTest() throws DockerException {
		// Given:
		String containerId = "123";
		DockerContainers httpDockerContainers = new HttpDocker(createMockClient(
				String.format("/containers/%s/stop", containerId), 204)).containers();
		// Then:
		httpDockerContainers.stop(containerId);
		verify(response, times(1)).close();
	}

	@Test
	@DisplayName("A non valid container ID is stopped so an exception is thrown")
	public void stoppingNonValidContainerIdTest() throws DockerException {
		// Given:
		String containerId = "123asdasdsd";
		DockerContainers httpDockerContainers = new HttpDocker(createMockClient(
				String.format("/containers/%s/stop", containerId), 404)).containers();
		// Then:
		assertThrows(DockerException.class, () -> httpDockerContainers.stop(containerId));
		verify(response, times(1)).close();
	}

	@Test
	@DisplayName("Pruning stopped containers finishes correctly")
	public void pruningStoppedContainersFinishesCorrectlyTest() throws DockerException {
		// Given:
		DockerContainers httpDockerContainers = new HttpDocker(createMockClient("/containers/prune", 200)).containers();
		// Then:
		httpDockerContainers.prune();
		verify(response, times(1)).close();
	}

	@Test
	@DisplayName("Pruning stopped containers finishes incorrectly so an exception is thrown")
	public void pruningStoppedContainersFinishesIncorrectlySoExceptionIsThrownTest() throws DockerException {
		// Given:
		DockerContainers httpDockerContainers = new HttpDocker(createMockClient("/containers/prune", 500)).containers();
		// Then:
		assertThrows(DockerException.class, httpDockerContainers::prune);
		verify(response, times(1)).close();
	}

	@Test
	@DisplayName("Executing a command in a valid container ID return exec ID")
	public void executingCommandInValidContainerIdReturnsExecIdTest() throws DockerException {
		// Given:
		String containerId = "123askdhads";
		DockerContainers httpDockerContainers = new HttpDocker(createMockClient(
				String.format("/containers/%s/exec", containerId), 201)).containers();
		// Then:
		assertEquals("123", httpDockerContainers.exec(containerId, "top"));
		verify(response, times(1)).close();
	}

	@Test
	@DisplayName("Executing a command in a non valid container ID throws an exception")
	public void executingCommandInNonValidContainerIdThrowsExceptionTest() throws DockerException {
		// Given:
		String containerId = "123asdadssad";
		DockerContainers httpDockerContainers = new HttpDocker(createMockClient(
				String.format("/containers/%s/exec", containerId), 404)).containers();
		// Then:
		assertThrows(DockerException.class, () -> httpDockerContainers.exec(containerId, "top"));
		verify(response, times(1)).close();
	}

	@Test
	@DisplayName("Removing a valid container returns ok")
	public void removingAValidContainerReturnsOkTest() throws DockerException {
		// Given:
		String containerId = "123askdhads";
		DockerContainers httpDockerContainers = new HttpDocker(createMockClient(
				String.format("/containers/%s", containerId), 204)).containers();
		// Then:
		httpDockerContainers.remove(containerId);
		verify(response, times(1)).close();
	}

	@Test
	@DisplayName("Removing a non valid container throws an exception")
	public void removingAValidContainerThrowsAnExceptionTest() throws DockerException {
		// Given:
		String containerId = "123asdadssad";
		DockerContainers httpDockerContainers = new HttpDocker(createMockClient(
				String.format("/containers/%s", containerId), 400)).containers();
		// Then:
		assertThrows(DockerException.class, () -> httpDockerContainers.remove(containerId));
		verify(response, times(1)).close();
	}
	
	private DockerHttpClient createMockClient(String endpoint, int statusCode) throws DockerException {
		DockerHttpClient client = mock(DockerHttpClient.class);
		this.response = mock(DockerHttpDockerResponse.class);
		when(response.getStatusCode()).thenReturn(statusCode);
		when(response.getContent()).thenReturn(new BufferedReader(new StringReader("{\"Id\":\"123\"}")));
		when(client.delete(eq(endpoint), anyList())).thenReturn(response);
		when(client.post(eq(endpoint))).thenReturn(response);
		when(client.post(eq(endpoint), any(ObjectNode.class))).thenReturn(response);
		when(client.post(eq(endpoint), anyList(), any(ObjectNode.class))).thenReturn(response);
		return client;
	}


}
