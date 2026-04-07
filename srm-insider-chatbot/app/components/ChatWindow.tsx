'use client';

import type { Message } from '../types/chat';
import ChatMessage from './ChatMessage';
import TypingIndicator from './TypingIndicator';

interface ChatWindowProps {
  messages: Message[];
  isLoading: boolean;
  error: string | null;
  messagesEndRef: React.RefObject<HTMLDivElement | null>;
}

export default function ChatWindow({
  messages,
  isLoading,
  error,
  messagesEndRef,
}: ChatWindowProps) {
  return (
    <div className="flex-1 overflow-y-auto bg-gray-50">
      <div className="max-w-4xl mx-auto px-4 py-6 space-y-6 min-h-full">
        {messages
          .filter((message) => message.content || message.role === 'user')
          .map((message) => (
          <ChatMessage key={message.id} message={message} />
        ))}
        
        {isLoading && <TypingIndicator />}
        
        {error && (
          <div className="flex justify-center animate-in fade-in duration-300">
            <div className="px-4 py-3 rounded-lg bg-red-50 border border-red-200 text-red-700 text-sm">
              <span className="font-medium">Error: </span>
              {error}
            </div>
          </div>
        )}
        
        <div ref={messagesEndRef} />
      </div>
    </div>
  );
}
