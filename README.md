# CDY2204 – Semana 2: Almacenando archivos en la nube (AWS S3)

Código S3 para el proyecto **bdget** (el microservicio Java de la Semana 1, repo base
`https://github.com/cvalverd/bdget`). Genera el resumen de una inscripción (archivo TXT)
y lo gestiona en un bucket de AWS S3. Cada resumen se guarda en una carpeta del bucket
cuyo nombre es el **número del resumen**. Paquete: `com.example.bdget`.

## Dos caminos

**Camino A – Tienes el proyecto bdget de la Semana 1 (recomendado):**
Copia estas 4 clases dentro de tu proyecto existente, respetando el paquete `com.example.bdget`:

```
config/StorageConfig.java
model/Inscripcion.java
service/ResumenService.java
service/S3Service.java
controller/AwsController.java
```

Luego añade al `pom.xml` la dependencia `software.amazon.awssdk:s3` y Lombok (ver el
`pom.xml` de este zip), y agrega el bloque `aws:` a tu `application.yml`. No necesitas
tocar tu endpoint `/students` ni el pipeline CI/CD: la demo de S3 se hace localmente.

**Camino B – No tienes bdget / prefieres standalone:**
Usa este proyecto tal cual; ya es ejecutable por sí solo.

## Requisitos

- Java 17 (el bdget original usa Java 22 en su Dockerfile; ambos sirven para correr local)
- Maven
- Lab de **AWS Academy** encendido con un bucket S3 creado

## Configuración

1. Enciende el lab de AWS Academy → **AWS Details → AWS CLI**.
2. Copia `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN`.
3. Pega esos valores y el nombre de tu bucket en `src/main/resources/application.yml`.

> Las credenciales **caducan** al reiniciar el lab: hay que volver a pegarlas.
> `application.yml` está en `.gitignore` para no subir credenciales al repo.

## Ejecutar

```bash
mvn clean spring-boot:run
```

App en `http://localhost:8080`.

## Endpoints

| Acción | Método | URL | Parámetros |
|---|---|---|---|
| Generar + subir resumen | POST | `/s3/inscripcion` | body JSON `Inscripcion` |
| Modificar / reemplazar | PUT | `/s3/inscripcion` | body JSON `Inscripcion` |
| Descargar | GET | `/s3/download` | `?numeroResumen=1001` |
| Borrar | DELETE | `/s3/inscripcion` | `?numeroResumen=1001` |

### Ejemplo de body (POST / PUT)

```json
{
  "numeroResumen": "1001",
  "nombreEstudiante": "Ean Perez",
  "curso": "Cloud Native",
  "fecha": "2026-06-01"
}
```

## Pruebas en Postman (orden para el video)

1. **POST** `/s3/inscripcion` → verifica la carpeta `1001/` con `resumen_1001.txt` en la consola S3.
2. **PUT** `/s3/inscripcion` (cambia un dato) → muestra el archivo actualizado.
3. **GET** `/s3/download?numeroResumen=1001` → "Save Response → Save to a file".
4. **DELETE** `/s3/inscripcion?numeroResumen=1001` → muestra que la carpeta queda vacía.

## Nota sobre el caso

El caso de la Semana 2 menciona "la funcionalidad de creación de resumen de la inscripción,
correspondiente a la semana 1". El repo base `bdget` es en realidad un microservicio de
estudiantes con BD en la nube, así que la función de generar el resumen se implementa aquí
(`ResumenService`) adaptada al caso de la plataforma de inscripción.
