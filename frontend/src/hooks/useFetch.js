import { useCallback, useEffect, useState } from 'react';

/**
 * Generic fetch-on-mount hook for GET-style calls to our services.
 * Returns { data, loading, error, refetch } so pages can show loading
 * skeletons and empty/error states consistently.
 *
 * @param {Function} fetchFn - async function returning the data
 * @param {Array} deps - dependency array, re-fetches when these change
 */
export function useFetch(fetchFn, deps = []) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await fetchFn();
      setData(result);
    } catch (err) {
      setError(err?.response?.data?.message || err.message || 'Something went wrong.');
    } finally {
      setLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  useEffect(() => {
    load();
  }, [load]);

  return { data, loading, error, refetch: load, setData };
}
