package com.example.bdget.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inscripcion {
    private String numeroResumen;   // define la carpeta en el bucket
    private String nombreEstudiante;
    private String curso;
    private String fecha;
}
