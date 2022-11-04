package com.syntepro.sueldazo.entity.app;

public class Provincia {

    private int idDcoumento, idDepto;
    private String nombre;

    public Provincia() {
    }

    public Provincia(int idDcoumento, int idDepto, String nombre) {
        this.idDcoumento = idDcoumento;
        this.idDepto = idDepto;
        this.nombre = nombre;
    }

    public int getIdDcoumento() {
        return idDcoumento;
    }

    public void setIdDcoumento(int idDcoumento) {
        this.idDcoumento = idDcoumento;
    }

    public int getIdDepto() {
        return idDepto;
    }

    public void setIdDepto(int idDepto) {
        this.idDepto = idDepto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
