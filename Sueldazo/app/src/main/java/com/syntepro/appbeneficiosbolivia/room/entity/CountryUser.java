package com.syntepro.appbeneficiosbolivia.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.syntepro.appbeneficiosbolivia.room.converter.DateConverter;

import java.util.Date;

@Entity(tableName = "countryUser")
@TypeConverters(DateConverter.class)
public class CountryUser {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "fechaActualizacion")
    private Date fechaActualizacion;

    @ColumnInfo(name = "pais")
    private String pais;

    @ColumnInfo(name = "codArea")
    private String codArea;

    @ColumnInfo(name = "abr")
    private String abr;

    @ColumnInfo(name = "moneda")
    private String moneda;

    @ColumnInfo(name = "timeZone")
    private String timeZone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCodArea() {
        return codArea;
    }

    public void setCodArea(String codArea) {
        this.codArea = codArea;
    }

    public String getAbr() {
        return abr;
    }

    public void setAbr(String abr) {
        this.abr = abr;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
