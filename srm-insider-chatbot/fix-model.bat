@echo off
echo ==========================================
echo Fixing SRM Insider Model
echo ==========================================

REM Stop Ollama
echo Stopping Ollama...
taskkill /F /IM ollama.exe 2>nul
timeout /t 2 /nobreak >nul

REM Start Ollama in background
echo Starting Ollama...
start /B ollama serve

timeout /t 3 /nobreak >nul

REM Recreate the model with optimized settings
echo Creating optimized srm-insider model...
cd /d "%~dp0"
ollama rm srm-insider 2>nul
ollama create srm-insider -f Modelfile

REM Test the model
echo Testing model...
curl -s http://localhost:11434/api/generate -H "Content-Type: application/json" -d "{\"model\":\"srm-insider\",\"prompt\":\"Hello\",\"stream\":false,\"options\":{\"temperature\":0.3,\"num_predict\":50}}" --max-time 30

echo.
echo ==========================================
echo Setup complete! 
echo ==========================================
pause
