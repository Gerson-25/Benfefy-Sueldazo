package com.syntepro.sueldazo.entity.app;

public class Pais {

    private int idPais;
    private String nombre;
    private String abreviacion;
    private String moneda;
    private String codigoArea;
    private String timeZone;
    private String bandera;
    private String depto;
    private String muni;

    public Pais() {
    }

    public Pais(int idPais, String nombre, String abreviacion, String moneda, String codigoArea, String timeZone, String bandera, String depto, String muni) {
        this.idPais = idPais;
        this.nombre = nombre;
        this.abreviacion = abreviacion;
        this.moneda = moneda;
        this.codigoArea = codigoArea;
        this.timeZone = timeZone;
        this.bandera = bandera;
        this.depto = depto;
        this.muni = muni;
    }

    public int getIdPais() {
        return idPais;
    }

    public void setIdPais(int idPais) {
        this.idPais = idPais;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAbreviacion() {
        return abreviacion;
    }

    public void setAbreviacion(String abreviacion) {
        this.abreviacion = abreviacion;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getCodigoArea() {
        return codigoArea;
    }

    public void setCodigoArea(String codigoArea) {
        this.codigoArea = codigoArea;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getBandera() {
        return bandera;
    }

    public void setBandera(String bandera) {
        this.bandera = bandera;
    }

    public String getDepto() {
        return depto;
    }

    public void setDepto(String depto) {
        this.depto = depto;
    }

    public String getMuni() {
        return muni;
    }

    public void setMuni(String muni) {
        this.muni = muni;
    }
}
