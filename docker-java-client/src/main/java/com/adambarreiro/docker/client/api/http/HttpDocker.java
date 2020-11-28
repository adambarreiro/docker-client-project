package com.adambarreiro.docker.client.api.http;

import com.adambarreiro.docker.client.api.DockerContainers;
import com.adambarreiro.docker.client.api.Docker;
import com.adambarreiro.docker.client.api.DockerExecs;
import com.adambarreiro.docker.client.api.DockerImages;
import com.adambarreiro.docker.client.http.client.DockerHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Main client to interact with the Docker API through HTTP.
 */
@Component
public class HttpDocker implements Docker {

	private final DockerHttpClient dockerHttpClient;

	@Autowired
	public HttpDocker(final DockerHttpClient dockerHttpClient) {
		this.dockerHttpClient = dockerHttpClient;
	}

	/**
	 * {@inheritDoc}
	 */
	public DockerImages images() {
		return new HttpDockerImages(dockerHttpClient);
	}

	/**
	 * {@inheritDoc}
	 */
	public DockerContainers containers() {
		return new HttpDockerContainers(dockerHttpClient);
	}

	/**
	 * {@inheritDoc}
	 */
	public DockerExecs execs() {
		return new HttpDockerExecs(dockerHttpClient);
	}

}
