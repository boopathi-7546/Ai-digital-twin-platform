import { Link } from 'react-router-dom';
import { Compass } from 'lucide-react';

export function NotFound() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-navy-950 px-6 text-center">
      <div className="mb-6 flex h-16 w-16 items-center justify-center rounded-full bg-twin-gradient-soft">
        <Compass className="h-7 w-7 text-twin-cyan" />
      </div>
      <h1 className="font-display text-3xl font-semibold text-ink-100">Page not found</h1>
      <p className="mt-2 max-w-sm text-sm text-ink-500">
        The page you're looking for doesn't exist, or may have moved.
      </p>
      <Link to="/" className="btn-primary mt-8">Back to home</Link>
    </div>
  );
}

export default NotFound;
