package com.adambarreiro.docker.client.api;

import com.adambarreiro.docker.client.exception.DockerException;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Run new commands inside running containers. Refer to the command-line reference for more information:
 * https://docs.docker.com/engine/reference/commandline/exec/
 *
 * To exec a command in a container, you first need to create an exec instance, then start it.
 */
public interface DockerExecs {

	/**
	 * Starts a previously set up exec instance.
	 * This call is executed with "Detach" set as "true", so it starts an interactive session with the command.
	 *
	 * @param executionId the ID of the previously created execution.
	 * @return the Docker response.
	 * @throws DockerException if something goes wrong.
	 */
	DockerResponse startInteractive(String executionId) throws DockerException;

	/**
	 * Starts a previously set up exec instance.
	 * In the given body, if "Detach" is "true", this endpoint returns immediately after starting the command.
	 * Otherwise, it sets up an interactive session with the command.
	 *
	 * @param executionId the previously set up exec instance.
	 * @param body the body parameters.
	 * @return the Docker response.
	 * @throws DockerException if something goes wrong.
	 */
	DockerResponse start(String executionId, ObjectNode body) throws DockerException;
}
