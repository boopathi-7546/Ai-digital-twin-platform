import { useForm } from 'react-hook-form';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { LogIn } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth.js';
import { useNotification } from '../../hooks/useNotification.js';
import { Button } from '../../components/common/Button.jsx';

export function Login() {
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();
  const { login, isAdmin } = useAuth();
  const notify = useNotification();
  const navigate = useNavigate();
  const location = useLocation();

  const onSubmit = async (formData) => {
    try {
      const response = await login(formData);
      notify.success(`Welcome back, ${response.fullName.split(' ')[0]}.`);
      const from = location.state?.from?.pathname;
      const isAdminUser = response.roles?.includes('ROLE_ADMIN');
      navigate(from || (isAdminUser ? '/admin/dashboard' : '/student/dashboard'), { replace: true });
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Invalid email or password.');
    }
  };

  return (
    <div>
      <h1 className="font-display text-2xl font-semibold text-ink-100">Welcome back</h1>
      <p className="mt-1 text-sm text-ink-500">Log in to continue building your digital twin.</p>

      <form onSubmit={handleSubmit(onSubmit)} className="mt-8 space-y-5">
        <div>
          <label className="label-field" htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            className="input-field"
            placeholder="you@example.com"
            {...register('email', { required: 'Email is required' })}
          />
          {errors.email && <p className="mt-1 text-xs text-danger">{errors.email.message}</p>}
        </div>

        <div>
          <div className="flex items-center justify-between">
            <label className="label-field" htmlFor="password">Password</label>
            <Link to="/forgot-password" className="mb-1.5 text-xs text-twin-cyan hover:underline">
              Forgot password?
            </Link>
          </div>
          <input
            id="password"
            type="password"
            className="input-field"
            placeholder="••••••••"
            {...register('password', { required: 'Password is required' })}
          />
          {errors.password && <p className="mt-1 text-xs text-danger">{errors.password.message}</p>}
        </div>

        <Button type="submit" loading={isSubmitting} className="w-full">
          <LogIn className="h-4 w-4" /> Log in
        </Button>
      </form>

      <p className="mt-6 text-center text-sm text-ink-500">
        Don't have an account?{' '}
        <Link to="/register" className="font-medium text-twin-cyan hover:underline">Create one</Link>
      </p>
    </div>
  );
}

export default Login;
