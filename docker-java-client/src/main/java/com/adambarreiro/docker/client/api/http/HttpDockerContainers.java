package com.adambarreiro.docker.client.api.http;

import com.adambarreiro.docker.client.api.DockerContainers;
import com.adambarreiro.docker.client.exception.DockerException;
import com.adambarreiro.docker.client.http.client.DockerHttpClient;
import com.adambarreiro.docker.client.http.DockerHttpDockerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Create and manage containers with the Docker REST API.
 */
public class HttpDockerContainers implements DockerContainers {

	private static final String BASE_PATH = "/containers";


	private final DockerHttpClient dockerHttpClient;
	private final ObjectMapper jsonBuilder;

	public HttpDockerContainers(DockerHttpClient dockerHttpClient) {
		this.dockerHttpClient = dockerHttpClient;
		this.jsonBuilder = new ObjectMapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String create(final String name, final String image) throws DockerException {
		List<NameValuePair> queryParams = new ArrayList<>();
		if (name != null && VALID_CONTAINER_NAME.matcher(name).matches()) {
			queryParams.add(new BasicNameValuePair("name", name));
		}
		ObjectNode jsonBody = jsonBuilder.createObjectNode()
				.put("Image", image)
				.put("Tty", true)
				.put("OpenStdin", true);

		DockerHttpDockerResponse response = this.dockerHttpClient.post(BASE_PATH.concat("/create"), queryParams, jsonBody);
		try (response) {
			switch(response.getStatusCode()) {
				case HttpStatus.SC_CREATED: return jsonBuilder.readTree(response.getContent()).get("Id").asText();
				case HttpStatus.SC_BAD_REQUEST:
					throw new DockerException(String.format("Error creating container %s with image %s, wrong parameters.", name, image));
				case HttpStatus.SC_NOT_FOUND:
					throw new DockerException(String.format("Error creating container %s with image %s, no such container", name, image));
				case HttpStatus.SC_CONFLICT:
					throw new DockerException(String.format("Error creating container %s with image %s, container already exists", name, image));
				default:
					throw new DockerException(
							String.format("Error creating container %s with image %s, unexpected error %s", name, image, response.getStatusCode()));
			}
		} catch (IOException e) {
			throw new DockerException("Couldn't get response content", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final String containerId) throws DockerException {
		DockerHttpDockerResponse response = this.dockerHttpClient.post(BASE_PATH.concat("/").concat(containerId).concat("/start"));
		try(response) {
			switch(response.getStatusCode()) {
				case HttpStatus.SC_NOT_FOUND:
					throw new DockerException(String.format("Error starting the container %s, no such container", containerId));
				case HttpStatus.SC_INTERNAL_SERVER_ERROR:
					throw new DockerException(String.format("Error starting the container %s, unexpected error", containerId));
				default:
					// Wait until completion.
					response.readAllContent();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String exec(final String containerId, final String command) throws DockerException {
		ObjectNode jsonBody = this.jsonBuilder.createObjectNode().put("AttachStdin",false)
				.put("AttachStdout",true).put("AttachStderr",false)
				.put("Tty", true).set("Cmd", jsonBuilder.createArrayNode().add(command));

		DockerHttpDockerResponse response = this.dockerHttpClient.post(BASE_PATH.concat("/").concat(containerId).concat("/exec"), jsonBody);
		try (response) {
			switch(response.getStatusCode()) {
				case HttpStatus.SC_CREATED: return this.jsonBuilder.readTree(response.getContent()).get("Id").asText();
				case HttpStatus.SC_NOT_FOUND:
					throw new DockerException(
							String.format("Error creating an execution for container %s, no such container", containerId));
				case HttpStatus.SC_CONFLICT:
					throw new DockerException(
							String.format("Error creating an execution for container %s, it is paused", containerId));
				default:
					throw new DockerException(
							String.format("Error creating an execution for container %s, unexpected error %s", containerId, response.getStatusCode()));
			}
		} catch (IOException e) {
			throw new DockerException("Expected a field \"Id\" in the response", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(final String containerId) throws DockerException {
		DockerHttpDockerResponse response = this.dockerHttpClient.post(BASE_PATH.concat("/").concat(containerId).concat("/stop"));
		try(response) {
			switch(response.getStatusCode()) {
				case HttpStatus.SC_NOT_FOUND:
					throw new DockerException(String.format("Error stopping container %s, no such container", containerId));
				case HttpStatus.SC_INTERNAL_SERVER_ERROR:
					throw new DockerException(String.format("Error stopping container %s, unexpected error", containerId));
				default:
					// Wait until completion
					response.readAllContent();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(final String containerId) throws DockerException {
		List<NameValuePair> queryParams = new ArrayList<>();
		queryParams.add(new BasicNameValuePair("force", "true"));

		DockerHttpDockerResponse response = this.dockerHttpClient.delete(BASE_PATH.concat("/").concat(containerId), queryParams);
		try(response) {
			switch(response.getStatusCode()) {
				case HttpStatus.SC_BAD_REQUEST:
					throw new DockerException(String.format("Error creating an execution for container %s, parameters are wrong", containerId));
				case HttpStatus.SC_NOT_FOUND:
					throw new DockerException(String.format("Error creating an execution for container %s, no such container", containerId));
				case HttpStatus.SC_CONFLICT:
					throw new DockerException(String.format("Error creating an execution for container %s, it is paused", containerId));
				case HttpStatus.SC_INTERNAL_SERVER_ERROR:
					throw new DockerException(String.format("Error creating an execution for container %s, unexpected error", containerId));
				default:
					// Wait until completion
					response.readAllContent();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * @throws DockerException
	 */
	@Override
	public void prune() throws DockerException {
		DockerHttpDockerResponse response = this.dockerHttpClient.post(BASE_PATH.concat("/prune"));
		try(response) {
			if (response.getStatusCode() != HttpStatus.SC_OK) {
				throw new DockerException("Error removing all containers");
			}
			// Wait until completion
			response.readAllContent();
		}
	}

}
