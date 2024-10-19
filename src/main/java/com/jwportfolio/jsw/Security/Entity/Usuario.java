package com.jwportfolio.jsw.Security.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jwportfolio.jsw.Entity.Educacion;
import com.jwportfolio.jsw.Entity.Experiencia;
import com.jwportfolio.jsw.Entity.Proyecto;
import com.jwportfolio.jsw.Entity.hys;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;



@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private String nombre;
    @NotNull
    @Column(unique = true)
    private String nombreUsuario;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_rol", joinColumns = @JoinColumn(name ="usuario_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Serializa esta lista
    private List<Educacion> educaciones;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Serializa esta lista
    private List<Experiencia> experiencias;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Serializa esta lista
    private List<hys> skills;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Serializa esta lista
    private List<Proyecto> proyectos;
    
    //Constructores

    public Usuario() {
    }

    public Usuario(String nombre, String nombreUsuario, String email, String password) {
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
    }
    
    //Getter Y Setter

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

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    public List<Educacion> getEducaciones() {return educaciones;}

    public void setEducaciones(List<Educacion> educaciones) {this.educaciones = educaciones;}

    public List<Experiencia> getExperiencias() {return experiencias;}

    public void setExperiencias(List<Experiencia> experiencias) {this.experiencias = experiencias;}

    public List<hys> getSkills() {return skills;}

    public void setSkills(List<hys> skills) {this.skills = skills;}

    public List<Proyecto> getProyectos() {return proyectos;}

    public void setProyectos(List<Proyecto> proyectos) {this.proyectos = proyectos;}
}
