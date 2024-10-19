
package com.jwportfolio.jsw.Controller;

import com.jwportfolio.jsw.Dto.dtoEducacion;
import com.jwportfolio.jsw.Entity.Educacion;
import com.jwportfolio.jsw.Security.Controller.Mensaje;
import com.jwportfolio.jsw.Security.Entity.Usuario;
import com.jwportfolio.jsw.Security.Enums.RolNombre;
import com.jwportfolio.jsw.Security.Service.UsuarioService;
import com.jwportfolio.jsw.Security.jwt.JwtProvider;
import com.jwportfolio.jsw.Service.SEducacion;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/educacion")
@CrossOrigin(origins = {"https://frontendprueba-291f0.web.app","http://localhost:4200"})
public class CEducacion {
    @Autowired
    SEducacion sEducacion;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private JwtProvider jwtProvider;
    
    @GetMapping("/lista")
    public ResponseEntity<List<Educacion>> list(){
        List<Educacion> list = sEducacion.list();
        return new ResponseEntity(list, HttpStatus.OK);
    }
    
    @GetMapping("/detail/{id}")
    public ResponseEntity<Educacion> getById(@PathVariable("id") int id){
        if(!sEducacion.existsById(id))
            return new ResponseEntity(new Mensaje("no existe"), HttpStatus.NOT_FOUND);
        Educacion educacion = sEducacion.getOne(id).get();
        return new ResponseEntity(educacion, HttpStatus.OK);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String token, @PathVariable("id") int id) {
        // Elimina el prefijo "Bearer " del token
        String jwtToken = token.replace("Bearer ", "");

        // Obtener el nombre de usuario desde el token
        String nombreUsuario = jwtProvider.getNombreUSuarioFromToken(jwtToken);

        // Obtener el usuario desde la base de datos
        Usuario usuario = usuarioService.getByNombreUsuario(nombreUsuario).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Verificar si la educacion existe
        if (!sEducacion.existsById(id)) {
            return new ResponseEntity(new Mensaje("no existe"), HttpStatus.NOT_FOUND);
        }

        // Obtener la educacion
        Educacion educacion = sEducacion.getOne(id).get();

        // Verificar si el usuario tiene el rol de administrador
        boolean esAdmin = usuario.getRoles().stream().anyMatch(rol -> rol.getRolNombre().equals(RolNombre.ROLE_ADMIN));

        // Si es admin o es el creador, puede eliminar
        if (esAdmin || educacion.getUsuario().getId() == usuario.getId()) {
            sEducacion.delete(id);
            return new ResponseEntity(new Mensaje("Educación eliminada"), HttpStatus.OK);
        } else {
            return new ResponseEntity(new Mensaje("No puedes eliminar esto"), HttpStatus.FORBIDDEN);
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token, @RequestBody dtoEducacion dtoEdu) {
        // Elimina el prefijo "Bearer " del token
        String jwtToken = token.replace("Bearer ", "");

        // Obtener el nombre de usuario desde el token
        String nombreUsuario = jwtProvider.getNombreUSuarioFromToken(jwtToken);

        // Obtener el usuario desde la base de datos
        Usuario usuario = usuarioService.getByNombreUsuario(nombreUsuario).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Validar campos
        if(StringUtils.isBlank(dtoEdu.getNombreE()))
            return new ResponseEntity(new Mensaje("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        if(sEducacion.existsByNombreE(dtoEdu.getNombreE()))
            return new ResponseEntity(new Mensaje("Esa educacion ya existe"), HttpStatus.BAD_REQUEST);

        // Verificar si el usuario tiene el rol de administrador
        boolean esAdmin = usuario.getRoles().stream().anyMatch(rol -> rol.getRolNombre().equals(RolNombre.ROLE_ADMIN));

        // Crear la educacion y marcarla como temporal si el usuario no es admin
        Educacion educacion = new Educacion(dtoEdu.getNombreE(), dtoEdu.getDescripcionE(), dtoEdu.getFechaE(), !esAdmin, usuario);

        sEducacion.save(educacion);
        return new ResponseEntity(new Mensaje("Educacion agregada"), HttpStatus.OK);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token, @PathVariable("id") int id, @RequestBody dtoEducacion dtoEdu) {
        // Elimina el prefijo "Bearer " del token
        String jwtToken = token.replace("Bearer ", "");

        // Obtener el nombre de usuario desde el token
        String nombreUsuario = jwtProvider.getNombreUSuarioFromToken(jwtToken);

        // Obtener el usuario desde la base de datos
        Usuario usuario = usuarioService.getByNombreUsuario(nombreUsuario).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Validacion para ver si existe el ID
        if(!sEducacion.existsById(id))
            return new ResponseEntity(new Mensaje("El id no existe"), HttpStatus.BAD_REQUEST);
        // Compara nombres para que no sean iguales
        if(sEducacion.existsByNombreE(dtoEdu.getNombreE()) && sEducacion.getByNombreE(dtoEdu.getNombreE()).get().getId() != id)
            return new ResponseEntity(new Mensaje("Esa educacion ya existe"), HttpStatus.BAD_REQUEST);
        // No puede ser vacío
        if(StringUtils.isBlank(dtoEdu.getNombreE()))
            return new ResponseEntity(new Mensaje("El nombre no puede estar en blanco"), HttpStatus.BAD_REQUEST);

        // Obtener la educacion
        Educacion educacion = sEducacion.getOne(id).get();

        // Verificar si el usuario tiene el rol de administrador
        boolean esAdmin = usuario.getRoles().stream().anyMatch(rol -> rol.getRolNombre().equals(RolNombre.ROLE_ADMIN));

        // Si es admin o es el creador, puede actualizar
        if (esAdmin || educacion.getUsuario().getId() == usuario.getId()) {
            // Actualiza los campos
            educacion.setNombreE(dtoEdu.getNombreE());
            educacion.setDescripcionE(dtoEdu.getDescripcionE());
            educacion.setFechaE(dtoEdu.getFechaE());
            sEducacion.save(educacion);
            return new ResponseEntity(new Mensaje("Educación actualizada"), HttpStatus.OK);
        } else {
            return new ResponseEntity(new Mensaje("No puedes actualizar esto"), HttpStatus.FORBIDDEN);
        }
    }
}
    

