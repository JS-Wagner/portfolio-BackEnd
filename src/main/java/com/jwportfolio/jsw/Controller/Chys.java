
package com.jwportfolio.jsw.Controller;

import com.jwportfolio.jsw.Dto.dtoHys;
import com.jwportfolio.jsw.Entity.Educacion;
import com.jwportfolio.jsw.Entity.Experiencia;
import com.jwportfolio.jsw.Entity.hys;
import com.jwportfolio.jsw.Security.Controller.Mensaje;
import com.jwportfolio.jsw.Security.Entity.Usuario;
import com.jwportfolio.jsw.Security.Enums.RolNombre;
import com.jwportfolio.jsw.Security.Service.UsuarioService;
import com.jwportfolio.jsw.Security.jwt.JwtProvider;
import com.jwportfolio.jsw.Service.Shys;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"https://frontendprueba-291f0.web.app","http://localhost:4200"})
@RequestMapping("/skill")
public class Chys {
    @Autowired
    Shys shys;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    JwtProvider jwtProvider;

    @GetMapping("/lista")
    public ResponseEntity<List<hys>> list() {
        List<hys> list = shys.list();
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<hys> getById(@PathVariable("id") int id) {
        if (!shys.existsById(id)) {
            return new ResponseEntity(new Mensaje("no existe"), HttpStatus.NOT_FOUND);
        }
        hys hYs = shys.getOne(id).get();
        return new ResponseEntity(hYs, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String token, @PathVariable("id") int id) {
        // Elimina el prefijo "Bearer " del token
        String jwtToken = token.replace("Bearer ", "");

        // Obtener el nombre de usuario desde el token
        String nombreUsuario = jwtProvider.getNombreUSuarioFromToken(jwtToken);

        // Obtener el usuario desde la base de datos
        Usuario usuario = usuarioService.getByNombreUsuario(nombreUsuario).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Verificar que la skill exista
        if (!shys.existsById(id)) {
            return new ResponseEntity(new Mensaje("no existe"), HttpStatus.NOT_FOUND);
        }

        // Obtener la skill
        hys hyss = shys.getOne(id).get();

        // Verificar si el usuario tiene el rol de administrador
        boolean esAdmin = usuario.getRoles().stream().anyMatch(rol -> rol.getRolNombre().equals(RolNombre.ROLE_ADMIN));

        // Si es admin o es el creador, puede eliminar
        if (esAdmin || hyss.getUsuario().getId() == usuario.getId()) {
            shys.delete(id);
            return new ResponseEntity(new Mensaje("Educación eliminada"), HttpStatus.OK);
        } else {
            return new ResponseEntity(new Mensaje("No puedes eliminar esto"), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token, @RequestBody dtoHys dtohys) {
        // Elimina el prefijo "Bearer " del token
        String jwtToken = token.replace("Bearer ", "");

        // Obtener el nombre de usuario desde el token
        String nombreUsuario = jwtProvider.getNombreUSuarioFromToken(jwtToken);

        // Obtener el usuario desde la base de datos
        Usuario usuario = usuarioService.getByNombreUsuario(nombreUsuario).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Validar campos
        if (StringUtils.isBlank(dtohys.getNombre())) {
            return new ResponseEntity(new Mensaje("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        }
        if (shys.existsByNombre(dtohys.getNombre())) {
            return new ResponseEntity(new Mensaje("Esa skill ya existe"), HttpStatus.BAD_REQUEST);
        }

        // Verificar si el usuario tiene el rol de administrador
        boolean esAdmin = usuario.getRoles().stream().anyMatch(rol -> rol.getRolNombre().equals(RolNombre.ROLE_ADMIN));

        // Crear la educacion y marcarla como temporal si el usuario no es admin
        hys hYs = new hys(dtohys.getNombre(), dtohys.getPorcentaje(), !esAdmin, usuario);

        shys.save(hYs);
        return new ResponseEntity(new Mensaje("Skill agregada"), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token, @PathVariable("id") int id, @RequestBody dtoHys dtohys) {
        // Elimina el prefijo "Bearer " del token
        String jwtToken = token.replace("Bearer ", "");

        // Obtener el nombre de usuario desde el token
        String nombreUsuario = jwtProvider.getNombreUSuarioFromToken(jwtToken);

        // Obtener el usuario desde la base de datos
        Usuario usuario = usuarioService.getByNombreUsuario(nombreUsuario).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));


        //Validacion para ver si existe el ID
        if (!shys.existsById(id)) {
            return new ResponseEntity(new Mensaje("El ID no existe"), HttpStatus.BAD_REQUEST);
        }
        // Compara nombres para que no sean iguales
        if (shys.existsByNombre(dtohys.getNombre()) && shys.getByNombre(dtohys.getNombre()).get()
                .getId() != id) {
            return new ResponseEntity(new Mensaje("Esa skill ya existe"), HttpStatus.BAD_REQUEST);
        }
        // No puede ser vacio
        if (StringUtils.isBlank(dtohys.getNombre())) {
            return new ResponseEntity(new Mensaje("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        // Obtener la skill
        hys hyss = shys.getOne(id).get();

        // Verificar si el usuario tiene el rol de administrador
        boolean esAdmin = usuario.getRoles().stream().anyMatch(rol -> rol.getRolNombre().equals(RolNombre.ROLE_ADMIN));

        // Si es admin o es el creador, puede actualizar
        if (esAdmin || hyss.getUsuario().getId() == usuario.getId()) {
            // Actualiza los campos
            hyss.setNombre(dtohys.getNombre());
            hyss.setPorcentaje(dtohys.getPorcentaje());
            shys.save(hyss);
            return new ResponseEntity(new Mensaje("Educación actualizada"), HttpStatus.OK);
        } else {
            return new ResponseEntity(new Mensaje("No puedes actualizar esto"), HttpStatus.FORBIDDEN);
        }
    }
}