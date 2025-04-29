package com.solar.calculator;


/**
 * The Calculator class provides methods to perform various calculations
 * related to solar energy systems, including adjustments for cloud cover,
 * temperature, wind cooling, and angle of incidence.
 */
public class Calculator {
    /**
     * Calculates the adjusted solar irradiance based on cloud cover.
     *
     * @param solarIrradiance The initial solar irradiance (W/m²).
     * @param cloudCover The fraction of cloud cover (0 to 1, where 1 is full cover).
     * @return The adjusted solar irradiance (W/m²).
     */
    public double calculateAdjustedIrradiance(double solarIrradiance, double cloudCover) {
        return solarIrradiance * (1 - cloudCover);
    }

    /**
     * Calculates the temperature-adjusted efficiency of a solar panel.
     *
     * @param ratedEfficiency The rated efficiency of the solar panel (0 to 1).
     * @param temperatureCoefficient The temperature coefficient (fraction per degree Celsius).
     * @param currentTemperature The current temperature (°C).
     * @param referenceTemperature The reference temperature (°C).
     * @return The temperature-adjusted efficiency (0 to 1).
     */
    public double calculateTemperatureEfficiency(double ratedEfficiency, double temperatureCoefficient, double currentTemperature, double referenceTemperature) {
        return ratedEfficiency * (1 - temperatureCoefficient * (currentTemperature - referenceTemperature));
    }

    /**
     * Calculates the adjusted temperature of a solar panel based on wind cooling.
     *
     * @param currentTemperature The current temperature (°C).
     * @param windSpeed The wind speed (m/s).
     * @param coolingFactor The cooling factor (°C per m/s).
     * @return The adjusted temperature (°C).
     */
    public double calculateAdjustedTemperature(double currentTemperature, double windSpeed, double coolingFactor) {
        return currentTemperature - windSpeed * coolingFactor;
    }

    /**
     * Calculates the irradiance adjustment based on the angle of incidence.
     *
     * @param adjustedIrradiance The adjusted solar irradiance (W/m²).
     * @param angleOfIncidence The angle of incidence (degrees).
     * @return The angle-adjusted irradiance (W/m²).
     */
    public double calculateAngleAdjustedIrradiance(double adjustedIrradiance, double angleOfIncidence) {
        double angleInRadians = Math.toRadians(angleOfIncidence);
        return adjustedIrradiance * Math.cos(angleInRadians);
    }

    /**
     * Calculates the total power output of a solar panel.
     *
     * @param angleAdjustedIrradiance The angle-adjusted solar irradiance (W/m²).
     * @param panelArea The area of the solar panel (m²).
     * @param panelEfficiency The efficiency of the solar panel (0 to 1).
     * @param temperatureEfficiency The temperature-adjusted efficiency (0 to 1).
     * @return The total power output (W).
     */
    public double calculatePowerOutput(double angleAdjustedIrradiance, double panelArea, double panelEfficiency, double temperatureEfficiency) {
        return angleAdjustedIrradiance * panelArea * panelEfficiency * temperatureEfficiency;
    }

    public double runCalc(){
        
        // Example inputs
        double solarIrradiance = 800; // W/m²

        
        // Weather forecast

        double cloudCover = 0.3; // 30%
        double currentTemperature = 35; // °C
        double windSpeed = 5; // m/s
        
        // Solar panel

        double panelEfficiency = 0.20; // 20%
        double panelArea = 1.5; // m²
        double angleOfIncidence = 30; // degrees
        double referenceTemperature = 25; // °C
        double temperatureCoefficient = 0.005; // per °C
        double coolingFactor = 0.1; // °C per m/s

        // Perform calculations
        double adjustedIrradiance = calculateAdjustedIrradiance(solarIrradiance, cloudCover);
        double adjustedTemperature = calculateAdjustedTemperature(currentTemperature, windSpeed, coolingFactor);
        double temperatureEfficiency = calculateTemperatureEfficiency(panelEfficiency, temperatureCoefficient, adjustedTemperature, referenceTemperature);
        double angleAdjustedIrradiance = calculateAngleAdjustedIrradiance(adjustedIrradiance, angleOfIncidence);
        double powerOutput = calculatePowerOutput(angleAdjustedIrradiance, panelArea, panelEfficiency, temperatureEfficiency);

        // Output the result
        System.out.println("Total Power Output: " + powerOutput + " W");
        return powerOutput;
    }
    

}
