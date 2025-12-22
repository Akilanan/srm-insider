const ProjectService = require('../services/ProjectService');

const ProjectController = {
  async createProject(req, res) {
    try {
      const { name, description } = req.body;
      const userId = req.userId;

      if (!name) {
        return res.status(400).json({ message: 'Project name is required' });
      }

      const project = await ProjectService.createProject(userId, name, description);
      res.status(201).json(project);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  },

  async getUserProjects(req, res) {
    try {
      const userId = req.userId;
      const projects = await ProjectService.getUserProjects(userId);
      res.status(200).json(projects);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  },

  async getProjectById(req, res) {
    try {
      const { projectId } = req.params;
      const userId = req.userId;

      const project = await ProjectService.getProjectById(projectId, userId);
      res.status(200).json(project);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  },

  async updateProject(req, res) {
    try {
      const { projectId } = req.params;
      const userId = req.userId;
      const updates = req.body;

      const project = await ProjectService.updateProject(
        projectId,
        userId,
        updates
      );
      res.status(200).json(project);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  },

  async deleteProject(req, res) {
    try {
      const { projectId } = req.params;
      const userId = req.userId;

      const result = await ProjectService.deleteProject(projectId, userId);
      res.status(200).json(result);
    } catch (error) {
      res.status(400).json({ message: error.message });
    }
  },
};

module.exports = ProjectController;
