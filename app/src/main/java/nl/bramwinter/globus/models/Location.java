package nl.bramwinter.globus.models;

import java.util.Date;

public class Location {

    private Long uuid;
    private double latitude;
    private double longitude;
    private Date addedAt;
    private String name;
    private String icon;

    public Location(double latitude, double longitude, Date addedAt, String name, String icon) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.addedAt = addedAt;
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

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }
}
