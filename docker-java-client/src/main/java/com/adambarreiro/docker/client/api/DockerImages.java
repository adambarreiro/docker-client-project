package com.adambarreiro.docker.client.api;

import com.adambarreiro.docker.client.exception.DockerException;

/**
 * Manipulates Docker images.
 */
public interface DockerImages {

	/**
	 * Pulls an image from the public registry, if it's not present locally.
	 *
	 * @param name name of the image to pull.
	 * @param tag tag of the image to pull.
	 * @throws DockerException if something went wrong.
	 */
	void pull(String name, String tag) throws DockerException;

}
