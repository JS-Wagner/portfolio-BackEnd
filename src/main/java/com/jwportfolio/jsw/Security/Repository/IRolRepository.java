package com.jwportfolio.jsw.Security.Repository;

import com.jwportfolio.jsw.Security.Entity.Rol;
import com.jwportfolio.jsw.Security.Enums.RolNombre;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IRolRepository extends JpaRepository<Rol, Integer>{
    Optional<Rol> findByRolNombre(RolNombre rolNombre);
}
