package nl.bramwinter.globus.models;

import java.util.Date;
import java.util.Map;

public class Location {

    private String uuid;
    private double latitude;
    private double longitude;
    private Date addedAt;
    private String name;
    private Integer icon;

    public Location(double latitude, double longitude, Date addedAt, String name, Integer icon) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.addedAt = addedAt;
        this.name = name;
        this.icon = icon;
    }

    public Location(Map map){
        try {
            this.uuid = (String) map.get("uuid");
            this.latitude = (double) map.get("latitude");
            this.longitude = (double) map.get("longitude");
            this.addedAt = (Date) map.get("addedAt");
            this.name = (String) map.get("name");
            this.icon = (Integer) map.get("icon");

        } catch (Exception e){

        }
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
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

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }
}
