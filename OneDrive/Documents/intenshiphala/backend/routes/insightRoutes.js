const express = require('express');
const InsightController = require('../controllers/InsightController');
const authMiddleware = require('../middleware/authMiddleware');

const router = express.Router();

router.use(authMiddleware);

router.post('/:projectId/insights', InsightController.generateInsights);
router.get('/:projectId/insights', InsightController.getProjectInsights);

module.exports = router;
