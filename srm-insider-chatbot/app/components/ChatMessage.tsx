'use client';

import { User, Bot } from 'lucide-react';
import type { Message } from '../types/chat';
import { sanitizeString } from '@/lib/utils';

interface ChatMessageProps {
  message: Message;
}

export default function ChatMessage({ message }: ChatMessageProps) {
  const isUser = message.role === 'user';
  const sanitizedContent = sanitizeString(message.content || '');
  const lines = sanitizedContent.split('\n');
  
  return (
    <div
      className={`flex w-full ${
        isUser ? 'justify-end' : 'justify-start'
      } animate-in fade-in slide-in-from-bottom-2 duration-300`}
    >
      <div
        className={`flex items-start gap-3 max-w-[85%] sm:max-w-[75%] ${
          isUser ? 'flex-row-reverse' : 'flex-row'
        }`}
      >
        {/* Avatar */}
        <div
          className={`flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center shadow-md ${
            isUser
              ? 'bg-blue-100'
              : 'bg-white border-2 border-gray-200'
          }`}
        >
          {isUser ? (
            <User className="w-4 h-4 text-blue-600" />
          ) : (
            <Bot className="w-4 h-4 text-gray-600" />
          )}
        </div>

        {/* Message Bubble */}
        <div
          className={`px-4 py-3 rounded-2xl shadow-sm text-sm leading-relaxed whitespace-pre-wrap break-words ${
            isUser
              ? 'bg-blue-50 text-gray-800 border border-blue-100 rounded-tr-md'
              : 'bg-white text-gray-800 border border-gray-200 rounded-tl-md'
          }`}
        >
          {lines.map((line, index) => (
            <span key={index}>
              {line}
              {index < lines.length - 1 && <br />}
            </span>
          ))}
        </div>
      </div>
    </div>
  );
}
