import { useState } from 'react';
import { useForm } from 'react-hook-form';
import clsx from 'clsx';
import { Plus, Trash2, Save, GraduationCap, Briefcase, Award, Trophy, Sparkles as SkillIcon } from 'lucide-react';
import studentService from '../../services/studentService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useNotification } from '../../hooks/useNotification.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Button } from '../../components/common/Button.jsx';
import { Badge } from '../../components/common/Badge.jsx';
import { Modal } from '../../components/common/Modal.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

const TABS = [
  { key: 'about', label: 'About' },
  { key: 'education', label: 'Education' },
  { key: 'skills', label: 'Skills' },
  { key: 'projects', label: 'Projects' },
  { key: 'certifications', label: 'Certifications' },
  { key: 'achievements', label: 'Achievements' },
];

export function Profile() {
  const [activeTab, setActiveTab] = useState('about');
  const { data: profile, loading, refetch } = useFetch(() => studentService.getProfile(), []);

  if (loading) return <PageLoader label="Loading your profile…" />;
  if (!profile) return null;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-semibold text-ink-100">Your profile</h1>
        <p className="mt-1 text-sm text-ink-500">This is what recruiters (and your digital twin) see.</p>
      </div>

      <div className="flex flex-wrap gap-2 border-b border-white/10 pb-3">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setActiveTab(tab.key)}
            className={clsx(
              'rounded-lg px-3.5 py-2 text-sm font-medium transition-colors',
              activeTab === tab.key
                ? 'bg-twin-gradient-soft text-ink-100 border border-twin-violet/30'
                : 'text-ink-500 hover:text-ink-100'
            )}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {activeTab === 'about' && <AboutTab profile={profile} onSaved={refetch} />}
      {activeTab === 'education' && <EducationTab profile={profile} onChanged={refetch} />}
      {activeTab === 'skills' && <SkillsTab profile={profile} onChanged={refetch} />}
      {activeTab === 'projects' && <ProjectsTab profile={profile} onChanged={refetch} />}
      {activeTab === 'certifications' && <CertificationsTab profile={profile} onChanged={refetch} />}
      {activeTab === 'achievements' && <AchievementsTab profile={profile} onChanged={refetch} />}
    </div>
  );
}

// ---------- About ----------

function AboutTab({ profile, onSaved }) {
  const { register, handleSubmit, formState: { isSubmitting } } = useForm({ defaultValues: profile });
  const notify = useNotification();

  const onSubmit = async (formData) => {
    try {
      await studentService.updateProfile(formData);
      notify.success('Profile updated.');
      onSaved();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not update your profile.');
    }
  };

  return (
    <GlassCard>
      <form onSubmit={handleSubmit(onSubmit)} className="grid grid-cols-1 gap-5 sm:grid-cols-2">
        <div>
          <label className="label-field">College name</label>
          <input className="input-field" {...register('collegeName')} />
        </div>
        <div>
          <label className="label-field">Degree</label>
          <input className="input-field" {...register('degree')} />
        </div>
        <div>
          <label className="label-field">Branch</label>
          <input className="input-field" {...register('branch')} />
        </div>
        <div>
          <label className="label-field">Graduation year</label>
          <input type="number" className="input-field" {...register('graduationYear')} />
        </div>
        <div>
          <label className="label-field">Target role</label>
          <input className="input-field" placeholder="e.g. Backend Developer" {...register('targetRole')} />
        </div>
        <div>
          <label className="label-field">City</label>
          <input className="input-field" {...register('city')} />
        </div>
        <div>
          <label className="label-field">LinkedIn URL</label>
          <input className="input-field" {...register('linkedinUrl')} />
        </div>
        <div>
          <label className="label-field">GitHub URL</label>
          <input className="input-field" {...register('githubUrl')} />
        </div>
        <div className="sm:col-span-2">
          <label className="label-field">Bio</label>
          <textarea rows={4} className="input-field" {...register('bio')} />
        </div>

        <div className="sm:col-span-2">
          <Button type="submit" loading={isSubmitting}>
            <Save className="h-4 w-4" /> Save changes
          </Button>
        </div>
      </form>
    </GlassCard>
  );
}

