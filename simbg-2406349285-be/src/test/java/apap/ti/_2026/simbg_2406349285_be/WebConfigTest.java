package apap.ti._2026.simbg_2406349285_be;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import apap.ti._2026.simbg_2406349285_be.config.WebConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@SpringBootTest(properties = "ALLOWED_ORIGINS=https://first.example, https://second.example")
class WebConfigTest {

    @Autowired
    private WebConfig config;

    @Test
    void configuredOriginsAreTrimmedAndRestrictRequests() {
        TestCorsRegistry registry = new TestCorsRegistry();
        config.addCorsMappings(registry);

        CorsConfiguration cors = registry.configuration();
        assertEquals("https://first.example", cors.checkOrigin("https://first.example"));
        assertEquals("https://second.example", cors.checkOrigin("https://second.example"));
        assertNull(cors.checkOrigin("https://other.example"));
    }

    @Test
    void wildcardOriginRemainsSupported() {
        WebConfig config = new WebConfig();
        ReflectionTestUtils.setField(config, "allowedOrigins", "*");
        TestCorsRegistry registry = new TestCorsRegistry();
        config.addCorsMappings(registry);

        assertEquals("*", registry.configuration().checkOrigin("https://example.com"));
    }

    private static class TestCorsRegistry extends CorsRegistry {
        CorsConfiguration configuration() {
            return getCorsConfigurations().get("/**");
        }
    }
}
