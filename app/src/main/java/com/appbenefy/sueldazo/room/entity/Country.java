package com.appbenefy.sueldazo.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.appbenefy.sueldazo.room.converter.DateConverter;

import java.util.Date;

@Entity(tableName = "country")
@TypeConverters(DateConverter.class)
public class Country {

    @PrimaryKey
    private int documentID;

    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "codArea")
    private String codArea;

    @ColumnInfo(name = "abreviacion")
    private String abreviacion;

    @ColumnInfo(name = "fechaActualizacion")
    private Date fechaActualizacion;

    public int getDocumentID() {
        return documentID;
    }

    public void setDocumentID(int documentID) {
        this.documentID = documentID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodArea() {
        return codArea;
    }

    public void setCodArea(String codArea) {
        this.codArea = codArea;
    }

    public String getAbreviacion() {
        return abreviacion;
    }

    public void setAbreviacion(String abreviacion) {
        this.abreviacion = abreviacion;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
