package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for loading configuration values from .env file
 * while keeping sensitive data secure.
 */
public class ConfigLoader {
    // Define multiple possible paths for the .env file
    private static final String[] ENV_FILE_PATHS = {
        ".env",                    // Current directory
        "../.env",                 // One directory up (parent)
        "../../.env",              // Two directories up (project root from classes)
        "../../../.env"            // Three directories up (when running from IDE)
    };
    
    private static Map<String, String> configValues = new HashMap<>();
    
    static {
        loadEnvFile();
    }
    
    private static void loadEnvFile() {
        boolean loaded = false;
        
        // Try loading from different possible locations
        for (String envPath : ENV_FILE_PATHS) {
            Path path = Paths.get(envPath);
            if (Files.exists(path)) {
                try {
                    Files.lines(path)
                        .filter(line -> !line.startsWith("#") && line.contains("="))
                        .forEach(line -> {
                            String[] parts = line.split("=", 2);
                            if (parts.length == 2) {
                                configValues.put(parts[0].trim(), parts[1].trim());
                            }
                        });
                    System.out.println("Successfully loaded .env file from: " + path.toAbsolutePath());
                    loaded = true;
                    break;
                } catch (IOException e) {
                    // Continue to next path
                }
            }
        }
        
        if (!loaded) {
            System.err.println("Warning: Could not load .env file. Make sure it exists in the project root or one of the parent directories.");
        }
    }
    
    /**
     * Gets a configuration value from the .env file
     * @param key The configuration key
     * @return The configuration value or null if not found
     */
    public static String get(String key) {
        return configValues.get(key);
    }
    
    /**
     * Gets the API key for OpenWeather
     * @return OpenWeather API key
     */
    public static String getOpenWeatherApiKey() {
        String apiKey = get("OPENWEATHER_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Warning: OPENWEATHER_API_KEY not found in .env file.");
        }
        return apiKey;
    }
}