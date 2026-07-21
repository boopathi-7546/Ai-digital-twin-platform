import { Navigate } from 'react-router-dom';

import MainLayout from './layouts/MainLayout.jsx';
import AuthLayout from './layouts/AuthLayout.jsx';
import DashboardLayout from './layouts/DashboardLayout.jsx';
import ProtectedRoute from './components/common/ProtectedRoute.jsx';

import Landing from './pages/Landing.jsx';
import NotFound from './pages/errors/NotFound.jsx';

import Login from './pages/auth/Login.jsx';
import Register from './pages/auth/Register.jsx';
import ForgotPassword from './pages/auth/ForgotPassword.jsx';
import ResetPassword from './pages/auth/ResetPassword.jsx';
import VerifyEmail from './pages/auth/VerifyEmail.jsx';

import StudentDashboard from './pages/student/Dashboard.jsx';
import StudentProfile from './pages/student/Profile.jsx';
import StudentResume from './pages/student/Resume.jsx';
import StudentDigitalTwin from './pages/student/DigitalTwin.jsx';
import StudentInterview from './pages/student/Interview.jsx';
import StudentInterviewSession from './pages/student/InterviewSession.jsx';
import StudentSkillGap from './pages/student/SkillGap.jsx';
import StudentRoadmap from './pages/student/Roadmap.jsx';
import StudentAnalytics from './pages/student/Analytics.jsx';
import StudentNotifications from './pages/student/Notifications.jsx';
import StudentSettings from './pages/student/Settings.jsx';

import AdminDashboard from './pages/admin/Dashboard.jsx';
import AdminStudents from './pages/admin/Students.jsx';
import AdminStudentDetail from './pages/admin/StudentDetail.jsx';
import AdminInterviews from './pages/admin/Interviews.jsx';
import AdminSkills from './pages/admin/Skills.jsx';
import AdminPrompts from './pages/admin/Prompts.jsx';
import AdminReports from './pages/admin/Reports.jsx';
import AdminAnalytics from './pages/admin/Analytics.jsx';
import AdminSettings from './pages/admin/Settings.jsx';

/**
 * Central route table, consumed by App.jsx via useRoutes(). Kept as
 * plain data (rather than JSX <Routes>) so it's easy to scan and
 * extend without hunting through nested markup.
 */
export const routes = [
  {
    element: <MainLayout />,
    children: [{ path: '/', element: <Landing /> }],
  },
  {
    element: <AuthLayout />,
    children: [
      { path: '/login', element: <Login /> },
      { path: '/register', element: <Register /> },
      { path: '/forgot-password', element: <ForgotPassword /> },
      { path: '/reset-password', element: <ResetPassword /> },
      { path: '/verify-email', element: <VerifyEmail /> },
    ],
  },
  {
    element: <ProtectedRoute role="STUDENT" />,
    children: [
      {
        element: <DashboardLayout />,
        children: [
          { path: '/student/dashboard', element: <StudentDashboard /> },
          { path: '/student/profile', element: <StudentProfile /> },
          { path: '/student/resume', element: <StudentResume /> },
          { path: '/student/digital-twin', element: <StudentDigitalTwin /> },
          { path: '/student/interview', element: <StudentInterview /> },
          { path: '/student/interview/:sessionId', element: <StudentInterviewSession /> },
          { path: '/student/skill-gap', element: <StudentSkillGap /> },
          { path: '/student/roadmap', element: <StudentRoadmap /> },
          { path: '/student/analytics', element: <StudentAnalytics /> },
          { path: '/student/notifications', element: <StudentNotifications /> },
          { path: '/student/settings', element: <StudentSettings /> },
        ],
      },
    ],
  },
  {
    element: <ProtectedRoute role="ADMIN" />,
    children: [
      {
        element: <DashboardLayout />,
        children: [
          { path: '/admin/dashboard', element: <AdminDashboard /> },
          { path: '/admin/students', element: <AdminStudents /> },
          { path: '/admin/students/:studentId', element: <AdminStudentDetail /> },
          { path: '/admin/interviews', element: <AdminInterviews /> },
          { path: '/admin/skills', element: <AdminSkills /> },
          { path: '/admin/prompts', element: <AdminPrompts /> },
          { path: '/admin/reports', element: <AdminReports /> },
          { path: '/admin/analytics', element: <AdminAnalytics /> },
          { path: '/admin/settings', element: <AdminSettings /> },
        ],
      },
    ],
  },
  { path: '/dashboard', element: <Navigate to="/student/dashboard" replace /> },
  { path: '*', element: <NotFound /> },
];

export default routes;
