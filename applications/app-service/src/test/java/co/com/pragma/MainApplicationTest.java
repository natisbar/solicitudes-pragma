package co.com.pragma;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class MainApplicationTest {

    @Test
    void mainMethodRuns() {
        assertDoesNotThrow(() -> {
            MainApplication.main(new String[] { "--server.port=0" });
        });
    }
}
