import apiClient from './apiClient.js';

const studentService = {
  getProfile: () => apiClient.get('/student/profile').then((r) => r.data),
  updateProfile: (payload) => apiClient.put('/student/profile', payload).then((r) => r.data),

  addEducation: (payload) => apiClient.post('/student/education', payload).then((r) => r.data),
  updateEducation: (id, payload) => apiClient.put(`/student/education/${id}`, payload).then((r) => r.data),
  deleteEducation: (id) => apiClient.delete(`/student/education/${id}`).then((r) => r.data),

  getSkills: () => apiClient.get('/student/skills').then((r) => r.data),
  addOrUpdateSkill: (payload) => apiClient.post('/student/skills', payload).then((r) => r.data),
  removeSkill: (id) => apiClient.delete(`/student/skills/${id}`).then((r) => r.data),

  addProject: (payload) => apiClient.post('/student/projects', payload).then((r) => r.data),
  updateProject: (id, payload) => apiClient.put(`/student/projects/${id}`, payload).then((r) => r.data),
  deleteProject: (id) => apiClient.delete(`/student/projects/${id}`).then((r) => r.data),

  addCertification: (payload) => apiClient.post('/student/certifications', payload).then((r) => r.data),
  updateCertification: (id, payload) => apiClient.put(`/student/certifications/${id}`, payload).then((r) => r.data),
  deleteCertification: (id) => apiClient.delete(`/student/certifications/${id}`).then((r) => r.data),

  addAchievement: (payload) => apiClient.post('/student/achievements', payload).then((r) => r.data),
  updateAchievement: (id, payload) => apiClient.put(`/student/achievements/${id}`, payload).then((r) => r.data),
  deleteAchievement: (id) => apiClient.delete(`/student/achievements/${id}`).then((r) => r.data),

  getDigitalTwin: () => apiClient.get('/student/digital-twin').then((r) => r.data),
  regenerateDigitalTwin: () => apiClient.post('/student/digital-twin/regenerate').then((r) => r.data),

  getAnalytics: () => apiClient.get('/student/analytics').then((r) => r.data),

  getSkillGapAnalyses: () => apiClient.get('/student/skill-gap').then((r) => r.data),
  analyzeSkillGap: (targetRole) => apiClient.post('/student/skill-gap/analyze', { targetRole }).then((r) => r.data),

  getRoadmaps: () => apiClient.get('/student/roadmaps').then((r) => r.data),
  getRoadmap: (id) => apiClient.get(`/student/roadmaps/${id}`).then((r) => r.data),
  generateRoadmap: (skillGapAnalysisId) =>
    apiClient.post(`/student/roadmaps/generate/${skillGapAnalysisId}`).then((r) => r.data),
  markRoadmapItemComplete: (roadmapId, itemId, completed) =>
    apiClient.patch(`/student/roadmaps/${roadmapId}/items/${itemId}`, { completed }).then((r) => r.data),
};

export default studentService;
