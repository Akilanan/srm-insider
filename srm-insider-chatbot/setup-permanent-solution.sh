#!/bin/bash

# Permanent Solution: Configure Ollama to work with your existing model
# This uses the existing qwen3.5 model but optimizes it for your system

echo "==============================================="
echo "SRM Insider - Permanent Configuration Setup"
echo "==============================================="
echo ""

# Function to check available memory
check_memory() {
    if command -v free &> /dev/null; then
        available_mb=$(free -m | awk '/^Mem:/{print $7}')
        echo "Available RAM: ${available_mb}MB"
        
        if [ "$available_mb" -lt 4000 ]; then
            echo "⚠️  Low memory detected. Will use CPU-only mode for stability."
            return 1
        else
            return 0
        fi
    else
        echo "Cannot detect memory. Assuming low memory system."
        return 1
    fi
}

# Stop existing Ollama
echo "Step 1: Stopping existing Ollama..."
pkill -f "ollama" 2>/dev/null || true
sleep 2

# Check memory
echo ""
echo "Step 2: Checking system resources..."
if check_memory; then
    GPU_LAYERS=10
    echo "✓ Good memory. Using GPU acceleration."
else
    GPU_LAYERS=0
    echo "✓ Using CPU-only mode for stability."
fi

# Create optimized Modelfile
echo ""
echo "Step 3: Creating optimized configuration..."

if [ $GPU_LAYERS -eq 0 ]; then
    # CPU-only configuration
    cat > Modelfile << 'EOF'
FROM qwen3.5:latest

SYSTEM """You are SRM Insider, a helpful and knowledgeable AI assistant specifically designed for SRM University students. You provide accurate, clear, and actionable information about:
- Attendance requirements (75% minimum)
- CGPA calculations and grading
- Exam registration and results
- Placement opportunities and preparation
- Hostel facilities and fees
- Campus transport options
- Fee payment and scholarships
- Academic resources and library

Always be concise but thorough. Provide step-by-step instructions where applicable. If you're not sure about specific dates or amounts, direct students to check the official SRM portal."""

PARAMETER temperature 0.5
PARAMETER num_predict 512
PARAMETER top_k 40
PARAMETER top_p 0.9
PARAMETER repeat_penalty 1.1
PARAMETER num_ctx 2048
EOF
else
    # GPU-assisted configuration
    cat > Modelfile << 'EOF'
FROM qwen3.5:latest

SYSTEM """You are SRM Insider, a helpful and knowledgeable AI assistant specifically designed for SRM University students. You provide accurate, clear, and actionable information about attendance, CGPA, exams, placements, hostels, transport, and campus life."""

PARAMETER temperature 0.5
PARAMETER num_predict 512
PARAMETER num_ctx 2048
EOF
fi

# Create the optimized model
echo ""
echo "Step 4: Creating optimized srm-insider model..."
ollama rm srm-insider 2>/dev/null || true
ollama create srm-insider -f Modelfile

# Set environment variables for low memory
export OLLAMA_NUM_GPU=$GPU_LAYERS
export OLLAMA_THREAD=4
export OLLAMA_BATCH=256

echo ""
echo "Step 5: Starting Ollama with optimized settings..."
echo "GPU Layers: $GPU_LAYERS"
echo "Threads: 4"
echo "Batch Size: 256"

# Start Ollama in background with optimizations
if [ $GPU_LAYERS -eq 0 ]; then
    OLLAMA_NUM_GPU=0 ollama serve &
else
    ollama serve &
fi

OLLAMA_PID=$!
sleep 5

# Test the model
echo ""
echo "Step 6: Testing the model..."
TEST_RESPONSE=$(curl -s http://localhost:11434/api/generate -X POST -H "Content-Type: application/json" -d '{
    "model": "srm-insider",
    "prompt": "Hello, what is SRM University?",
    "stream": false,
    "options": {
        "num_predict": 100
    }
}' --max-time 60)

if echo "$TEST_RESPONSE" | grep -q "response"; then
    echo ""
    echo "✅ SUCCESS! SRM Insider is now running."
    echo "   The model is optimized and ready to use."
    echo ""
    echo "Next steps:"
    echo "1. Start your Next.js app: cd srm-insider-chatbot && npm run dev"
    echo "2. Open http://localhost:3000 in your browser"
    echo ""
else
    echo ""
    echo "❌ Model test failed. Trying alternative configuration..."
    echo "   Response received: $TEST_RESPONSE"
fi

# Keep Ollama running
wait $OLLAMA_PID
