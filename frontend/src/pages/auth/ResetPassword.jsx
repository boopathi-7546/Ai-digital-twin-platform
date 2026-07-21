import { useForm } from 'react-hook-form';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { KeyRound } from 'lucide-react';
import authService from '../../services/authService.js';
import { useNotification } from '../../hooks/useNotification.js';
import { Button } from '../../components/common/Button.jsx';

const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#]).+$/;

export function ResetPassword() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const { register, handleSubmit, watch, formState: { errors, isSubmitting } } = useForm();
  const notify = useNotification();
  const navigate = useNavigate();
  const newPassword = watch('newPassword');

  const onSubmit = async ({ newPassword: password }) => {
    if (!token) {
      notify.error('This reset link is missing its token. Please request a new one.');
      return;
    }
    try {
      await authService.resetPassword(token, password);
      notify.success('Password reset. You can now log in with your new password.');
      navigate('/login');
    } catch (err) {
      notify.error(err?.response?.data?.message || 'This reset link is invalid or has expired.');
    }
  };

  return (
    <div>
      <h1 className="font-display text-2xl font-semibold text-ink-100">Set a new password</h1>
      <p className="mt-1 text-sm text-ink-500">Choose something you haven't used before.</p>

      {!token && (
        <p className="mt-4 rounded-lg border border-danger/30 bg-danger/10 px-3 py-2 text-sm text-danger">
          No reset token found in this link. Please request a new password reset email.
        </p>
      )}

      <form onSubmit={handleSubmit(onSubmit)} className="mt-8 space-y-5">
        <div>
          <label className="label-field" htmlFor="newPassword">New password</label>
          <input
            id="newPassword"
            type="password"
            className="input-field"
            placeholder="At least 8 characters"
            {...register('newPassword', {
              required: 'New password is required',
              minLength: { value: 8, message: 'Must be at least 8 characters' },
              pattern: {
                value: PASSWORD_PATTERN,
                message: 'Include upper, lower, number, and special character',
              },
            })}
          />
          {errors.newPassword && <p className="mt-1 text-xs text-danger">{errors.newPassword.message}</p>}
        </div>

        <div>
          <label className="label-field" htmlFor="confirmPassword">Confirm new password</label>
          <input
            id="confirmPassword"
            type="password"
            className="input-field"
            placeholder="Re-enter your new password"
            {...register('confirmPassword', {
              required: 'Please confirm your new password',
              validate: (value) => value === newPassword || 'Passwords do not match',
            })}
          />
          {errors.confirmPassword && <p className="mt-1 text-xs text-danger">{errors.confirmPassword.message}</p>}
        </div>

        <Button type="submit" loading={isSubmitting} disabled={!token} className="w-full">
          <KeyRound className="h-4 w-4" /> Reset password
        </Button>
      </form>

      <p className="mt-6 text-center text-sm text-ink-500">
        Remembered it after all?{' '}
        <Link to="/login" className="font-medium text-twin-cyan hover:underline">Log in</Link>
      </p>
    </div>
  );
}

export default ResetPassword;
