import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Bot, FileText, MessagesSquare, Map, ArrowRight } from 'lucide-react';

const FEATURES = [
  {
    icon: FileText,
    title: 'AI resume analysis',
    description: 'Upload your resume and get an ATS score, extracted skills, and concrete suggestions in seconds.',
  },
  {
    icon: Bot,
    title: 'Your digital twin',
    description: 'A living model of your behavior, learning pattern, and predicted career fit — built from your real data.',
  },
  {
    icon: MessagesSquare,
    title: 'AI mock interviews',
    description: 'Practice role-specific questions and get feedback on confidence, communication, and technical depth.',
  },
  {
    icon: Map,
    title: 'Personalized roadmap',
    description: 'See exactly which skills, projects, and certifications close the gap to your target role.',
  },
];

export function Landing() {
  return (
    <div>
      <section className="relative overflow-hidden px-6 pb-20 pt-20 lg:pt-28">
        <div className="absolute inset-0 bg-navy-radial" />
        <div className="relative mx-auto max-w-4xl text-center">
          <motion.h1
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
            className="font-display text-4xl font-semibold leading-tight text-ink-100 sm:text-5xl"
          >
            Meet the version of you that's
            <span className="bg-twin-gradient bg-clip-text text-transparent"> ready for the interview.</span>
          </motion.h1>
          <motion.p
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.1 }}
            className="mx-auto mt-5 max-w-2xl text-ink-300"
          >
            Upload your resume, build a digital twin of your skills and behavior, and practice with an AI
            interviewer that tells you exactly what to fix — before a real recruiter does.
          </motion.p>
          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.2 }}
            className="mt-8 flex items-center justify-center gap-3"
          >
            <Link to="/register" className="btn-primary">
              Build your digital twin <ArrowRight className="h-4 w-4" />
            </Link>
            <Link to="/login" className="btn-secondary">Log in</Link>
          </motion.div>
        </div>
      </section>

      <section className="px-6 pb-24">
        <div className="mx-auto grid max-w-6xl gap-5 sm:grid-cols-2 lg:grid-cols-4">
          {FEATURES.map(({ icon: Icon, title, description }, i) => (
            <motion.div
              key={title}
              initial={{ opacity: 0, y: 16 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.4, delay: i * 0.08 }}
              className="glass-card-hover p-6"
            >
              <div className="mb-4 flex h-10 w-10 items-center justify-center rounded-lg bg-twin-gradient-soft">
                <Icon className="h-5 w-5 text-twin-cyan" />
              </div>
              <h3 className="font-display text-base font-semibold text-ink-100">{title}</h3>
              <p className="mt-2 text-sm text-ink-500">{description}</p>
            </motion.div>
          ))}
        </div>
      </section>
    </div>
  );
}

export default Landing;
