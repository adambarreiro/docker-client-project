package com.adambarreiro.docker.client.actions;

import com.adambarreiro.docker.client.http.DockerHttpClient;
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

public class Images {

	private static final Logger LOGGER = LoggerFactory.getLogger(Images.class);

	private static final String BASE_PATH = "/images";

	private final DockerHttpClient dockerHttpClient;

	public Images(DockerHttpClient dockerHttpClient) {
		this.dockerHttpClient = dockerHttpClient;
	}

	public Boolean pull(String name, String tag) throws IOException, URISyntaxException {
		List<NameValuePair> queryParams = new ArrayList<>();
		queryParams.add(new BasicNameValuePair("fromImage", name));
		queryParams.add(new BasicNameValuePair("tag", tag));

		HttpResponse response = dockerHttpClient.post(BASE_PATH.concat("/create"), queryParams);
		int responseStatusCode = response.getStatusLine().getStatusCode();
		if (responseStatusCode != HttpStatus.SC_OK) {
			LOGGER.error("Error pulling image {}:{}, API returned status code {}.", name, tag, responseStatusCode);
			return Boolean.FALSE;
		}

		// Docker engine is pulling the image in the background, so we force the stream to end.
		response.getEntity().getContent().readAllBytes();
		return Boolean.TRUE;
	}

}
