const FeedbackService = require('../services/FeedbackService');

const FeedbackController = {
  async addFeedback(req, res) {
    try {
      const { projectId } = req.params;
      const { message, rating, email } = req.body;

      if (!message) {
        return res.status(400).json({ message: 'Feedback message is required' });
      }

      const feedback = await FeedbackService.addFeedback(
        projectId,
        message,
        rating,
        email
      );
      res.status(201).json(feedback);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  },

  async getProjectFeedback(req, res) {
    try {
      const { projectId } = req.params;
      const feedback = await FeedbackService.getProjectFeedback(projectId);
      res.status(200).json(feedback);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  },

  async deleteFeedback(req, res) {
    try {
      const { feedbackId } = req.params;
      const result = await FeedbackService.deleteFeedback(feedbackId);
      res.status(200).json(result);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  },

  async getFeedbackStats(req, res) {
    try {
      const { projectId } = req.params;
      const stats = await FeedbackService.getFeedbackStats(projectId);
      res.status(200).json(stats);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  },
};

module.exports = FeedbackController;
