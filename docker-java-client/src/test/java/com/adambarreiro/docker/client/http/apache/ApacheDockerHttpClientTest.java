package com.adambarreiro.docker.client.http.apache;

import com.adambarreiro.docker.client.exception.DockerException;
import com.adambarreiro.docker.client.http.DockerHttpDockerResponse;
import com.adambarreiro.docker.client.http.client.apache.ApacheDockerHttpClient;
import com.adambarreiro.docker.client.http.client.DockerHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStream;
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

public class ApacheDockerHttpClientTest {

	private Environment environment;

	@BeforeEach
	public void setup() {
		this.environment = mock(Environment.class);
		when(this.environment.getProperty(eq("docker.api.version"), anyString())).thenReturn("1.40");
	}

	@Test
	@DisplayName("A valid POST request returns valid response")
	public void validPostRequestReturnsValidResponseTest() throws DockerException, IOException {
		// Given:
		HttpResponse expectedResponse = createMockHttpResponse();
		HttpClient httpClient = createMockHttpClient(expectedResponse);
		DockerHttpClient dockerHttpClient = new ApacheDockerHttpClient(httpClient, environment);

		// When:
		DockerHttpDockerResponse actualResponse = dockerHttpClient.post("/images/create");

		// Then:
		verify(httpClient, times(1)).execute(any(HttpPost.class));
		assertEquals(expectedResponse.getStatusLine().getStatusCode(), actualResponse.getStatusCode());
	}

	@Test
	@DisplayName("A valid POST request with parameters returns valid response")
	public void validPostRequestWithParametersReturnsValidResponseTest() throws DockerException, IOException {
		// Given:
		HttpResponse expectedResponse = createMockHttpResponse();
		HttpClient httpClient = createMockHttpClient(expectedResponse, false);
		DockerHttpClient dockerApacheHttpClient = new ApacheDockerHttpClient(httpClient, environment);
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("fromImage", "ubuntu"));
		params.add(new BasicNameValuePair("tag", "latest"));

		// When:
		DockerHttpDockerResponse actualResponse = dockerApacheHttpClient.post("/images/create", params);

		// Then:
		verify(httpClient, times(1)).execute(any(HttpPost.class));
		assertEquals(expectedResponse.getStatusLine().getStatusCode(), actualResponse.getStatusCode());
	}

	@Test
	@DisplayName("A non valid POST request returns an empty response")
	public void noValidPostRequestReturnsValidResponseTest() throws IOException {
		// Given:
		HttpResponse expectedResponse = createMockHttpResponse();
		HttpClient httpClient = createMockHttpClient(expectedResponse, true);
		DockerHttpClient dockerApacheHttpClient = new ApacheDockerHttpClient(httpClient, environment);

		// Then:
		assertThrows(DockerException.class, () -> dockerApacheHttpClient.post("/images/create"));
	}

	@Test
	@DisplayName("A valid GET request returns valid response")
	public void validGetRequestReturnsValidResponseTest() throws IOException, DockerException {
		// Given:
		HttpResponse expectedResponse = createMockHttpResponse();
		HttpClient httpClient = createMockHttpClient(expectedResponse);
		DockerHttpClient dockerApacheHttpClient = new ApacheDockerHttpClient(httpClient, environment);

		// When:
		DockerHttpDockerResponse actualResponse = dockerApacheHttpClient.get("/images/create");

		// Then:
		verify(httpClient, times(1)).execute(any(HttpGet.class));
		assertEquals(expectedResponse.getStatusLine().getStatusCode(), actualResponse.getStatusCode());
	}

	@Test
	@DisplayName("A non valid GET request returns an empty response")
	public void noValidGetRequestReturnsValidResponseTest() throws IOException {
		// Given:
		HttpResponse expectedResponse = createMockHttpResponse();
		HttpClient httpClient = createMockHttpClient(expectedResponse, true);
		DockerHttpClient dockerHttpClient = new ApacheDockerHttpClient(httpClient, environment);

		// Then:
		assertThrows(DockerException.class, () -> dockerHttpClient.get("/images/create"));
	}

	@Test
	@DisplayName("A valid DELETE request returns valid response")
	public void validDeleteRequestReturnsValidResponseTest() throws IOException, DockerException {
		// Given:
		HttpResponse expectedResponse = createMockHttpResponse();
		HttpClient httpClient = createMockHttpClient(expectedResponse);
		DockerHttpClient dockerApacheHttpClient = new ApacheDockerHttpClient(httpClient, environment);

		// When:
		DockerHttpDockerResponse actualResponse = dockerApacheHttpClient.delete("/containers/123");

		// Then:
		verify(httpClient, times(1)).execute(any(HttpDelete.class));
		assertEquals(expectedResponse.getStatusLine().getStatusCode(), actualResponse.getStatusCode());
	}

	@Test
	@DisplayName("A non valid DELETE request returns an empty response")
	public void noValidDeleteRequestReturnsValidResponseTest() throws IOException {
		// Given:
		HttpResponse expectedResponse = createMockHttpResponse();
		HttpClient httpClient = createMockHttpClient(expectedResponse, true);
		DockerHttpClient dockerHttpClient = new ApacheDockerHttpClient(httpClient, environment);

		// Then:
		assertThrows(DockerException.class, () -> dockerHttpClient.delete("/containers/123"));
	}

	@Test
	@DisplayName("A non valid path in the request throws an exception")
	public void nonValidPathInRequestThrowsExceptionTest() throws IOException {
		// Given:
		HttpResponse expectedResponse = createMockHttpResponse();
		HttpClient httpClient = createMockHttpClient(expectedResponse);
		DockerHttpClient dockerHttpClient = new ApacheDockerHttpClient(httpClient, environment);

		// Then:
		assertThrows(DockerException.class, () -> dockerHttpClient.get("^"));
	}

	private HttpClient createMockHttpClient(HttpResponse expectedResponse) throws IOException {
		return createMockHttpClient(expectedResponse, false);
	}

	private HttpClient createMockHttpClient(HttpResponse expectedResponse, boolean throwException) throws IOException {
		HttpClient httpClient = mock(HttpClient.class);
		if (throwException) {
			when(httpClient.execute(any(HttpPost.class))).thenThrow(IOException.class);
			when(httpClient.execute(any(HttpGet.class))).thenThrow(IOException.class);
			when(httpClient.execute(any(HttpDelete.class))).thenThrow(IOException.class);
		} else {
			when(httpClient.execute(any(HttpPost.class))).thenReturn(expectedResponse);
			when(httpClient.execute(any(HttpGet.class))).thenReturn(expectedResponse);
			when(httpClient.execute(any(HttpDelete.class))).thenReturn(expectedResponse);
		}

		return httpClient;
	}

	private HttpResponse createMockHttpResponse() throws IOException {
		HttpResponse httpResponse = mock(HttpResponse.class);
		HttpEntity httpEntity = mock(HttpEntity.class);
		InputStream inputStream = mock(InputStream.class);
		StatusLine statusLine = mock(StatusLine.class);

		when(statusLine.getStatusCode()).thenReturn(200);
		when(httpEntity.getContent()).thenReturn(inputStream);
		when(httpResponse.getEntity()).thenReturn(httpEntity);
		when(httpResponse.getStatusLine()).thenReturn(statusLine);

		return httpResponse;
	}
}
