package types;

import types.enums.MountingType;
import types.enums.PanelTechnology;

public class Panel {
    private double panelArea; // m²
    private double angleOfIncidence; // degrees
    private double panelEfficiency;
    private double temperatureCoefficient; // per °C
    private double coolingFactor; // °C per m/s
    private double powerOutput; // W
    private PanelTechnology panelTechnology;
    private MountingType mountingType;
    
    // Calculated values
    private double adjustedIrradiance;
    private double temperatureEfficiency;
    private double adjustedTemperature;
    private double angleAdjustedIrradiance;
    
    // Reference temperature constant
    private static final double REFERENCE_TEMPERATURE = 25.0; // °C

    public Panel(PanelTechnology panelTechnology, double panelArea, double angleOfIncidence, MountingType mountingType) {
        this.panelTechnology = panelTechnology;
        this.panelArea = panelArea;
        this.angleOfIncidence = angleOfIncidence;
        this.mountingType = mountingType;
        
        // Set panel efficiency and temperature coefficient based on technology
        setPanelCharacteristics();
        
        // Set cooling factor based on mounting type
        setCoolingFactor();
    }
    
    private void setPanelCharacteristics() {
        switch (panelTechnology) {
            case CRYSTALLINE_SILICON:
                this.panelEfficiency = 0.20; // 20%
                this.temperatureCoefficient = 0.004; // -0.4% per °C
                break;
            case THIN_FILM:
                this.panelEfficiency = 0.15; // 15%
                this.temperatureCoefficient = 0.0033; // -0.33% per °C
                break;
            case CADMIUM_TELLURIDE:
                this.panelEfficiency = 0.16; // 16%
                this.temperatureCoefficient = 0.0028; // -0.28% per °C
                break;
            case AMORPHOUS_SILICON:
                this.panelEfficiency = 0.08; // 8%
                this.temperatureCoefficient = 0.0022; // -0.22% per °C
                break;
            case HETEROJUNCTION:
                this.panelEfficiency = 0.22; // 22%
                this.temperatureCoefficient = 0.0028; // -0.28% per °C
                break;
            default:
                this.panelEfficiency = 0.18; // Default value
                this.temperatureCoefficient = 0.0035;
        }
    }
    
    private void setCoolingFactor() {
        switch (mountingType) {
            case FREE_STANDING:
                this.coolingFactor = 0.10; // 0.10°C per m/s
                break;
            case ROOF_MOUNTED:
                this.coolingFactor = 0.07; // 0.07°C per m/s
                break;
            case BUILDING_INTEGRATED:
                this.coolingFactor = 0.04; // 0.04°C per m/s
                break;
            case ROOF_MOUNTED_WITH_GAP:
                this.coolingFactor = 0.07; // 0.07°C per m/s
                break;
            default:
                this.coolingFactor = 0.06; // Default value
        }
    }
    
    /**
     * Calculate adjusted irradiance based on solar irradiance and cloud cover
     * Formula: Adjusted Irradiance = Solar Irradiance × (1 - Cloud Cover)
     */
    public void calculateAdjustedIrradiance(double solarIrradiance, double cloudCover) {
        this.adjustedIrradiance = solarIrradiance * (1 - cloudCover);
    }
    
    /**
     * Calculate adjusted temperature with wind cooling
     * Formula: Adjusted Temperature = Current Temperature - (Wind Speed × Cooling Factor)
     */
    public void calculateAdjustedTemperature(double currentTemperature, double windSpeed) {
        this.adjustedTemperature = currentTemperature - (windSpeed * coolingFactor);
    }
    
    /**
     * Calculate temperature-adjusted efficiency
     * Formula: Temperature Efficiency = Rated Efficiency × (1 - Temperature Coefficient × (Current Temperature - Reference Temperature))
     */
    public void calculateTemperatureEfficiency() {
        this.temperatureEfficiency = 1 - temperatureCoefficient * (adjustedTemperature - REFERENCE_TEMPERATURE);
    }
    
    /**
     * Calculate angle-adjusted irradiance
     * Formula: Angle-Adjusted Irradiance = Adjusted Irradiance × cos(Angle of Incidence)
     */
    public void calculateAngleAdjustedIrradiance() {
        // Convert angle from degrees to radians
        double angleInRadians = Math.toRadians(angleOfIncidence);
        this.angleAdjustedIrradiance = adjustedIrradiance * Math.cos(angleInRadians);
    }
    
    /**
     * Calculate total power output
     * Formula: Power Output = Angle-Adjusted Irradiance × Panel Area × Panel Efficiency × Temperature Efficiency
     */
    public void calculatePowerOutput() {
        this.powerOutput = angleAdjustedIrradiance * panelArea * panelEfficiency * temperatureEfficiency;
    }
    
    /**
     * Run all calculations in sequence with the provided environmental parameters
     */
    public double runCalculations(double solarIrradiance, double cloudCover, double currentTemperature, double windSpeed) {
        calculateAdjustedIrradiance(solarIrradiance, cloudCover);
        calculateAdjustedTemperature(currentTemperature, windSpeed);
        calculateTemperatureEfficiency();
        calculateAngleAdjustedIrradiance();
        calculatePowerOutput();
        
        return powerOutput;
    }
    
    // Getters for all properties
    public double getPanelArea() {
        return panelArea;
    }
    
    public double getAngleOfIncidence() {
        return angleOfIncidence;
    }
    
    public double getPanelEfficiency() {
        return panelEfficiency;
    }
    
    public double getTemperatureCoefficient() {
        return temperatureCoefficient;
    }
    
    public double getCoolingFactor() {
        return coolingFactor;
    }
    
    public double getPowerOutput() {
        return powerOutput;
    }
    
    public PanelTechnology getPanelTechnology() {
        return panelTechnology;
    }
    
    public double getAdjustedIrradiance() {
        return adjustedIrradiance;
    }
    
    public double getTemperatureEfficiency() {
        return temperatureEfficiency;
    }
    
    public double getAdjustedTemperature() {
        return adjustedTemperature;
    }
    
    public double getAngleAdjustedIrradiance() {
        return angleAdjustedIrradiance;
    }
    
    public MountingType getMountingType() {
        return mountingType;
    }

    @Override
    public String toString() {
        return String.format("Panel [Area: %.2f m², Angle of Incidence: %.2f°, Efficiency: %.2f%%, "
                + "Temperature Coefficient: %.4f, Cooling Factor: %.2f, Power Output: %.2f W, "
                + "Technology: %s, Mounting Type: %s]", 
                panelArea, angleOfIncidence, panelEfficiency * 100, temperatureCoefficient, 
                coolingFactor, powerOutput, panelTechnology, mountingType);
    }
}