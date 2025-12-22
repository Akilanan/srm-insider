const Feedback = require('../models/Feedback');

const FeedbackService = {
  async addFeedback(projectId, message, rating, email) {
    try {
      const feedback = new Feedback({
        projectId,
        message,
        rating,
        email,
      });
      await feedback.save();
      return feedback;
    } catch (error) {
      throw error;
    }
  },

  async getProjectFeedback(projectId) {
    try {
      return await Feedback.find({ projectId }).sort({ createdAt: -1 });
    } catch (error) {
      throw error;
    }
  },

  async deleteFeedback(feedbackId) {
    try {
      await Feedback.deleteOne({ _id: feedbackId });
      return { success: true };
    } catch (error) {
      throw error;
    }
  },

  async getFeedbackStats(projectId) {
    try {
      const feedback = await Feedback.find({ projectId });
      const totalCount = feedback.length;
      const avgRating =
        feedback.reduce((sum, f) => sum + (f.rating || 0), 0) /
          Math.max(totalCount, 1) || 0;

      return {
        totalCount,
        averageRating: Math.round(avgRating * 10) / 10,
      };
    } catch (error) {
      throw error;
    }
  },
};

module.exports = FeedbackService;
