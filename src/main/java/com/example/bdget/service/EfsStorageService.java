package com.example.bdget.service;

import com.example.bdget.model.GuiaDespacho;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class EfsStorageService {

    @Value("${efs.path:./efs-mount}")
    private String efsRootPath;

    public File writeGuideToEfs(GuiaDespacho guia) throws IOException {
        Path guideFolder = Paths.get(efsRootPath, guia.getFecha(), guia.getTransportista());
        Files.createDirectories(guideFolder);

        String fileName = "guia_" + guia.getIdGuia() + ".pdf";
        Path guidePath = guideFolder.resolve(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(guidePath, StandardCharsets.UTF_8)) {
            writer.write("===== GUIA DE DESPACHO =====\n");
            writer.write("ID Guia        : " + guia.getIdGuia() + "\n");
            writer.write("Transportista  : " + guia.getTransportista() + "\n");
            writer.write("Fecha          : " + guia.getFecha() + "\n");
            writer.write("Pedido         : " + guia.getPedido() + "\n");
            writer.write("Destino        : " + guia.getDestino() + "\n");
            writer.write("Info adicional : " + guia.getDatosAdicionales() + "\n");
            writer.write("============================\n");
        }

        return guidePath.toFile();
    }
}
