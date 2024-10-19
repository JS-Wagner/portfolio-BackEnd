
package com.jwportfolio.jsw.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jwportfolio.jsw.Security.Entity.Usuario;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombreE;
    private String descripcionE;
    private String fechaE;
    private boolean temporal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference
    private Usuario usuario; // Campo para almacenar el usuario que creó la educación
    
    //Constructor

    public Proyecto() {
    }

    public Proyecto(String nombreE, String descripcionE, String fechaE, boolean temporal, Usuario usuario) {
        this.nombreE = nombreE;
        this.descripcionE = descripcionE;
        this.fechaE = fechaE;
        this.temporal = temporal;
        this.usuario = usuario;
    }
    
    //Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreE() {
        return nombreE;
    }

    public void setNombreE(String nombreE) {
        this.nombreE = nombreE;
    }

    public String getDescripcionE() {
        return descripcionE;
    }

    public void setDescripcionE(String descripcionE) {
        this.descripcionE = descripcionE;
    }
    
    public String getFechaE() {return this.fechaE;}

    public void setFechaE(String fechaE) {this.fechaE = fechaE;}

    public boolean isTemporal() {return this.temporal;}

    public void setTemporal(boolean temporal) {this.temporal = temporal;}

    public Usuario getUsuario() {return usuario;}

    public void setUsuario(Usuario usuario) {this.usuario = usuario;}
}
