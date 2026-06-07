# CDY2204 – Semana 3: Entrega S3_Grupo3

Proyecto base adaptado para el caso de guías de despacho con almacenamiento temporal en
EFS y persistencia final en AWS S3. El microservicio se desarrolla con Spring Boot y
puede desplegarse con Docker + GitHub Actions.

## Qué hay en este proyecto

- `config/StorageConfig.java`: configuración del cliente AWS S3.
- `model/GuiaDespacho.java`: modelo de la guía de despacho.
- `service/EfsStorageService.java`: escribe la guía en un directorio montado de EFS.
- `service/S3Service.java`: sube, descarga, borra y lista objetos en S3.
- `service/GuiaService.java`: orquesta creación, subida, actualización y consulta.
- `controller/GuiaController.java`: endpoints REST para el nuevo caso de uso.
- `Dockerfile`: construye la imagen Docker del servicio.
- `.github/workflows/ci-cd.yml`: workflow de GitHub Actions para build, push y deploy.

## Requisitos

- Java 17
- Maven (local o en CI)
- Cuenta AWS con S3 y EFS
- Repositorio en GitHub
- Docker Hub para publicar la imagen (puede ser privado)
- Instancia EC2 para despliegue automático (opcional)

> Si aún no tienes Docker Hub, crea el repositorio `s3-grupo3` en tu cuenta.
> Si el repositorio es privado, documenta ese detalle en el informe y asegúrate de que el despliegue automático tenga acceso mediante los secretos de GitHub.

## Configuración

1. Reutiliza el bucket S3 existente `cdy2204-fparra-s3`.
   - No hace falta crear un bucket nuevo.
   - Solo cambia la estructura interna de carpetas para el caso de guías.
2. Configura `efs.path` como directorio local para desarrollo o la ruta montada en EC2.
3. En AWS puedes usar credenciales en variables de entorno o una IAM role en EC2.

```yaml
server:
  port: 8080

aws:
  region: us-east-1
  bucket: cdy2204-fparra-s3

efs:
  path: ./efs-mount
```

### Nota sobre configuraciones adicionales

- El bucket S3 existente no requiere configuraciones especiales distintas. Solo necesita permisos normales de lectura/escritura para la aplicación.
- Lo único adicional es el uso de EFS para almacenamiento temporal, que no está en el bucket.
- Si llegas a crear un nuevo bucket, debe tener el mismo `region` y permisos S3 equivalentes.

## Ejecutar localmente

```bash
mvn clean spring-boot:run
```

La aplicación quedará disponible en `http://localhost:8080`.

## Endpoints

| Acción | Método | URL | Parámetros |
|---|---|---|---|
| Crear guía en EFS | POST | `/guias` | body JSON `GuiaDespacho` |
| Subir guía a S3 | POST | `/guias/{idGuia}/upload` | `fecha`, `transportista` (sube a S3 la guía ya creada en EFS) |
| Actualizar guía | PUT | `/guias/{idGuia}` | body JSON `GuiaDespacho` |
| Descargar guía | GET | `/guias/{idGuia}/download` | `fecha`, `transportista` |
| Borrar guía | DELETE | `/guias/{idGuia}` | `fecha`, `transportista` |
| Listar guías | GET | `/guias` | `fecha`, `transportista` |

### Ejemplo de body `GuiaDespacho`

```json
{
  "idGuia": "1234",
  "transportista": "transportistaA",
  "fecha": "20261101",
  "pedido": "Pedido 1001",
  "destino": "Santiago",
  "datosAdicionales": "Entrega mañana a primera hora"
}
```

## Flujo de despliegue

1. Push a `main` en GitHub.
2. GitHub Actions construye la imagen con el Dockerfile multi-stage (compila con Maven dentro de la imagen).
3. Se publica la imagen Docker en Docker Hub (`fcoxvrparraa/s3-grupo3:latest`).
4. Si está configurado el secreto `EC2_HOST`, se despliega por SSH en la EC2: `docker pull` + `docker run` con las credenciales AWS y el EFS montado (`-v /mnt/efs:/app/efs`).

## Secretos requeridos en GitHub (Settings → Secrets and variables → Actions)

| Secret | Valor |
|---|---|
| `DOCKERHUB_USERNAME` | usuario de Docker Hub |
| `DOCKERHUB_TOKEN` | token de acceso de Docker Hub (read/write) |
| `EC2_HOST` | IP elástica de la EC2 |
| `USER_SERVER` | usuario SSH (`ec2-user`) |
| `EC2_SSH_KEY` | contenido completo de la llave privada `.pem` |
| `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY` / `AWS_SESSION_TOKEN` | credenciales del Learner Lab (rotan al reiniciar) |
| `AWS_BUCKET` | nombre del bucket S3 (`cdy2204-fparra-s3`) |

> Las credenciales `AWS_*` del Learner Lab caducan al reiniciar el lab: actualízalas y vuelve a disparar el deploy.

## Próximos pasos

- Validar recursos AWS: bucket S3, EFS y EC2.
- Ajustar el formato de archivo si se requiere PDF real en lugar de texto con extensión `.pdf`.
