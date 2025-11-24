package stephenowinoh.com.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("!cloudinary") // Active when 'cloudinary' profile is NOT active
public class MockCloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        // Mock/stub Cloudinary instance for local development
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "local-dev-mock");
        config.put("api_key", "mock-api-key");
        config.put("api_secret", "mock-api-secret");
        config.put("secure", true);

        return new Cloudinary(config);
    }
}
