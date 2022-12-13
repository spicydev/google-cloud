package dev.mirchi.googlecloud;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GoogleCloudApplicationTests {

	static {
		System.setProperty("PUBSUB_EMULATOR_HOST", "localhost:8085");
		System.setProperty("SPANNER_EMULATOR_HOST", "localhost:9010");
	}

	@Test
	void contextLoads() {
	}

}
