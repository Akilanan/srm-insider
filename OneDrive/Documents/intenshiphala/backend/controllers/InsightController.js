const InsightService = require('../services/InsightService');

const InsightController = {
  async generateInsights(req, res) {
    try {
      const { projectId } = req.params;
      const insights = await InsightService.generateInsights(projectId);
      res.status(200).json(insights);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  },

  async getProjectInsights(req, res) {
    try {
      const { projectId } = req.params;
      const insights = await InsightService.getProjectInsights(projectId);

      if (!insights) {
        return res.status(404).json({ message: 'No insights generated yet' });
      }

      res.status(200).json(insights);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  },
};

module.exports = InsightController;
