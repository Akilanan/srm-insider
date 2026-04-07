#!/bin/bash
set -e

echo "=========================================="
echo "Fixing SRM Insider Model"
echo "=========================================="

# Stop Ollama
echo "Stopping Ollama..."
pkill -f "ollama serve" 2>/dev/null || true
sleep 2

# Start Ollama in background
echo "Starting Ollama..."
ollama serve &
OLLAMA_PID=$!
sleep 3

# Recreate the model with optimized settings
echo "Creating optimized srm-insider model..."
cd "$(dirname "$0")"
ollama rm srm-insider 2>/dev/null || true
ollama create srm-insider -f Modelfile

# Test the model
echo "Testing model..."
curl -s http://localhost:11434/api/generate \
  -H "Content-Type: application/json" \
  -d '{"model":"srm-insider","prompt":"Hello","stream":false,"options":{"temperature":0.3,"num_predict":50}}' \
  --max-time 30 || echo "Test failed - may need more memory"

echo ""
echo "=========================================="
echo "Setup complete!"
echo "=========================================="

# Keep Ollama running
wait $OLLAMA_PID
