import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { projectsAPI, feedbackAPI, insightsAPI } from '../api/client';
import './Dashboard.css';

const Dashboard = () => {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showNewProjectForm, setShowNewProjectForm] = useState(false);
  const [newProject, setNewProject] = useState({ name: '', description: '' });
  const [stats, setStats] = useState({ totalFeedback: 0, projectCount: 0 });
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  useEffect(() => {
    loadProjects();
  }, []);

  const loadProjects = async () => {
    try {
      setLoading(true);
      const response = await projectsAPI.getAll();
      setProjects(response.data);
      
      // Calculate stats
      let totalFeedback = 0;
      for (const project of response.data) {
        try {
          const statsResponse = await feedbackAPI.getStats(project._id);
          totalFeedback += statsResponse.data.totalCount;
        } catch (err) {
          // Ignore errors for individual stats
        }
      }
      
      setStats({
        totalFeedback,
        projectCount: response.data.length,
      });
    } catch (err) {
      setError('Failed to load projects');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateProject = async (e) => {
    e.preventDefault();
    if (!newProject.name.trim()) {
      setError('Project name is required');
      return;
    }

    try {
      await projectsAPI.create(newProject);
      setNewProject({ name: '', description: '' });
      setShowNewProjectForm(false);
      loadProjects();
    } catch (err) {
      setError('Failed to create project');
    }
  };

  const handleDeleteProject = async (projectId) => {
    if (window.confirm('Are you sure you want to delete this project?')) {
      try {
        await projectsAPI.delete(projectId);
        loadProjects();
      } catch (err) {
        setError('Failed to delete project');
      }
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <div className="header-content">
          <h1>Clueso</h1>
          <div className="user-menu">
            <span>{user?.name}</span>
            <button onClick={handleLogout} className="btn btn-secondary">
              Logout
            </button>
          </div>
        </div>
      </header>

      <div className="dashboard-container">
        <div className="dashboard-grid">
          {/* Stats Section */}
          <section className="stats-section">
            <h2>Overview</h2>
            <div className="stats-grid">
              <div className="stat-card">
                <div className="stat-number">{stats.projectCount}</div>
                <div className="stat-label">Projects</div>
              </div>
              <div className="stat-card">
                <div className="stat-number">{stats.totalFeedback}</div>
                <div className="stat-label">Total Feedback</div>
              </div>
            </div>
          </section>

          {/* Projects Section */}
          <section className="projects-section">
            <div className="section-header">
              <h2>Projects</h2>
              <button
                onClick={() => setShowNewProjectForm(!showNewProjectForm)}
                className="btn btn-primary"
              >
                {showNewProjectForm ? 'Cancel' : '+ New Project'}
              </button>
            </div>

            {error && <div className="error-message">{error}</div>}

            {showNewProjectForm && (
              <form onSubmit={handleCreateProject} className="new-project-form">
                <div className="form-group">
                  <label htmlFor="projectName">Project Name</label>
                  <input
                    id="projectName"
                    type="text"
                    value={newProject.name}
                    onChange={(e) =>
                      setNewProject({ ...newProject, name: e.target.value })
                    }
                    placeholder="e.g., Mobile App Feedback"
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="projectDesc">Description (Optional)</label>
                  <textarea
                    id="projectDesc"
                    value={newProject.description}
                    onChange={(e) =>
                      setNewProject({ ...newProject, description: e.target.value })
                    }
                    placeholder="Brief description of your project"
                    rows="3"
                  />
                </div>

                <button type="submit" className="btn btn-primary">
                  Create Project
                </button>
              </form>
            )}

            <div className="projects-list">
              {projects.length === 0 ? (
                <p className="empty-state">No projects yet. Create one to get started!</p>
              ) : (
                projects.map((project) => (
                  <div key={project._id} className="project-card">
                    <h3>{project.name}</h3>
                    <p>{project.description || 'No description'}</p>
                    <div className="project-actions">
                      <button
                        onClick={() => navigate(`/project/${project._id}`)}
                        className="btn btn-primary"
                      >
                        View
                      </button>
                      <button
                        onClick={() => handleDeleteProject(project._id)}
                        className="btn btn-danger"
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                ))
              )}
            </div>
          </section>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
