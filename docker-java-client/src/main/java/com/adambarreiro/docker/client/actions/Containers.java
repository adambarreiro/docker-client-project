package com.adambarreiro.docker.client.actions;

import com.adambarreiro.docker.client.http.DockerHttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class Containers {

	private static final Logger LOGGER = LoggerFactory.getLogger(Containers.class);
	private static final String BASE_PATH = "/containers";
	private static final Pattern VALID_CONTAINER_NAME = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_.-]+$");

	private final DockerHttpClient dockerHttpClient;
	private final ObjectMapper objectMapper;

	public Containers(DockerHttpClient dockerHttpClient) {
		this.dockerHttpClient = dockerHttpClient;
		this.objectMapper = new ObjectMapper();
	}

	public Optional<String> create(String name, String image) throws IOException, URISyntaxException {
		List<NameValuePair> queryParams = new ArrayList<>();
		if (name != null && VALID_CONTAINER_NAME.matcher(name).matches()) {
			queryParams.add(new BasicNameValuePair("name", name));
		}
		ObjectNode jsonBody = objectMapper.createObjectNode()
				.put("Image", image)
				.put("Tty", true)
				.put("OpenStdin", true);

		HttpResponse response = dockerHttpClient.post(BASE_PATH.concat("/create"), queryParams,
				objectMapper.writer().writeValueAsString(jsonBody));
		int responseStatusCode = response.getStatusLine().getStatusCode();
		if (responseStatusCode != HttpStatus.SC_CREATED) {
			LOGGER.error("Error creating container {} with image {}, API returned status code {}", name, image, responseStatusCode);
			return Optional.empty();
		}

		return Optional.of(new ObjectMapper().readTree(response.getEntity().getContent()).get("Id").asText());
	}

	public Boolean start(final String containerId) throws IOException, URISyntaxException {
		HttpResponse response = dockerHttpClient.post(BASE_PATH.concat("/").concat(containerId).concat("/start"));
		int responseStatusCode = response.getStatusLine().getStatusCode();

		if (responseStatusCode != HttpStatus.SC_NO_CONTENT) {
			LOGGER.error("Error starting container {}, API returned status code {}", containerId, responseStatusCode);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public Optional<String> exec(final String containerId, final String command) throws IOException, URISyntaxException {
		ObjectNode jsonBody = objectMapper.createObjectNode()
				.put("AttachStdin",false)
				.put("AttachStdout",true)
				.put("AttachStderr",false)
				.put("Tty", true)
				.set("Cmd", objectMapper.createArrayNode().add(command));

		HttpResponse response = dockerHttpClient.post(BASE_PATH.concat("/").concat(containerId).concat("/exec"),
				objectMapper.writer().writeValueAsString(jsonBody));
		int responseStatusCode = response.getStatusLine().getStatusCode();

		if (responseStatusCode != HttpStatus.SC_CREATED) {
			LOGGER.error("Error creating an execution for container {}, API returned status code {}", containerId, responseStatusCode);
			return Optional.empty();
		}
		return Optional.of(new ObjectMapper().readTree(response.getEntity().getContent()).get("Id").asText());
	}

	public Boolean stop(String containerId) throws IOException, URISyntaxException {
		List<NameValuePair> queryParams = new ArrayList<>();
		queryParams.add(new BasicNameValuePair("id", containerId));

		HttpResponse response = dockerHttpClient.post(BASE_PATH.concat("/").concat(containerId).concat("/stop"), queryParams);
		int responseStatusCode = response.getStatusLine().getStatusCode();
		if (responseStatusCode != HttpStatus.SC_NO_CONTENT) {
			LOGGER.error("Error stopping container {}, API returned status code {}", containerId, responseStatusCode);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public Boolean prune() throws IOException, URISyntaxException {
		HttpResponse response = dockerHttpClient.post(BASE_PATH.concat("/prune"));
		int responseStatusCode = response.getStatusLine().getStatusCode();
		if (responseStatusCode != HttpStatus.SC_OK) {
			LOGGER.error("Error removing all containers, API returned status code {}", responseStatusCode);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

}
