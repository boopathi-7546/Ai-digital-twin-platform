import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import { Mail, ArrowLeft } from 'lucide-react';
import authService from '../../services/authService.js';
import { useNotification } from '../../hooks/useNotification.js';
import { Button } from '../../components/common/Button.jsx';

export function ForgotPassword() {
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();
  const [sent, setSent] = useState(false);
  const notify = useNotification();

  const onSubmit = async ({ email }) => {
    try {
      await authService.forgotPassword(email);
      setSent(true);
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Something went wrong. Please try again.');
    }
  };

  if (sent) {
    return (
      <div className="text-center">
        <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-twin-gradient-soft">
          <Mail className="h-6 w-6 text-twin-cyan" />
        </div>
        <h1 className="font-display text-xl font-semibold text-ink-100">Check your email</h1>
        <p className="mt-2 text-sm text-ink-500">
          If an account with that email exists, we've sent a link to reset your password. The link expires in 30 minutes.
        </p>
        <Link to="/login" className="mt-6 inline-flex items-center gap-1.5 text-sm text-twin-cyan hover:underline">
          <ArrowLeft className="h-4 w-4" /> Back to login
        </Link>
      </div>
    );
  }

  return (
    <div>
      <h1 className="font-display text-2xl font-semibold text-ink-100">Reset your password</h1>
      <p className="mt-1 text-sm text-ink-500">Enter the email associated with your account.</p>

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

        <Button type="submit" loading={isSubmitting} className="w-full">
          Send reset link
        </Button>
      </form>

      <Link to="/login" className="mt-6 inline-flex items-center gap-1.5 text-sm text-ink-500 hover:text-ink-100">
        <ArrowLeft className="h-4 w-4" /> Back to login
      </Link>
    </div>
  );
}

export default ForgotPassword;
