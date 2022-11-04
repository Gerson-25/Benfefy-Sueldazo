package com.syntepro.sueldazo.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.syntepro.sueldazo.room.converter.DateConverter;

import java.util.Date;

@Entity(tableName = "navigationCategoryUser")
@TypeConverters(DateConverter.class)
public class NavigationCategoryUser {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "fechaRegistro")
    private Date fechaRegistro;

    @ColumnInfo(name = "idCategoria")
    private String idCategoria;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }
}
