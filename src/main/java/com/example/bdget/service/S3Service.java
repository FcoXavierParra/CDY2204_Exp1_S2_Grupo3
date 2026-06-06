package com.example.bdget.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.util.List;

/**
 * Operaciones contra AWS S3: subir, descargar, borrar y listar objetos.
 * La clave de S3 se construye en base a la estructura de carpetas requerida.
 */
@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    public String uploadFile(String bucket, String key, File file) {
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

    public List<S3Object> listFiles(String bucket, String prefix) {
        ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder().bucket(bucket);
        if (prefix != null && !prefix.isBlank()) {
            requestBuilder.prefix(prefix);
        }

        ListObjectsV2Response response = s3Client.listObjectsV2(requestBuilder.build());
        return response.contents();
    }
}
