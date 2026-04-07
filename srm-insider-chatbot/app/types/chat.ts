export interface Message {
  id: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
  timestamp: number;
  isComplete?: boolean;
  isCached?: boolean;
}

export interface ChatRequest {
  message: string;
  history: Pick<Message, 'role' | 'content'>[];
}

export interface OllamaMessage {
  role: string;
  content: string;
}

export interface OllamaStreamResponse {
  model: string;
  created_at: string;
  message: {
    role: string;
    content: string;
  };
  done: boolean;
}
