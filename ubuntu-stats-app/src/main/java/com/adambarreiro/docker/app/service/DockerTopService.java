package com.adambarreiro.docker.app.service;

import com.adambarreiro.docker.client.exception.DockerException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public interface DockerTopService {

	void start(SimpMessagingTemplate template, String socketEndpoint) throws DockerException;

	void stop(SimpMessagingTemplate template, String socketEndpoint) throws DockerException;
}
