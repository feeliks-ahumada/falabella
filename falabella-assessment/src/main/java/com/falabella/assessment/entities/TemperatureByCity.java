package com.falabella.assessment.entities;

public class TemperatureByCity {
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(float averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    public float getAvgTempUncertainty() {
        return avgTempUncertainty;
    }

    public void setAvgTempUncertainty(float avgTempUncertainty) {
        this.avgTempUncertainty = avgTempUncertainty;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatitude() {
        return latitude.replaceAll("[0-9]", "X");
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude.replaceAll("[0-9]", "X");
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    private String date;
    private float averageTemperature;
    private float avgTempUncertainty;
    private String city;
    private String country;
    private String latitude;
    private String longitude;

}
