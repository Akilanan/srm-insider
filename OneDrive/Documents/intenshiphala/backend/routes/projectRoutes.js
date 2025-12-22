const express = require('express');
const ProjectController = require('../controllers/ProjectController');
const authMiddleware = require('../middleware/authMiddleware');

const router = express.Router();

router.use(authMiddleware);

router.post('/', ProjectController.createProject);
router.get('/', ProjectController.getUserProjects);
router.get('/:projectId', ProjectController.getProjectById);
router.put('/:projectId', ProjectController.updateProject);
router.delete('/:projectId', ProjectController.deleteProject);

module.exports = router;
