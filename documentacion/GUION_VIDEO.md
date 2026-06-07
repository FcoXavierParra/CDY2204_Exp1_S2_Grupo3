# Guion del video — Sumativa S3 (CDY2204)

**Duración sugerida:** 6–9 minutos
**Objetivo:** demostrar en funcionamiento las funciones implementadas y explicar cómo cumplen cada requerimiento (cubre el criterio 7, 20 pts).
**Recomendación:** graba pantalla con voz. Ten abiertos: (a) Postman o `pruebas_endpoints.ps1`, (b) consola AWS (S3 + EC2), (c) GitHub (Actions + el repo), (d) Docker Hub.

> Tip: deja la app ya desplegada y corriendo (`docker ps` debe mostrar `s3-grupo3` Up) antes de grabar. URL base: `http://107.23.67.117:8080`.

---

## 0. Presentación (0:00 – 0:40)
- "Somos el Grupo 3 de Desarrollo Cloud Native. Presentamos la solución para la empresa transportista: un Sistema de Gestión de Guías de Despacho desplegado con CI/CD en AWS."
- Menciona integrantes.
- Enuncia rápido lo que mostrarás: EFS, S3, los endpoints REST y el pipeline.

## 1. Arquitectura (0:40 – 1:40)
- Muestra el diagrama (Figura 1 del informe).
- Explica el flujo en una frase: *"Hacemos push a GitHub → GitHub Actions construye la imagen Docker, la sube a Docker Hub y la despliega por SSH en una EC2; la app guarda las guías temporalmente en EFS y las persiste en S3."*
- Muestra el repositorio y la estructura del proyecto (carpetas `src`, `Dockerfile`, `.github/workflows`).

## 2. El pipeline CI/CD (1:40 – 3:10) — Criterio 6
- Abre `.github/workflows/ci-cd.yml` y explica los pasos (login, build, push, deploy).
- Muestra **GitHub → Actions** con el último run **en verde**.
- Muestra **Docker Hub** con la imagen `fcoxvrparraa/s3-grupo3:latest`.
- (Opcional potente) Haz un cambio mínimo, push a `main`, y muestra cómo se dispara el workflow en vivo.
- Muestra en la EC2: `docker ps` con el contenedor `s3-grupo3` corriendo.

## 3. Demo funcional de los endpoints (3:10 – 7:00)
> Usa Postman apuntando a `http://107.23.67.117:8080`. Ejecuta en este orden y **narra qué criterio cumple cada uno**:

1. **Crear guía** — `POST /guias`
   - Body de ejemplo (idGuia, transportista, fecha, pedido, destino, datosAdicionales).
   - Muestra la respuesta *"Guía creada en EFS: /app/efs/..."* → **Criterio 1 (EFS)**.
   - (Refuerzo) En la terminal de la EC2: `find /mnt/efs -type f` para ver el archivo en el EFS.

2. **Subir a S3** — `POST /guias/{id}/upload`
   - Muestra *"Guía subida a S3 en: 2026-06-07/transportistaX/guia_1001.pdf"* → **Criterio 2**.
   - Ve a la **consola S3** y muestra la carpeta `2026-06-07/transportistaX/` con el archivo → estructura por fecha/transportista.

3. **Consultar/listar** — `GET /guias?fecha=...&transportista=...`
   - Muestra el JSON con la lista de guías → **Criterio 5**.

4. **Descargar** — `GET /guias/{id}/download?fecha=...&transportista=...`
   - Muestra la descarga del PDF y su contenido → **Criterio 4**.

5. **Actualizar** — `PUT /guias/{id}`
   - Cambia un dato (ej. destino) y muestra *"Guía actualizada en: ..."* → **Criterio 3**.
   - Vuelve a descargar/listar para evidenciar el cambio.

6. **Eliminar** — `DELETE /guias/{id}?fecha=...&transportista=...`
   - Muestra el mensaje de eliminación y que ya no aparece al listar.

## 4. Cierre (7:00 – 8:00)
- Recapitula: *"Demostramos los 6 endpoints, el almacenamiento temporal en EFS, la persistencia organizada en S3 y el despliegue 100% automatizado con GitHub Actions a EC2."*
- Menciona aprendizajes/retos (ej. credenciales temporales del lab, configuración del Dockerfile multi-stage).
- Agradece y cierra.

---

## Checklist de cobertura de la rúbrica (no termines de grabar sin esto)
- [ ] Criterio 1 — EFS (crear guía + ver archivo en `/mnt/efs`)
- [ ] Criterio 2 — Subir a S3 (respuesta + objeto en consola S3 con carpetas fecha/transportista)
- [ ] Criterio 3 — Actualizar (PUT + evidencia del cambio)
- [ ] Criterio 4 — Descargar (PDF descargado)
- [ ] Criterio 5 — Consultar/listar (GET con filtros)
- [ ] Criterio 6 — Pipeline (workflow verde + Docker Hub + `docker ps` en EC2)
- [ ] Explicación clara de cómo cada función cumple el requerimiento
