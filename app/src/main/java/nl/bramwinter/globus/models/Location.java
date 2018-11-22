package nl.bramwinter.globus.models;

public class Location {

    private Long uuid;
    private double latitude;
    private double longitude;
    private String name;
    private String icon;

    public Location(Long uuid, double latitude, double longitude, String name, String icon) {
        this.uuid = uuid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.icon = icon;
    }

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
