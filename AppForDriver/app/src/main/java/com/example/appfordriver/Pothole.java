package com.example.appfordriver;

public class Pothole {
    private double latitude;
    private double longitude;

    // 기본 생성자 (Firebase가 객체를 생성하는 데 필요)
    public Pothole() {
    }

    public Pothole(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
}
