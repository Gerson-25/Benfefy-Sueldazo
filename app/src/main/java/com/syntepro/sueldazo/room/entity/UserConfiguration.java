package com.syntepro.sueldazo.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.syntepro.sueldazo.room.converter.DateConverter;

import java.util.Date;

@Entity(tableName = "userConfiguration")
@TypeConverters(DateConverter.class)
public class UserConfiguration {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "fechaModificacion")
    private Date fechaModificacion;

    @ColumnInfo(name = "nuevoCupon")
    private boolean nuevoCupon;

    @ColumnInfo(name = "ruta")
    private boolean ruta;

    @ColumnInfo(name = "proximos")
    private boolean proximos;

    @ColumnInfo(name = "numNotificaciones")
    private int numNotificaciones;

    @ColumnInfo(name = "idioma")
    private String idioma;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public boolean isNuevoCupon() {
        return nuevoCupon;
    }

    public void setNuevoCupon(boolean nuevoCupon) {
        this.nuevoCupon = nuevoCupon;
    }

    public boolean isRuta() {
        return ruta;
    }

    public void setRuta(boolean ruta) {
        this.ruta = ruta;
    }

    public boolean isProximos() {
        return proximos;
    }

    public void setProximos(boolean proximos) {
        this.proximos = proximos;
    }

    public int getNumNotificaciones() {
        return numNotificaciones;
    }

    public void setNumNotificaciones(int numNotificaciones) {
        this.numNotificaciones = numNotificaciones;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
}
