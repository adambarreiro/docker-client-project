package com.adambarreiro.docker.client.api;

import com.adambarreiro.docker.client.exception.DockerException;

import java.io.BufferedReader;

/**
 * Response from the Docker API.
 */
public interface DockerResponse {

	/**
	 * Gets the content stream from the run command.
	 *
	 * @return the stream.
	 */
	BufferedReader getContent();

	/**
	 * Closes the content stream from the run command.
	 *
	 * @throws DockerException when something goes wrong.
	 */
	void close() throws DockerException;

}
