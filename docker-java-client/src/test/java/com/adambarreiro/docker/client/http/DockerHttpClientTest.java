package com.adambarreiro.docker.client.http;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DockerHttpClientTest {

	private DockerHttpClient dockerHttpClient;
	private Environment environment;
	private HttpResponse expectedResponse;

	@BeforeEach
	public void setup() {
		this.environment = mock(Environment.class);
		when(this.environment.getProperty(eq("docker.api.version"), anyString())).thenReturn("1.40");
	}

	@Test
	@DisplayName("A valid POST request returns valid response")
	public void validPostRequestReturnsValidResponseTest() throws IOException, URISyntaxException {
		// Given:
		HttpClient httpClient = createMockHttpClient(false);
		DockerHttpClient dockerHttpClient = new DockerHttpClient(httpClient, environment);

		// When:
		HttpResponse actualResponse = dockerHttpClient.post("/images/create");

		// Then:
		verify(httpClient, times(1)).execute(any(HttpPost.class));
		assertEquals(this.expectedResponse,actualResponse);
	}

	@Test
	@DisplayName("A valid POST request with parameters returns valid response")
	public void validPostRequestWithParametersReturnsValidResponseTest() throws IOException, URISyntaxException {
		// Given:
		HttpClient httpClient = createMockHttpClient(false);
		DockerHttpClient dockerHttpClient = new DockerHttpClient(httpClient, environment);
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("fromImage", "ubuntu"));
		params.add(new BasicNameValuePair("tag", "latest"));

		// When:
		HttpResponse actualResponse = dockerHttpClient.post("/images/create", params);

		// Then:
		verify(httpClient, times(1)).execute(any(HttpPost.class));
		assertEquals(this.expectedResponse,actualResponse);
	}

	@Test
	@DisplayName("A non valid POST request returns an empty response")
	public void noValidPostRequestReturnsValidResponseTest() throws IOException {
		// Given:
		HttpClient httpClient = createMockHttpClient(true);
		dockerHttpClient = new DockerHttpClient(httpClient, environment);

		// Then:
		assertThrows(IOException.class, () -> dockerHttpClient.post("/images/create"));
	}

	@Test
	@DisplayName("A valid GET request returns valid response")
	public void validGetRequestReturnsValidResponseTest() throws IOException, URISyntaxException {
		// Given:
		HttpClient httpClient = createMockHttpClient(false);
		DockerHttpClient dockerHttpClient = new DockerHttpClient(httpClient, environment);

		// When:
		HttpResponse actualResponse = dockerHttpClient.get("/images/create");

		// Then:
		verify(httpClient, times(1)).execute(any(HttpGet.class));
		assertEquals(this.expectedResponse,actualResponse);
	}

	@Test
	@DisplayName("A non valid GET request returns an empty response")
	public void noValidGetRequestReturnsValidResponseTest() throws IOException {
		// Given:
		HttpClient httpClient = createMockHttpClient(true);
		dockerHttpClient = new DockerHttpClient(httpClient, environment);

		// Then:
		assertThrows(IOException.class, () -> dockerHttpClient.get("/images/create"));
	}

	private HttpClient createMockHttpClient(boolean throwException) throws IOException {
		HttpClient httpClient = mock(HttpClient.class);
		this.expectedResponse = mock(HttpResponse.class);

		if (throwException) {
			when(httpClient.execute(any(HttpPost.class))).thenThrow(IOException.class);
			when(httpClient.execute(any(HttpGet.class))).thenThrow(IOException.class);
		} else {
			when(httpClient.execute(any(HttpPost.class))).thenReturn(this.expectedResponse);
			when(httpClient.execute(any(HttpGet.class))).thenReturn(this.expectedResponse);
		}

		return httpClient;
	}
}
