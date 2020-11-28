package com.adambarreiro.docker.client.http;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class wraps the Apache HTTP Client to make calls to the Docker API with ease.
 */
@Component
final public class DockerHttpClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(DockerHttpClient.class);
	private static final String DOCKER_API_VERSION_PROPERTY = "docker.api.version";
	private static final String DOCKER_API_VERSION_DEFAULT_VALUE = "1.40";

	private final HttpClient httpClient;
	private final Environment environment;

	private URI baseUri;

	@Autowired
	public DockerHttpClient(final HttpClient httpClient, final Environment environment) {
		this.httpClient = httpClient;
		this.environment = environment;
	}

	/**
	 * Performs a GET call to the Docker API to the specified path.
	 *
	 * @param path resource path from the Docker API.
	 * @return A {@link HttpResponse} if the call succeeded.
	 */
	public HttpResponse get(String path) throws IOException, URISyntaxException {
		return this.get(path, new ArrayList<>());
	}

	/**
	 * Performs a GET call to the Docker API to the specified path and the given query parameters.
	 *
	 * @param path resource path from the Docker API.
	 * @param queryParams query parameters for that specific resource.
	 * @return A {@link HttpResponse} if the call succeeded.
	 */
	public HttpResponse get(String path, List<NameValuePair> queryParams) throws URISyntaxException, IOException {
		final HttpGet httpGet = new HttpGet(this.getUriWithParams(path, queryParams));
		try {
			return httpClient.execute(httpGet);
		} finally {
			// httpGet.releaseConnection();
		}
	}

	/**
	 * Performs a POST call to the Docker API to the specified path.
	 *
	 * @param path resource path from the Docker API.
	 * @return A {@link HttpResponse} if the call succeeded.
	 */
	public HttpResponse post(String path) throws IOException, URISyntaxException {
		return this.post(path, "");
	}

	/**
	 * Performs a POST call to the Docker API to the specified path and with the given query parameters.
	 *
	 * @param path resource path from the Docker API.
	 * @param queryParams query parameters for that specific resource.
	 * @return A valid {@link HttpResponse} if the call succeeded, an empty {@link Optional} if it didn't.
	 */
	public HttpResponse post(String path, List<NameValuePair> queryParams) throws IOException, URISyntaxException {
		return this.post(path, queryParams, "");
	}

	/**
	 * Performs a POST call to the Docker API to the specified path and with the given body in JSON format.
	 *
	 * @param path resource path from the Docker API.
	 * @param jsonBody body in JSON format.
	 * @return A valid {@link HttpResponse} if the call succeeded, an empty {@link Optional} if it didn't.
	 */
	public HttpResponse post(String path, String jsonBody) throws IOException, URISyntaxException {
		return this.post(path, new ArrayList<>(), jsonBody);
	}

	/**
	 * Performs a POST call to the Docker API to the specified path, the specified query parameters
	 * and with the given JSON body.
	 *
	 * @param path resource path from the Docker API.
	 * @param queryParams query parameters for that specific resource.
	 * @param jsonBody body in JSON format.
	 * @return A valid {@link HttpResponse} if the call succeeded, an empty {@link Optional} if it didn't.
	 */
	public HttpResponse post(String path, List<NameValuePair> queryParams, String jsonBody) throws URISyntaxException, IOException {
		final HttpPost httpPost = new HttpPost(this.getUriWithParams(path, queryParams));
		httpPost.addHeader(HttpHeaders.USER_AGENT, "application/json");
		if (!jsonBody.isEmpty()) {
			httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
		}
		try {
			return httpClient.execute(httpPost);
		} finally {
			//httpPost.releaseConnection();
		}
	}

	/**
	 * Given an API resource path and some query parameters, builds the full URL. For example, for the resource
	 * "/images/create" and parameters "image: ubuntu, tag: latest" it will create the URL:
	 * "/images/create?image=ubuntu&tag=latest"
	 *
	 * @param path resource path from the Docker API.
	 * @param queryParams query parameters for that specific resource.
	 * @return the full built URL.
	 * @throws URISyntaxException if the configured URI and the given resource path is invalid.
	 */
	private URI getUriWithParams(final String path, final List<NameValuePair> queryParams) throws URISyntaxException {
		final URIBuilder uriBuilder = new URIBuilder(this.getBaseUri().concat(path));
		queryParams.forEach(queryParam ->
			uriBuilder.addParameter(queryParam.getName(), queryParam.getValue())
		);
		return uriBuilder.build();
	}

	/**
	 * Retrieves the base Docker API URI from environment configuration, or picks the default one.
	 * @return the base Docker API URI.
	 */
	private String getBaseUri() {
		if (this.baseUri == null) {
			final String uri = "unix://localhost:80/v";
			final String apiVersion = this.environment.getProperty(DOCKER_API_VERSION_PROPERTY, DOCKER_API_VERSION_DEFAULT_VALUE);
			this.baseUri = URI.create(uri + apiVersion);
		}
		return this.baseUri.toString();
	}

}
