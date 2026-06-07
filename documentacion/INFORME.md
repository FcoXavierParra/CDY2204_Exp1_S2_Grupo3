# Informe — Construyendo y desplegando una solución Cloud Native

**Asignatura:** Desarrollo Cloud Native (CDY2204)
**Experiencia:** Exp 1 — Semana 3 (Sumativa)
**Actividad:** Construyendo y desplegando una solución Cloud Native
**Grupo:** 3
**Integrantes:** _(completar nombres del grupo)_
**Docente:** _(completar)_
**Fecha:** _(completar)_
**Repositorio:** https://github.com/FcoXavierParra/CDY2204_Exp1_S2_Grupo3
**Imagen Docker Hub:** https://hub.docker.com/r/fcoxvrparraa/s3-grupo3

---

## 1. Introducción

> _(Breve párrafo: contexto de CI/CD y cloud native, y qué se desarrolló esta semana.)_

El presente informe documenta el desarrollo y despliegue de una solución *cloud native* para el caso planteado: una **empresa transportista** que requiere un **Sistema de Gestión de Pedidos y Generación de Guías de Despacho**. La solución integra un microservicio en **Spring Boot** con almacenamiento temporal en **Amazon EFS**, persistencia de archivos en **Amazon S3**, y un **pipeline de CI/CD con GitHub Actions** que construye la imagen Docker, la publica en Docker Hub y la despliega automáticamente en una instancia **EC2**.

---

## 2. Descripción del caso y requisitos

El sistema debe cumplir:

- **EFS** para almacenamiento temporal de las guías generadas.
- **Subida automática a S3**, en carpetas organizadas por **fecha y transportista** (ej. `2026-06-07/transportistaX/guia_1001.pdf`).
- **Endpoints REST:** crear, subir, descargar (con validación), modificar/actualizar, eliminar y consultar guías por transportista y fecha.
- **Despliegue automatizado:** imagen Docker publicada en Docker Hub al hacer push a `main`, y despliegue automático en EC2 vía GitHub Actions.

---

## 3. Arquitectura de la solución

> _(Insertar diagrama de arquitectura.)_

![Diagrama de arquitectura](imagenes/01_arquitectura.png)
*Figura 1. Arquitectura general: GitHub Actions → Docker Hub → EC2 (contenedor) → S3 + EFS.*

**Componentes:**

| Componente | Detalle |
|---|---|
| Microservicio | Spring Boot 3.2.5, Java 17, AWS SDK v2 |
| Contenerización | Dockerfile multi-stage (build con Maven, runtime JRE 17) |
| Registro de imágenes | Docker Hub: `fcoxvrparraa/s3-grupo3:latest` |
| Cómputo | EC2 t3.micro (Amazon Linux 2023), IP elástica `107.23.67.117` |
| Almacenamiento temporal | Amazon EFS montado en `/mnt/efs` → contenedor `/app/efs` |
| Almacenamiento persistente | Amazon S3, bucket `cdy2204-fparra-s3` |
| CI/CD | GitHub Actions (`.github/workflows/ci-cd.yml`) |

---

## 4. Evidencias por criterio

### Criterio 1 — Almacenamiento temporal en EFS (15 pts)

> _(Explicar: las guías se escriben primero en el EFS montado en `/app/efs`, organizadas por fecha/transportista.)_

![EFS montado en la EC2](imagenes/02_efs_montado.png)
*Figura 2. `df -h | grep efs` mostrando el EFS montado en `/mnt/efs`.*

![Guía creada en EFS](imagenes/03_guia_creada_efs.png)
*Figura 3. Respuesta del endpoint POST /guias: "Guía creada en EFS: /app/efs/...".*

![Archivos en EFS](imagenes/04_find_efs.png)
*Figura 4. `find /mnt/efs -type f` mostrando los archivos de guías en el EFS.*

### Criterio 2 — Subida a AWS S3 (10 pts)

> _(Explicar: el endpoint /upload sube la guía a S3 en carpetas por fecha/transportista.)_

