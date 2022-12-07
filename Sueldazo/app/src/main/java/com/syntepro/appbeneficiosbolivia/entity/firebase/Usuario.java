package com.appbenefy.sueldazo.entity.firebase;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Usuario implements Serializable {

    @DocumentId
    private String id;
    private String nombre;
    private String apellido;
    private String fechaNac;
    private String genero;
    private String correo;
    private String contrasena;
    private String provincia;
    private String departamento;
    private String tyc;
    private String fechaTyc;
    private String estadoCivil;
    private String numeroTel;
    private String imagenPerfil;
    private String pais;
    private String codigoArea;
    private String abreviacion;
    private String moneda;

    public Usuario() {
    }

    public Usuario(String id, String nombre, String apellido, String fechaNac, String genero, String correo, String contrasena, String provincia, String departamento, String tyc, String fechaTyc, String estadoCivil, String numeroTel, String imagenPerfil, String pais, String codigoArea, String abreviacion, String moneda) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNac = fechaNac;
        this.genero = genero;
        this.correo = correo;
        this.contrasena = contrasena;
        this.provincia = provincia;
        this.departamento = departamento;
        this.tyc = tyc;
        this.fechaTyc = fechaTyc;
        this.estadoCivil = estadoCivil;
        this.numeroTel = numeroTel;
        this.imagenPerfil = imagenPerfil;
        this.pais = pais;
        this.codigoArea = codigoArea;
        this.abreviacion = abreviacion;
        this.moneda = moneda;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getTyc() {
        return tyc;
    }

    public void setTyc(String tyc) {
        this.tyc = tyc;
    }

    public String getFechaTyc() {
        return fechaTyc;
    }

    public void setFechaTyc(String fechaTyc) {
        this.fechaTyc = fechaTyc;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getNumeroTel() {
        return numeroTel;
    }

    public void setNumeroTel(String numeroTel) {
        this.numeroTel = numeroTel;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCodigoArea() {
        return codigoArea;
    }

    public void setCodigoArea(String codigoArea) {
        this.codigoArea = codigoArea;
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
}
