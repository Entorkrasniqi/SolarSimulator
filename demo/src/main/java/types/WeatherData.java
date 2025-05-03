package types;

/**
 * WeatherData - Combined Weather and Solar Radiation Information Container
 * 
 * Purpose:
 * This class encapsulates all key meteorological and solar radiation factors affecting
 * solar panel performance, including temperature, wind speed, solar irradiance, and cloud cover.
 * 
 * Key properties:
 * 1. temperature - Ambient air temperature (°C)
 * 2. windSpeed - Wind speed (m/s)
 * 3. irradiance - Solar power per unit area (W/m²)
 * 4. cloudCover - Percentage of sky covered by clouds (0-100%)
 * 
 * Usage:
 * Used by weather services or simulations requiring a full set of parameters
 * influencing solar energy systems.
 */
public class WeatherData {
    private double temperature;
    private double windSpeed;
    private double irradiance;
    private int cloudCover;

    /**
     * Constructs a WeatherData object with specified values.
     * 
     * @param temperature Ambient temperature in degrees Celsius
     * @param windSpeed Wind speed in meters per second
     * @param irradiance Solar irradiance in watts per square meter
     * @param cloudCover Cloud cover as a percentage (0-100%)
     */
    public WeatherData(double temperature, double windSpeed, int cloudCover, double irradiance) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.cloudCover = cloudCover;
        this.irradiance = irradiance;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getIrradiance() {
        return irradiance;
    }

    public int getCloudCover() {
        return cloudCover;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setIrradiance(double irradiance) {
        this.irradiance = irradiance;
    }

    public void setCloudCover(int cloudCover) {
        this.cloudCover = cloudCover;
    }
}
