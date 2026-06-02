# Borrado del resumen en S3 - CDY2204 Exp1 S2
# Ejecutar DESPUES de haber capturado el bucket con el archivo presente.
# Uso: powershell -ExecutionPolicy Bypass -File .\borrar_endpoint.ps1

$base = "http://localhost:8080"
$num  = "1001"

Write-Host ""
Write-Host ("=" * 60) -ForegroundColor Cyan
Write-Host "4) DELETE /s3/inscripcion  -> borrar resumen" -ForegroundColor Cyan
Write-Host ("=" * 60) -ForegroundColor Cyan

$r = Invoke-RestMethod -Uri "$base/s3/inscripcion?numeroResumen=$num" -Method Delete
Write-Host "Respuesta: $r" -ForegroundColor Green
Write-Host ""
Write-Host "LISTO: ahora revisa que la carpeta $num/ quedo vacia en la consola S3 y captura." -ForegroundColor Magenta
