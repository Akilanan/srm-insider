'use client';

import { Send } from 'lucide-react';
import { useRef, useEffect } from 'react';

interface InputBarProps {
  value: string;
  onChange: (value: string) => void;
  onSend: () => void;
  disabled: boolean;
}

export default function InputBar({ value, onChange, onSend, disabled }: InputBarProps) {
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      onSend();
    }
  };

  // Auto-resize textarea
  useEffect(() => {
    const textarea = textareaRef.current;
    if (textarea) {
      textarea.style.height = 'auto';
      textarea.style.height = `${Math.min(textarea.scrollHeight, 120)}px`;
    }
  }, [value]);

  return (
    <div className="flex gap-2 items-end">
      <textarea
        ref={textareaRef}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        onKeyDown={handleKeyDown}
        placeholder="Type your message..."
        disabled={disabled}
        rows={1}
        className="flex-1 px-4 py-3 bg-white border border-gray-300 rounded-xl resize-none text-gray-800 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[#003087] focus:border-transparent transition-all duration-200 disabled:bg-gray-50 disabled:text-gray-500 min-h-[48px] max-h-[120px] text-sm"
      />
      <button
        onClick={onSend}
        disabled={disabled || !value.trim()}
        className="flex-shrink-0 px-4 py-3 rounded-xl font-medium text-white transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed hover:opacity-90 active:scale-95 flex items-center gap-2"
        style={{ backgroundColor: '#003087' }}
        title="Send message"
      >
        <Send className="w-5 h-5" />
      </button>
    </div>
  );
}
