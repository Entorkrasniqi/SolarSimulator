package types;

import java.util.List;
// What is this?
// This is an interface for a weather service that fetches weather data based on latitude and longitude.
// It defines a method fetchWeather that takes latitude and longitude as parameters and returns a WeatherData object

public interface WeatherService {
    WeatherData fetchWeather(double lat, double lon) throws Exception;
    // Fetch solar irradiance data (mocked or from API)
    double fetchSolarIrradiance(double lat, double lon) throws Exception;
    
    // Fetch a multi-day weather forecast (up to 16 days)
    List<WeatherData> fetchWeatherForecast(double lat, double lon, int days) throws Exception;
}