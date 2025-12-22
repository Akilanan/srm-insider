const mongoose = require('mongoose');

const insightSchema = new mongoose.Schema({
  projectId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Project',
    required: true,
  },
  themes: [String],
  sentiment: {
    type: String,
    enum: ['positive', 'neutral', 'negative'],
  },
  suggestions: [String],
  summaryText: String,
  feedbackCount: Number,
  generatedAt: {
    type: Date,
    default: Date.now,
  },
});

module.exports = mongoose.model('Insight', insightSchema);
