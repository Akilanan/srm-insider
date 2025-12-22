import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { projectsAPI, feedbackAPI, insightsAPI } from '../api/client';
import './ProjectDetail.css';

const ProjectDetail = () => {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const [project, setProject] = useState(null);
  const [feedback, setFeedback] = useState([]);
  const [insights, setInsights] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showFeedbackForm, setShowFeedbackForm] = useState(false);
  const [newFeedback, setNewFeedback] = useState({
    message: '',
    rating: 5,
    email: '',
  });
  const [generatingInsights, setGeneratingInsights] = useState(false);

  useEffect(() => {
    loadProjectData();
  }, [projectId]);

  const loadProjectData = async () => {
    try {
      setLoading(true);
      const projectResponse = await projectsAPI.getById(projectId);
      setProject(projectResponse.data);

      const feedbackResponse = await feedbackAPI.getAll(projectId);
      setFeedback(feedbackResponse.data);

      try {
        const insightsResponse = await insightsAPI.get(projectId);
        setInsights(insightsResponse.data);
      } catch (err) {
        // No insights yet
      }
    } catch (err) {
      setError('Failed to load project');
    } finally {
      setLoading(false);
    }
  };

  const handleAddFeedback = async (e) => {
    e.preventDefault();
    if (!newFeedback.message.trim()) {
      setError('Feedback message is required');
      return;
    }

    try {
      await feedbackAPI.add(projectId, newFeedback);
      setNewFeedback({ message: '', rating: 5, email: '' });
      setShowFeedbackForm(false);
      loadProjectData();
    } catch (err) {
      setError('Failed to add feedback');
    }
  };

  const handleGenerateInsights = async () => {
    if (feedback.length === 0) {
      setError('No feedback available to generate insights');
      return;
    }

    try {
      setGeneratingInsights(true);
      const response = await insightsAPI.generate(projectId);
      setInsights(response.data);
    } catch (err) {
      setError('Failed to generate insights');
    } finally {
      setGeneratingInsights(false);
    }
  };

  const handleDeleteFeedback = async (feedbackId) => {
    try {
      await feedbackAPI.delete(feedbackId);
      loadProjectData();
    } catch (err) {
      setError('Failed to delete feedback');
    }
  };

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  if (!project) {
    return <div className="error-message">Project not found</div>;
  }

  return (
    <div className="project-detail">
      <header className="detail-header">
        <div className="header-content">
          <button onClick={() => navigate('/dashboard')} className="btn btn-back">
            ← Back
          </button>
          <h1>{project.name}</h1>
        </div>
      </header>

      <div className="detail-container">
        {error && <div className="error-message">{error}</div>}

        {/* Feedback Section */}
        <section className="feedback-section">
          <div className="section-header">
            <h2>Feedback ({feedback.length})</h2>
            <button
              onClick={() => setShowFeedbackForm(!showFeedbackForm)}
              className="btn btn-primary"
            >
              {showFeedbackForm ? 'Cancel' : '+ Add Feedback'}
            </button>
          </div>

          {showFeedbackForm && (
            <form onSubmit={handleAddFeedback} className="feedback-form">
              <div className="form-group">
                <label htmlFor="feedbackEmail">Email (Optional)</label>
                <input
                  id="feedbackEmail"
                  type="email"
                  value={newFeedback.email}
                  onChange={(e) =>
                    setNewFeedback({ ...newFeedback, email: e.target.value })
                  }
                  placeholder="user@example.com"
                />
              </div>

              <div className="form-group">
                <label htmlFor="feedbackMessage">Feedback Message</label>
                <textarea
                  id="feedbackMessage"
                  value={newFeedback.message}
                  onChange={(e) =>
                    setNewFeedback({ ...newFeedback, message: e.target.value })
                  }
                  placeholder="Share your feedback..."
                  rows="4"
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="feedbackRating">Rating</label>
                <select
                  id="feedbackRating"
                  value={newFeedback.rating}
                  onChange={(e) =>
                    setNewFeedback({
                      ...newFeedback,
                      rating: parseInt(e.target.value),
                    })
                  }
                >
                  <option value="5">5 - Excellent</option>
                  <option value="4">4 - Good</option>
                  <option value="3">3 - Average</option>
                  <option value="2">2 - Poor</option>
                  <option value="1">1 - Very Poor</option>
                </select>
              </div>

              <button type="submit" className="btn btn-primary">
                Submit Feedback
              </button>
            </form>
          )}

          <div className="feedback-list">
            {feedback.length === 0 ? (
              <p className="empty-state">No feedback yet. Start collecting!</p>
            ) : (
              feedback.map((item) => (
                <div key={item._id} className="feedback-card">
                  <div className="feedback-header">
                    <div>
                      {item.email && <strong>{item.email}</strong>}
                      {item.rating && (
                        <div className="rating">
                          {'⭐'.repeat(item.rating)} ({item.rating}/5)
                        </div>
                      )}
                    </div>
                    <button
                      onClick={() => handleDeleteFeedback(item._id)}
                      className="btn btn-small btn-danger"
                    >
                      Delete
                    </button>
                  </div>
                  <p>{item.message}</p>
                  <small>
                    {new Date(item.createdAt).toLocaleDateString()} at{' '}
                    {new Date(item.createdAt).toLocaleTimeString()}
                  </small>
                </div>
              ))
            )}
          </div>
        </section>

        {/* Insights Section */}
        <section className="insights-section">
          <div className="section-header">
            <h2>AI Insights</h2>
            <button
              onClick={handleGenerateInsights}
              className="btn btn-primary"
              disabled={generatingInsights || feedback.length === 0}
            >
              {generatingInsights ? 'Generating...' : 'Generate Insights'}
            </button>
          </div>

          {insights ? (
            <div className="insights-card">
              <div className="insight-item">
                <h3>Summary</h3>
                <p>{insights.summaryText}</p>
              </div>

              {insights.sentiment && (
                <div className="insight-item">
                  <h3>Overall Sentiment</h3>
                  <div className={`sentiment-badge sentiment-${insights.sentiment}`}>
                    {insights.sentiment.toUpperCase()}
                  </div>
                </div>
              )}

              {insights.themes && insights.themes.length > 0 && (
                <div className="insight-item">
                  <h3>Key Themes</h3>
                  <div className="tags">
                    {insights.themes.map((theme, i) => (
                      <span key={i} className="tag">
                        {theme}
                      </span>
                    ))}
                  </div>
                </div>
              )}

              {insights.suggestions && insights.suggestions.length > 0 && (
                <div className="insight-item">
                  <h3>Actionable Suggestions</h3>
                  <ul>
                    {insights.suggestions.map((suggestion, i) => (
                      <li key={i}>{suggestion}</li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          ) : (
            <p className="empty-state">
              No insights yet. Add some feedback and generate insights to get started!
            </p>
          )}
        </section>
      </div>
    </div>
  );
};

export default ProjectDetail;
