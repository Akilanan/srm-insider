@echo off
REM Permanent Solution: Configure Ollama to work with your existing model
REM This uses the existing qwen3.5 model but optimizes it for your system

echo ===============================================
echo SRM Insider - Permanent Configuration Setup
echo ===============================================
echo.

REM Step 1: Stop existing Ollama
echo Step 1: Stopping existing Ollama...
taskkill /F /IM ollama.exe 2>NUL
taskkill /F /IM ollama-app.exe 2>NUL
timeout /t 2 /nobreak >NUL

REM Step 2: Check memory
echo.
echo Step 2: Detecting system configuration...
echo (Using CPU-optimized mode for maximum compatibility)

set GPU_LAYERS=0
set THREADS=4
set BATCH=256

REM Step 3: Create optimized Modelfile
echo.
echo Step 3: Creating optimized configuration...

cd /d "%~dp0"

(
echo FROM qwen3.5:latest
echo.
echo SYSTEM """You are SRM Insider, a helpful and knowledgeable AI assistant specifically designed for SRM University students. You provide accurate, clear, and actionable information about:
echo - Attendance requirements (75%% minimum)
echo - CGPA calculations and grading
echo - Exam registration and results
echo - Placement opportunities and preparation
echo - Hostel facilities and fees
echo - Campus transport options
echo - Fee payment and scholarships
echo - Academic resources and library
echo.
echo Always be concise but thorough. Provide step-by-step instructions where applicable."""
echo.
echo PARAMETER temperature 0.5
echo PARAMETER num_predict 512
echo PARAMETER top_k 40
echo PARAMETER top_p 0.9
echo PARAMETER repeat_penalty 1.1
echo PARAMETER num_ctx 2048
) > Modelfile

REM Step 4: Create optimized model
echo.
echo Step 4: Creating optimized srm-insider model...
ollama rm srm-insider 2>NUL
ollama create srm-insider -f Modelfile

IF %ERRORLEVEL% NEQ 0 (
    echo Error: Failed to create model
    pause
    exit /b 1
)

REM Step 5: Configure environment
echo.
echo Step 5: Setting up Ollama environment...
echo GPU Layers: %GPU_LAYERS%
echo Threads: %THREADS%
echo Batch Size: %BATCH%

set OLLAMA_NUM_GPU=%GPU_LAYERS%
set OLLAMA_THREAD=%THREADS%
set OLLAMA_BATCH=%BATCH%

REM Step 6: Start Ollama
echo.
echo Step 6: Starting Ollama server...
start /B ollama serve > ollama.log 2>&1

timeout /t 5 /nobreak >NUL

echo.
echo Step 7: Testing the model...
curl -s -X POST http://localhost:11434/api/generate -H "Content-Type: application/json" -d "{\"model\":\"srm-insider\",\"prompt\":\"Hello, what is SRM University?\",\"stream\":false,\"options\":{\"num_predict\":100}}" --max-time 60 > test_response.json 2>NUL

type test_response.json | findstr "response" >NUL
IF %ERRORLEVEL% EQU 0 (
    echo.
    echo ===========================================
    echo SUCCESS! SRM Insider is now running.
    echo The model is optimized and ready.
    echo ===========================================
    echo.
    echo Next steps:
    echo 1. cd srm-insider-chatbot
    echo 2. npm run dev
    echo 3. Open http://localhost:3000
    echo.
) ELSE (
    echo.
    echo ===========================================
    echo WARNING: Model test failed.
    echo Check ollama.log for details.
    echo ===========================================
    echo.
)

pause
