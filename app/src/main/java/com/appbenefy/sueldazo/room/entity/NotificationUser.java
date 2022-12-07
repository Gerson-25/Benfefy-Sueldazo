package com.appbenefy.sueldazo.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.appbenefy.sueldazo.room.converter.DateConverter;

import java.util.Date;

@Entity(tableName = "notificationUser")
@TypeConverters(DateConverter.class)
public class NotificationUser {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "fechaNotificacion")
    private Date fechaNotificacion;

    @ColumnInfo(name = "notificacion")
    private int notificacion;

    @ColumnInfo(name = "longitud")
    private double longitud;

    @ColumnInfo(name = "latitud")
    private double latitud;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFechaNotificacion() {
        return fechaNotificacion;
    }

    public void setFechaNotificacion(Date fechaNotificacion) {
        this.fechaNotificacion = fechaNotificacion;
    }

    public int getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(int notificacion) {
        this.notificacion = notificacion;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }
}
