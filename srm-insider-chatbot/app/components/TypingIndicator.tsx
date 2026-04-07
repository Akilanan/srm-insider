'use client';

import { Bot } from 'lucide-react';

export default function TypingIndicator() {
  return (
    <div className="flex w-full justify-start animate-in fade-in duration-300">
      <div className="flex items-start gap-3 max-w-[85%] sm:max-w-[75%]">
        {/* Avatar */}
        <div className="flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center shadow-md bg-white border-2 border-gray-200">
          <Bot className="w-4 h-4 text-gray-600" />
        </div>

        {/* Typing Bubble */}
        <div className="px-4 py-3 rounded-2xl bg-white border border-gray-200 rounded-tl-md shadow-sm">
          <div className="flex items-center gap-1">
            <span className="text-xs text-gray-500 mr-2">SRM Insider is typing</span>
            <div className="flex gap-1">
              <span
                className="w-2 h-2 rounded-full bg-gray-400 animate-bounce"
                style={{ animationDelay: '0ms' }}
              />
              <span
                className="w-2 h-2 rounded-full bg-gray-400 animate-bounce"
                style={{ animationDelay: '150ms' }}
              />
              <span
                className="w-2 h-2 rounded-full bg-gray-400 animate-bounce"
                style={{ animationDelay: '300ms' }}
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
