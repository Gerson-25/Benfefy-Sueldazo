package com.syntepro.sueldazo.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.syntepro.sueldazo.room.converter.DateConverter;

import java.util.Date;

@Entity(tableName = "navigationCouponUser")
@TypeConverters(DateConverter.class)
public class NavigationCouponUser {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "fechaRegistro")
    private Date fechaRegistro;

    @ColumnInfo(name = "idCupon")
    private String idCupon;

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

    public String getIdCupon() {
        return idCupon;
    }

    public void setIdCupon(String idCupon) {
        this.idCupon = idCupon;
    }
}
