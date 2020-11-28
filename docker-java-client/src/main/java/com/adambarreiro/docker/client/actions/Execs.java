package com.adambarreiro.docker.client.actions;

import com.adambarreiro.docker.client.http.DockerHttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Optional;

public class Execs {

	private static final Logger LOGGER = LoggerFactory.getLogger(Execs.class);

	private final DockerHttpClient dockerHttpClient;
	private final ObjectMapper objectMapper;

	public Execs(final DockerHttpClient dockerHttpClient) {
		this.dockerHttpClient = dockerHttpClient;
		this.objectMapper = new ObjectMapper();
	}

	public Optional<InputStream> start(final String executionId) throws IOException, URISyntaxException {
		ObjectNode jsonBody = objectMapper.createObjectNode()
				.put("Detach", false)
				.put("Tty", true);

		HttpResponse response = dockerHttpClient.post("/exec/".concat(executionId).concat("/start"),
				objectMapper.writer().writeValueAsString(jsonBody));

		int responseStatusCode = response.getStatusLine().getStatusCode();
		if (responseStatusCode != HttpStatus.SC_OK) {
			LOGGER.error("Error starting exec with ID {}, API returned status code {}.", executionId, responseStatusCode);
			return Optional.empty();
		}
		return Optional.of(response.getEntity().getContent());
	}
}
