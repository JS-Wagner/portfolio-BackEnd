
package com.jwportfolio.jsw.Entity;

import com.jwportfolio.jsw.Security.Entity.Usuario;

import javax.persistence.*;

@Entity
public class hys {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private int porcentaje;
    private boolean temporal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Campo para almacenar el usuario que creó la educación

    public hys() {
    }

    public hys(String nombre, int porcentaje, boolean temporal, Usuario usuario) {
        this.nombre = nombre;
        this.porcentaje = porcentaje;
        this.temporal = temporal;
        this.usuario = usuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public boolean isTemporal() {return this.temporal;}

    public void setTemporal(boolean temporal) {this.temporal = temporal;}

    public Usuario getUsuario() {return usuario;}

    public void setUsuario(Usuario usuario) {this.usuario = usuario;}
}
