'use client';

import { Bot, Trash2 } from 'lucide-react';

interface NavbarProps {
  onClearChat: () => void;
}

export default function Navbar({ onClearChat }: NavbarProps) {
  return (
    <nav className="fixed top-0 left-0 right-0 z-50" style={{ backgroundColor: '#003087' }}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-white/10 rounded-lg">
              <Bot className="w-6 h-6 text-white" />
            </div>
            <h1 className="text-xl font-bold text-white tracking-tight">
              SRM Insider AI
            </h1>
          </div>
          
          <button
            onClick={onClearChat}
            className="flex items-center gap-2 px-4 py-2 text-sm text-white bg-white/10 hover:bg-white/20 rounded-lg transition-all duration-200 border border-white/20"
            title="Clear Chat"
          >
            <Trash2 className="w-4 h-4" />
            <span className="hidden sm:inline">Clear Chat</span>
          </button>
        </div>
      </div>
    </nav>
  );
}
