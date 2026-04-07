import { NextRequest, NextResponse } from 'next/server';
import { headers as nextHeaders } from 'next/headers';
import { checkRateLimit } from '@/lib/rate-limit';
import { validateChatRequest, logError, safeJsonParse } from '@/lib/utils';

const OLLAMA_URL = process.env.OLLAMA_URL || 'http://localhost:11434/api/generate';
const MODEL_NAME = process.env.OLLAMA_MODEL || 'llama3.2';
const REQUEST_TIMEOUT = 55000; // 55 seconds to accommodate local LLM startup

// In-memory cache for common queries
interface CacheEntry {
  content: string;
  timestamp: number;
}
const responseCache = new Map<string, CacheEntry>();
const CACHE_TTL = 5 * 60 * 1000; // 5 minutes
const MAX_CACHE_SIZE = 100;



// Generate cache key
function getCacheKey(message: string): string {
  return message.toLowerCase().trim().replace(/\s+/g, ' ').substring(0, 200);
}

// Check cache
function getCachedResponse(message: string): string | null {
  const key = getCacheKey(message);
  const cached = responseCache.get(key);
  if (cached && Date.now() - cached.timestamp < CACHE_TTL) {
    return cached.content;
  }
  if (cached) responseCache.delete(key);
  return null;
}

// Store in cache
function cacheResponse(message: string, content: string): void {
  const key = getCacheKey(message);
  if (content.length < 1000 && responseCache.size < MAX_CACHE_SIZE) {
    responseCache.set(key, { content, timestamp: Date.now() });
  }
}

// Clean cache periodically
setInterval(() => {
  const now = Date.now();
  for (const [key, entry] of responseCache.entries()) {
    if (now - entry.timestamp > CACHE_TTL) {
      responseCache.delete(key);
    }
  }
}, 60 * 1000);



// AI fallback when Ollama is unavailable
function generateOfflineResponse(message: string): string {
  const lowerMsg = message.toLowerCase().trim();
  
  // Try to extract topic
  const topics = [];
  if (/cgpa|grade|gpa|pointer|mark/i.test(lowerMsg)) topics.push('cgpa');
  if (/attendance|present|absent/i.test(lowerMsg)) topics.push('attendance');
  if (/exam|test|cat|fat/i.test(lowerMsg)) topics.push('exams');
  if (/placement|job|company|career/i.test(lowerMsg)) topics.push('placements');
  if (/hostel|room|mess|stay/i.test(lowerMsg)) topics.push('hostel');
  if (/bus|transport|travel/i.test(lowerMsg)) topics.push('transport');
  if (/fee|payment|scholarship|money/i.test(lowerMsg)) topics.push('fees');
  if (/backlog|arrear|fail/i.test(lowerMsg)) topics.push('backlogs');
  if (/wifi|internet|portal|login/i.test(lowerMsg)) topics.push('technology');
  if (/library|book|study/i.test(lowerMsg)) topics.push('library');
  
  if (topics.length > 0) {
    return `I noticed you're asking about ${topics.join(', ')}. While I'm currently running offline mode, I have extensive info on these topics.\n\nTry asking one of these specific questions:\n• "What is the attendance requirement?"\n• "How to calculate CGPA?"\n• "What are placement criteria?"\n• "What are hostel fees?"\n• "How to pay exam fees?"\n\nOr check your student portal for detailed information.`;
  }
  
  return `I'm currently operating offline mode due to system maintenance.\n\nBut I can help with these common SRM topics:\n\n📚 **Academic:**\n• Attendance rules\n• CGPA calculation\n• Exam registration\n• Backlogs/re-exams\n\n🏠 **Campus:**\n• Hostel info & fees\n• Transport routes\n• WiFi access\n• Library facilities\n\n💼 **Career:**\n• Placement criteria\n• Internship opportunities\n• Preparation tips\n\nTry asking a specific question about any of these!`;
}

