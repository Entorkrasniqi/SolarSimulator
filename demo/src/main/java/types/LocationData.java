package types;

public class LocationData {
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