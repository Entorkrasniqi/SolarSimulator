package model;

import types.WeatherData;
import types.WeatherService;
import util.ConfigLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * OpenWeatherService - Real-time Weather Data Provider
 * 
 * Purpose:
 * This service class connects to external weather APIs (OpenWeatherMap and NASA POWER)
 * to fetch real weather data for solar panel power generation calculations.
 * It implements the WeatherService interface to provide both current weather data
 * and 16-day forecasts, which are essential for accurately predicting solar panel output.
 * 
 * Key responsibilities:
 * 1. Connect to OpenWeatherMap API for temperature, cloud cover, and wind data
 * 2. Connect to NASA POWER API for solar irradiance data
 * 3. Parse JSON responses from both APIs into usable WeatherData objects
 * 4. Provide forecasts for up to 16 days to allow future power generation predictions
 * 
 * Usage:
 * This service should be instantiated when real weather data is needed instead of mocked data.
 * It requires a valid API key stored in the application's .env file.
 */
public class OpenWeatherService implements WeatherService {

    // API key loaded using the ConfigLoader utility class
    private static final String API_KEY = ConfigLoader.getOpenWeatherApiKey();
    
    // Base URL for the current weather API endpoint
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    
    // URL for the 16-day forecast API endpoint
    private static final String FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast/daily";
    
    // URL for NASA's POWER API which provides solar irradiance data
    private static final String NASA_POWER_URL = "https://power.larc.nasa.gov/api/temporal/daily/point";

    /**
     * Fetches current weather data for a specific geographic location.
     * 
     * @param lat Latitude of the location
     * @param lon Longitude of the location
     * @return WeatherData object containing temperature, cloud cover, wind speed and solar irradiance
     * @throws IOException If there are connection issues or problems parsing the API response
     */
    @Override
    public WeatherData fetchWeather(double lat, double lon) throws IOException {
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new IllegalStateException("API key is not set. Please ensure the OPENWEATHER_API_KEY is set in your .env file.");
        }

        // Construct the API URL with the location coordinates and API key
        String url = String.format("%s?lat=%.4f&lon=%.4f&appid=%s&units=metric", BASE_URL, lat, lon, API_KEY);

        // Open HTTP connection to the API
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");

        // Check if the request was successful
        int status = conn.getResponseCode();
        if (status != 200) {
            throw new IOException("Failed API call, status code: " + status);
        }