![Subida a S3](imagenes/05_upload_s3.png)
*Figura 5. Respuesta de POST /guias/{id}/upload: "Guía subida a S3 en: 2026-06-07/transportistaX/guia_1001.pdf".*

![Objeto en bucket S3](imagenes/06_s3_objeto.png)
*Figura 6. Consola S3 mostrando `2026-06-07/transportistaX/guia_1001.pdf` (estructura por fecha/transportista).*

### Criterio 3 — Modificar/actualizar en S3 (15 pts)

> _(Explicar: PUT /guias/{id} regenera la guía y la reemplaza en S3.)_

![Actualizar guía](imagenes/07_update.png)
*Figura 7. Respuesta de PUT /guias/{id}: "Guía actualizada en: ...".*

### Criterio 4 — Descargar desde S3 (10 pts)

![Descargar guía](imagenes/08_download.png)
*Figura 8. Descarga de la guía vía GET /guias/{id}/download y su contenido.*

### Criterio 5 — Consultar historial (10 pts)

![Listado de guías](imagenes/09_listar.png)
*Figura 9. GET /guias devolviendo el listado de guías filtrado por fecha/transportista.*

### Criterio 6 — Despliegue mediante pipeline automatizado (20 pts)

> _(Explicar el flujo del pipeline: push a main → build → push a Docker Hub → deploy SSH a EC2.)_

![Workflow en verde](imagenes/10_actions_verde.png)
*Figura 10. GitHub Actions: run del workflow "CI/CD - Build and Push Docker Image" en verde.*

![Imagen en Docker Hub](imagenes/11_dockerhub.png)
*Figura 11. Imagen `fcoxvrparraa/s3-grupo3:latest` publicada en Docker Hub.*

![Contenedor corriendo en EC2](imagenes/12_docker_ps.png)
*Figura 12. `docker ps` en la EC2 mostrando el contenedor `s3-grupo3` activo en el puerto 8080.*

---

## 5. Pipeline CI/CD (explicación del workflow)

> _(Pegar/explicar el contenido de `.github/workflows/ci-cd.yml` y los secrets utilizados.)_

El workflow se dispara con cada `push` a `main` y ejecuta:
1. **Checkout** del repositorio.
2. **Log in a Docker Hub** (secrets `DOCKERHUB_USERNAME` / `DOCKERHUB_TOKEN`).
3. **Build** de la imagen con el Dockerfile multi-stage.
4. **Push** de la imagen a Docker Hub.
5. **Deploy a EC2** por SSH: `docker pull` + `docker run` con las credenciales AWS y el EFS montado.

**Secrets configurados en GitHub:** `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`, `EC2_HOST`, `USER_SERVER`, `EC2_SSH_KEY`, `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN`, `AWS_BUCKET`.

![Secrets en GitHub](imagenes/13_secrets.png)
*Figura 13. Secrets del repositorio configurados en GitHub Actions.*

---

## 6. Endpoints REST implementados

| Método | Ruta | Función | Criterio |
|---|---|---|---|
| POST | `/guias` | Crear guía (se escribe en EFS) | 1 |
| POST | `/guias/{id}/upload` | Subir guía a S3 | 2 |
| PUT | `/guias/{id}` | Modificar/actualizar guía en S3 | 3 |
| GET | `/guias/{id}/download?fecha=&transportista=` | Descargar guía desde S3 | 4 |
| GET | `/guias?fecha=&transportista=` | Consultar/listar guías | 5 |
| DELETE | `/guias/{id}?fecha=&transportista=` | Eliminar guía | — |

---

## 7. Conclusiones

> _(2–3 párrafos: aprendizajes sobre CI/CD, EFS/S3, retos enfrentados — ej. credenciales temporales del lab, configuración del Dockerfile — y cómo se resolvieron.)_

---

## 8. Anexos

- Link del repositorio: https://github.com/FcoXavierParra/CDY2204_Exp1_S2_Grupo3
- Colección Postman: `CDY2204_S3_postman_collection.json`
- Script de pruebas: `pruebas_endpoints.ps1`
