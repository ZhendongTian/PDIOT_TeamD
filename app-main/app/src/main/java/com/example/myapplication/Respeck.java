package com.example.myapplication;

public class Respeck {
    private String acc_x;
    private String acc_y;
    private String acc_z;
    private String gyro_x;
    private String gyro_y;
    private String gyro_z;

    public Respeck() {
    }

    public Respeck(String acc_x, String acc_y, String acc_z, String gyro_x, String gyro_y, String gyro_z) {
        this.acc_x = acc_x;
        this.acc_y = acc_y;
        this.acc_z = acc_z;
        this.gyro_x = gyro_x;
        this.gyro_y = gyro_y;
        this.gyro_z = gyro_z;
    }

    public String getAcc_x() {
        return acc_x;
    }

    public void setAcc_x(String acc_x) {
        this.acc_x = acc_x;
    }

    public String getAcc_y() {
        return acc_y;
    }

    public void setAcc_y(String acc_y) {
        this.acc_y = acc_y;
    }

    public String getAcc_z() {
        return acc_z;
    }

    public void setAcc_z(String acc_z) {
        this.acc_z = acc_z;
    }

    public String getGyro_x() {
        return gyro_x;
    }

    public void setGyro_x(String gyro_x) {
        this.gyro_x = gyro_x;
    }

    public String getGyro_y() {
        return gyro_y;
    }

    public void setGyro_y(String gyro_y) {
        this.gyro_y = gyro_y;
    }

    public String getGyro_z() {
        return gyro_z;
    }

    public void setGyro_z(String gyro_z) {
        this.gyro_z = gyro_z;
    }
}