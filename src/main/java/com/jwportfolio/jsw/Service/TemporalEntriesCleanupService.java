package com.jwportfolio.jsw.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TemporalEntriesCleanupService {
    @Autowired
    private SEducacion educacionService;

    @Autowired
    private SExperiencia experienciaService;

    @Autowired
    private SProyecto proyectoService;

    @Autowired
    private Shys skillsService;

    @Scheduled(fixedRate = 3600000) // Cada hora
    public void eliminarEntradasTemporales() {
        // Lógica para eliminar las entradas temporales de todas las entidades
        experienciaService.eliminarExperienciasTemporales();
        educacionService.eliminarEducacionesTemporales();
        skillsService.eliminarSkillsTemporales();
        proyectoService.eliminarProyectosTemporales();
    }

}
