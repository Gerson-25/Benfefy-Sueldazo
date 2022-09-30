package com.syntepro.appbeneficiosbolivia.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "locationServiceCounter")
public class LocationServiceCounter {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "notificationWSCounter")
    private int notificationWSCounter;

    @ColumnInfo(name = "locationServiceCounter")
    private int locationServiceCounter;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNotificationWSCounter() {
        return notificationWSCounter;
    }

    public void setNotificationWSCounter(int notificationWSCounter) {
        this.notificationWSCounter = notificationWSCounter;
    }

    public int getLocationServiceCounter() {
        return locationServiceCounter;
    }

    public void setLocationServiceCounter(int locationServiceCounter) {
        this.locationServiceCounter = locationServiceCounter;
    }
}
