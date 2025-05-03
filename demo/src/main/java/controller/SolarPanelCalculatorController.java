package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.solar.MapSelector;
import model.Calculator;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.EventQueue;
import model.MockWeatherService;
import model.OpenWeatherService;
import types.Panel;
import types.WeatherData;
import types.LocationData;
import types.WeatherService;

import types.enums.PanelTechnology;
import types.enums.MountingType;
import types.enums.EventType;

import events.UserSettingsEvent;
import events.handlers.UserSettingsEventHandler;

public class SolarPanelCalculatorController implements Initializable {
    
    // Top Section
    @FXML private Button selectLocationBtn;
    @FXML private CheckBox calculatedHorizonCb;
    @FXML private CheckBox uploadHorizonCb;
    
    // Left Menu
    @FXML private Button gridConnectedBtn;
    @FXML private Button monthlyDataBtn;
    @FXML private Button dailyDataBtn;
    @FXML private Button hourlyDataBtn;
    
    // Main Form
    @FXML private Button helpBtn;
    @FXML private TextField panelAreaField;
    @FXML private ComboBox<String> pvTechCombo;
    @FXML private TextField peakPowerField;
    @FXML private TextField systemLossField;
    @FXML private ComboBox<String> mountingPosCombo;
    @FXML private TextField slopeField;
    @FXML private TextField azimuthField;
    
    // Bottom Buttons
    @FXML private Button runBtn;
    @FXML private Button visualizeBtn;
    @FXML private Button csvBottomBtn;
    @FXML private Button jsonBottomBtn;

    // Fields to store location data
    private double latitude = 0.0;
    private double longitude = 0.0;

    // Label to display selected location
    @FXML private Label selectedLocationLabel;
    
    // Weather service integration
    private WeatherService weatherService;
    private WeatherData currentWeatherData;
    private List<WeatherData> forecastData;
    
    // UI elements for weather data display
    @FXML private Label weatherStatusLabel;
    
    // Flag to use mock weather service for testing
    private boolean useMockWeatherService = true;

    private final EventQueue eventQueue = new EventQueue();
    private final Calculator calculator = new Calculator();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ComboBoxes with values
        pvTechCombo.getItems().addAll("Crystalline silicon", "Thin-Film CIS/CIGS", "Cadmium Telluride (CdTe)", "Amorphous Silicon (a-Si)", "Heterojunction Technology (HJT)");
        pvTechCombo.setValue("Crystalline silicon");
        
        mountingPosCombo.getItems().addAll("Free-standing", "Roof-mounted with gap", "Building integrated");
        mountingPosCombo.setValue("Free-standing");
        
        // Initialize form validation listeners
        setupValidationListeners();
        
        // Initialize weather service with error handling
        initializeWeatherService();

        // register event handlers
        eventQueue.registerHandler(EventType.USER_SETTINGS, new UserSettingsEventHandler(calculator));