        // Read the API response into a string
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        // Parse the JSON response into a WeatherData object
        return parseWeather(response.toString(), lat, lon);
    }

    /**
     * Fetches weather forecast data for up to 16 days for a specific location.
     * This allows predicting solar panel output over the coming days.
     * 
     * @param lat Latitude of the location
     * @param lon Longitude of the location
     * @param days Number of days to forecast (1-16)
     * @return List of WeatherData objects, one for each day in the forecast
     * @throws IOException If there are connection issues or problems parsing the API response
     */
    @Override
    public List<WeatherData> fetchWeatherForecast(double lat, double lon, int days) throws IOException {
        // Validate the requested number of days
        if (days < 1 || days > 16) {
            throw new IllegalArgumentException("Days must be between 1 and 16");
        }
        
        // Check if API key is available
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new IllegalStateException("API key is not set. Please ensure the OPENWEATHER_API_KEY is set in your .env file.");
        }

        // Construct the forecast API URL with location, number of days, and API key
        String url = String.format("%s?lat=%.4f&lon=%.4f&cnt=%d&appid=%s&units=metric", 
            FORECAST_URL, lat, lon, days, API_KEY);

        // Open HTTP connection to the API
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");

        // Check if the request was successful
        int status = conn.getResponseCode();
        if (status != 200) {
            throw new IOException("Failed API call, status code: " + status);
        }

        // Read the API response into a string
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        // Parse the JSON response into a list of WeatherData objects
        return parseWeatherForecast(response.toString(), lat, lon, days);
    }

    /**
     * Fetches solar irradiance data from NASA's POWER API.
     * This data is crucial for calculating potential solar panel output.
     * 
     * @param lat Latitude of the location
     * @param lon Longitude of the location
     * @return Solar irradiance value in W/m²
     * @throws IOException If there are connection issues or problems parsing the API response
     */
    @Override
    public double fetchSolarIrradiance(double lat, double lon) throws IOException {
        // Construct the NASA POWER API URL with location coordinates
        // We use a fixed date (2025-01-01) as a reference point for irradiance data
        String url = String.format(
            "%s?parameters=ALLSKY_SFC_SW_DWN&community=SB&longitude=%.4f&latitude=%.4f&format=JSON&start=20250101&end=20250101",
            NASA_POWER_URL, lon, lat
        );

        System.out.println("Fetching solar irradiance from: " + url);

        // Open HTTP connection to the API
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");

        // Check if the request was successful
        int status = conn.getResponseCode();
        if (status != 200) {
            throw new IOException("Failed API call to NASA POWER, status code: " + status);
        }

        // Read the API response into a string
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        // Parse the JSON response to extract the solar irradiance value
        return parseSolarIrradiance(response.toString());
    }

    /**
     * Parses the JSON response from the current weather API.
     * Extracts temperature, cloud cover, wind speed, and gets solar irradiance.
     * 
     * @param json The JSON string response from the OpenWeatherMap API
     * @param lat Latitude (needed to fetch solar irradiance)
     * @param lon Longitude (needed to fetch solar irradiance)
     * @return A complete WeatherData object with all required weather parameters
     * @throws IOException If there are issues fetching or parsing solar irradiance data
     */
    private WeatherData parseWeather(String json, double lat, double lon) throws IOException {
        // Extract temperature from the JSON response
        String tempKey = "\"temp\":";
        int tempIndex = json.indexOf(tempKey) + tempKey.length();
        double temperature = Double.parseDouble(json.substring(tempIndex, json.indexOf(",", tempIndex)));

        // Extract cloud cover percentage from the JSON response
        String cloudsKey = "\"all\":";
        int cloudsIndex = json.indexOf(cloudsKey) + cloudsKey.length();
        int cloudCover = Integer.parseInt(json.substring(cloudsIndex, json.indexOf("}", cloudsIndex)));

        // Extract wind speed from the JSON response
        String windKey = "\"speed\":";
        int windIndex = json.indexOf(windKey) + windKey.length();
        double windSpeed = Double.parseDouble(json.substring(windIndex, json.indexOf(",", windIndex)));

        // Fetch solar irradiance from NASA POWER API
        // double irradiance = fetchSolarIrradiance(lat, lon);
        double irradiance = 550.0; // Default value for testing purposes

        // Create and return a new WeatherData object with all the extracted information
        return new WeatherData(temperature, windSpeed, cloudCover, irradiance);
    }

    /**
     * Parses the JSON response from the forecast API to extract weather data for multiple days.
     * 
     * @param json The JSON string response from the OpenWeatherMap forecast API
     * @param lat Latitude (needed to fetch solar irradiance)
     * @param lon Longitude (needed to fetch solar irradiance)
     * @param days Number of days in the forecast
     * @return List of WeatherData objects representing the forecast for each day
     * @throws IOException If there are issues fetching or parsing solar irradiance data
     */
    private List<WeatherData> parseWeatherForecast(String json, double lat, double lon, int days) throws IOException {
        List<WeatherData> forecast = new ArrayList<>();
        String jsonContent = json;
        
        // Find the start of the "list" array in the JSON which contains data for each day
        String listKey = "\"list\":[";
        int listStartIndex = jsonContent.indexOf(listKey) + listKey.length();
        String listContent = jsonContent.substring(listStartIndex, jsonContent.indexOf("]", listStartIndex));
        
        // Process each day's data separately
        int currentIndex = 0;
        for (int i = 0; i < days; i++) {
            // Find the start of the current day's data object
            int nextItemStart = listContent.indexOf("{", currentIndex);
            if (nextItemStart == -1) break;
            
            // Find the end of the current day's data object
            int nextItemEnd = listContent.indexOf("},", nextItemStart);
            if (nextItemEnd == -1) {
                nextItemEnd = listContent.indexOf("}", nextItemStart);
            }
            
            // Extract the current day's data as a JSON string
            String dayData = listContent.substring(nextItemStart, nextItemEnd + 1);
            currentIndex = nextItemEnd + 1;
            
            // Extract day temperature from the JSON
            String tempKey = "\"day\":";
            int tempIndex = dayData.indexOf(tempKey) + tempKey.length();
            if (tempIndex - tempKey.length() < 0) continue; // Skip if temperature not found
            double temperature = Double.parseDouble(dayData.substring(tempIndex, dayData.indexOf(",", tempIndex)));

            // Extract cloud cover percentage from the JSON
            String cloudsKey = "\"clouds\":";
            int cloudsIndex = dayData.indexOf(cloudsKey) + cloudsKey.length();
            if (cloudsIndex - cloudsKey.length() < 0) continue; // Skip if cloud cover not found
            int cloudCover = Integer.parseInt(dayData.substring(cloudsIndex, dayData.indexOf(",", cloudsIndex)));

            // Extract wind speed from the JSON
            String windKey = "\"speed\":";
            int windIndex = dayData.indexOf(windKey) + windKey.length();
            if (windIndex - windKey.length() < 0) continue; // Skip if wind speed not found
            double windSpeed = Double.parseDouble(dayData.substring(windIndex, dayData.indexOf(",", windIndex)));

            // For multi-day forecasts, use the same irradiance value for simplicity
            // In a production app, this could be improved to get day-specific irradiance
            double irradiance = fetchSolarIrradiance(lat, lon);
            
            // Add this day's weather data to the forecast list
            forecast.add(new WeatherData(temperature, windSpeed, cloudCover, irradiance));
        }
        
        return forecast;
    }

    /**
     * Parses the JSON response from NASA POWER API to extract solar irradiance value.
     * The NASA POWER API returns a complex nested JSON structure.
     * 
     * @param json The JSON string response from the NASA POWER API
     * @return Solar irradiance value in W/m², or a default value if parsing fails
     */
    private double parseSolarIrradiance(String json) {
        try {
            // NASA POWER API returns a complex JSON structure
            // We need to navigate to the ALLSKY_SFC_SW_DWN property and extract the first date's value
            
            // Find the properties section in the JSON
            String propertiesKey = "\"properties\":";
            int propertiesIndex = json.indexOf(propertiesKey);
            if (propertiesIndex == -1) {
                return 550.0; // Default value if structure not found
            }
            
            // Find the ALLSKY_SFC_SW_DWN parameter section which contains the irradiance data
            String paramKey = "\"ALLSKY_SFC_SW_DWN\":";
            int paramIndex = json.indexOf(paramKey, propertiesIndex);
            if (paramIndex == -1) {
                return 550.0; // Default value if parameter not found
            }
            
            // Extract the section with date-based values
            String datesSection = json.substring(paramIndex + paramKey.length());
            
            // Find the date-value pair (format: "YYYYMMDD": value)
            int openBrace = datesSection.indexOf("{");
            int closeBrace = datesSection.indexOf("}");
            
            if (openBrace != -1 && closeBrace != -1) {
                // Extract the content between braces which contains the date and value
                String dateValues = datesSection.substring(openBrace + 1, closeBrace);
                
                // Find the value part (after the colon)
                int colonIndex = dateValues.indexOf(":");
                if (colonIndex != -1) {
                    String valueStr = dateValues.substring(colonIndex + 1).trim();
                    // Clean up the value by removing any trailing commas or braces
                    valueStr = valueStr.replaceAll("[,}]", "").trim();
                    return Double.parseDouble(valueStr);
                }
            }
            
            // If parsing fails, return a reasonable default value (REASONABLE BASED ON WHAT???!)
            return 550.0;
            
        } catch (Exception e) {
            // Log the error and return a default value in case of parsing failure
            System.out.println("Error parsing solar irradiance data: " + e.getMessage());
            return 550.0; // Default value if parsing fails
        }
    }
}