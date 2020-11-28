package com.adambarreiro.docker.client.api;

import com.adambarreiro.docker.client.exception.DockerException;

import java.util.regex.Pattern;

/**
 * Create and manage containers.
 */
public interface DockerContainers {

	Pattern VALID_CONTAINER_NAME = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_.-]+$");

	/**
	 * Creates a container with given name and image.
	 *
	 * @param name name of the container. Must match {@see VALID_CONTAINER_NAME}, otherwise a default one will be picked.
	 * @param image image name with tag. For example: 'ubuntu:latest'.
	 * @return the ID of the created container.
	 * @throws DockerException if something went wrong.
	 */
	String create(String name, String image) throws DockerException;

	/**
	 * Starts a container with given ID.
	 *
	 * @param containerId ID of the previously created container.
	 * @throws DockerException if something went wrong.
	 */
	void start(String containerId) throws DockerException;

	/**
	 * Creates an exec in the given container.
	 *
	 * @param containerId the ID of the container.
	 * @param command the command to run.
	 * @return the ID of the created exec.
	 * @throws DockerException if something went wrong.
	 */
	String exec(String containerId, String command) throws DockerException;

	/**
	 * Stops a running container.
	 *
	 * @param containerId the ID of the container.
	 * @throws DockerException if something went wrong.
	 */
	void stop(String containerId) throws DockerException;

	/**
	 * Deletes a stopped container.
	 *
	 * @param containerId the ID of the container.
	 * @throws DockerException if something went wrong.
	 */
	void remove(String containerId) throws DockerException;

	/**
	 * Removes all stopped containers. Take into account that they need to be stopped first.
	 *
	 * @throws DockerException if something went wrong.
	 */
	void prune() throws DockerException;
}
