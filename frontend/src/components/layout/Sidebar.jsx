import { NavLink } from 'react-router-dom';
import { motion } from 'framer-motion';
import clsx from 'clsx';
import {
  LayoutDashboard, User, FileText, Bot, MessagesSquare, TrendingUp,
  Map, BarChart3, Bell, Settings, Sparkles, Users, ClipboardList,
  Wrench, MessageSquareCode, FileBarChart2,
} from 'lucide-react';
import { useAuth } from '../../hooks/useAuth.js';

const STUDENT_NAV = [
  { to: '/student/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/student/profile', label: 'Profile', icon: User },
  { to: '/student/resume', label: 'Resume', icon: FileText },
  { to: '/student/digital-twin', label: 'Digital Twin', icon: Bot },
  { to: '/student/interview', label: 'Mock Interview', icon: MessagesSquare },
  { to: '/student/skill-gap', label: 'Skill Gap', icon: TrendingUp },
  { to: '/student/roadmap', label: 'Roadmap', icon: Map },
  { to: '/student/analytics', label: 'Analytics', icon: BarChart3 },
  { to: '/student/notifications', label: 'Notifications', icon: Bell },
  { to: '/student/settings', label: 'Settings', icon: Settings },
];

const ADMIN_NAV = [
  { to: '/admin/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/admin/students', label: 'Students', icon: Users },
  { to: '/admin/interviews', label: 'Interviews', icon: ClipboardList },
  { to: '/admin/skills', label: 'Skills', icon: Wrench },
  { to: '/admin/prompts', label: 'AI Prompts', icon: MessageSquareCode },
  { to: '/admin/reports', label: 'Reports', icon: FileBarChart2 },
  { to: '/admin/analytics', label: 'Analytics', icon: BarChart3 },
  { to: '/admin/settings', label: 'Settings', icon: Settings },
];

export function Sidebar({ open, onClose }) {
  const { isAdmin } = useAuth();
  const navItems = isAdmin ? ADMIN_NAV : STUDENT_NAV;

  return (
    <>
      {/* Mobile backdrop */}
      {open && (
        <div className="fixed inset-0 z-30 bg-navy-950/70 backdrop-blur-sm lg:hidden" onClick={onClose} />
      )}

      <motion.aside
        initial={false}
        animate={{ x: 0 }}
        className={clsx(
          'fixed inset-y-0 left-0 z-40 flex w-64 flex-col border-r border-white/10 bg-navy-900/95 backdrop-blur-glass transition-transform duration-300 lg:static lg:translate-x-0',
          open ? 'translate-x-0' : '-translate-x-full'
        )}
      >
        <div className="flex items-center gap-2 px-6 py-6">
          <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-twin-gradient">
            <Sparkles className="h-5 w-5 text-navy-950" />
          </div>
          <span className="font-display text-lg font-semibold text-ink-100">Digital Twin</span>
        </div>

        <nav className="flex-1 space-y-1 overflow-y-auto px-3 pb-6">
          {navItems.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              onClick={onClose}
              className={({ isActive }) =>
                clsx(
                  'flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors duration-150',
                  isActive
                    ? 'bg-twin-gradient-soft text-ink-100 border border-twin-violet/30'
                    : 'text-ink-500 hover:bg-white/[0.05] hover:text-ink-100'
                )
              }
            >
              <Icon className="h-4.5 w-4.5" />
              {label}
            </NavLink>
          ))}
        </nav>

        <div className="border-t border-white/10 px-6 py-4">
          <p className="text-xs text-ink-700">
            {isAdmin ? 'Admin Console' : 'Student Workspace'}
          </p>
        </div>
      </motion.aside>
    </>
  );
}

export default Sidebar;
