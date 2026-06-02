# Pruebas de los endpoints S3 - CDY2204 Exp1 S2
# Ejecutar con la app corriendo en http://localhost:8080
# Uso: powershell -ExecutionPolicy Bypass -File .\pruebas_endpoints.ps1

$base = "http://localhost:8080"
$num  = "1001"

function Titulo($t) {
    Write-Host ""
    Write-Host ("=" * 60) -ForegroundColor Cyan
    Write-Host $t -ForegroundColor Cyan
    Write-Host ("=" * 60) -ForegroundColor Cyan
}

# 1. POST - Generar y subir el resumen
Titulo "1) POST /s3/inscripcion  -> generar y subir resumen"
$bodyPost = @{
    numeroResumen    = $num
    nombreEstudiante = "Francisco Javier Parra Andia"
    curso            = "Cloud Native"
    fecha            = "2026-06-01"
} | ConvertTo-Json
Write-Host "Body enviado:" -ForegroundColor Yellow
Write-Host $bodyPost
$r1 = Invoke-RestMethod -Uri "$base/s3/inscripcion" -Method Post -ContentType "application/json" -Body $bodyPost
Write-Host "Respuesta: $r1" -ForegroundColor Green

# 2. PUT - Modificar / reemplazar el resumen
Titulo "2) PUT /s3/inscripcion  -> modificar / reemplazar resumen"
$bodyPut = @{
    numeroResumen    = $num
    nombreEstudiante = "Francisco Javier Parra Andia"
    curso            = "Cloud Native - Seccion 002"
    fecha            = "2026-06-01"
} | ConvertTo-Json
Write-Host "Body enviado (dato modificado: curso):" -ForegroundColor Yellow
Write-Host $bodyPut
$r2 = Invoke-RestMethod -Uri "$base/s3/inscripcion" -Method Put -ContentType "application/json" -Body $bodyPut
Write-Host "Respuesta: $r2" -ForegroundColor Green

# 3. GET - Descargar el resumen y mostrar su contenido
Titulo "3) GET /s3/download  -> descargar resumen"
$destino = Join-Path $PSScriptRoot "resumen_$num`_descargado.txt"
Invoke-RestMethod -Uri "$base/s3/download?numeroResumen=$num" -Method Get -OutFile $destino
Write-Host "Archivo descargado en: $destino" -ForegroundColor Green
Write-Host "Contenido del archivo:" -ForegroundColor Yellow
Get-Content $destino | ForEach-Object { Write-Host "   $_" }

Write-Host ""
Write-Host "LISTO: ahora revisa la carpeta $num/ en la consola S3 y captura." -ForegroundColor Magenta
Write-Host "Cuando hayas capturado, ejecuta: .\borrar_endpoint.ps1" -ForegroundColor Magenta
