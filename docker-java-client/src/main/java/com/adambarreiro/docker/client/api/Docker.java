package com.adambarreiro.docker.client.api;

/**
 * Main client interface to interact with the Docker API.
 */
public interface Docker {

	/**
	 * Namespace for interacting with images.
	 * @return image methods.
	 */
	DockerImages images();

	/**
	 * Namespace for interacting with containers.
	 * @return container methods.
	 */
	DockerContainers containers();

	/**
	 * Namespace for interacting with executions.
	 * @return exec methods.
	 */
	DockerExecs execs();
}
