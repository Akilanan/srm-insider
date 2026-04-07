'use client';

import { Sparkles } from 'lucide-react';

const SUGGESTED_QUESTIONS = [
  'How do I check my attendance?',
  'What is the minimum attendance required?',
  'How is CGPA calculated?',
  'How do I register for exams?',
];

interface SuggestedQuestionsProps {
  onSelect: (question: string) => void;
  disabled: boolean;
}

export default function SuggestedQuestions({ onSelect, disabled }: SuggestedQuestionsProps) {
  return (
    <div className="mt-4">
      <div className="flex items-center gap-2 mb-3">
        <Sparkles className="w-4 h-4 text-gray-500" />
        <span className="text-xs font-medium text-gray-500 uppercase tracking-wider">
          Suggested Questions
        </span>
      </div>
      
      <div className="flex flex-wrap gap-2">
        {SUGGESTED_QUESTIONS.map((question, index) => (
          <button
            key={index}
            onClick={() => onSelect(question)}
            disabled={disabled}
            className="px-4 py-2 text-sm text-gray-700 bg-white border border-gray-300 rounded-full hover:border-[#003087] hover:text-[#003087] transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:border-gray-300 disabled:hover:text-gray-700"
          >
            {question}
          </button>
        ))}
      </div>
    </div>
  );
}
