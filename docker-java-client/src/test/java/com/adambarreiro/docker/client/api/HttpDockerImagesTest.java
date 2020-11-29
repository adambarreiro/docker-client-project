package com.adambarreiro.docker.client.api;

import com.adambarreiro.docker.client.api.http.HttpDocker;
import com.adambarreiro.docker.client.exception.DockerException;
import com.adambarreiro.docker.client.http.DockerHttpDockerResponse;
import com.adambarreiro.docker.client.http.client.DockerHttpClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpDockerImagesTest {

	private DockerHttpDockerResponse response;

	@Test
	@DisplayName("Existent image is pulled correctly")
	public void existentImageIsPulledCorrectlyTest() throws DockerException {
		// Given:
		DockerImages httpDockerImages = new HttpDocker(createMockClient("/images/create", 200)).images();

		// Then:
		httpDockerImages.pull("ubuntu","latest");
		verify(response, times(1)).close();
	}

	@Test
	@DisplayName("Non existent image is pulled and exception is thrown")
	public void nonExistentImageIsPulledAndExceptionIsThrown() throws DockerException {
		int[] statusCodes = {404, 500};
		for (int statusCode : statusCodes) {
			// Given:
			DockerImages httpDockerImages = new HttpDocker(createMockClient("/images/create", statusCode)).images();

			// Then:
			assertThrows(DockerException.class, () -> httpDockerImages.pull("ubuntu", "latest"));
			verify(response, times(1)).close();
		}
	}

	private DockerHttpClient createMockClient(String endpoint, int statusCode) throws DockerException {
		DockerHttpClient client = mock(DockerHttpClient.class);
		this.response = mock(DockerHttpDockerResponse.class);
		when(response.getStatusCode()).thenReturn(statusCode);
		when(client.post(eq(endpoint), anyList())).thenReturn(response);
		return client;
	}


}
