import apiClient from './apiClient.js';

const authService = {
  async register(payload) {
    const { data } = await apiClient.post('/auth/register', payload);
    return data;
  },

  async login(payload) {
    const { data } = await apiClient.post('/auth/login', payload);
    return data;
  },

  async refreshToken(refreshToken) {
    const { data } = await apiClient.post('/auth/refresh-token', { refreshToken });
    return data;
  },

  async verifyEmail(token) {
    const { data } = await apiClient.post('/auth/verify-email', { token });
    return data;
  },

  async forgotPassword(email) {
    const { data } = await apiClient.post('/auth/forgot-password', { email });
    return data;
  },

  async resetPassword(token, newPassword) {
    const { data } = await apiClient.post('/auth/reset-password', { token, newPassword });
    return data;
  },
};

export default authService;
