const Project = require('../models/Project');
const Feedback = require('../models/Feedback');
const Insight = require('../models/Insight');

const ProjectService = {
  async createProject(userId, name, description) {
    try {
      const project = new Project({
        name,
        description,
        userId,
      });
      await project.save();
      return project;
    } catch (error) {
      throw error;
    }
  },

  async getUserProjects(userId) {
    try {
      return await Project.find({ userId }).sort({ createdAt: -1 });
    } catch (error) {
      throw error;
    }
  },

  async getProjectById(projectId, userId) {
    try {
      const project = await Project.findById(projectId);
      if (!project || project.userId.toString() !== userId) {
        throw new Error('Project not found');
      }
      return project;
    } catch (error) {
      throw error;
    }
  },

  async updateProject(projectId, userId, updates) {
    try {
      const project = await Project.findById(projectId);
      if (!project || project.userId.toString() !== userId) {
        throw new Error('Project not found');
      }
      Object.assign(project, updates);
      await project.save();
      return project;
    } catch (error) {
      throw error;
    }
  },

  async deleteProject(projectId, userId) {
    try {
      const project = await Project.findById(projectId);
      if (!project || project.userId.toString() !== userId) {
        throw new Error('Project not found');
      }
      await Project.deleteOne({ _id: projectId });
      await Feedback.deleteMany({ projectId });
      await Insight.deleteMany({ projectId });
      return { success: true };
    } catch (error) {
      throw error;
    }
  },
};

module.exports = ProjectService;
