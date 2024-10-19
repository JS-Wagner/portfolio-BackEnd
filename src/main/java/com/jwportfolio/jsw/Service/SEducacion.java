
package com.jwportfolio.jsw.Service;

import com.jwportfolio.jsw.Entity.Educacion;
import com.jwportfolio.jsw.Repository.REducacion;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class SEducacion {
    @Autowired
    REducacion rEducacion;
    
    public List<Educacion> list(){
        return rEducacion.findAll();
    }
    
    public Optional<Educacion> getOne(int id) {
        return rEducacion.findById(id);
    }
    
    public Optional<Educacion> getByNombreE(String nombreE){
        return rEducacion.findByNombreE(nombreE);
    }
    
    public void save(Educacion exp) {
        rEducacion.save(exp);
    }
    
    public void delete(int id) {
        rEducacion.deleteById(id);
    }
    
    public boolean existsById(int id) {
        return rEducacion.existsById(id);
    }
    
    public boolean existsByNombreE(String nombreE) {
        return rEducacion.existsByNombreE(nombreE);
    }

    public void eliminarEducacionesTemporales() {
        List<Educacion> experienciasTemporales = rEducacion.findByTemporalTrue();
        rEducacion.deleteAll(experienciasTemporales);
    }
}
