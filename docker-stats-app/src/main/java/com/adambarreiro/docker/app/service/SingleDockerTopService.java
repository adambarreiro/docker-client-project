package com.adambarreiro.docker.app.service;

import com.adambarreiro.docker.client.api.Docker;
import com.adambarreiro.docker.client.api.DockerResponse;
import com.adambarreiro.docker.client.exception.DockerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class SingleDockerTopService implements DockerTopService {

	private static final String COMMAND_NAME = "top";

	private final Docker docker;
	private final ExecutorService executorService;
	private final SimpMessagingTemplate template;

	private Future<Object> topCommand = null;
	private String containerId = null;
	private DockerResponse execOutput = null;

	@Autowired
	public SingleDockerTopService(final Docker docker, final SimpMessagingTemplate template) {
		this.docker = docker;
		this.template = template;
		this.executorService = Executors.newScheduledThreadPool(1);
	}

	@Override
	public void start(final String socketEndpoint, final String dockerImage) throws DockerException {
		if (this.topCommand == null) {
			this.execOutput = this.execTop(socketEndpoint, dockerImage);
			this.topCommand = this.executorService.submit(() -> {
				while (true) {
					this.execOutput.getContent().lines().forEach(
							line -> this.template.convertAndSend(socketEndpoint, line));
				}
			});
		}
	}

	@Override
	public void stop(final String socketEndpoint) throws DockerException {
		if (this.topCommand != null) {
			this.topCommand.cancel(true);
			this.topCommand = null;
			this.removeContainers();
			this.execOutput.close();
			this.template.convertAndSend(socketEndpoint, this.containerId);
		}
	}

	/**
	 * This is the main scenario. Pulling an image, creating a container,
	 * executing top on it and returning the results.
	 *
	 * @return the client response, that contains a stream of the container results.
	 * @throws DockerException if something goes wrong.
	 */
	private DockerResponse execTop(final String socketEndpoint, final String dockerImage) throws DockerException {
		try {
			final String image = dockerImage.split(":")[0];
			final String tag = dockerImage.split(":")[1];
			this.docker.images().pull(image, tag);
			this.containerId = this.docker.containers().create(String.format("%s-%s",image,COMMAND_NAME), dockerImage);
			this.docker.containers().start(this.containerId);
			final String execId = this.docker.containers().exec(this.containerId, COMMAND_NAME);
			return this.docker.execs().startInteractive(execId);
		} catch (DockerException e) {
			this.template.convertAndSend(socketEndpoint, String.format("Exception: %s", e.getMessage()));
			throw e;
		}
	}

	/**
	 * Removes all the stuff created by start() method.
	 *
	 * @throws DockerException if something goes wrong.
	 */
	private void removeContainers() throws DockerException {
		this.docker.containers().stop(this.containerId);
		this.docker.containers().remove(this.containerId);
	}
}
