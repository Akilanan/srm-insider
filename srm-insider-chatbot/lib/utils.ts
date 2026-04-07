// Generate unique ID
export function generateId(): string {
  return `${Date.now()}-${Math.random().toString(36).substring(2, 11)}`;
}

// Input validation helpers
export function isValidString(value: unknown): value is string {
  return typeof value === 'string' && value.length > 0 && value.length <= 10000;
}

export function sanitizeString(input: string): string {
  return input
    .replace(/<script\b[^\u003c]*(?:(?!\u003c\/script\u003e)<[^\u003c]*)*<\/script>/gi, '')
    .trim();
}

// Validate message object
export function isValidMessage(msg: unknown): msg is { role: string; content: string } {
  if (typeof msg !== 'object' || msg === null) return false;
  const m = msg as Record<string, unknown>;
  return (
    typeof m.role === 'string' &&
    ['user', 'assistant', 'system'].includes(m.role) &&
    typeof m.content === 'string' &&
    m.content.length > 0 &&
    m.content.length <= 10000
  );
}

// Validate chat request body
export function validateChatRequest(body: unknown): {
  valid: boolean;
  message?: string;
  history?: { role: string; content: string }[];
  error?: string;
} {
  if (typeof body !== 'object' || body === null) {
    return { valid: false, error: 'Invalid request body' };
  }

  const { message, history } = body as { message?: unknown; history?: unknown };

  // Validate message
  if (!isValidString(message)) {
    return { valid: false, error: 'Message must be a non-empty string (max 10000 chars)' };
  }

  const sanitizedMessage = sanitizeString(message);
  if (sanitizedMessage.length === 0) {
    return { valid: false, error: 'Message cannot be empty after sanitization' };
  }

  // Validate history
  let validatedHistory: { role: string; content: string }[] = [];
  
  if (history !== undefined) {
    if (!Array.isArray(history)) {
      return { valid: false, error: 'History must be an array' };
    }

    if (history.length > 20) {
      return { valid: false, error: 'History cannot exceed 20 messages' };
    }

    validatedHistory = history.filter(isValidMessage).map((m) => ({
      role: m.role,
      content: m.content.substring(0, 10000),
    }));
  }

  return {
    valid: true,
    message: sanitizedMessage,
    history: validatedHistory,
  };
}

// Log errors properly
export function logError(context: string, error: unknown): void {
  console.error(`[ERROR] ${context}:`, error instanceof Error ? error.message : error);
  // Could also send to error tracking service here
}

// Safe JSON parse
export function safeJsonParse<T>(text: string): T | null {
  try {
    return JSON.parse(text) as T;
  } catch {
    return null;
  }
}
