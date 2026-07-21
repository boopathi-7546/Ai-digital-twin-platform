import { useRoutes } from 'react-router-dom';
import { routes } from './router.jsx';

/**
 * Root component. All route definitions live in router.jsx so they
 * can be unit-tested / reasoned about independently of app bootstrap
 * concerns (see main.jsx for providers).
 */
function App() {
  const element = useRoutes(routes);
  return element;
}

export default App;
