package com.syntepro.appbeneficiosbolivia.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.syntepro.appbeneficiosbolivia.room.converter.DateConverter;

import java.util.Date;

@Entity(tableName = "firebaseToken")
@TypeConverters(DateConverter.class)
public class FirebaseToken {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "fechaGeneracion")
    private Date fechaGeneracion;

    @ColumnInfo(name = "token")
    private String token;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(Date fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

