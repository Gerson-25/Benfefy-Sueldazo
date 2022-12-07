package com.appbenefy.sueldazo.entity.service;

public class CercaDeTi {

    private String idSucursal, idComercio, nombreComercio, urlImagen, nombreSucursal, campanas, distance, direccion, latitude, longitud;

    public CercaDeTi() {
    }

    public CercaDeTi(String idSucursal, String idComercio, String nombreComercio, String urlImagen, String nombreSucursal, String campanas, String distance, String direccion, String latitude, String longitud) {
        this.idSucursal = idSucursal;
        this.idComercio = idComercio;
        this.nombreComercio = nombreComercio;
        this.urlImagen = urlImagen;
        this.nombreSucursal = nombreSucursal;
        this.campanas = campanas;
        this.distance = distance;
        this.direccion = direccion;
        this.latitude = latitude;
        this.longitud = longitud;
    }

    public String getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(String idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getIdComercio() {
        return idComercio;
    }

    public void setIdComercio(String idComercio) {
        this.idComercio = idComercio;
    }

    public String getNombreComercio() {
        return nombreComercio;
    }

    public void setNombreComercio(String nombreComercio) {
        this.nombreComercio = nombreComercio;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public String getCampanas() {
        return campanas;
    }

    public void setCampanas(String campanas) {
        this.campanas = campanas;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
