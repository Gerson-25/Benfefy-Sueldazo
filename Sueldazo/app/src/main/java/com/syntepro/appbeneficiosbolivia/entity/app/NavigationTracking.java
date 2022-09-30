package com.syntepro.appbeneficiosbolivia.entity.app;

import java.util.ArrayList;

public class NavigationTracking {

    private String pais;
    private String idUsuario;
    private String dispositivo;
    private ArrayList<NavigationCategory> categorias;
    private ArrayList<NavigationCoupon> cupones;

    public NavigationTracking() {
    }

    public NavigationTracking(String pais, String idUsuario, String dispositivo, ArrayList<NavigationCategory> categorias, ArrayList<NavigationCoupon> cupones) {
        this.pais = pais;
        this.idUsuario = idUsuario;
        this.dispositivo = dispositivo;
        this.categorias = categorias;
        this.cupones = cupones;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public ArrayList<NavigationCategory> getCategorias() {
        return categorias;
    }

    public void setCategorias(ArrayList<NavigationCategory> categorias) {
        this.categorias = categorias;
    }

    public ArrayList<NavigationCoupon> getCupones() {
        return cupones;
    }

    public void setCupones(ArrayList<NavigationCoupon> cupones) {
        this.cupones = cupones;
    }
}