export async function POST(request: NextRequest) {
  const startTime = Date.now();
  
  try {
    // Rate limiting
    const headersList = await nextHeaders();
    const ip = headersList.get('x-forwarded-for')?.split(',')[0]?.trim() || 'unknown';
    const rateLimit = checkRateLimit(ip);
    
    if (!rateLimit.allowed) {
      return NextResponse.json(
        { 
          error: 'Rate limit exceeded', 
          retryAfter: Math.ceil((rateLimit.resetTime - Date.now()) / 1000) 
        },
        { 
          status: 429,
          headers: {
            'X-RateLimit-Limit': '10',
            'X-RateLimit-Remaining': '0',
            'X-RateLimit-Reset': String(Math.ceil(rateLimit.resetTime / 1000)),
          }
        }
      );
    }

    // Parse and validate request body
    let body: unknown;
    try {
      body = await request.json();
    } catch (e) {
      logError('JSON Parse', e);
      return NextResponse.json(
        { error: 'Invalid JSON in request body' },
        { status: 400 }
      );
    }

    const validation = validateChatRequest(body);
    if (!validation.valid) {
      return NextResponse.json(
        { error: validation.error },
        { status: 400 }
      );
    }

    const { message: userMessage, history = [] } = validation;
    if (!userMessage) {
      return NextResponse.json(
        { error: 'Message is required' },
        { status: 400 }
      );
    }

    // 2. Check cache
    const cachedResponse = getCachedResponse(userMessage);
    if (cachedResponse) {
      return NextResponse.json({
        content: cachedResponse,
        done: true,
        cached: true,
      }, {
        headers: {
          'X-Response-Time': `${Date.now() - startTime}ms`,
          'X-Cache': 'HIT',
        }
      });
    }

    // 3. Try Ollama with quick timeout
    const controller = new AbortController();
    const timeoutId = setTimeout(() => {
      controller.abort();
    }, REQUEST_TIMEOUT);

    let ollamaResponse: Response | null = null;
    let ollamaFailed = false;
    
    try {
      ollamaResponse = await fetch(OLLAMA_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          model: MODEL_NAME,
          prompt: `You are SRM Insider, an assistant for SRM University students.
You only answer based on SRM-related information such as:
- Courses
- Clubs
- Events
- Placements
- Hostel & campus life

If you don't know something, say:
"I don’t have that information yet. Please check official SRM sources."

Keep answers clear, short, and helpful.

User: ${userMessage}
Assistant:`,
          stream: false,
          options: {
            temperature: 0.3,
            num_predict: 150,
            top_k: 20,
            top_p: 0.85,
          },
        }),
        signal: controller.signal,
      });
    } catch (error) {
      ollamaFailed = true;
    }

    clearTimeout(timeoutId);

    // If Ollama failed, use offline response
    if (ollamaFailed || !ollamaResponse || !ollamaResponse.ok) {
      const offlineResponse = generateOfflineResponse(userMessage);
      
      cacheResponse(userMessage, offlineResponse);
      
      return NextResponse.json({
        content: offlineResponse,
        done: true,
        offline: true,
      }, {
        headers: {
          'X-Response-Time': `${Date.now() - startTime}ms`,
          'X-Cache': 'MISS',
          'X-RateLimit-Remaining': String(rateLimit.remaining),
        }
      });
    }

    // Parse Ollama response
    const data = await ollamaResponse.json().catch(() => null);
    
    if (!data?.response) {
      const offlineResponse = generateOfflineResponse(userMessage);
      cacheResponse(userMessage, offlineResponse);
      
      return NextResponse.json({
        content: offlineResponse,
        done: true,
        offline: true,
      }, {
        headers: {
          'X-Response-Time': `${Date.now() - startTime}ms`,
        }
      });
    }

    const aiResponse = data.response.trim();
    cacheResponse(userMessage, aiResponse);

    return NextResponse.json({
      content: aiResponse,
      done: true,
      ai: true,
    }, {
      headers: {
        'X-Response-Time': `${Date.now() - startTime}ms`,
        'X-RateLimit-Remaining': String(rateLimit.remaining),
      }
    });

  } catch (error) {
    logError('Chat API', error);
    return NextResponse.json(
      { 
        error: 'Internal server error',
        details: process.env.NODE_ENV === 'development' 
          ? error instanceof Error ? error.message : 'Unknown error'
          : undefined
      },
      { status: 500 }
    );
  }
}
