
package com.jwportfolio.jsw.Controller;

import com.jwportfolio.jsw.Dto.dtoEducacion;
import com.jwportfolio.jsw.Dto.dtoProyecto;
import com.jwportfolio.jsw.Entity.Educacion;
import com.jwportfolio.jsw.Entity.Proyecto;
import com.jwportfolio.jsw.Security.Controller.Mensaje;
import com.jwportfolio.jsw.Security.Entity.Usuario;
import com.jwportfolio.jsw.Security.Enums.RolNombre;
import com.jwportfolio.jsw.Security.Service.UsuarioService;
import com.jwportfolio.jsw.Security.jwt.JwtProvider;
import com.jwportfolio.jsw.Service.SProyecto;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/proyecto")
@CrossOrigin(origins = {"https://frontendprueba-291f0.web.app","http://localhost:4200"})
public class CProyecto {
    @Autowired
    SProyecto sProyecto;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    JwtProvider jwtProvider;
    
    @GetMapping("/lista")
    public ResponseEntity<List<Proyecto>> list(){
        List<Proyecto> list = sProyecto.list();
        return new ResponseEntity(list, HttpStatus.OK);
    }
    
    @GetMapping("/detail/{id}")
    public ResponseEntity<Proyecto> getById(@PathVariable("id") int id){
        if(!sProyecto.existsById(id))
            return new ResponseEntity(new Mensaje("no existe"), HttpStatus.NOT_FOUND);
        Proyecto proyecto = sProyecto.getOne(id).get();
        return new ResponseEntity(proyecto, HttpStatus.OK);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String token, @PathVariable("id") int id) {
        // Elimina el prefijo "Bearer " del token
        String jwtToken = token.replace("Bearer ", "");

        // Obtener el nombre de usuario desde el token
        String nombreUsuario = jwtProvider.getNombreUSuarioFromToken(jwtToken);

        // Obtener el usuario desde la base de datos
        Usuario usuario = usuarioService.getByNombreUsuario(nombreUsuario).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Verificar si el proyecto existe
        if (!sProyecto.existsById(id)) {
            return new ResponseEntity(new Mensaje("no existe"), HttpStatus.NOT_FOUND);
        }

        // Obtener el proyecto
        Proyecto proyecto = sProyecto.getOne(id).get();

        // Verificar si el usuario tiene el rol de administrador
        boolean esAdmin = usuario.getRoles().stream().anyMatch(rol -> rol.getRolNombre().equals(RolNombre.ROLE_ADMIN));

        // Si es admin o es el creador, puede eliminar
        if (esAdmin || proyecto.getUsuario().getId() == usuario.getId()) {
            sProyecto.delete(id);
            return new ResponseEntity(new Mensaje("Educación eliminada"), HttpStatus.OK);
        } else {
            return new ResponseEntity(new Mensaje("No puedes eliminar esto"), HttpStatus.FORBIDDEN);
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token, @RequestBody dtoProyecto dtoPro) {
        // Elimina el prefijo "Bearer " del token
        String jwtToken = token.replace("Bearer ", "");

        // Obtener el nombre de usuario desde el token
        String nombreUsuario = jwtProvider.getNombreUSuarioFromToken(jwtToken);

        // Obtener el usuario desde la base de datos
        Usuario usuario = usuarioService.getByNombreUsuario(nombreUsuario).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Validar campos
        if(StringUtils.isBlank(dtoPro.getNombreE()))
            return new ResponseEntity(new Mensaje("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        if(sProyecto.existsByNombreE(dtoPro.getNombreE()))
            return new ResponseEntity(new Mensaje("Ese proyecto ya existe"), HttpStatus.BAD_REQUEST);

        // Verificar si el usuario tiene el rol de administrador
        boolean esAdmin = usuario.getRoles().stream().anyMatch(rol -> rol.getRolNombre().equals(RolNombre.ROLE_ADMIN));

        // Crear la educacion y marcarla como temporal si el usuario no es admin
        Proyecto proyecto = new Proyecto(dtoPro.getNombreE(), dtoPro.getDescripcionE(), dtoPro.getFechaE(), !esAdmin, usuario);

        sProyecto.save(proyecto);
        return new ResponseEntity(new Mensaje("Proyecto agregado"), HttpStatus.OK);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token, @PathVariable("id") int id, @RequestBody dtoProyecto dtoPro) {
        // Elimina el prefijo "Bearer " del token
        String jwtToken = token.replace("Bearer ", "");

        // Obtener el nombre de usuario desde el token
        String nombreUsuario = jwtProvider.getNombreUSuarioFromToken(jwtToken);

        // Obtener el usuario desde la base de datos
        Usuario usuario = usuarioService.getByNombreUsuario(nombreUsuario).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Validacion para ver si existe el ID
        if(!sProyecto.existsById(id))
            return new ResponseEntity(new Mensaje("El id no existe"), HttpStatus.BAD_REQUEST);
        // Compara nombres para que no sean iguales
        if(sProyecto.existsByNombreE(dtoPro.getNombreE()) && sProyecto.getByNombreE(dtoPro.getNombreE()).get().getId() != id)
            return new ResponseEntity(new Mensaje("Ese proyecto ya existe"), HttpStatus.BAD_REQUEST);
        // No puede ser vacío
        if(StringUtils.isBlank(dtoPro.getNombreE()))
            return new ResponseEntity(new Mensaje("El nombre no puede estar en blanco"), HttpStatus.BAD_REQUEST);

        // Obtener el proyecto
        Proyecto proyecto = sProyecto.getOne(id).get();

        // Verificar si el usuario tiene el rol de administrador
        boolean esAdmin = usuario.getRoles().stream().anyMatch(rol -> rol.getRolNombre().equals(RolNombre.ROLE_ADMIN));

        // Si es admin o es el creador, puede actualizar
        if (esAdmin || proyecto.getUsuario().getId() == usuario.getId()) {
            // Actualiza los campos
            proyecto.setNombreE(dtoPro.getNombreE());
            proyecto.setDescripcionE(dtoPro.getDescripcionE());
            proyecto.setFechaE(dtoPro.getFechaE());
            sProyecto.save(proyecto);
            return new ResponseEntity(new Mensaje("Educación actualizada"), HttpStatus.OK);
        } else {
            return new ResponseEntity(new Mensaje("No puedes actualizar esto"), HttpStatus.FORBIDDEN);
        }
    }
}
