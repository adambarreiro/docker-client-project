package com.adambarreiro.docker.client.http.client;

import com.adambarreiro.docker.client.api.http.HttpDocker;
import com.adambarreiro.docker.client.exception.DockerException;
import com.adambarreiro.docker.client.http.DockerHttpDockerResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.NameValuePair;

import java.util.List;

/**
 * This class specifies the required HTTP methods that the {@link HttpDocker} client needs to obtain and perform
 * operations with the Docker API.
 */
public interface DockerHttpClient {

	/**
	 * Performs a GET call to the Docker API to the specified path.
	 *
	 * @param path resource path from the Docker API.
	 * @return A {@link DockerHttpDockerResponse} if the call succeeded.
	 * @throws DockerException if something went wrong.
	 */
	DockerHttpDockerResponse get(String path) throws DockerException;

	/**
	 * Performs a GET call to the Docker API to the specified path and the given query parameters.
	 *
	 * @param path resource path from the Docker API.
	 * @param queryParams query parameters for that specific resource.
	 * @return A {@link DockerHttpDockerResponse} if the call succeeded.
	 */
	DockerHttpDockerResponse get(String path, List<NameValuePair> queryParams) throws DockerException;

	/**
	 * Performs a POST call to the Docker API to the specified path.
	 *
	 * @param path resource path from the Docker API.
	 * @return A {@link DockerHttpDockerResponse} if the call succeeded.
	 * @throws DockerException if the call didn't succeed.
	 */
	DockerHttpDockerResponse post(String path) throws DockerException;

	/**
	 * Performs a POST call to the Docker API to the specified path and with the given query parameters.
	 *
	 * @param path resource path from the Docker API.
	 * @param queryParams query parameters for that specific resource.
	 * @return A valid {@link DockerHttpDockerResponse} if the call succeeded.
	 * @throws DockerException if the call didn't succeed.
	 */
	DockerHttpDockerResponse post(String path, List<NameValuePair> queryParams) throws DockerException;

	/**
	 * Performs a POST call to the Docker API to the specified path and with the given body in JSON format.
	 *
	 * @param path resource path from the Docker API.
	 * @param jsonBody body in JSON format.
	 * @return A valid {@link DockerHttpDockerResponse} if the call succeeded.
	 * @throws DockerException if the call didn't succeed.
	 */
	DockerHttpDockerResponse post(String path, ObjectNode jsonBody) throws DockerException;

	/**
	 * Performs a POST call to the Docker API to the specified path, the specified query parameters
	 * and with the given JSON body.
	 *
	 * @param path resource path from the Docker API.
	 * @param queryParams query parameters for that specific resource.
	 * @param jsonBody body in JSON format.
	 * @return A valid {@link DockerHttpDockerResponse} if the call succeeded.
	 * @throws DockerException if the call didn't succeed.
	 */
	DockerHttpDockerResponse post(String path, List<NameValuePair> queryParams, ObjectNode jsonBody) throws DockerException;

	/**
	 * Performs a DELETE call to the Docker API to the specified path.
	 *
	 * @param path resource path from the Docker API.
	 * @return A valid {@link DockerHttpDockerResponse} if the call succeeded.
	 * @throws DockerException if the call didn't succeed.
	 */
	DockerHttpDockerResponse delete(String path) throws DockerException;

	/**
	 * Performs a DELETE call to the Docker API to the specified path and with the given query parameters.
	 *
	 * @param path resource path from the Docker API.
	 * @param queryParams query parameters for that specific resource.
	 * @return A valid {@link DockerHttpDockerResponse} if the call succeeded.
	 * @throws DockerException if the call didn't succeed.
	 */
	DockerHttpDockerResponse delete(String path, List<NameValuePair> queryParams) throws DockerException;

}
