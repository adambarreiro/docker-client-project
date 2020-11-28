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
public class UbuntuDockerTopService implements DockerTopService {

	private static final String IMAGE_NAME = "ubuntu";
	private static final String IMAGE_TAG = "latest";
	private static final String CONTAINER_NAME = "ubuntu-top";
	private static final String COMMAND_NAME = "top";

	private final Docker docker;
	private final ExecutorService executorService;

	private Future<Object> topCommand = null;
	private String containerId = null;
	private DockerResponse execOutput = null;

	@Autowired
	public UbuntuDockerTopService(final Docker docker) {
		this.docker = docker;
		this.executorService = Executors.newScheduledThreadPool(1);
	}

	@Override
	public void start(final SimpMessagingTemplate template, final String socketEndpoint) throws DockerException {
		if (this.topCommand == null) {
			this.execOutput = this.execTopInUbuntu();
			this.topCommand = this.executorService.submit(() -> {
				while (true) {
					this.execOutput.getContent().lines().forEach(
							line -> template.convertAndSend(socketEndpoint, line));
				}
			});
		}
	}

	@Override
	public void stop(final SimpMessagingTemplate template, final String socketEndpoint) throws DockerException {
		if (this.topCommand != null) {
			this.topCommand.cancel(true);
			this.topCommand = null;
			this.removeContainers();
			this.execOutput.close();
			template.convertAndSend(socketEndpoint, this.containerId);
		}
	}

	/**
	 * This is the main scenario. Pulling an Ubuntu image, creating a container,
	 * executing top on it and returning the results.
	 *
	 * @return the client response, that contains a stream of the container results.
	 * @throws DockerException if something goes wrong.
	 */
	private DockerResponse execTopInUbuntu() throws DockerException {
		this.docker.images().pull(IMAGE_NAME, IMAGE_TAG);
		this.containerId = this.docker.containers().create(CONTAINER_NAME, String.format("%s:%s", IMAGE_NAME, IMAGE_TAG));
		this.docker.containers().start(this.containerId);
		String execId = this.docker.containers().exec(this.containerId, COMMAND_NAME);
		return this.docker.execs().startInteractive(execId);
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
