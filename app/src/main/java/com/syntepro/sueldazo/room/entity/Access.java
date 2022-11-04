package com.syntepro.sueldazo.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "access")
public class Access {

    @PrimaryKey
    @NonNull
    private String documentID;

    @ColumnInfo(name = "idCategoria")
    private String idCategoria;

    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "icono")
    private String icono;

    @ColumnInfo(name = "nombreImagen")
    private String nombreImagen;

    @NonNull
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(@NonNull String documentID) {
        this.documentID = documentID;
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getNombreImagen() {
        return nombreImagen;
    }

    public void setNombreImagen(String nombreImagen) {
        this.nombreImagen = nombreImagen;
    }
}
