package com.syntepro.sueldazo.entity.app;

public class Departamento {

    private int idDepartamento, idPais;
    private String nombre, bandera;

    public Departamento() {
    }

    public Departamento(int idDepartamento, int idPais, String nombre, String bandera) {
        this.idDepartamento = idDepartamento;
        this.idPais = idPais;
        this.nombre = nombre;
        this.bandera = bandera;
    }

    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
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

    public String getBandera() {
        return bandera;
    }

    public void setBandera(String bandera) {
        this.bandera = bandera;
    }
}
