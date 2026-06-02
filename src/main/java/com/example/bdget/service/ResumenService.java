package com.example.bdget.service;

import com.example.bdget.model.Inscripcion;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Genera el archivo fisico del resumen de inscripcion (funcionalidad Semana 1).
 */
@Service
public class ResumenService {

    public File generarResumen(Inscripcion ins) throws IOException {
        String fileName = "resumen_" + ins.getNumeroResumen() + ".txt";
        File file = new File(System.getProperty("java.io.tmpdir"), fileName);

        try (FileWriter w = new FileWriter(file)) {
            w.write("===== RESUMEN DE INSCRIPCION =====\n");
            w.write("Numero de resumen : " + ins.getNumeroResumen() + "\n");
            w.write("Estudiante        : " + ins.getNombreEstudiante() + "\n");
            w.write("Curso             : " + ins.getCurso() + "\n");
            w.write("Fecha             : " + ins.getFecha() + "\n");
            w.write("==================================\n");
        }
        return file;
    }
}
