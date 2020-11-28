package com.adambarreiro.docker.client;

import com.adambarreiro.docker.client.actions.Containers;
import com.adambarreiro.docker.client.actions.Execs;
import com.adambarreiro.docker.client.actions.Images;
import com.adambarreiro.docker.client.http.DockerHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public final class Docker {

	private final DockerHttpClient dockerHttpClient;
	private final Environment environment;

	@Autowired
	public Docker(final DockerHttpClient dockerHttpClient, final Environment environment) {
		this.dockerHttpClient = dockerHttpClient;
		this.environment = environment;
	}

	public Images images() {
		return new Images(dockerHttpClient);
	}

	public Containers containers() {
		return new Containers(dockerHttpClient);
	}

	public Execs execs() {
		return new Execs(dockerHttpClient);
	}

}
