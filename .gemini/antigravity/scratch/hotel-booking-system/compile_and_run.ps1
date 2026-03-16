# compile_and_run.ps1
# Hotel Booking Management System
# Compiles all Java sources and runs the main demo with assertions enabled.

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$src = Join-Path $projectRoot "src"
$out = Join-Path $projectRoot "out"

Write-Host ""
Write-Host "=========================================="
Write-Host "  Hotel Booking Management System"
Write-Host "  Compiling..."
Write-Host "=========================================="

# Create output directory
New-Item -ItemType Directory -Force -Path $out | Out-Null

# Collect all .java files recursively
$javaFiles = Get-ChildItem -Recurse -Filter "*.java" $src | Select-Object -ExpandProperty FullName

if ($javaFiles.Count -eq 0) {
    Write-Host "ERROR: No .java files found under $src" -ForegroundColor Red
    exit 1
}

Write-Host "Found $($javaFiles.Count) source files."

# Compile
javac -d $out $javaFiles
if ($LASTEXITCODE -ne 0) {
    Write-Host "COMPILATION FAILED." -ForegroundColor Red
    exit 1
}

Write-Host "Compilation successful." -ForegroundColor Green
Write-Host ""
Write-Host "=========================================="
Write-Host "  Running demo (assertions enabled)..."
Write-Host "=========================================="
Write-Host ""

# Run with assertions enabled (-ea)
java -ea -cp $out com.hotel.Main

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "RUNTIME ERROR or ASSERTION FAILURE." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "All done!" -ForegroundColor Green
