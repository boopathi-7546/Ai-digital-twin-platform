import { createContext, useCallback, useEffect, useMemo, useState } from 'react';
import authService from '../services/authService.js';

export const AuthContext = createContext(null);

const ACCESS_TOKEN_KEY = 'dtp_access_token';
const REFRESH_TOKEN_KEY = 'dtp_refresh_token';
const USER_KEY = 'dtp_user';

/**
 * Holds the authenticated user + tokens for the whole app. Tokens are
 * kept in memory + a simple persisted object (not localStorage APIs
 * directly used by *artifacts*, but fine here since this is a real
 * Vite app running in the user's own browser, not the Claude.ai
 * artifact sandbox).
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  });
  const [accessToken, setAccessToken] = useState(() => localStorage.getItem(ACCESS_TOKEN_KEY));
  const [refreshToken, setRefreshToken] = useState(() => localStorage.getItem(REFRESH_TOKEN_KEY));
  const [loading, setLoading] = useState(false);

  const persistSession = useCallback((loginResponse) => {
    const { accessToken: at, refreshToken: rt, tokenType, ...userInfo } = loginResponse;
    localStorage.setItem(ACCESS_TOKEN_KEY, at);
    localStorage.setItem(REFRESH_TOKEN_KEY, rt);
    localStorage.setItem(USER_KEY, JSON.stringify(userInfo));
    setAccessToken(at);
    setRefreshToken(rt);
    setUser(userInfo);
  }, []);

  const login = useCallback(async (credentials) => {
    setLoading(true);
    try {
      const response = await authService.login(credentials);
      persistSession(response);
      return response;
    } finally {
      setLoading(false);
    }
  }, [persistSession]);

  const register = useCallback(async (payload) => {
    setLoading(true);
    try {
      return await authService.register(payload);
    } finally {
      setLoading(false);
    }
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    setAccessToken(null);
    setRefreshToken(null);
    setUser(null);
  }, []);

  const refreshSession = useCallback(async () => {
    if (!refreshToken) throw new Error('No refresh token available');
    const response = await authService.refreshToken(refreshToken);
    persistSession(response);
    return response;
  }, [refreshToken, persistSession]);

  // Keep tabs in sync if the user logs out in another tab.
  useEffect(() => {
    const onStorage = (e) => {
      if (e.key === ACCESS_TOKEN_KEY && !e.newValue) {
        setAccessToken(null);
        setUser(null);
      }
    };
    window.addEventListener('storage', onStorage);
    return () => window.removeEventListener('storage', onStorage);
  }, []);

  const isAdmin = useMemo(() => user?.roles?.includes('ROLE_ADMIN'), [user]);
  const isStudent = useMemo(() => user?.roles?.includes('ROLE_STUDENT'), [user]);

  const value = useMemo(() => ({
    user,
    accessToken,
    refreshToken,
    loading,
    isAuthenticated: !!accessToken,
    isAdmin,
    isStudent,
    login,
    register,
    logout,
    refreshSession,
  }), [user, accessToken, refreshToken, loading, isAdmin, isStudent, login, register, logout, refreshSession]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
