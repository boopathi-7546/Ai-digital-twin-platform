import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { CheckCircle2, XCircle } from 'lucide-react';
import authService from '../../services/authService.js';
import { Loader } from '../../components/common/Loader.jsx';

export function VerifyEmail() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const [status, setStatus] = useState('verifying'); // verifying | success | error
  const [message, setMessage] = useState('');

  useEffect(() => {
    if (!token) {
      setStatus('error');
      setMessage('No verification token found in this link.');
      return;
    }

    authService
      .verifyEmail(token)
      .then((res) => {
        setStatus('success');
        setMessage(res.message || 'Email verified successfully.');
      })
      .catch((err) => {
        setStatus('error');
        setMessage(err?.response?.data?.message || 'This verification link is invalid or has expired.');
      });
  }, [token]);

  return (
    <div className="text-center">
      {status === 'verifying' && (
        <>
          <Loader size={32} className="mx-auto" />
          <p className="mt-4 text-sm text-ink-500">Verifying your email…</p>
        </>
      )}

      {status === 'success' && (
        <>
          <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-success/15">
            <CheckCircle2 className="h-6 w-6 text-success" />
          </div>
          <h1 className="font-display text-xl font-semibold text-ink-100">Email verified</h1>
          <p className="mt-2 text-sm text-ink-500">{message}</p>
          <Link to="/login" className="btn-primary mt-6 inline-flex">Log in</Link>
        </>
      )}

      {status === 'error' && (
        <>
          <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-danger/15">
            <XCircle className="h-6 w-6 text-danger" />
          </div>
          <h1 className="font-display text-xl font-semibold text-ink-100">Verification failed</h1>
          <p className="mt-2 text-sm text-ink-500">{message}</p>
          <Link to="/register" className="mt-6 inline-block text-sm text-twin-cyan hover:underline">
            Back to registration
          </Link>
        </>
      )}
    </div>
  );
}

export default VerifyEmail;
