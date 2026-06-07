# Imágenes de evidencia

Guarda aquí las capturas que referencia `INFORME.md`. Nombres sugeridos:

| Archivo | Qué capturar |
|---|---|
| `01_arquitectura.png` | Diagrama de arquitectura (puedes hacerlo en draw.io / PowerPoint) |
| `02_efs_montado.png` | `df -h \| grep efs` en la EC2 |
| `03_guia_creada_efs.png` | Respuesta de POST /guias ("Guía creada en EFS: ...") |
| `04_find_efs.png` | `find /mnt/efs -type f` |
| `05_upload_s3.png` | Respuesta de POST /guias/{id}/upload |
| `06_s3_objeto.png` | Consola S3 con `2026-06-07/transportistaX/guia_1001.pdf` |
| `07_update.png` | Respuesta de PUT /guias/{id} |
| `08_download.png` | Descarga de la guía (GET /download) |
| `09_listar.png` | GET /guias con el listado |
| `10_actions_verde.png` | Run del workflow en verde (GitHub Actions) |
| `11_dockerhub.png` | Imagen en Docker Hub |
| `12_docker_ps.png` | `docker ps` en la EC2 |
| `13_secrets.png` | Secrets configurados en GitHub |
