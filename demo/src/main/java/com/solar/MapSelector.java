package com.solar;

import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.concurrent.Worker.State;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import netscape.javascript.JSObject;
import java.util.function.Consumer;

public class MapSelector {
    private WebView webView;
    private WebEngine webEngine;
    private TextField latitudeField;
    private TextField longitudeField;
    private TextField elevationField;
    private TextField addressField;
    private Consumer<LocationData> locationCallback;
    private Stage mapStage;
    
    /**
     * Data class to hold location information
     */
    public static class LocationData {
        private final double latitude;
        private final double longitude;
        private final double elevation;
        private final String address;
        
        public LocationData(double latitude, double longitude, double elevation, String address) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.elevation = elevation;
            this.address = address;
        }
        
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public double getElevation() { return elevation; }
        public String getAddress() { return address; }
        
        @Override
        public String toString() {
            return String.format("Location: %.6f, %.6f, Elevation: %.1fm, Address: %s", 
                                 latitude, longitude, elevation, address);
        }
    }
    
    /**
     * Creates a new map selector component
     * @param parentStage The parent stage to attach the modal dialog to
     * @param locationCallback Callback function that receives the selected location data
     */
    public MapSelector(Stage parentStage, Consumer<LocationData> locationCallback) {
        this.locationCallback = locationCallback;
        
        // Create the web view for the map
        webView = new WebView();
        webEngine = webView.getEngine();
        
        // Create coordinate display fields
        latitudeField = createFormattedTextField("Latitude", "51.505");
        longitudeField = createFormattedTextField("Longitude", "-0.09");
        elevationField = createFormattedTextField("Elevation (m)", "0");
        addressField = createFormattedTextField("Address", "");
        addressField.setPrefWidth(300);
        
        // Create search box
        TextField searchField = new TextField();
        searchField.setPromptText("Search for location...");
        searchField.setPrefWidth(300);
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                // Execute search in JavaScript
                webEngine.executeScript(
                    "searchLocation('" + query.replace("'", "\\'") + "');"
                );
            }
        });
        
        // Create confirm and cancel buttons
        Button confirmButton = new Button("Confirm Location");
        confirmButton.getStyleClass().add("button-primary");
        confirmButton.setOnAction(e -> confirmLocation());
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> mapStage.close());
        
        // Layout for search
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(10));
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.getChildren().addAll(
            new Label("Search:"), searchField, searchButton
        );
        HBox.setHgrow(searchField, Priority.ALWAYS);
        
        // Layout for coordinates display
        VBox coordsBox = new VBox(10);
        coordsBox.setPadding(new Insets(10));
        
        HBox latLngBox = new HBox(10);
        latLngBox.getChildren().addAll(
            new Label("Latitude:"), latitudeField,
            new Label("Longitude:"), longitudeField,
            new Label("Elevation:"), elevationField
        );
        
        HBox addressBox = new HBox(10);
        addressBox.getChildren().addAll(
            new Label("Address:"), addressField
        );
        HBox.setHgrow(addressField, Priority.ALWAYS);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(cancelButton, confirmButton);
        
        coordsBox.getChildren().addAll(latLngBox, addressBox, buttonBox);
        
        // Main layout
        BorderPane mapPane = new BorderPane();
        mapPane.setTop(searchBox);
        mapPane.setCenter(webView);
        mapPane.setBottom(coordsBox);
        
        // Create stage for the map
        mapStage = new Stage();
        mapStage.initModality(Modality.APPLICATION_MODAL);
        mapStage.initOwner(parentStage);
        mapStage.setTitle("Select Location for Solar Installation");
        mapStage.setScene(new Scene(mapPane, 900, 700));
        
        // Apply CSS styling - safely handle missing CSS file
        try {
            java.net.URL cssUrl = getClass().getResource("/styles/main.css");
            if (cssUrl != null) {
                mapStage.getScene().getStylesheets().add(cssUrl.toExternalForm());
            } else {
                // Apply basic styling inline if CSS file is not found
                Scene scene = mapStage.getScene();
                scene.getRoot().setStyle("-fx-font-family: 'Arial'; -fx-background-color: #f4f4f4;");
            }
        } catch (Exception e) {
            System.err.println("Could not load CSS file: " + e.getMessage());
        }
        
        // Load the map
        loadMap();
    }
    
    /**
     * Creates a formatted text field with label
     */
    private TextField createFormattedTextField(String prompt, String defaultValue) {
        TextField field = new TextField(defaultValue);
        field.setPromptText(prompt);
        field.setPrefWidth(120);
        return field;
    }
    
    /**
     * Confirms the location selection
     */
    private void confirmLocation() {
        try {
            // Parse values with explicit US locale to handle decimal point consistently
            java.text.NumberFormat nf = java.text.NumberFormat.getInstance(java.util.Locale.US);
            double lat = nf.parse(latitudeField.getText()).doubleValue();
            double lng = nf.parse(longitudeField.getText()).doubleValue();
            double elevation = nf.parse(elevationField.getText()).doubleValue();
            String address = addressField.getText();
            
            // Validate coordinates
            if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
                throw new IllegalArgumentException("Coordinates out of valid range");
            }
            
            // Create location data and pass to callback
            LocationData location = new LocationData(lat, lng, elevation, address);
            locationCallback.accept(location);
            mapStage.close();
        } catch (Exception ex) {
            // Show error to user
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Coordinate Error");
            alert.setHeaderText("Invalid Coordinate Format");
            alert.setContentText("Please ensure valid coordinates are selected. Error: " + ex.getMessage());
            alert.showAndWait();
        }
    }
    
    /**
     * Loads the OpenStreetMap map using Leaflet.js with improved functionality
     */
    private void loadMap() {
        String mapHtml = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Solar Location Selector</title>
                <meta charset="utf-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.css" />
                <script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.js"></script>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet-control-geocoder/2.4.0/Control.Geocoder.min.js"></script>
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet-control-geocoder/2.4.0/Control.Geocoder.css" />
                <style>
                    html, body, #map {
                        height: 100%;
                        width: 100%;
                        margin: 0;
                        padding: 0;
                    }
                    .info-box {
                        padding: 6px 8px;
                        font: 14px/16px Arial, Helvetica, sans-serif;
                        background: white;
                        background: rgba(255,255,255,0.8);
                        box-shadow: 0 0 15px rgba(0,0,0,0.2);
                        border-radius: 5px;
                    }
                    .info-box h4 {
                        margin: 0 0 5px;
                        color: #777;
                    }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    // Initialize map
                    var map = L.map('map').setView([51.505, -0.09], 13);
                    
                    // Add OpenStreetMap tile layer
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        maxZoom: 19,
                        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    }).addTo(map);
                    
                    // Add satellite imagery as optional layer
                    var satelliteLayer = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
                        attribution: 'Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community'
                    });
                    
                    // Add layer control
                    var baseMaps = {
                        "Street Map": L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            maxZoom: 19,
                            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                        }),
                        "Satellite": satelliteLayer
                    };
                    
                    L.control.layers(baseMaps).addTo(map);
                    
                    // Add scale control
                    L.control.scale().addTo(map);
                    
                    // Create marker
                    var marker = L.marker([51.505, -0.09], {draggable: true}).addTo(map);
                    
                    // Info box for current location
                    var info = L.control();
                    info.onAdd = function (map) {
                        this._div = L.DomUtil.create('div', 'info-box');
                        this.update();
                        return this._div;
                    };
                    info.update = function (lat, lng, address) {
                        lat = lat || 51.505;
                        lng = lng || -0.09;
                        address = address || 'Loading...';
                        this._div.innerHTML = '<h4>Selected Location</h4>' +
                            '<b>Lat:</b> ' + lat.toFixed(6) + '<br>' +
                            '<b>Lng:</b> ' + lng.toFixed(6) + '<br>' +
                            '<b>Address:</b> ' + address;
                    };
                    info.addTo(map);
                    
                    // Add geocoder for search functionality
                    var geocoder = L.Control.geocoder({
                        defaultMarkGeocode: false
                    }).addTo(map);
                    
                    geocoder.on('markgeocode', function(e) {
                        var latlng = e.geocode.center;
                        marker.setLatLng(latlng);
                        map.setView(latlng, 16);
                        updateLocationData(latlng.lat, latlng.lng);
                    });
                    
                    // Update coordinates when marker is moved
                    marker.on('dragend', function(event) {
                        var position = marker.getLatLng();
                        updateLocationData(position.lat, position.lng);
                    });
                    
                    // Update on map click
                    map.on('click', function(e) {
                        marker.setLatLng(e.latlng);
                        updateLocationData(e.latlng.lat, e.latlng.lng);
                    });
                    
                    // Function to update all location data
                    function updateLocationData(lat, lng) {
                        // Ensure values are numbers and properly rounded
                        lat = Number(lat.toFixed(6));
                        lng = Number(lng.toFixed(6));
                        
                        // Update info box
                        info.update(lat, lng, 'Fetching address...');
                        
                        // Send to Java
                        if (window.javaConnector) {
                            try {
                                window.javaConnector.updateCoordinates(lat, lng);
                            } catch (e) {
                                console.error("Error sending coordinates to Java:", e);
                            }
                        }
                        
                        // Fetch address using reverse geocoding
                        reverseGeocode(lat, lng);
                        
                        // Get elevation data
                        getElevation(lat, lng);
                    }
                    
                    // Search for location by name
                    function searchLocation(query) {
                        if (!query) return;
                        
                        fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}`)
                            .then(response => response.json())
                            .then(data => {
                                if (data && data.length > 0) {
                                    var lat = parseFloat(data[0].lat);
                                    var lng = parseFloat(data[0].lon);
                                    marker.setLatLng([lat, lng]);
                                    map.setView([lat, lng], 16);
                                    updateLocationData(lat, lng);
                                } else {
                                    alert("Location not found. Please try a different search term.");
                                }
                            })
                            .catch(error => {
                                console.error('Error searching location:', error);
                                alert("Error searching for location. Please try again.");
                            });
                    }
                    
                    // Reverse geocode to get address
                    function reverseGeocode(lat, lng) {
                        fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1`)
                            .then(response => response.json())
                            .then(data => {
                                var address = data.display_name || "Unknown location";
                                if (window.javaConnector) {
                                    window.javaConnector.updateAddress(address);
                                }
                                info.update(lat, lng, address);
                            })
                            .catch(error => {
                                console.error('Error getting address:', error);
                                if (window.javaConnector) {
                                    window.javaConnector.updateAddress("Address lookup failed");
                                }
                            });
                    }
                    
                    // Get elevation data
                    function getElevation(lat, lng) {
                        fetch(`https://api.open-elevation.com/api/v1/lookup?locations=${lat},${lng}`)
                            .then(response => response.json())
                            .then(data => {
                                if (data.results && data.results.length > 0) {
                                    var elevation = data.results[0].elevation;
                                    if (window.javaConnector) {
                                        window.javaConnector.updateElevation(elevation);
                                    }
                                }
                            })
                            .catch(error => {
                                console.error('Error fetching elevation:', error);
                                if (window.javaConnector) {
                                    window.javaConnector.updateElevation(0);
                                }
                            });
                    }
                    
                    // Try to get user location
                    if (navigator.geolocation) {
                        navigator.geolocation.getCurrentPosition(function(position) {
                            var lat = position.coords.latitude;
                            var lng = position.coords.longitude;
                            map.setView([lat, lng], 13);
                            marker.setLatLng([lat, lng]);
                            updateLocationData(lat, lng);
                        }, function(error) {
                            // Fallback if geolocation fails or is denied
                            console.warn("Geolocation failed:", error.message);
                        });
                    }
                </script>
            </body>
            </html>
        """;
        
        webEngine.loadContent(mapHtml);
        
        // Set up JavaScript bridge once the page is loaded
        webEngine.getLoadWorker().stateProperty().addListener(
            new ChangeListener<State>() {
                @Override
                public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
                    if (newState == State.SUCCEEDED) {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaConnector", new JavaConnector());
                    }
                }
            }
        );
    }
    
    /**
     * Shows the map selector dialog
     */
    public void show() {
        mapStage.show();
    }
    
    /**
     * Shows the map selector dialog with initial coordinates
     */
    public void show(double latitude, double longitude) {
        // Set initial coordinates in the UI
        latitudeField.setText(String.format(java.util.Locale.US, "%.6f", latitude));
        longitudeField.setText(String.format(java.util.Locale.US, "%.6f", longitude));
        
        // Set marker position on map
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            @Override
            public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
                if (newState == State.SUCCEEDED) {
                    webEngine.executeScript(
                        String.format(java.util.Locale.US,
                            "map.setView([%f, %f], 16); " +
                            "marker.setLatLng([%f, %f]); " +
                            "updateLocationData(%f, %f);",
                            latitude, longitude, latitude, longitude, latitude, longitude)
                    );
                    // Remove this listener after it's been executed once
                    webEngine.getLoadWorker().stateProperty().removeListener(this);
                }
            }
        });
        
        mapStage.show();
    }
    
    /**
     * Java connector class for JavaScript callbacks
     */
    public class JavaConnector {
        public void updateCoordinates(double latitude, double longitude) {
            javafx.application.Platform.runLater(() -> {
                // Use a locale-independent format to avoid decimal separator issues
                latitudeField.setText(String.format(java.util.Locale.US, "%.6f", latitude));
                longitudeField.setText(String.format(java.util.Locale.US, "%.6f", longitude));
            });
        }
        
        public void updateElevation(double elevation) {
            javafx.application.Platform.runLater(() -> {
                elevationField.setText(String.format(java.util.Locale.US, "%.1f", elevation));
            });
        }
        
        public void updateAddress(String address) {
            javafx.application.Platform.runLater(() -> {
                addressField.setText(address);
            });
        }
    }
}