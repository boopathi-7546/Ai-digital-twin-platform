import apiClient from './apiClient.js';

const adminService = {
  getDashboardStats: () => apiClient.get('/admin/dashboard').then((r) => r.data),

  getAllStudents: () => apiClient.get('/admin/students').then((r) => r.data),
  getStudentById: (id) => apiClient.get(`/admin/students/${id}`).then((r) => r.data),
  updateStudentStatus: (id, active) =>
    apiClient.patch(`/admin/students/${id}/status`, { active }).then((r) => r.data),

  getAllSkills: () => apiClient.get('/admin/skills').then((r) => r.data),
  createSkill: (payload) => apiClient.post('/admin/skills', payload).then((r) => r.data),
  updateSkill: (id, payload) => apiClient.put(`/admin/skills/${id}`, payload).then((r) => r.data),
  deleteSkill: (id) => apiClient.delete(`/admin/skills/${id}`).then((r) => r.data),

  createQuestion: (payload) => apiClient.post('/admin/interviews/questions', payload).then((r) => r.data),
  updateQuestion: (id, payload) => apiClient.put(`/admin/interviews/questions/${id}`, payload).then((r) => r.data),
  deleteQuestion: (id) => apiClient.delete(`/admin/interviews/questions/${id}`).then((r) => r.data),
  getAllSessions: () => apiClient.get('/admin/interviews/sessions').then((r) => r.data),
  getSessionById: (id) => apiClient.get(`/admin/interviews/sessions/${id}`).then((r) => r.data),

  getAllPrompts: () => apiClient.get('/admin/prompts').then((r) => r.data),
  updatePrompt: (key, payload) => apiClient.put(`/admin/prompts/${key}`, payload).then((r) => r.data),

  generateReport: (payload) => apiClient.post('/admin/reports/generate', payload).then((r) => r.data),
  getAllReports: () => apiClient.get('/admin/reports').then((r) => r.data),
  downloadReportUrl: (id) => `/api/admin/reports/${id}/download`,

  getSettings: () => apiClient.get('/admin/settings').then((r) => r.data),
};

export default adminService;
