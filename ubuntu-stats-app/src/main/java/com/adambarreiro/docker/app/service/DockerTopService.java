package com.adambarreiro.docker.app.service;

import com.adambarreiro.docker.client.exception.DockerException;

public interface DockerTopService {

	void start(String socketEndpoint, String dockerImage) throws DockerException;

	void stop(String socketEndpoint) throws DockerException;
}
