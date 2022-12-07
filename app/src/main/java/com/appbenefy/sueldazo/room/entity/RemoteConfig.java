package com.appbenefy.sueldazo.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.appbenefy.sueldazo.room.converter.DateConverter;

import java.util.Date;

@Entity(tableName = "remoteConfig")
@TypeConverters(DateConverter.class)
public class RemoteConfig {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "microServicio")
    private String microServicio;

    @ColumnInfo(name = "tracking")
    private String tracking;

    @ColumnInfo(name = "isBeneficio")
    private boolean isBeneficio;

    @ColumnInfo(name = "minSpeed")
    private int minSpeed;

    @ColumnInfo(name = "minNotification")
    private int minNotification;

    @ColumnInfo(name = "fechaIngreso")
    private Date fechaIngreso;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMicroServicio() {
        return microServicio;
    }

    public void setMicroServicio(String microServicio) {
        this.microServicio = microServicio;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public boolean isBeneficio() {
        return isBeneficio;
    }

    public void setBeneficio(boolean beneficio) {
        isBeneficio = beneficio;
    }

    public int getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(int minSpeed) {
        this.minSpeed = minSpeed;
    }

    public int getMinNotification() {
        return minNotification;
    }

    public void setMinNotification(int minNotification) {
        this.minNotification = minNotification;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
}
