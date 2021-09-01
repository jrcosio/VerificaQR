package com.jrblanco.verificaqr;

public class DatosCertificado {
    private boolean Estado;
    private String apellidos;
    private String nombre;
    private String nombrecompleto;
    private String fechaNacimiento;

    public boolean getEstado() { return Estado; }

    public void setEstado(boolean estado) { Estado = estado; }

    public String getApellidos() { return apellidos; }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombrecompleto() {
        return nombrecompleto;
    }

    public void setNombrecompleto(String nombrecompleto) {
        this.nombrecompleto = nombrecompleto;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

}
