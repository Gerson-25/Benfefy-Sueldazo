package com.appbenefy.sueldazo.entity.firebase;

import java.io.Serializable;

public class Cupon implements Serializable {
    private String idCategoria;
    private String titulo;
    private String subtitulo;
    private String precioDesc;
    private String precioReal;
    private String descripcion;
    private String tyc;
    private String cantCanje;
    private String cantCanjeUSER;
    private String fechaInicio;
    private String fechaFin;
    private String imagenCupon;
    private String imagenComercio;
    private String nombreComercio;
    private String idComercio;
    private String whatsapp;
    private String instagram;
    private String facebook;
    private String pais;
    private String fbCodeType = null;
    private boolean fav;

    public Cupon() {
    }

    public Cupon(String idCategoria, String titulo, String subtitulo, String precioDesc, String precioReal, String descripcion, String tyc, String cantCanje, String cantCanjeUSER, String fechaInicio, String fechaFin, String imagenCupon, String imagenComercio, String nombreComercio, String idComercio, String whatsapp, String instagram, String facebook, String pais, String fbCodeType, boolean fav) {
        this.idCategoria = idCategoria;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.precioDesc = precioDesc;
        this.precioReal = precioReal;
        this.descripcion = descripcion;
        this.tyc = tyc;
        this.cantCanje = cantCanje;
        this.cantCanjeUSER = cantCanjeUSER;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.imagenCupon = imagenCupon;
        this.imagenComercio = imagenComercio;
        this.nombreComercio = nombreComercio;
        this.idComercio = idComercio;
        this.whatsapp = whatsapp;
        this.instagram = instagram;
        this.facebook = facebook;
        this.pais = pais;
        this.fbCodeType = fbCodeType;
        this.fav = fav;
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getPrecioDesc() {
        return precioDesc;
    }

    public void setPrecioDesc(String precioDesc) {
        this.precioDesc = precioDesc;
    }

    public String getPrecioReal() {
        return precioReal;
    }

    public void setPrecioReal(String precioReal) {
        this.precioReal = precioReal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTyc() {
        return tyc;
    }

    public void setTyc(String tyc) {
        this.tyc = tyc;
    }

    public String getCantCanje() {
        return cantCanje;
    }

    public void setCantCanje(String cantCanje) {
        this.cantCanje = cantCanje;
    }

    public String getCantCanjeUSER() {
        return cantCanjeUSER;
    }

    public void setCantCanjeUSER(String cantCanjeUSER) {
        this.cantCanjeUSER = cantCanjeUSER;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getImagenCupon() {
        return imagenCupon;
    }

    public void setImagenCupon(String imagenCupon) {
        this.imagenCupon = imagenCupon;
    }

    public String getImagenComercio() {
        return imagenComercio;
    }

    public void setImagenComercio(String imagenComercio) {
        this.imagenComercio = imagenComercio;
    }

    public String getNombreComercio() {
        return nombreComercio;
    }

    public void setNombreComercio(String nombreComercio) {
        this.nombreComercio = nombreComercio;
    }

    public String getIdComercio() {
        return idComercio;
    }

    public void setIdComercio(String idComercio) {
        this.idComercio = idComercio;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getFbCodeType() {
        return fbCodeType;
    }

    public void setFbCodeType(String fbCodeType) {
        this.fbCodeType = fbCodeType;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }
}