        // start the event processor
        eventQueue.start();
    }
    
    /**
     * Initialize the weather service with proper error handling
     */
    private void initializeWeatherService() {
        try {
            if (useMockWeatherService) {
                // Use mock service for testing
                weatherService = new MockWeatherService();
                System.out.println("Using mock weather service for testing");
            } else {
                // Use real OpenWeatherService
                weatherService = new OpenWeatherService();
                System.out.println("OpenWeatherService initialized successfully");
            }
        } catch (Exception e) {
            System.err.println("Error initializing weather service: " + e.getMessage());
            e.printStackTrace();
            
            // Show error dialog
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Service Initialization Error");
                alert.setHeaderText("Weather Service Error");
                alert.setContentText("Could not initialize weather service: " + e.getMessage() + 
                                   "\n\nThe application will continue with limited functionality.");
                alert.showAndWait();
            });
        }
    }
    
    private void setupValidationListeners() {
        // Add numeric validation for text fields
        addNumericValidation(peakPowerField);
        addNumericValidation(systemLossField);
        addNumericValidation(slopeField);
        addNumericValidation(azimuthField);
    }
    
    private void addNumericValidation(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                field.setText(oldValue);
            }
        });
    }
    
    @FXML
    private void handleSwitchVersion(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Version Switch");
        alert.setHeaderText("Switching to Version 5.2");
        alert.setContentText("The application will now switch to version 5.2");
        alert.showAndWait();
        
        // Logic to switch versions would go here
    }
    
    @FXML
    private void handleGridConnected(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        // Load grid connected specific UI/options
    }
    
    @FXML
    private void handleTrackingPv(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        // Load tracking PV specific UI/options
    }
    
    @FXML
    private void handleOffGrid(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        // Load off-grid specific UI/options
    }
    
    @FXML
    private void handleMonthlyData(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        // Load monthly data specific UI/options
    }
    
    @FXML
    private void handleDailyData(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        // Load daily data specific UI/options
    }
    
    @FXML
    private void handleHourlyData(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        // Load hourly data specific UI/options
    }
    
    @FXML
    private void handleTmy(ActionEvent event) {
        setActiveButton((Button) event.getSource());
        // Load TMY specific UI/options
    }
    
    private void setActiveButton(Button activeButton) {
        // Reset all buttons to default style
        for (Button btn : new Button[]{gridConnectedBtn, monthlyDataBtn, dailyDataBtn, hourlyDataBtn}) {
            btn.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #0070c0;");
        }
        
        // Set the active button style
        activeButton.setStyle("-fx-background-color: #0070c0; -fx-text-fill: white;");
    }
    
    @FXML
    private void handleHelp(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("Solar Panel Calculator Help");
        alert.setContentText("This application helps you calculate the performance of solar panel installations. " +
                           "Fill in the required fields and click 'Visualize results' to see the performance metrics.");
        alert.showAndWait();
    }

    @FXML
    private void handleRun(ActionEvent event) {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }
        
        // Check if weather service is initialized
        if (weatherService == null) {
            showErrorAlert("Service Error", "Weather service is not initialized. Please restart the application.");
            return;
        }

        // collect the user inputs into a panel object
        double panelArea = Double.parseDouble(panelAreaField.getText());
        String pvTechnologyString = pvTechCombo.getValue();
        String mountingPositionString = mountingPosCombo.getValue();
        double peakPower = Double.parseDouble(peakPowerField.getText());
        double systemLoss = Double.parseDouble(systemLossField.getText());
        double slope = Double.parseDouble(slopeField.getText());
        double azimuth = Double.parseDouble(azimuthField.getText());
        PanelTechnology pvTechnology = null;
        MountingType mountingPosition;

        switch (pvTechnologyString) {
            case "Crystalline silicon":
                pvTechnology = PanelTechnology.CRYSTALLINE_SILICON;
                break;
            case "Thin-Film CIS/CIGS":
                pvTechnology = PanelTechnology.THIN_FILM;
                break;
            case "Cadmium Telluride (CdTe)":
                pvTechnology = PanelTechnology.CADMIUM_TELLURIDE;
                break;
            case "Amorphous Silicon (a-Si)":
                pvTechnology = PanelTechnology.AMORPHOUS_SILICON;
                break;
            case "Heterojunction Technology (HJT)":
                pvTechnology = PanelTechnology.HETEROJUNCTION;
                break;
            default:
        }

        switch (mountingPositionString) {
            case "Free-standing":
                mountingPosition = MountingType.FREE_STANDING;
                break;
            case "Roof-mounted with gap":
                mountingPosition = MountingType.ROOF_MOUNTED_WITH_GAP;
                break;
            case "Building integrated":
                mountingPosition = MountingType.BUILDING_INTEGRATED;
                break;
            default:
                mountingPosition = MountingType.FREE_STANDING;
        }
        
        Panel panel = new Panel(pvTechnology, panelArea, slope, mountingPosition);

        // Create new event for user settings
        UserSettingsEvent newUserSettingsEvent = new UserSettingsEvent(panel);
        eventQueue.queueEvent(newUserSettingsEvent);
    }
    
    @FXML
    private void handleVisualizeResults(ActionEvent event) {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }
        
        // Check if weather service is initialized
        if (weatherService == null) {
            showErrorAlert("Service Error", "Weather service is not initialized. Please restart the application.");
            return;
        }
        
        // Fetch weather data if location is selected
        if (latitude != 0.0 && longitude != 0.0) {
            // Disable the button to prevent multiple clicks
            visualizeBtn.setDisable(true);
            visualizeBtn.setText("Loading...");
            
            // Use CompletableFuture to handle weather data fetching asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    // Fetch weather data
                    fetchWeatherData();
                    
                    // Update UI on JavaFX thread
                    Platform.runLater(() -> {
                        // Use weather data in calculations
                        performSolarCalculations();
                        
                        // Re-enable the button
                        visualizeBtn.setDisable(false);
                        visualizeBtn.setText("Visualize results");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    // Update UI on JavaFX thread
                    Platform.runLater(() -> {
                        showErrorAlert("Weather Data Error", "Could not fetch weather data: " + e.getMessage());
                        // Re-enable the button
                        visualizeBtn.setDisable(false);
                        visualizeBtn.setText("Visualize results");
                    });
                }
            });
        } else {
            showErrorAlert("Location Error", "Please select a location first.");
        }
    }
    
    /**
     * Fetches weather data using the weather service implementation
     * @throws Exception 
     */
    private void fetchWeatherData() throws Exception {
        // Show loading indicator
        Platform.runLater(() -> weatherStatusLabel.setText("Fetching weather data..."));
        
        try {
            // Fetch current weather data
            currentWeatherData = weatherService.fetchWeather(latitude, longitude);
            
            // Update UI with weather information
            Platform.runLater(this::updateWeatherDisplay);
            
        } catch (IOException e) {
            Platform.runLater(() -> weatherStatusLabel.setText("Error fetching weather data"));
            throw e;
        }
    }
    
    /**
     * Updates the UI with weather information
     */
    private void updateWeatherDisplay() {
        if (currentWeatherData != null) {
            weatherStatusLabel.setText(String.format(
                "Current conditions: %.1f°C, Cloud cover: %.1f%%, Wind: %.1f m/s, Solar irradiance: %.1f W/m²",
                currentWeatherData.getTemperature(),
                currentWeatherData.getCloudCover(),
                currentWeatherData.getWindSpeed(),
                currentWeatherData.getSolarIrradiance()
            ));
        }
    }    
    
    /**
     * Performs solar panel output calculations using weather data
     */
    private void performSolarCalculations() {
        // Validate inputs
        if (!validateInputs() || currentWeatherData == null) {
            return;
        }
        
        try {
            // Extract values from form fields
            double peakPower = Double.parseDouble(peakPowerField.getText());
            double systemLoss = Double.parseDouble(systemLossField.getText());
            double slope = Double.parseDouble(slopeField.getText());
            double azimuth = Double.parseDouble(azimuthField.getText());
            
            // Calculate expected energy production
            // This is a simplified calculation, real implementation would use more complex models
            double performanceRatio = (100.0 - systemLoss) / 100.0;
            double irradiance = currentWeatherData.getSolarIrradiance();
            double cloudFactor = (100.0 - currentWeatherData.getCloudCover()) / 100.0;
            double temperatureFactor = calculateTemperatureFactor(currentWeatherData.getTemperature());
            
            // Apply orientation factor based on slope and azimuth
            double orientationFactor = calculateOrientationFactor(slope, azimuth);
            
            // Calculate daily energy output in kWh
            double dailyOutput = peakPower * performanceRatio * irradiance * cloudFactor * temperatureFactor * orientationFactor * 0.001; // Convert W to kW
            
            // Calculate monthly output based on forecast data
            double monthlyOutput = calculateMonthlyOutput(peakPower, performanceRatio, orientationFactor);
            
            // Display results
            showResults(dailyOutput, monthlyOutput);
        } catch (NumberFormatException e) {
            showErrorAlert("Calculation Error", "Invalid numeric input. Please check all fields.");
        } catch (Exception e) {
            showErrorAlert("Calculation Error", "Error calculating solar output: " + e.getMessage());
        }
    }

    /**
     * Calculate monthly output based on forecast data
     */
    private double calculateMonthlyOutput(double peakPower, double performanceRatio, double orientationFactor) {
        if (forecastData != null && !forecastData.isEmpty()) {
            double totalOutput = 0.0;
            
            // Use available forecast data
            for (WeatherData day : forecastData) {
                double cloudFactor = (100.0 - day.getCloudCover()) / 100.0;
                double temperatureFactor = calculateTemperatureFactor(day.getTemperature());
                double dailyOutput = peakPower * performanceRatio * day.getSolarIrradiance() * 
                                    cloudFactor * temperatureFactor * orientationFactor * 0.001;
                totalOutput += dailyOutput;
            }
            
            // Extrapolate to a full month (30 days)
            return (totalOutput / forecastData.size()) * 30;
        } else {
            // Fallback to a simple estimation if no forecast data
            return calculateDailyOutput(currentWeatherData, peakPower, performanceRatio, orientationFactor) * 30;
        }
    }
    
    /**
     * Calculate daily output for a specific weather data
     */
    private double calculateDailyOutput(WeatherData weather, double peakPower, double performanceRatio, double orientationFactor) {
        double cloudFactor = (100.0 - weather.getCloudCover()) / 100.0;
        double temperatureFactor = calculateTemperatureFactor(weather.getTemperature());
        return peakPower * performanceRatio * weather.getSolarIrradiance() * 
               cloudFactor * temperatureFactor * orientationFactor * 0.001;
    }

    /**
     * Calculate temperature effect on panel efficiency
     */
    private double calculateTemperatureFactor(double temperature) {
        // Use the specific PV technology to determine temperature coefficient
        String pvTechnology = pvTechCombo.getValue();
        double temperatureCoefficient;
        
        switch (pvTechnology) {
            case "Crystalline silicon":
                temperatureCoefficient = 0.004; // -0.4% per degree C
                break;
            case "Thin-Film CIS/CIGS":
                temperatureCoefficient = 0.0035; // -0.35% per degree C
                break;
            case "Cadmium Telluride (CdTe)":
                temperatureCoefficient = 0.0025; // -0.25% per degree C
                break;
            case "Amorphous Silicon (a-Si)":
                temperatureCoefficient = 0.002; // -0.2% per degree C
                break;
            case "Heterojunction Technology (HJT)":
                temperatureCoefficient = 0.003; // -0.3% per degree C
                break;
            default:
                temperatureCoefficient = 0.004; // Default to crystalline silicon
        }
        
        // Standard Test Conditions temperature is 25°C
        if (temperature <= 25.0) {
            return 1.0;
        } else {
            return 1.0 - temperatureCoefficient * (temperature - 25.0);
        }
    }
    
    /**
     * Calculate orientation factor based on slope and azimuth
     * This is a simplified implementation - real world calculations are more complex
     */
    private double calculateOrientationFactor(double slope, double azimuth) {
        // Optimal slope is roughly equal to latitude
        double optimalSlope = Math.abs(latitude);
        
        // Deviation from optimal slope (0-90 degrees)
        double slopeDifference = Math.abs(slope - optimalSlope);
        double slopeFactor = 1.0 - (slopeDifference / 180.0);
        
        // Azimuth factor (0 = south in northern hemisphere, 180 = south in southern hemisphere)
        double optimalAzimuth = (latitude >= 0) ? 0.0 : 180.0;
        double azimuthDifference = Math.abs(azimuth - optimalAzimuth);
        double azimuthFactor = 1.0 - (azimuthDifference / 180.0);
        
        // Combine both factors
        return Math.max(0.5, slopeFactor * azimuthFactor);
    }
    
    /**
     * Display calculation results
     */
    private void showResults(double dailyOutput, double monthlyOutput) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Solar Panel Calculation Results");
        alert.setHeaderText("Energy Production Estimates");
        
        // Calculate annual output
        double annualOutput = monthlyOutput * 12;
        
        // Calculate CO2 emissions saved (assuming 0.5 kg CO2 per kWh of electricity)
        double co2Saved = annualOutput * 0.5;
        
        String contentText = String.format(
            "Based on the current weather conditions and your system specifications:\n\n" +
            "Daily energy production: %.2f kWh\n" +
            "Monthly energy production: %.2f kWh\n" +
            "Annual energy production: %.2f kWh\n" +
            "CO2 emissions saved annually: %.2f kg\n\n" +
            "Weather conditions used in calculation:\n" +
            "Temperature: %.1f°C\n" +
            "Cloud cover: %d%%\n" +
            "Solar irradiance: %.1f W/m²\n" +
            "Wind speed: %.1f m/s",
            dailyOutput,
            monthlyOutput,
            annualOutput,
            co2Saved,
            currentWeatherData.getTemperature(),
            currentWeatherData.getCloudCover(),
            currentWeatherData.getSolarIrradiance(),
            currentWeatherData.getWindSpeed()
        );
        
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    
    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();
        
        // Check for empty required fields
        if (peakPowerField.getText().isEmpty()) {
            errorMessage.append("- Peak PV power must be specified.\n");
        }
        if (systemLossField.getText().isEmpty()) {
            errorMessage.append("- System loss must be specified.\n");
        }
        if (slopeField.getText().isEmpty()) {
            errorMessage.append("- Slope must be specified.\n");
        }
        if (azimuthField.getText().isEmpty()) {
            errorMessage.append("- Azimuth must be specified.\n");
        }
        
        // Validate ranges
        try {
            double systemLoss = Double.parseDouble(systemLossField.getText());
            if (systemLoss < 0 || systemLoss > 100) {
                errorMessage.append("- System loss must be between 0 and 100%.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("- System loss must be a valid number.\n");
        }
        
        try {
            double slope = Double.parseDouble(slopeField.getText());
            if (slope < 0 || slope > 90) {
                errorMessage.append("- Slope must be between 0 and 90 degrees.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("- Slope must be a valid number.\n");
        }
        
        try {
            double azimuth = Double.parseDouble(azimuthField.getText());
            if (azimuth < -180 || azimuth > 180) {
                errorMessage.append("- Azimuth must be between -180 and 180 degrees.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("- Azimuth must be a valid number.\n");
        }
        
        // If there are errors, show them and return false
        if (errorMessage.length() > 0) {
            showErrorAlert("Validation Error", "Please correct the following errors:\n" + errorMessage.toString());
            return false;
        }
        
        return true;
    }
    
    /**
     * Helper method to show error alerts
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private void handleCsvExport(ActionEvent event) {
        // Ensure we have data to export
        if (currentWeatherData == null) {
            showErrorAlert("Export Error", "No weather data to export. Please fetch weather data first.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export as CSV");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File file = fileChooser.showSaveDialog(csvBottomBtn.getScene().getWindow());
        if (file != null) {
            try {
                // Implementation for CSV export
                exportToCsv(file);
                
                // Show success message
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Export Successful");
                alert.setHeaderText(null);
                alert.setContentText("Data successfully exported to: " + file.getAbsolutePath());
                alert.showAndWait();
            } catch (IOException e) {
                showErrorAlert("Export Error", "Failed to export data: " + e.getMessage());
            }
        }
    }
    
    /**
     * Export data to CSV file
     */
    private void exportToCsv(File file) throws IOException {
        // Implementation for CSV export
        // This would write the current weather data and calculation results to a CSV file
    }
    
    @FXML
    private void handleJsonExport(ActionEvent event) {
        // Ensure we have data to export
        if (currentWeatherData == null) {
            showErrorAlert("Export Error", "No weather data to export. Please fetch weather data first.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export as JSON");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        File file = fileChooser.showSaveDialog(jsonBottomBtn.getScene().getWindow());
        if (file != null) {
            try {
                // Implementation for JSON export
                exportToJson(file);
                
                // Show success message
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Export Successful");
                alert.setHeaderText(null);
                alert.setContentText("Data successfully exported to: " + file.getAbsolutePath());
                alert.showAndWait();
            } catch (IOException e) {
                showErrorAlert("Export Error", "Failed to export data: " + e.getMessage());
            }
        }
    }
    
    /**
     * Export data to JSON file
     */
    private void exportToJson(File file) throws IOException {
        // Implementation for JSON export
        // This would write the current weather data and calculation results to a JSON file
    }

    /**
     * Handles the selection of a location on the map
     */
    @FXML
    public void handleSelectLocation() {
        // Get the current stage
        Stage currentStage = (Stage) selectLocationBtn.getScene().getWindow();
        
        // Create callback to handle selected location data
        Consumer<LocationData> locationCallback = (locationData) -> {
            this.latitude = locationData.getLatitude();
            this.longitude = locationData.getLongitude();
            
            // Store additional location data if needed
            double elevation = locationData.getElevation();
            String address = locationData.getAddress();
            
            // Update the selected location display with more comprehensive information
            if (selectedLocationLabel != null) {
                selectedLocationLabel.setText(String.format(
                    "Location: %.6f, %.6f\nElevation: %.1f m\nAddress: %s", 
                    latitude, longitude, elevation, 
                    address != null ? address : "Unknown"
                ));
            }
            
            // Change the button text to indicate location is selected
            selectLocationBtn.setText("Change location");
            selectLocationBtn.setStyle("-fx-text-fill: #009900;");
            
            // Attempt to load weather data for the selected location
            try {
                // Check if weather service is initialized
                if (weatherService == null) {
                    initializeWeatherService();
                }
                
                // Disable UI while fetching data
                selectLocationBtn.setDisable(true);
                
                // Use CompletableFuture to fetch weather data asynchronously
                CompletableFuture.runAsync(() -> {
                    try {
                        fetchWeatherData();
                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            showErrorAlert("Weather Data Error", "Could not fetch weather data: " + e.getMessage());
                        });
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        Platform.runLater(() -> {
                            selectLocationBtn.setDisable(false);
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Weather Service Error", "Error initializing weather service: " + e.getMessage());
            }
        };
        
        // Create and show the map selector
        MapSelector mapSelector = new MapSelector(currentStage, locationCallback);
        
        // Optionally, if you have previously selected coordinates, you can show the map at that location
        if (this.latitude != 0 && this.longitude != 0) {
            mapSelector.show(this.latitude, this.longitude);
        } else {
            mapSelector.show();
        }
    }
}