
package com.jwportfolio.jsw.Interface;

import com.jwportfolio.jsw.Entity.Persona;
import java.util.List;


public interface IPersonaService {
    //Traer lista de Personas
    public List<Persona> getPersona();
    
    //Guardar un objeto de tipo Persona
    public void savePersona(Persona persona);
    
    //Eliminar un objeto
    public void deletePersona(Long id);
    
    //Buscar una persona
    public Persona findPersona(Long id);
}
