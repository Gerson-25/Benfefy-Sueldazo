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
    private String estadoCivil;
    private String numeroTel;
    private String imagenPerfil;
    private String country;

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
        this.estadoCivil = estadoCivil;
        this.numeroTel = numeroTel;
        this.imagenPerfil = imagenPerfil;
        this.country = pais;
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
        return country;
    }

    public void setPais(String pais) {
        this.country = pais;
    }
}
