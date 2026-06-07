# Pruebas de los endpoints de Guias de Despacho - CDY2204 Exp1 S3
# Ejecutar con la app corriendo (local o EC2).
# Uso: powershell -ExecutionPolicy Bypass -File .\pruebas_endpoints.ps1
# Cambia $base a http://107.23.67.117:8080 para probar contra la EC2 desplegada.

$base          = "http://localhost:8080"
$idGuia        = "1001"
$fecha         = "2026-06-07"
$transportista = "transportistaX"

function Titulo($t) {
    Write-Host ""
    Write-Host ("=" * 60) -ForegroundColor Cyan
    Write-Host $t -ForegroundColor Cyan
    Write-Host ("=" * 60) -ForegroundColor Cyan
}

$guia = @{
    idGuia           = $idGuia
    transportista    = $transportista
    fecha            = $fecha
    pedido           = "PED-5001"
    destino          = "Santiago"
    datosAdicionales = "Caja fragil"
} | ConvertTo-Json

# 1. POST - Crear guia (se escribe en EFS) -> Criterio 1
Titulo "1) POST /guias  -> crear guia en EFS"
$r = Invoke-RestMethod -Uri "$base/guias" -Method Post -ContentType "application/json" -Body $guia
Write-Host "Respuesta: $r" -ForegroundColor Green

# 2. POST - Subir a S3 la guia ya creada en EFS (sin body) -> Criterio 2
Titulo "2) POST /guias/$idGuia/upload  -> subir a S3 lo creado en EFS"
$r = Invoke-RestMethod -Uri "$base/guias/$idGuia/upload?fecha=$fecha&transportista=$transportista" -Method Post
Write-Host "Respuesta: $r" -ForegroundColor Green

# 3. GET - Consultar / listar -> Criterio 5
Titulo "3) GET /guias  -> listar por fecha y transportista"
$r = Invoke-RestMethod -Uri "$base/guias?fecha=$fecha&transportista=$transportista" -Method Get
Write-Host "Guias:" -ForegroundColor Green
$r | ForEach-Object { Write-Host "  - $_" }

# 4. GET - Descargar -> Criterio 4
Titulo "4) GET /guias/$idGuia/download  -> descargar de S3"
$destino = Join-Path $PSScriptRoot "guia_$idGuia.descargada.pdf"
Invoke-RestMethod -Uri "$base/guias/$idGuia/download?fecha=$fecha&transportista=$transportista" -Method Get -OutFile $destino
Write-Host "Descargada en: $destino" -ForegroundColor Green
Get-Content $destino | ForEach-Object { Write-Host "  $_" }

# 5. PUT - Actualizar -> Criterio 3
Titulo "5) PUT /guias/$idGuia  -> actualizar guia en S3"
$guiaUpd = @{
    transportista    = $transportista
    fecha            = $fecha
    pedido           = "PED-5001"
    destino          = "Concepcion"
    datosAdicionales = "Destino corregido"
} | ConvertTo-Json
$r = Invoke-RestMethod -Uri "$base/guias/$idGuia" -Method Put -ContentType "application/json" -Body $guiaUpd
Write-Host "Respuesta: $r" -ForegroundColor Green

# 6. DELETE - Eliminar
Titulo "6) DELETE /guias/$idGuia  -> eliminar de S3"
$r = Invoke-RestMethod -Uri "$base/guias/$idGuia?fecha=$fecha&transportista=$transportista" -Method Delete
Write-Host "Respuesta: $r" -ForegroundColor Green

Write-Host ""
Write-Host "Pruebas finalizadas." -ForegroundColor Yellow
