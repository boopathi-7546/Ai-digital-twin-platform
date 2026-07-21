import apiClient from './apiClient.js';

const resumeService = {
  getMyResumes: () => apiClient.get('/student/resumes').then((r) => r.data),

  uploadResume: (file, onProgress) => {
    const formData = new FormData();
    formData.append('file', file);
    return apiClient
      .post('/student/resumes/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        onUploadProgress: (evt) => {
          if (onProgress && evt.total) {
            onProgress(Math.round((evt.loaded * 100) / evt.total));
          }
        },
      })
      .then((r) => r.data);
  },

  analyzeResume: (resumeId) => apiClient.post(`/student/resumes/${resumeId}/analyze`).then((r) => r.data),

  getLatestAnalysis: (resumeId) => apiClient.get(`/student/resumes/${resumeId}/analysis`).then((r) => r.data),

  downloadResumeUrl: (resumeId) => `/api/student/resumes/${resumeId}/download`,

  deleteResume: (resumeId) => apiClient.delete(`/student/resumes/${resumeId}`).then((r) => r.data),
};

export default resumeService;
