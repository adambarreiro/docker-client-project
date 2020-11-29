package com.adambarreiro.docker.app.controller;

import com.adambarreiro.docker.app.service.DockerTopService;
import com.adambarreiro.docker.client.exception.DockerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import javax.annotation.PreDestroy;

/**
 * Websocket controller that executes a service that runs a top shell command
 * in a container and updates the results through that socket.
 */
@Controller
public class StatsWebsocketController {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatsWebsocketController.class);

	private final DockerTopService dockerTopService;

	@Autowired
	StatsWebsocketController(final DockerTopService dockerTopService) {
		this.dockerTopService = dockerTopService;
	}

	@MessageMapping("/start")
	public void startSendingStats(String dockerImage) {
		try {
			this.dockerTopService.start("/docker/stats", dockerImage);
		} catch (DockerException e) {
			LOGGER.error("An error occurred!", e);
		}
	}

	@MessageMapping("/stop")
	public void stop() {
		try {
			this.dockerTopService.stop("/docker/stopped");
		} catch (DockerException e) {
			LOGGER.error("An error occurred!", e);
		}
	}

	@PreDestroy
	public void onDestroy() {
		LOGGER.info("Cleaning up...");
		this.stop();
	}
}
