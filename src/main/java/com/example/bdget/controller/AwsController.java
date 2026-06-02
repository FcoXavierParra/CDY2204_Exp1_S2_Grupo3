package com.example.bdget.controller;

import com.example.bdget.model.Inscripcion;
import com.example.bdget.service.ResumenService;
import com.example.bdget.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/s3")
public class AwsController {

    @Autowired private ResumenService resumenService;
    @Autowired private S3Service s3Service;

    @Value("${aws.bucket}")
    private String bucket;

    private String keyDe(String numeroResumen) {
        return numeroResumen + "/resumen_" + numeroResumen + ".txt";
    }

    // 1. Genera el resumen TXT y lo sube a S3 (carpeta = numeroResumen)
    @PostMapping("/inscripcion")
    public ResponseEntity<String> crearYSubir(@RequestBody Inscripcion ins) throws Exception {
        File f = resumenService.generarResumen(ins);
        String key = s3Service.uploadFile(bucket, ins.getNumeroResumen(), f);
        return ResponseEntity.ok("Resumen generado y subido en: " + key);
    }

    // 2. Modifica / reemplaza el resumen existente
    @PutMapping("/inscripcion")
    public ResponseEntity<String> actualizar(@RequestBody Inscripcion ins) throws Exception {
        File f = resumenService.generarResumen(ins);
        String key = s3Service.uploadFile(bucket, ins.getNumeroResumen(), f);
        return ResponseEntity.ok("Resumen actualizado en: " + key);
    }

    // 3. Descarga el resumen
    @GetMapping("/download")
    public ResponseEntity<byte[]> descargar(@RequestParam String numeroResumen) {
        byte[] data = s3Service.downloadFile(bucket, keyDe(numeroResumen));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=resumen_" + numeroResumen + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(data);
    }

    // 4. Borra el resumen
    @DeleteMapping("/inscripcion")
    public ResponseEntity<String> borrar(@RequestParam String numeroResumen) {
        return ResponseEntity.ok(s3Service.deleteFile(bucket, keyDe(numeroResumen)));
    }
}
