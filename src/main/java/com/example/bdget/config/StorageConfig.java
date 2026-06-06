package com.example.bdget.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Configura el cliente S3 para AWS. Si no se entregan credenciales explícitas,
 * se usa el proveedor de credenciales por defecto del entorno.
 */
@Configuration
public class StorageConfig {

    @Value("${aws.access-key:}")
    private String accessKey;

    @Value("${aws.secret-key:}")
    private String secretKey;

    @Value("${aws.region:us-east-1}")
    private String region;

    @Bean
    public S3Client s3Client() {
        S3Client.Builder builder = S3Client.builder().region(Region.of(region));
        if (!accessKey.isBlank() && !secretKey.isBlank()) {
            AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey));
            builder.credentialsProvider(credentialsProvider);
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        return builder.build();
    }
}
