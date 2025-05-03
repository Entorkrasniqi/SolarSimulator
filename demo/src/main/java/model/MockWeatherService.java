package model;

import java.util.ArrayList;
import java.util.List;
import types.WeatherService;
import types.WeatherData;

public class MockWeatherService implements WeatherService {

    @Override
    public WeatherData fetchWeather(double lat, double lon) {
        // Return hardcoded weather data
        // Example: 22.5°C, 40% cloud cover, 3.2 m/s wind, 550 W/m² irradiance
        return new WeatherData(22.5, 3.2, 40, 550.0);
    }

    @Override
    public double fetchSolarIrradiance(double lat, double lon) {
        // Return hardcoded solar irradiance
        return 550.0; // W/m²
    }
    
    @Override
    public List<WeatherData> fetchWeatherForecast(double lat, double lon, int days) {
        if (days < 1 || days > 16) {
            throw new IllegalArgumentException("Days must be between 1 and 16");
        }
        
        List<WeatherData> forecast = new ArrayList<>();
        
        // Generate simulated forecast data for the requested number of days
        for (int i = 0; i < days; i++) {
            // Simulate slight variations in temperature, cloud cover, and wind speed for each day
            double tempVariation = Math.random() * 10 - 5; // -5 to +5 degrees
            int cloudVariation = (int) (Math.random() * 40 - 20); // -20% to +20%
            double windVariation = Math.random() * 4 - 2; // -2 to +2 m/s
            
            double temperature = 22.5 + tempVariation;
            int cloudCover = Math.max(0, Math.min(100, 40 + cloudVariation)); // Ensure between 0-100%
            double windSpeed = Math.max(0, 3.2 + windVariation); // Ensure not negative
            
            forecast.add(new WeatherData(temperature, windSpeed, cloudCover, 550));
        }
        
        return forecast;
    }
}