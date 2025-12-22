const Feedback = require('../models/Feedback');
const Insight = require('../models/Insight');

const InsightService = {
  async generateInsights(projectId) {
    try {
      const feedback = await Feedback.find({ projectId });

      if (feedback.length === 0) {
        throw new Error('No feedback available to generate insights');
      }

      // Mock AI logic - extract themes and sentiment from feedback
      const themes = this._extractThemes(feedback);
      const sentiment = this._determineSentiment(feedback);
      const suggestions = this._generateSuggestions(feedback, themes);
      const summaryText = this._generateSummary(
        feedback,
        themes,
        sentiment
      );

      // Check if insight already exists
      let insight = await Insight.findOne({ projectId });

      if (insight) {
        insight.themes = themes;
        insight.sentiment = sentiment;
        insight.suggestions = suggestions;
        insight.summaryText = summaryText;
        insight.feedbackCount = feedback.length;
        insight.generatedAt = new Date();
      } else {
        insight = new Insight({
          projectId,
          themes,
          sentiment,
          suggestions,
          summaryText,
          feedbackCount: feedback.length,
        });
      }

      await insight.save();
      return insight;
    } catch (error) {
      throw error;
    }
  },

  async getProjectInsights(projectId) {
    try {
      return await Insight.findOne({ projectId });
    } catch (error) {
      throw error;
    }
  },

  _extractThemes(feedback) {
    // Simple keyword extraction from feedback messages
    const keywords = {};
    const commonWords = new Set([
      'the',
      'a',
      'an',
      'and',
      'or',
      'but',
      'is',
      'are',
      'was',
      'were',
      'be',
      'been',
      'being',
      'have',
      'has',
      'had',
      'do',
      'does',
      'did',
      'will',
      'would',
      'could',
      'should',
      'may',
      'might',
      'must',
      'can',
      'this',
      'that',
      'it',
      'to',
      'for',
      'of',
      'in',
      'on',
      'at',
      'by',
      'from',
      'as',
      'with',
      'i',
      'you',
      'we',
      'they',
      'him',
      'her',
      'its',
      'my',
      'your',
      'our',
      'their',
    ]);

    feedback.forEach((item) => {
      const words = item.message
        .toLowerCase()
        .split(/[\s\.,;:!?]+/)
        .filter((w) => w.length > 3 && !commonWords.has(w));

      words.forEach((word) => {
        keywords[word] = (keywords[word] || 0) + 1;
      });
    });

    // Get top 5 keywords as themes
    return Object.entries(keywords)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 5)
      .map(([word]) => word);
  },

  _determineSentiment(feedback) {
    // Simple sentiment analysis based on rating
    const avgRating =
      feedback.reduce((sum, f) => sum + (f.rating || 3), 0) / feedback.length;

    if (avgRating >= 4) return 'positive';
    if (avgRating <= 2) return 'negative';
    return 'neutral';
  },

  _generateSuggestions(feedback, themes) {
    // Generate actionable suggestions based on feedback
    const suggestions = [];

    // Check if multiple users mention similar issues
    const messageLength =
      feedback.reduce((sum, f) => sum + f.message.length, 0) /
      feedback.length;

    if (messageLength < 50) {
      suggestions.push('Consider asking more detailed questions in feedback form');
    }

    if (themes.length > 0) {
      suggestions.push(
        `Focus on improving: ${themes.slice(0, 2).join(', ')}`
      );
    }

    const lowRatings = feedback.filter((f) => f.rating && f.rating <= 2).length;
    if (lowRatings > feedback.length * 0.3) {
      suggestions.push('High proportion of negative feedback - urgent review recommended');
    }

    if (suggestions.length === 0) {
      suggestions.push('Continue current approach - feedback is positive');
    }

    return suggestions;
  },

  _generateSummary(feedback, themes, sentiment) {
    const count = feedback.length;
    return `Analyzed ${count} feedback submissions. Overall sentiment is ${sentiment}. 
Key themes: ${themes.join(', ') || 'general feedback'}. 
Generated insights can help improve your product based on user feedback.`;
  },
};

module.exports = InsightService;
