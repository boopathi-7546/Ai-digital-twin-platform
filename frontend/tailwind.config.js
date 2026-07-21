/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        // Deep space-navy base — deliberately not the cream/terracotta
        // or near-black/acid-green defaults. The "twin" concept lives in
        // the violet/cyan accent pair used throughout (see signature.md).
        navy: {
          950: '#070B14',
          900: '#0B1120',
          800: '#111827',
          700: '#1A2236',
        },
        twin: {
          violet: '#7C5CFF',
          violetDark: '#5B3DE0',
          cyan: '#22D3EE',
          cyanDark: '#0EA5C4',
        },
        ink: {
          100: '#E7E9F5',
          300: '#B7BEDA',
          500: '#8890B5',
          700: '#5C6488',
        },
        success: '#34D399',
        warning: '#FBBF24',
        danger: '#F87171',
      },
      fontFamily: {
        display: ['"Space Grotesk"', 'sans-serif'],
        body: ['"Inter"', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'monospace'],
      },
      backgroundImage: {
        'twin-gradient': 'linear-gradient(135deg, #7C5CFF 0%, #22D3EE 100%)',
        'twin-gradient-soft': 'linear-gradient(135deg, rgba(124,92,255,0.18) 0%, rgba(34,211,238,0.12) 100%)',
        'navy-radial': 'radial-gradient(circle at 20% 20%, rgba(124,92,255,0.15), transparent 40%), radial-gradient(circle at 80% 60%, rgba(34,211,238,0.12), transparent 45%)',
      },
      boxShadow: {
        glass: '0 8px 32px rgba(0,0,0,0.35)',
        glow: '0 0 40px rgba(124,92,255,0.35)',
      },
      backdropBlur: {
        glass: '16px',
      },
      borderRadius: {
        xl2: '1.25rem',
      },
    },
  },
  plugins: [],
};
