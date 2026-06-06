package com.example.bdget.controller;

import com.example.bdget.model.GuiaDespacho;
import com.example.bdget.service.GuiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/guias")
public class GuiaController {

    @Autowired
    private GuiaService guiaService;

    @PostMapping
    public ResponseEntity<String> crearGuia(@RequestBody GuiaDespacho guia) throws Exception {
        File file = guiaService.createGuide(guia);
        return ResponseEntity.ok("Guía creada en EFS: " + file.getAbsolutePath());
    }

    @PostMapping("/{idGuia}/upload")
    public ResponseEntity<String> subirGuia(@PathVariable String idGuia, @RequestBody GuiaDespacho guia) throws Exception {
        guia.setIdGuia(idGuia);
        String key = guiaService.uploadGuide(guia);
        return ResponseEntity.ok("Guía subida a S3 en: " + key);
    }

    @PutMapping("/{idGuia}")
    public ResponseEntity<String> actualizarGuia(@PathVariable String idGuia, @RequestBody GuiaDespacho guia) throws Exception {
        guia.setIdGuia(idGuia);
        String key = guiaService.updateGuide(guia);
        return ResponseEntity.ok("Guía actualizada en: " + key);
    }

    @GetMapping("/{idGuia}/download")
    public ResponseEntity<byte[]> descargarGuia(
            @PathVariable String idGuia,
            @RequestParam String fecha,
            @RequestParam String transportista) {
        byte[] data = guiaService.downloadGuide(fecha, transportista, idGuia);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=guia_" + idGuia + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @DeleteMapping("/{idGuia}")
    public ResponseEntity<String> borrarGuia(
            @PathVariable String idGuia,
            @RequestParam String fecha,
            @RequestParam String transportista) {
        String mensaje = guiaService.deleteGuide(fecha, transportista, idGuia);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping
    public ResponseEntity<List<String>> listarGuias(
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) String transportista) {
        return ResponseEntity.ok(guiaService.listGuides(fecha, transportista));
    }
}
