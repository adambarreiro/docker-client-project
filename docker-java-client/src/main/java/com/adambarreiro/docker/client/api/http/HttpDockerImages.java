package com.adambarreiro.docker.client.api.http;

import com.adambarreiro.docker.client.api.DockerImages;
import com.adambarreiro.docker.client.exception.DockerException;
import com.adambarreiro.docker.client.http.DockerHttpDockerResponse;
import com.adambarreiro.docker.client.http.client.DockerHttpClient;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Manipulates Docker images through its REST API.
 */
public class HttpDockerImages implements DockerImages {

	private static final String BASE_PATH = "/images";

	private final DockerHttpClient dockerHttpClient;

	public HttpDockerImages(DockerHttpClient dockerHttpClient) {
		this.dockerHttpClient = dockerHttpClient;
	}

	/**
	 * {@inheritDoc}
	 */
	public void pull(final String name, final String tag) throws DockerException {
		List<NameValuePair> queryParams = new ArrayList<>();
		queryParams.add(new BasicNameValuePair("fromImage", name));
		queryParams.add(new BasicNameValuePair("tag", tag));

		DockerHttpDockerResponse response = this.dockerHttpClient.post(BASE_PATH.concat("/create"), queryParams);
		try(response) {
			switch(response.getStatusCode()) {
				case HttpStatus.SC_NOT_FOUND:
					throw new DockerException(String.format("Error pulling image %s:%s, repository doesn't exist or has not read access", name, tag));
				case HttpStatus.SC_INTERNAL_SERVER_ERROR:
					throw new DockerException(String.format("Error pulling image %s:%s, unexpected error", name, tag));
				default:
					// Docker engine is pulling the image in the background, so we force the stream to end.
					response.readAllContent();
			}
		}
	}

}
