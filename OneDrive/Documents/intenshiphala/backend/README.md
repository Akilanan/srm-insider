# Clueso Backend

Node.js + Express REST API for feedback collection and AI insights generation.

## 🚀 Quick Start

### Installation

```bash
npm install
```

### Environment Setup

Create `.env` file:
```
PORT=5000
MONGODB_URI=mongodb://localhost:27017/clueso-feedback
JWT_SECRET=your-secret-key-change-this
NODE_ENV=development
```

### Run Server

```bash
# Development (with auto-restart)
npm run dev

# Production
npm start
```

Server runs on `http://localhost:5000`

## 📚 API Routes

### Authentication
- `POST /api/auth/register` - Register new user
  - Body: `{ email, password, name }`
  - Returns: `{ user: { id, email, name }, token }`

- `POST /api/auth/login` - Login user
  - Body: `{ email, password }`
  - Returns: `{ user: { id, email, name }, token }`

### Projects (Protected)
- `GET /api/projects` - Get all user's projects
- `POST /api/projects` - Create new project
  - Body: `{ name, description? }`
- `GET /api/projects/:projectId` - Get project by ID
- `PUT /api/projects/:projectId` - Update project
- `DELETE /api/projects/:projectId` - Delete project

### Feedback
- `POST /api/feedback/:projectId/feedback` - Add feedback
  - Body: `{ message, rating?, email? }`
- `GET /api/feedback/:projectId/feedback` - Get all feedback
- `GET /api/feedback/:projectId/feedback-stats` - Get stats
- `DELETE /api/feedback/:feedbackId` - Delete feedback

### Insights (Protected)
- `POST /api/insights/:projectId/insights` - Generate insights
- `GET /api/insights/:projectId/insights` - Get insights

## 🏗️ Architecture

### Models
- **User** - Authentication user data
- **Project** - User projects
- **Feedback** - Collected feedback with ratings
- **Insight** - Generated insights from feedback

### Services
Each service handles business logic:
- `AuthService` - User registration and login
- `ProjectService` - Project CRUD operations
- `FeedbackService` - Feedback management
- `InsightService` - AI insight generation (mocked)

### Controllers
Request handlers that use services and return responses.

### Middleware
- `authMiddleware` - JWT token validation

## 🔐 Authentication

All protected routes require JWT token in header:
```
Authorization: Bearer <token>
```

Token expires in 7 days. Passwords are hashed with bcryptjs (10 salt rounds).

## 🧠 Insights

The `InsightService` generates insights by:
1. Extracting keywords from feedback messages
2. Computing average rating for sentiment
3. Creating actionable suggestions
4. Generating summary text

To integrate OpenAI, modify `InsightService.js` with your API key.

## 📦 Dependencies

- **express** - Web framework
- **mongoose** - MongoDB ODM
- **bcryptjs** - Password hashing
- **jsonwebtoken** - JWT tokens
- **dotenv** - Environment variables
- **cors** - Cross-origin requests

## ✅ Health Check

```bash
curl http://localhost:5000/api/health
```

Response: `{ "status": "OK" }`

## 🐛 Troubleshooting

**MongoDB connection error:**
- Ensure MongoDB is running
- Check MONGODB_URI in .env
- Verify port 27017 is accessible

**Token errors:**
- Verify JWT_SECRET matches
- Check token hasn't expired
- Ensure Authorization header is correct

**CORS errors:**
- Verify frontend URL matches CORS config
- Check backend is running on correct port

---

See [main README](../README.md) for full documentation.
