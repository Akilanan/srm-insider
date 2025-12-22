import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL;

export const apiClient = axios.create({
  baseURL: API_URL,
});

// Add token to requests
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth endpoints
export const authAPI = {
  register: (email, password, name) =>
    apiClient.post('/auth/register', { email, password, name }),
  login: (email, password) =>
    apiClient.post('/auth/login', { email, password }),
};

// Projects endpoints
export const projectsAPI = {
  getAll: () => apiClient.get('/projects'),
  getById: (id) => apiClient.get(`/projects/${id}`),
  create: (data) => apiClient.post('/projects', data),
  update: (id, data) => apiClient.put(`/projects/${id}`, data),
  delete: (id) => apiClient.delete(`/projects/${id}`),
};

// Feedback endpoints
export const feedbackAPI = {
  add: (projectId, data) =>
    apiClient.post(`/feedback/${projectId}/feedback`, data),
  getAll: (projectId) =>
    apiClient.get(`/feedback/${projectId}/feedback`),
  getStats: (projectId) =>
    apiClient.get(`/feedback/${projectId}/feedback-stats`),
  delete: (feedbackId) =>
    apiClient.delete(`/feedback/${feedbackId}`),
};

// Insights endpoints
export const insightsAPI = {
  generate: (projectId) =>
    apiClient.post(`/insights/${projectId}/insights`),
  get: (projectId) =>
    apiClient.get(`/insights/${projectId}/insights`),
};
