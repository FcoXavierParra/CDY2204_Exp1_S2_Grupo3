package com.example.bdget.service;

import com.example.bdget.model.GuiaDespacho;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuiaService {

    @Autowired
    private EfsStorageService efsStorageService;

    @Autowired
    private S3Service s3Service;

    @Value("${aws.bucket}")
    private String bucket;

    public File createGuide(GuiaDespacho guia) throws Exception {
        return efsStorageService.writeGuideToEfs(guia);
    }

    public String uploadGuide(GuiaDespacho guia) throws Exception {
        File guideFile = efsStorageService.writeGuideToEfs(guia);
        String key = String.join("/", guia.getFecha(), guia.getTransportista(), guideFile.getName());
        return s3Service.uploadFile(bucket, key, guideFile);
    }

    public byte[] downloadGuide(String fecha, String transportista, String idGuia) {
        String key = buildS3Key(fecha, transportista, idGuia);
        return s3Service.downloadFile(bucket, key);
    }

    public String deleteGuide(String fecha, String transportista, String idGuia) {
        String key = buildS3Key(fecha, transportista, idGuia);
        return s3Service.deleteFile(bucket, key);
    }

    public List<String> listGuides(String fecha, String transportista) {
        if (fecha != null && transportista != null) {
            String prefix = buildS3Prefix(fecha, transportista);
            return s3Service.listFiles(bucket, prefix).stream()
                    .map(S3Object::key)
                    .collect(Collectors.toList());
        }

        List<S3Object> objects = s3Service.listFiles(bucket, fecha != null ? fecha + "/" : "");
        return objects.stream()
                .map(S3Object::key)
                .filter(key -> transportista == null || key.contains("/" + transportista + "/"))
                .collect(Collectors.toList());
    }

    public String updateGuide(GuiaDespacho guia) throws Exception {
        File guideFile = efsStorageService.writeGuideToEfs(guia);
        String key = buildS3Key(guia.getFecha(), guia.getTransportista(), guia.getIdGuia());
        return s3Service.uploadFile(bucket, key, guideFile);
    }

    private String buildS3Key(String fecha, String transportista, String idGuia) {
        return String.join("/", fecha, transportista, "guia_" + idGuia + ".pdf");
    }

    private String buildS3Prefix(String fecha, String transportista) {
        return fecha + "/" + transportista + "/";
    }
}
