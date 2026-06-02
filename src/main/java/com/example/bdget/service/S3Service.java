package com.example.bdget.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

/**
 * Operaciones contra AWS S3: subir, descargar y borrar.
 * La carpeta del objeto corresponde al numero del resumen.
 */
@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    private String buildKey(String numeroResumen, String fileName) {
        return numeroResumen + "/" + fileName;
    }

    public String uploadFile(String bucket, String numeroResumen, File file) {
        String key = buildKey(numeroResumen, file.getName());
        s3Client.putObject(
                PutObjectRequest.builder().bucket(bucket).key(key).build(),
                RequestBody.fromFile(file));
        return key;
    }

    public byte[] downloadFile(String bucket, String key) {
        try (ResponseInputStream<GetObjectResponse> obj = s3Client.getObject(
                GetObjectRequest.builder().bucket(bucket).key(key).build())) {
            return obj.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Error al descargar: " + e.getMessage());
        }
    }

    public String deleteFile(String bucket, String key) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder().bucket(bucket).key(key).build());
        return "Archivo eliminado: " + key;
    }
}
