import { useState, useRef, useCallback, useEffect } from 'react';
import type { Message } from '../types/chat';
import { generateId, safeJsonParse, logError } from '@/lib/utils';

interface UseChatReturn {
  messages: Message[];
  inputValue: string;
  isLoading: boolean;
  error: string | null;
  setInputValue: (value: string) => void;
  sendMessage: (text: string) => Promise<void>;
  clearChat: () => void;
  messagesEndRef: React.RefObject<HTMLDivElement | null>;
}

// Simple hash for cache key
const simpleHash = (str: string): string => {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    const char = str.charCodeAt(i);
    hash = ((hash << 5) - hash) + char;
    hash = hash & hash;
  }
  return hash.toString(16);
};

// In-memory cache
const messageCache = new Map<string, string>();
const MAX_CACHE_SIZE = 50;

const WELCOME_CONTENT = 'Hi! I am your SRM Insider Assistant. I can help you with SRMIST-related queries about attendance, CGPA, exams, placements, hostels, and more!';



export function useChat(): UseChatReturn {
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  
  // Use refs to avoid stale closures
  const contentBufferRef = useRef('');
  const updateTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const abortControllerRef = useRef<AbortController | null>(null);

  // Initialize welcome message
  useEffect(() => {
    setMessages([{
      id: generateId(),
      role: 'assistant',
      content: WELCOME_CONTENT,
      timestamp: Date.now(),
    }]);
  }, []);

  const scrollToBottom = useCallback(() => {
    requestAnimationFrame(() => {
      messagesEndRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
    });
  }, []);

  const sendMessage = useCallback(async (text: string) => {
    if (!text.trim() || isLoading) return;

    const trimmedText = text.trim();
    const startTime = performance.now();
    
    // Add user message
    const userMessage: Message = {
      id: generateId(),
      role: 'user',
      content: trimmedText,
      timestamp: Date.now(),
    };
    
    setMessages((prev) => [...prev, userMessage]);
    setInputValue('');
    setIsLoading(true);
    setError(null);
    
    scrollToBottom();


    // Prepare history
    const history = messages.slice(-4).map((m) => ({
      role: m.role,
      content: m.content,
    }));

    const botMessageId = generateId();
    const botMessage: Message = {
      id: botMessageId,
      role: 'assistant',
      content: '',
      timestamp: Date.now(),
      isComplete: false,
    };
    setMessages((prev) => [...prev, botMessage]);

    // Create abort controller for timeout
    abortControllerRef.current = new AbortController();
    const timeoutId = setTimeout(() => {
      abortControllerRef.current?.abort();
    }, 60000); // 60 second frontend timeout

    try {
      const response = await fetch('/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
          message: trimmedText, 
          history,
        }),
        signal: abortControllerRef.current.signal,
      });

      clearTimeout(timeoutId);

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ error: 'Unknown error' }));
        throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
      }

      // Parse JSON response (non-streaming now)
      const data = await response.json();
      
      setMessages((prev) => {
        const newMessages = [...prev];
        const lastMessage = newMessages[newMessages.length - 1];
        if (lastMessage?.role === 'assistant' && lastMessage.id === botMessageId) {
          lastMessage.content = data.content || 'No response received';
          lastMessage.isComplete = true;
          lastMessage.isCached = data.cached || data.offline || false;
          lastMessage.timestamp = Date.now();
        }
        return newMessages;
      });

      console.log(`[Performance] Response time: ${(performance.now() - startTime).toFixed(0)}ms`);

    } catch (err) {
      const isTimeout = err instanceof Error && err.name === 'AbortError';
      
      if (!isTimeout) {
        logError('Send Message', err);
      }
      
      // Show graceful error or fallback response
      const errorContent = isTimeout 
        ? `⏱️ *Response taking longer than expected...*\n\nHere is what I can tell you:\n\nI can help with common SRM topics like attendance (75% required), CGPA calculation, exam registration, placements, hostels, and transport.\n\nTry asking a specific question like:\n• "What is CGPA?"\n• "Attendance requirement?"\n• "How to register for exams?"\n• "Placement criteria?"`
        : `❌ Unable to connect. Showing offline info:\n\n**Quick Help:**\n• Attendance: 75% minimum required\n• CGPA: Weighted average of grade points\n• Exams: Register via portal, pay fees\n• Placements: 6.0+ CGPA, no backlogs\n• Hostel: AC/Non-AC with mess\n• Transport: Buses cover city routes`;
      
      setMessages((prev) => {
        const newMessages = [...prev];
        const lastMessage = newMessages[newMessages.length - 1];
        if (lastMessage?.role === 'assistant' && lastMessage.id === botMessageId) {
          lastMessage.content = errorContent;
          lastMessage.isComplete = true;
          lastMessage.timestamp = Date.now();
        }
        return newMessages;
      });
    } finally {
      setIsLoading(false);
      abortControllerRef.current = null;
      scrollToBottom();
    }
  }, [messages, isLoading, scrollToBottom]);

  const clearChat = useCallback(() => {
    abortControllerRef.current?.abort();
    if (updateTimeoutRef.current) {
      clearTimeout(updateTimeoutRef.current);
    }
    contentBufferRef.current = '';
    
    setMessages([{
      id: generateId(),
      role: 'assistant',
      content: WELCOME_CONTENT,
      timestamp: Date.now(),
    }]);
    setError(null);
    setInputValue('');
  }, []);

  return {
    messages,
    inputValue,
    isLoading,
    error,
    setInputValue,
    sendMessage,
    clearChat,
    messagesEndRef,
  };
}
