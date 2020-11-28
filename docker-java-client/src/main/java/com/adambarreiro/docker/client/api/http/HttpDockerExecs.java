package com.adambarreiro.docker.client.api.http;

import com.adambarreiro.docker.client.api.DockerExecs;
import com.adambarreiro.docker.client.exception.DockerException;
import com.adambarreiro.docker.client.http.client.DockerHttpClient;
import com.adambarreiro.docker.client.http.DockerHttpDockerResponse;
import com.adambarreiro.docker.client.api.DockerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpStatus;

/**
 * Run new commands inside running containers through the Docker REST API.
 * Refer to the command-line reference for more information:
 * https://docs.docker.com/engine/reference/commandline/exec/
 *
 * To exec a command in a container, you first need to create an exec instance, then start it.
 */
public class HttpDockerExecs implements DockerExecs {

	private final DockerHttpClient dockerHttpClient;
	private final ObjectMapper objectMapper;

	public HttpDockerExecs(final DockerHttpClient dockerHttpClient) {
		this.dockerHttpClient = dockerHttpClient;
		this.objectMapper = new ObjectMapper();
	}

	/**
	 * {@inheritDoc}
	 *
	 * After you finish the execution, you need to execute {@link DockerHttpDockerResponse#close()}.
	 */
	@Override
	public DockerResponse startInteractive(final String executionId) throws DockerException {
		ObjectNode jsonBody = this.objectMapper.createObjectNode()
				.put("Detach", false)
				.put("Tty", true);

		return this.start(executionId, jsonBody);
	}

	/**
	 * {@inheritDoc}
	 *
	 * After you finish the execution, if you ran the exec in attached mode,
	 * you need to execute {@link DockerHttpDockerResponse#close()}.
	 */
	@Override
	public DockerResponse start(final String executionId, final ObjectNode body) throws DockerException {
		DockerHttpDockerResponse response = this.dockerHttpClient.post("/exec/".concat(executionId).concat("/start"), body);
		switch(response.getStatusCode()) {
			case HttpStatus.SC_NOT_FOUND:
				response.close();
				throw new DockerException(String.format("Error starting exec with ID %s, no such exec instance.", executionId));
			case HttpStatus.SC_CONFLICT:
				response.close();
				throw new DockerException(String.format("Error starting exec with ID %s, container is stopped or paused.", executionId));
			default:
				return response;
		}
	}
}
