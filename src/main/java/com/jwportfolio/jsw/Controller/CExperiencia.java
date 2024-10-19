
package com.jwportfolio.jsw.Controller;

import com.jwportfolio.jsw.Dto.dtoExperiencia;
import com.jwportfolio.jsw.Entity.Educacion;
import com.jwportfolio.jsw.Entity.Experiencia;
import com.jwportfolio.jsw.Security.Controller.Mensaje;
import com.jwportfolio.jsw.Security.Entity.Usuario;
import com.jwportfolio.jsw.Security.Enums.RolNombre;
import com.jwportfolio.jsw.Security.Service.UsuarioService;
import com.jwportfolio.jsw.Security.jwt.JwtProvider;
import com.jwportfolio.jsw.Service.SExperiencia;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/explaboral")
@CrossOrigin(origins = {"https://frontendprueba-291f0.web.app","http://localhost:4200"})
public class CExperiencia {
    @Autowired
    SExperiencia sExperiencia;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private JwtProvider jwtProvider;
    
    @GetMapping("/lista")
    public ResponseEntity<List<Experiencia>> list(){
        List<Experiencia> list = sExperiencia.list();
        return new ResponseEntity(list, HttpStatus.OK);
    }
    
    @GetMapping("/detail/{id}")
    public ResponseEntity<Experiencia> getById(@PathVariable("id") int id){
        if(!sExperiencia.existsById(id))
            return new ResponseEntity(new Mensaje("no existe"), HttpStatus.NOT_FOUND);
        Experiencia experiencia = sExperiencia.getOne(id).get();
        return new ResponseEntity(experiencia, HttpStatus.OK);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String token, @PathVariable("id") int id) {
        // Elimina el prefijo "Bearer " del token
        String jwtToken = token.replace("Bearer ", "");

        // Obtener el nombre de usuario desde el token
        String nombreUsuario = jwtProvider.getNombreUSuarioFromToken(jwtToken);

        // Obtener el usuario desde la base de datos
        Usuario usuario = usuarioService.getByNombreUsuario(nombreUsuario).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Verificar si la experiencia existe
        if (!sExperiencia.existsById(id)) {
            return new ResponseEntity(new Mensaje("no existe"), HttpStatus.NOT_FOUND);
        }

        // Obtener la experiencia
        Experiencia experiencia = sExperiencia.getOne(id).get();

        // Verificar si el usuario tiene el rol de administrador
        boolean esAdmin = usuario.getRoles().stream().anyMatch(rol -> rol.getRolNombre().equals(RolNombre.ROLE_ADMIN));

        // Si es admin o es el creador, puede eliminar
        if (esAdmin || experiencia.getUsuario().getId() == usuario.getId()) {
            sExperiencia.delete(id);
            return new ResponseEntity(new Mensaje("Educación eliminada"), HttpStatus.OK);
        } else {
            return new ResponseEntity(new Mensaje("No puedes eliminar esto"), HttpStatus.FORBIDDEN);
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token, @RequestBody dtoExperiencia dtoExp) {
        // Elimina el prefijo "Bearer " del token
        String jwtToken = token.replace("Bearer ", "");

        // Obtener el nombre de usuario desde el token
        String nombreUsuario = jwtProvider.getNombreUSuarioFromToken(jwtToken);

        // Obtener el usuario desde la base de datos
        Usuario usuario = usuarioService.getByNombreUsuario(nombreUsuario).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Validar campos
        if(StringUtils.isBlank(dtoExp.getNombreE()))
            return new ResponseEntity(new Mensaje("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        if(sExperiencia.existsByNombreE(dtoExp.getNombreE()))
            return new ResponseEntity(new Mensaje("Esa experiencia ya existe"), HttpStatus.BAD_REQUEST);

        // Verificar si el usuario tiene el rol de administrador
        boolean esAdmin = usuario.getRoles().stream().anyMatch(rol -> rol.getRolNombre().equals(RolNombre.ROLE_ADMIN));

        // Crear la experiencia y marcarla como temporal si el usuario no es admin
        Experiencia experiencia = new Experiencia(dtoExp.getNombreE(), dtoExp.getDescripcionE(), dtoExp.getFechaE(), !esAdmin, usuario);

        sExperiencia.save(experiencia);
        return new ResponseEntity(new Mensaje("Experiencia agregada"), HttpStatus.OK);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token, @PathVariable("id") int id, @RequestBody dtoExperiencia dtoExp) {
        // Elimina el prefijo "Bearer " del token
        String jwtToken = token.replace("Bearer ", "");

        // Obtener el nombre de usuario desde el token
        String nombreUsuario = jwtProvider.getNombreUSuarioFromToken(jwtToken);

        // Obtener el usuario desde la base de datos
        Usuario usuario = usuarioService.getByNombreUsuario(nombreUsuario).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));


        //Validacion para ver si existe el ID
        if(!sExperiencia.existsById(id))
            return new ResponseEntity(new Mensaje("El id no existe"), HttpStatus.BAD_REQUEST);
        //Compara nombres para que no sean iguales
        if(sExperiencia.existsByNombreE(dtoExp.getNombreE()) && sExperiencia.getByNombreE(dtoExp.getNombreE()).get().getId() != id)
            return new ResponseEntity(new Mensaje("Esa experiencia ya existe"), HttpStatus.BAD_REQUEST);
        //No puede ser vacío
        if(StringUtils.isBlank(dtoExp.getNombreE()))
            return new ResponseEntity(new Mensaje("El nombre no puede estar en blanco"), HttpStatus.BAD_REQUEST);

        // Obtener la experiencia
        Experiencia experiencia = sExperiencia.getOne(id). get();

        // Verificar si el usuario tiene el rol de administrador
        boolean esAdmin = usuario.getRoles().stream().anyMatch(rol -> rol.getRolNombre().equals(RolNombre.ROLE_ADMIN));

        // Si es admin o es el creador, puede actualizar
        if (esAdmin || experiencia.getUsuario().getId() == usuario.getId()) {
            // Actualiza los campos
            experiencia.setNombreE(dtoExp.getNombreE());
            experiencia.setDescripcionE(dtoExp.getDescripcionE());
            experiencia.setFechaE(dtoExp.getFechaE());
            sExperiencia.save(experiencia);
            return new ResponseEntity(new Mensaje("Educación actualizada"), HttpStatus.OK);
        } else {
            return new ResponseEntity(new Mensaje("No puedes actualizar esto"), HttpStatus.FORBIDDEN);
        }
    }
}
