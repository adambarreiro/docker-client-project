package com.adambarreiro.docker.client.http.client.apache;

import com.adambarreiro.docker.client.exception.DockerException;
import com.adambarreiro.docker.client.http.DockerHttpDockerResponse;
import com.adambarreiro.docker.client.http.client.DockerHttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the required HTTP methods to interact with Docker API using the Apache HTTP client, {@link HttpClient}.
 */
@Component
public final class ApacheDockerHttpClient implements DockerHttpClient {

	private static final String DOCKER_API_VERSION_PROPERTY = "docker.api.version";
	private static final String DOCKER_API_VERSION_DEFAULT_VALUE = "1.40";

	private final HttpClient httpClient;
	private final Environment environment;
	private final ObjectMapper jsonBuilder;

	private URI baseUri;

	@Autowired
	public ApacheDockerHttpClient(final HttpClient httpClient, final Environment environment) {
		this.httpClient = httpClient;
		this.environment = environment;
		this.jsonBuilder = new ObjectMapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DockerHttpDockerResponse get(final String path) throws DockerException {
		return this.get(path, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DockerHttpDockerResponse get(final String path, final List<NameValuePair> queryParams) throws DockerException {
		final HttpGet httpGet = new HttpGet(this.getUriWithParams(path, queryParams));
		try {
			return this.toDockerResponse(httpGet, this.httpClient.execute(httpGet));
		} catch (Exception e) {
			throw new DockerException(String.format("Couldn't GET to %s", httpGet.getRequestLine().getUri()), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DockerHttpDockerResponse post(final String path) throws DockerException {
		return this.post(path, this.jsonBuilder.createObjectNode());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DockerHttpDockerResponse post(final String path, final List<NameValuePair> queryParams) throws DockerException {
		return this.post(path, queryParams, this.jsonBuilder.createObjectNode());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DockerHttpDockerResponse post(final String path, final ObjectNode jsonBody) throws DockerException {
		return this.post(path, new ArrayList<>(), jsonBody);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DockerHttpDockerResponse post(final String path, final List<NameValuePair> queryParams, final ObjectNode jsonBody) throws DockerException {
		final HttpPost httpPost = new HttpPost(this.getUriWithParams(path, queryParams));
		httpPost.addHeader(HttpHeaders.USER_AGENT, "application/json");
		try {
			httpPost.setEntity(new StringEntity(this.jsonBuilder.writer().writeValueAsString(jsonBody),
					ContentType.APPLICATION_JSON));
			return this.toDockerResponse(httpPost, this.httpClient.execute(httpPost));
		} catch (Exception e) {
			throw new DockerException(String.format("Couldn't POST to %s", httpPost.getRequestLine().getUri()), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DockerHttpDockerResponse delete(final String path) throws DockerException {
		return this.delete(path, new ArrayList<>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DockerHttpDockerResponse delete(final String path, final List<NameValuePair> queryParams) throws DockerException {
		final HttpDelete httpDelete = new HttpDelete(this.getUriWithParams(path, queryParams));
		try {
			return this.toDockerResponse(httpDelete, this.httpClient.execute(httpDelete));
		} catch (Exception e) {
			throw new DockerException(String.format("Couldn't DELETE to %s", httpDelete.getRequestLine().getUri()), e);
		}
	}


	/**
	 * Given an API resource path and some query parameters, builds the full URI. For example, for the resource
	 * "/images/create" and parameters "image: ubuntu, tag: latest" it will create the URI:
	 * "unix://localhost:80/v1.57/images/create?image=ubuntu&tag=latest"
	 *
	 * @param path resource path from the Docker API.
	 * @param queryParams query parameters for that specific resource.
	 * @return the full built URI.
	 * @throws DockerException if the configured URI and the given resource path is invalid.
	 */
	private URI getUriWithParams(final String path, final List<NameValuePair> queryParams) throws DockerException {
		try {
			final URIBuilder uriBuilder = new URIBuilder(this.getBaseUri().concat(path));
			queryParams.forEach(queryParam ->
					uriBuilder.addParameter(queryParam.getName(), queryParam.getValue())
			);
			return uriBuilder.build();
		} catch (URISyntaxException e) {
			throw new DockerException(String.format("Couldn't create URI for %s", path), e);
		}
	}

	/**
	 * Retrieves the base Docker API URI from environment configuration, or picks the default one.
	 * @return the base Docker API URI.
	 */
	private String getBaseUri() {
		if (this.baseUri == null) {
			final String apiVersion = this.environment.getProperty(DOCKER_API_VERSION_PROPERTY, DOCKER_API_VERSION_DEFAULT_VALUE);
			this.baseUri = URI.create("unix://localhost:80/v" + apiVersion);
		}
		return this.baseUri.toString();
	}

	/**
	 * Creates a valid Docker response object to ease response manipulation.
	 *
	 * @param request the original request that we performed against the Docker API.
	 * @param httpResponse the response we got from Docker API.
	 * @return a Docker response object wrapping the info we need.
	 * @throws DockerException if something wrong happens.
	 */
	private DockerHttpDockerResponse toDockerResponse(final HttpRequestBase request, final HttpResponse httpResponse) throws DockerException {
		try {
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				return new DockerHttpDockerResponse(httpResponse.getStatusLine().getStatusCode(), entity.getContent(), request);
			}
			return new DockerHttpDockerResponse(httpResponse.getStatusLine().getStatusCode(), request);
		} catch (IOException e) {
			throw new DockerException("Couldn't get the response contents", e);
		}
	}

}
