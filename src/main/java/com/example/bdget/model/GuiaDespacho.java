package com.example.bdget.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuiaDespacho {
    private String idGuia;
    private String transportista;
    private String fecha;
    private String pedido;
    private String destino;
    private String datosAdicionales;
}
