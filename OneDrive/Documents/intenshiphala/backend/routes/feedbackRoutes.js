const express = require('express');
const FeedbackController = require('../controllers/FeedbackController');

const router = express.Router();

router.post('/:projectId/feedback', FeedbackController.addFeedback);
router.get('/:projectId/feedback', FeedbackController.getProjectFeedback);
router.get('/:projectId/feedback-stats', FeedbackController.getFeedbackStats);
router.delete('/:feedbackId', FeedbackController.deleteFeedback);

module.exports = router;
