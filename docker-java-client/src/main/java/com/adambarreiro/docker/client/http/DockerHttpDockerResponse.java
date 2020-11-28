package com.adambarreiro.docker.client.http;

import com.adambarreiro.docker.client.api.DockerResponse;
import com.adambarreiro.docker.client.exception.DockerException;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 * Wrapper for the HTTP response we get from the HTTP client. Here we have everything we need,
 * the status code, the stream of content (this is useful when we run a container in attached mode) and
 * the original request to be able to close it afterwards (we implement {@link AutoCloseable} to do it automatically
 * if using try-with-resources).
 */
public class DockerHttpDockerResponse implements DockerResponse, AutoCloseable {

	private final int statusCode;
	private final BufferedReader content;
	private final HttpRequestBase request;

	public DockerHttpDockerResponse(final int statusCode, final InputStream content, final HttpRequestBase request) {
		this.statusCode = statusCode;
		this.content = new BufferedReader(new InputStreamReader(content, StandardCharsets.UTF_8));;
		this.request = request;
	}

	public DockerHttpDockerResponse(final int statusCode, final HttpRequestBase request) {
		this.statusCode = statusCode;
		this.content = new BufferedReader(new StringReader(""));
		this.request = request;
	}

	/**
	 * Gets the HTTP response code.
	 * @return the HTTP response code.
	 */
	public int getStatusCode() {
		return this.statusCode;
	}

	/**
	 * Gets the HTTP body contents.
	 * @return the HTTP body contents.
	 */
	@Override
	public BufferedReader getContent() {
		return this.content;
	}

	/**
	 * Reads the full contents of the HTTP body. This call is blocking and requires a limited
	 * amount of data in the body (don't call this with containers in attached mode).
	 * @return the full content of the response.
	 */
	public String readAllContent() throws DockerException {
		String line = "";
		StringBuilder stringBuilder = new StringBuilder();
		try {
			while ((line = this.content.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (IOException e) {
			throw new DockerException("Couldn't read the stream of the Docker response", e);
		}
		return stringBuilder.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws DockerException if the stream cannot be closed.
	 */
	@Override
	public void close() throws DockerException {
		try {
			this.content.close();
		} catch (IOException e) {
			throw new DockerException("Couldn't close the stream of the Docker response", e);
		} finally {
			this.request.releaseConnection();
		}
	}
}