// ---------- Education ----------

function EducationTab({ profile, onChanged }) {
  const [modalOpen, setModalOpen] = useState(false);
  const notify = useNotification();
  const { register, handleSubmit, reset, formState: { isSubmitting } } = useForm();

  const openAdd = () => {
    reset({ institutionName: '', qualification: '', fieldOfStudy: '', startYear: '', endYear: '', grade: '', current: false });
    setModalOpen(true);
  };

  const onSubmit = async (formData) => {
    try {
      await studentService.addEducation(formData);
      notify.success('Education entry added.');
      setModalOpen(false);
      onChanged();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not add education entry.');
    }
  };

  const handleDelete = async (id) => {
    try {
      await studentService.deleteEducation(id);
      notify.success('Education entry removed.');
      onChanged();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not remove this entry.');
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex justify-end">
        <Button size="sm" onClick={openAdd}><Plus className="h-4 w-4" /> Add education</Button>
      </div>

      {!profile.education?.length ? (
        <GlassCard><EmptyState icon={GraduationCap} title="No education added yet" description="Add your degrees and qualifications." /></GlassCard>
      ) : (
        <div className="space-y-3">
          {profile.education.map((edu) => (
            <GlassCard key={edu.id} className="flex items-start justify-between p-5">
              <div>
                <p className="font-medium text-ink-100">{edu.institutionName}</p>
                <p className="text-sm text-ink-500">{edu.qualification}{edu.fieldOfStudy ? ` · ${edu.fieldOfStudy}` : ''}</p>
                <p className="mt-1 text-xs text-ink-700">{edu.startYear || '—'} – {edu.current ? 'Present' : (edu.endYear || '—')}</p>
              </div>
              <button onClick={() => handleDelete(edu.id)} className="text-ink-500 hover:text-danger" aria-label="Delete">
                <Trash2 className="h-4 w-4" />
              </button>
            </GlassCard>
          ))}
        </div>
      )}

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Add education">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="label-field">Institution name</label>
            <input className="input-field" {...register('institutionName', { required: true })} />
          </div>
          <div>
            <label className="label-field">Qualification</label>
            <input className="input-field" placeholder="B.E. / B.Tech / M.Sc." {...register('qualification', { required: true })} />
          </div>
          <div>
            <label className="label-field">Field of study</label>
            <input className="input-field" {...register('fieldOfStudy')} />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label-field">Start year</label>
              <input type="number" className="input-field" {...register('startYear')} />
            </div>
            <div>
              <label className="label-field">End year</label>
              <input type="number" className="input-field" {...register('endYear')} />
            </div>
          </div>
          <Button type="submit" loading={isSubmitting} className="w-full">Save</Button>
        </form>
      </Modal>
    </div>
  );
}

// ---------- Skills ----------

const PROFICIENCY_TONES = { BEGINNER: 'neutral', INTERMEDIATE: 'cyan', ADVANCED: 'violet', EXPERT: 'success' };

function SkillsTab({ profile, onChanged }) {
  const [modalOpen, setModalOpen] = useState(false);
  const notify = useNotification();
  const { register, handleSubmit, reset, formState: { isSubmitting } } = useForm();

  const openAdd = () => {
    reset({ skillId: '', proficiency: 'BEGINNER', yearsExperience: 0 });
    setModalOpen(true);
  };

  const onSubmit = async (formData) => {
    try {
      await studentService.addOrUpdateSkill({ ...formData, skillId: Number(formData.skillId) });
      notify.success('Skill saved.');
      setModalOpen(false);
      onChanged();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not save this skill.');
    }
  };

  const handleRemove = async (id) => {
    try {
      await studentService.removeSkill(id);
      notify.success('Skill removed.');
      onChanged();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not remove this skill.');
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex justify-end">
        <Button size="sm" onClick={openAdd}><Plus className="h-4 w-4" /> Add skill</Button>
      </div>

      {!profile.skills?.length ? (
        <GlassCard><EmptyState icon={SkillIcon} title="No skills tracked yet" description="Add skills to power your skill-gap analysis." /></GlassCard>
      ) : (
        <GlassCard className="flex flex-wrap gap-2">
          {profile.skills.map((s) => (
            <span key={s.id} className="group inline-flex items-center gap-2 rounded-full border border-white/10 bg-white/[0.03] py-1.5 pl-3.5 pr-2 text-sm text-ink-100">
              {s.skillName}
              <Badge tone={PROFICIENCY_TONES[s.proficiency]}>{s.proficiency}</Badge>
              <button onClick={() => handleRemove(s.id)} className="text-ink-700 hover:text-danger">
                <Trash2 className="h-3.5 w-3.5" />
              </button>
            </span>
          ))}
        </GlassCard>
      )}

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Add / update skill">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="label-field">Skill ID</label>
            <input type="number" className="input-field" placeholder="See Admin > Skills for IDs" {...register('skillId', { required: true })} />
          </div>
          <div>
            <label className="label-field">Proficiency</label>
            <select className="input-field" {...register('proficiency')}>
              <option value="BEGINNER">Beginner</option>
              <option value="INTERMEDIATE">Intermediate</option>
              <option value="ADVANCED">Advanced</option>
              <option value="EXPERT">Expert</option>
            </select>
          </div>
          <div>
            <label className="label-field">Years of experience</label>
            <input type="number" step="0.5" className="input-field" {...register('yearsExperience')} />
          </div>
          <Button type="submit" loading={isSubmitting} className="w-full">Save</Button>
        </form>
      </Modal>
    </div>
  );
}

// ---------- Projects ----------

function ProjectsTab({ profile, onChanged }) {
  const [modalOpen, setModalOpen] = useState(false);
  const notify = useNotification();
  const { register, handleSubmit, reset, formState: { isSubmitting } } = useForm();

  const openAdd = () => {
    reset({ title: '', description: '', techStack: '', projectUrl: '', repoUrl: '', ongoing: false });
    setModalOpen(true);
  };

  const onSubmit = async (formData) => {
    try {
      await studentService.addProject(formData);
      notify.success('Project added.');
      setModalOpen(false);
      onChanged();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not add project.');
    }
  };

  const handleDelete = async (id) => {
    try {
      await studentService.deleteProject(id);
      notify.success('Project removed.');
      onChanged();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not remove this project.');
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex justify-end">
        <Button size="sm" onClick={openAdd}><Plus className="h-4 w-4" /> Add project</Button>
      </div>

      {!profile.projects?.length ? (
        <GlassCard><EmptyState icon={Briefcase} title="No projects added yet" description="Showcase what you've built." /></GlassCard>
      ) : (
        <div className="space-y-3">
          {profile.projects.map((p) => (
            <GlassCard key={p.id} className="flex items-start justify-between p-5">
              <div>
                <p className="font-medium text-ink-100">{p.title}</p>
                {p.techStack && <p className="text-sm text-ink-500">{p.techStack}</p>}
                {p.description && <p className="mt-1 text-sm text-ink-500 line-clamp-2">{p.description}</p>}
              </div>
              <button onClick={() => handleDelete(p.id)} className="text-ink-500 hover:text-danger" aria-label="Delete">
                <Trash2 className="h-4 w-4" />
              </button>
            </GlassCard>
          ))}
        </div>
      )}

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Add project">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="label-field">Title</label>
            <input className="input-field" {...register('title', { required: true })} />
          </div>
          <div>
            <label className="label-field">Description</label>
            <textarea rows={3} className="input-field" {...register('description')} />
          </div>
          <div>
            <label className="label-field">Tech stack</label>
            <input className="input-field" placeholder="React, Spring Boot, MySQL" {...register('techStack')} />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label-field">Project URL</label>
              <input className="input-field" {...register('projectUrl')} />
            </div>
            <div>
              <label className="label-field">Repo URL</label>
              <input className="input-field" {...register('repoUrl')} />
            </div>
          </div>
          <Button type="submit" loading={isSubmitting} className="w-full">Save</Button>
        </form>
      </Modal>
    </div>
  );
}

// ---------- Certifications ----------

function CertificationsTab({ profile, onChanged }) {
  const [modalOpen, setModalOpen] = useState(false);
  const notify = useNotification();
  const { register, handleSubmit, reset, formState: { isSubmitting } } = useForm();

  const openAdd = () => {
    reset({ title: '', issuingOrganization: '', issueDate: '', expiryDate: '', credentialId: '', credentialUrl: '' });
    setModalOpen(true);
  };

  const onSubmit = async (formData) => {
    try {
      await studentService.addCertification(formData);
      notify.success('Certification added.');
      setModalOpen(false);
      onChanged();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not add certification.');
    }
  };

  const handleDelete = async (id) => {
    try {
      await studentService.deleteCertification(id);
      notify.success('Certification removed.');
      onChanged();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not remove this certification.');
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex justify-end">
        <Button size="sm" onClick={openAdd}><Plus className="h-4 w-4" /> Add certification</Button>
      </div>

      {!profile.certifications?.length ? (
        <GlassCard><EmptyState icon={Award} title="No certifications added yet" /></GlassCard>
      ) : (
        <div className="space-y-3">
          {profile.certifications.map((c) => (
            <GlassCard key={c.id} className="flex items-start justify-between p-5">
              <div>
                <p className="font-medium text-ink-100">{c.title}</p>
                {c.issuingOrganization && <p className="text-sm text-ink-500">{c.issuingOrganization}</p>}
              </div>
              <button onClick={() => handleDelete(c.id)} className="text-ink-500 hover:text-danger" aria-label="Delete">
                <Trash2 className="h-4 w-4" />
              </button>
            </GlassCard>
          ))}
        </div>
      )}

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Add certification">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="label-field">Title</label>
            <input className="input-field" {...register('title', { required: true })} />
          </div>
          <div>
            <label className="label-field">Issuing organization</label>
            <input className="input-field" {...register('issuingOrganization')} />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label-field">Issue date</label>
              <input type="date" className="input-field" {...register('issueDate')} />
            </div>
            <div>
              <label className="label-field">Expiry date</label>
              <input type="date" className="input-field" {...register('expiryDate')} />
            </div>
          </div>
          <Button type="submit" loading={isSubmitting} className="w-full">Save</Button>
        </form>
      </Modal>
    </div>
  );
}

// ---------- Achievements ----------

function AchievementsTab({ profile, onChanged }) {
  const [modalOpen, setModalOpen] = useState(false);
  const notify = useNotification();
  const { register, handleSubmit, reset, formState: { isSubmitting } } = useForm();

  const openAdd = () => {
    reset({ title: '', description: '', achievementDate: '' });
    setModalOpen(true);
  };

  const onSubmit = async (formData) => {
    try {
      await studentService.addAchievement(formData);
      notify.success('Achievement added.');
      setModalOpen(false);
      onChanged();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not add achievement.');
    }
  };

  const handleDelete = async (id) => {
    try {
      await studentService.deleteAchievement(id);
      notify.success('Achievement removed.');
      onChanged();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not remove this achievement.');
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex justify-end">
        <Button size="sm" onClick={openAdd}><Plus className="h-4 w-4" /> Add achievement</Button>
      </div>

      {!profile.achievements?.length ? (
        <GlassCard><EmptyState icon={Trophy} title="No achievements added yet" /></GlassCard>
      ) : (
        <div className="space-y-3">
          {profile.achievements.map((a) => (
            <GlassCard key={a.id} className="flex items-start justify-between p-5">
              <div>
                <p className="font-medium text-ink-100">{a.title}</p>
                {a.description && <p className="text-sm text-ink-500">{a.description}</p>}
              </div>
              <button onClick={() => handleDelete(a.id)} className="text-ink-500 hover:text-danger" aria-label="Delete">
                <Trash2 className="h-4 w-4" />
              </button>
            </GlassCard>
          ))}
        </div>
      )}

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Add achievement">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="label-field">Title</label>
            <input className="input-field" {...register('title', { required: true })} />
          </div>
          <div>
            <label className="label-field">Description</label>
            <textarea rows={3} className="input-field" {...register('description')} />
          </div>
          <div>
            <label className="label-field">Date</label>
            <input type="date" className="input-field" {...register('achievementDate')} />
          </div>
          <Button type="submit" loading={isSubmitting} className="w-full">Save</Button>
        </form>
      </Modal>
    </div>
  );
}

export default Profile;
