package controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.solar.MapSelector;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SolarPanelCalculatorController implements Initializable {
    
    // Top Section
    @FXML private Button selectLocationBtn;
    @FXML private CheckBox calculatedHorizonCb;
    @FXML private CheckBox uploadHorizonCb;
    
    // Left Menu
    @FXML private Button gridConnectedBtn;
    @FXML private Button trackingPvBtn;
    @FXML private Button offGridBtn;
    @FXML private Button monthlyDataBtn;
    @FXML private Button dailyDataBtn;
    @FXML private Button hourlyDataBtn;
    @FXML private Button tmyBtn;
    
    // Main Form
    @FXML private Button helpBtn;
    @FXML private ComboBox<String> pvTechCombo;
    @FXML private TextField peakPowerField;
    @FXML private TextField systemLossField;
    @FXML private ComboBox<String> mountingPosCombo;
    @FXML private TextField slopeField;
    @FXML private CheckBox optimizeSlopeCb;
    @FXML private TextField azimuthField;
    @FXML private CheckBox optimizeAzimuthCb;
    @FXML private CheckBox pvElectricityPriceCb;
    @FXML private TextField pvSystemCostField;
    @FXML private TextField interestField;
    @FXML private TextField lifetimeField;
    
    // Bottom Buttons
    @FXML private Button visualizeBtn;
    @FXML private Button csvBottomBtn;
    @FXML private Button jsonBottomBtn;

    // Fields to store location data
    private double latitude = 0.0;
    private double longitude = 0.0;
    private double elevation = 0.0;

    // Label to display selected location
    @FXML private Label selectedLocationLabel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ComboBoxes with values
        pvTechCombo.getItems().addAll("Crystalline silicon", "CIS", "CdTe", "Unknown");
        pvTechCombo.setValue("Crystalline silicon");
        
        mountingPosCombo.getItems().addAll("Free-standing", "Building integrated");
        mountingPosCombo.setValue("Free-standing");
        
        // Initialize form validation listeners
        setupValidationListeners();
        
        // Set up checkbox dependencies
        setupCheckboxDependencies();
    }
    
    private void setupValidationListeners() {
        // Add numeric validation for text fields
        addNumericValidation(peakPowerField);
        addNumericValidation(systemLossField);
        addNumericValidation(slopeField);
        addNumericValidation(azimuthField);
        addNumericValidation(pvSystemCostField);
        addNumericValidation(interestField);
        addNumericValidation(lifetimeField);
    }
    
    private void addNumericValidation(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                field.setText(oldValue);
            }
        });
    }
    
    private void setupCheckboxDependencies() {
        // When optimize slope is checked, disable slope field
        optimizeSlopeCb.selectedProperty().addListener((observable, oldValue, newValue) -> {
            slopeField.setDisable(newValue);
            // If both optimizations are selected, prioritize the combined one
            if (newValue && optimizeAzimuthCb.isSelected()) {
                optimizeSlopeCb.setSelected(false);
            }
        });
        
        // When optimize slope and azimuth is checked, disable both fields
        optimizeAzimuthCb.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                slopeField.setDisable(true);
                azimuthField.setDisable(true);
                // Ensure single optimization is not selected at the same time
                optimizeSlopeCb.setSelected(false);
            } else {
                // Only re-enable if the single optimization isn't selected
                if (!optimizeSlopeCb.isSelected()) {
                    slopeField.setDisable(false);
                }
                azimuthField.setDisable(false);
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
        for (Button btn : new Button[]{gridConnectedBtn, trackingPvBtn, offGridBtn, 
                                       monthlyDataBtn, dailyDataBtn, hourlyDataBtn, tmyBtn}) {
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
    private void handleVisualizeResults(ActionEvent event) {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }
        
        // Perform calculations and display results
        // This would typically call your calculation engine/model
        System.out.println("Visualizing results with the following parameters:");
        System.out.println("Peak Power: " + peakPowerField.getText() + " kWp");
        System.out.println("System Loss: " + systemLossField.getText() + "%");
        System.out.println("Slope: " + slopeField.getText() + "°");
        System.out.println("Azimuth: " + azimuthField.getText() + "°");
        
        // In a real app, this would display charts/tables with the results
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Results");
        alert.setHeaderText("Solar Panel Performance Results");
        alert.setContentText("Calculation completed. Results would be displayed here in a real application.");
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
        if (!optimizeSlopeCb.isSelected() && !optimizeAzimuthCb.isSelected() && slopeField.getText().isEmpty()) {
            errorMessage.append("- Slope must be specified or optimization must be enabled.\n");
        }
        if (!optimizeAzimuthCb.isSelected() && azimuthField.getText().isEmpty()) {
            errorMessage.append("- Azimuth must be specified or optimization must be enabled.\n");
        }
        
        // If there are errors, show them and return false
        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Please correct the following errors:");
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void handleCsvExport(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export as CSV");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File file = fileChooser.showSaveDialog(csvBottomBtn.getScene().getWindow());
        if (file != null) {
            System.out.println("Exporting to CSV: " + file.getAbsolutePath());
            // TODO: Implementation for CSV export
        }
    }
    
    @FXML
    private void handleJsonExport(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export as JSON");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        File file = fileChooser.showSaveDialog(jsonBottomBtn.getScene().getWindow());
        if (file != null) {
            System.out.println("Exporting to JSON: " + file.getAbsolutePath());
            // TODO: Implementation for JSON export
        }
    }

    /**
     * Handles the selection of a location on the map
     */
    @FXML
    public void handleSelectLocation() {
        // Get the current stage
        Stage currentStage = (Stage) selectLocationBtn.getScene().getWindow();
        
        // Create callback to handle selected location data
        Consumer<MapSelector.LocationData> locationCallback = (locationData) -> {
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
            selectLocationBtn.setText("Location selected");
            selectLocationBtn.setStyle("-fx-text-fill: #009900;");
            
            // You can now handle the elevation data directly from the locationData object
            // No need to fetch it separately
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

    public void setElevation(double elevation) {
        this.elevation = elevation;
        // TODO: Update UI or other components that need elevation data
    }
}