package swp.group4.be_ev_service_center_management.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Load .env file before Spring Boot starts
 * This ensures environment variables are available for application.properties
 */
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        try {
            // Load .env file from project root
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing() // Don't crash if .env is missing
                    .load();

            // Convert dotenv entries to Map
            Map<String, Object> dotenvProperties = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                dotenvProperties.put(entry.getKey(), entry.getValue());
            });

            // Add to Spring Environment with high priority
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            environment.getPropertySources().addFirst(
                    new MapPropertySource("dotenvProperties", dotenvProperties)
            );

            System.out.println("✅ Loaded .env file successfully!");
            System.out.println("   DB_HOST: " + dotenv.get("DB_HOST"));
            System.out.println("   DB_PORT: " + dotenv.get("DB_PORT"));
            System.out.println("   DB_NAME: " + dotenv.get("DB_NAME"));
            
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not load .env file: " + e.getMessage());
            System.err.println("   Using default values from application.properties");
        }
    }
}
