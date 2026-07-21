import apiClient from './apiClient.js';

const interviewService = {
  startSession: (payload) => apiClient.post('/student/interviews/start', payload).then((r) => r.data),
  getMySessions: () => apiClient.get('/student/interviews').then((r) => r.data),
  getSession: (sessionId) => apiClient.get(`/student/interviews/${sessionId}`).then((r) => r.data),
  submitAnswer: (sessionId, payload) =>
    apiClient.post(`/student/interviews/${sessionId}/answers`, payload).then((r) => r.data),
  completeSession: (sessionId) => apiClient.post(`/student/interviews/${sessionId}/complete`).then((r) => r.data),
  getFeedback: (sessionId) => apiClient.get(`/student/interviews/${sessionId}/feedback`).then((r) => r.data),

  getQuestionBank: (params) => apiClient.get('/student/question-bank', { params }).then((r) => r.data),
};

export default interviewService;
