import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { UserPlus } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth.js';
import { useNotification } from '../../hooks/useNotification.js';
import { Button } from '../../components/common/Button.jsx';

const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#]).+$/;

export function Register() {
  const { register, handleSubmit, watch, formState: { errors, isSubmitting } } = useForm();
  const { register: registerAccount } = useAuth();
  const notify = useNotification();
  const navigate = useNavigate();
  const password = watch('password');

  const onSubmit = async (formData) => {
    try {
      await registerAccount(formData);
      notify.success('Account created. Check your email to verify your address.');
      navigate('/login');
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not create your account. Please try again.');
    }
  };

  return (
    <div>
      <h1 className="font-display text-2xl font-semibold text-ink-100">Create your account</h1>
      <p className="mt-1 text-sm text-ink-500">Start building your profile and digital twin.</p>

      <form onSubmit={handleSubmit(onSubmit)} className="mt-8 space-y-5">
        <div>
          <label className="label-field" htmlFor="fullName">Full name</label>
          <input
            id="fullName"
            className="input-field"
            placeholder="Ananya Rao"
            {...register('fullName', { required: 'Full name is required', minLength: { value: 2, message: 'Too short' } })}
          />
          {errors.fullName && <p className="mt-1 text-xs text-danger">{errors.fullName.message}</p>}
        </div>

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
          <label className="label-field" htmlFor="phoneNumber">Phone number (optional)</label>
          <input id="phoneNumber" className="input-field" placeholder="+91 98765 43210" {...register('phoneNumber')} />
        </div>

        <div>
          <label className="label-field" htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            className="input-field"
            placeholder="At least 8 characters"
            {...register('password', {
              required: 'Password is required',
              minLength: { value: 8, message: 'Must be at least 8 characters' },
              pattern: {
                value: PASSWORD_PATTERN,
                message: 'Include upper, lower, number, and special character',
              },
            })}
          />
          {errors.password && <p className="mt-1 text-xs text-danger">{errors.password.message}</p>}
        </div>

        <div>
          <label className="label-field" htmlFor="confirmPassword">Confirm password</label>
          <input
            id="confirmPassword"
            type="password"
            className="input-field"
            placeholder="Re-enter your password"
            {...register('confirmPassword', {
              required: 'Please confirm your password',
              validate: (value) => value === password || 'Passwords do not match',
            })}
          />
          {errors.confirmPassword && <p className="mt-1 text-xs text-danger">{errors.confirmPassword.message}</p>}
        </div>

        <Button type="submit" loading={isSubmitting} className="w-full">
          <UserPlus className="h-4 w-4" /> Create account
        </Button>
      </form>

      <p className="mt-6 text-center text-sm text-ink-500">
        Already have an account?{' '}
        <Link to="/login" className="font-medium text-twin-cyan hover:underline">Log in</Link>
      </p>
    </div>
  );
}

export default Register;
