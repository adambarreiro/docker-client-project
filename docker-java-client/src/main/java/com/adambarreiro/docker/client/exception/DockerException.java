package com.adambarreiro.docker.client.exception;

/**
 * Simple exception wrapper for the Docker client.
 */
public class DockerException extends Exception {

	public DockerException(final String message) {
		super(message);
	}

	public DockerException(final String message, final Throwable e) {
		super(message, e);
	}

}
