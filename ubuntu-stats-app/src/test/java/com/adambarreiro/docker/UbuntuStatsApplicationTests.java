package com.adambarreiro.docker;

import com.adambarreiro.docker.client.Docker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UbuntuStatsApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	Docker docker;

	@Test
	void asd() throws IOException, URISyntaxException {
//		docker.images().pull("ubuntu","latest");
//		Optional<String> containerId = docker.containers().create("asd", "ubuntu:latest");
//		docker.containers().start(containerId.orElseThrow());
//		Optional<String> execId = docker.containers().exec(containerId.orElseThrow(), "top");
//		Optional<InputStream> is = docker.execs().start(execId.orElseThrow());
//		try {
//			BufferedReader in = new BufferedReader(new InputStreamReader(is.get()));
//			List<String> lines = new ArrayList<>();
//			String line = null;
//			int pos = 0;
//			while ((line = in.readLine()) != null) {
//				if (!line.equals("")) {
//					lines.add(pos, line);
//				}
//				System.out.println(line);
//				//lines.stream().forEach(System.out::println);
//				if (line.equals("\r")) {
//					pos = 0;
//					System.out.println("END");
//				}
//			}
//		} catch(UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}

	}

}
