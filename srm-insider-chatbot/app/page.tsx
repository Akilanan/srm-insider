'use client';

import Navbar from './components/Navbar';
import ChatWindow from './components/ChatWindow';
import InputBar from './components/InputBar';
import SuggestedQuestions from './components/SuggestedQuestions';
import ErrorBoundary from './components/ErrorBoundary';
import { useChat } from './hooks/useChat';

function ChatApp() {
  const {
    messages,
    inputValue,
    isLoading,
    error,
    setInputValue,
    sendMessage,
    clearChat,
    messagesEndRef,
  } = useChat();

  const handleSend = async () => {
    await sendMessage(inputValue);
  };

  const handleQuestionSelect = (question: string) => {
    setInputValue(question);
  };

  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Navbar onClearChat={clearChat} />

      <main className="flex flex-col flex-1 pt-16">
        {/* Chat Messages Area */}
        <ChatWindow
          messages={messages}
          isLoading={isLoading}
          error={error}
          messagesEndRef={messagesEndRef}
        />

        {/* Input Area with Suggested Questions */}
        <div className="sticky bottom-0 bg-white border-t border-gray-200 shadow-lg">
          <div className="max-w-4xl mx-auto px-4 py-4">
            <SuggestedQuestions
              onSelect={handleQuestionSelect}
              disabled={isLoading}
            />
            
            <div className="mt-4">
              <InputBar
                value={inputValue}
                onChange={setInputValue}
                onSend={handleSend}
                disabled={isLoading}
              />
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

export default function Home() {
  return (
    <ErrorBoundary>
      <ChatApp />
    </ErrorBoundary>
  );
}
