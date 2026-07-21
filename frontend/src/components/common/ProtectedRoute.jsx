import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth.js';
import { PageLoader } from './Loader.jsx';

/**
 * Guards a route subtree behind authentication, and optionally a
 * specific role ("STUDENT" or "ADMIN"). Unauthenticated users are
 * bounced to /login with the attempted location preserved so they
 * land back where they meant to go after signing in.
 */
export function ProtectedRoute({ role }) {
  const { isAuthenticated, isAdmin, isStudent, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return <PageLoader label="Checking your session…" />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (role === 'ADMIN' && !isAdmin) {
    return <Navigate to="/student/dashboard" replace />;
  }

  if (role === 'STUDENT' && !isStudent && !isAdmin) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}

export default ProtectedRoute;
