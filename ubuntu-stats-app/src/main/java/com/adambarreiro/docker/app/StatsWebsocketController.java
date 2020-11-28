package com.adambarreiro.docker.app;

import com.adambarreiro.docker.client.Docker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Controller
public class StatsWebsocketController {

	private final SimpMessagingTemplate template;
	private final Docker docker;
	private final ExecutorService executorService;

	private Future topCommand = null;;
	private String containerId = null;
	private BufferedReader execOutput = null;

	@Autowired
	StatsWebsocketController(Docker docker, SimpMessagingTemplate template) {
		this.docker = docker;
		this.template = template;
		this.executorService = Executors.newScheduledThreadPool(1);
	}

	@MessageMapping("/start")
	public void startSendingStats() throws IOException, URISyntaxException {
		if (this.topCommand == null) {
			this.execOutput = this.execTopInUbuntu();
			this.topCommand = this.executorService.submit(() -> {
				while(true) {
					execOutput.lines().forEach(line -> template.convertAndSend("/docker/stats", line.replaceAll("[^\\u0000-\\uFFFF]", "")));
				}
			});
		}
	}

	@MessageMapping("/stop")
	public void stop() throws IOException, URISyntaxException {
		if (topCommand != null) {
			topCommand.cancel(true);
			topCommand = null;
			removeContainers();
			this.execOutput.close();
			this.execOutput = null;
		}
	}

	private BufferedReader execTopInUbuntu() throws IOException, URISyntaxException {
		docker.images().pull("ubuntu","latest");
		this.containerId = docker.containers().create("ubuntu1","ubuntu:latest").orElseThrow();
		docker.containers().start(this.containerId);
		String execId = docker.containers().exec(containerId, "top").orElseThrow();
		Optional<InputStream> is = docker.execs().start(execId);
		return new BufferedReader(new InputStreamReader(is.orElseThrow()));
	}

	private void removeContainers() throws IOException, URISyntaxException {
		docker.containers().stop(this.containerId);
		docker.containers().prune();
	}
}
