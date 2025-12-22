# Clueso - Feedback Collection & AI Insights Platform

A full-stack SaaS application that helps teams collect user feedback, organize it, and generate AI-powered insights. Built with React, Node.js, Express, and MongoDB.

## 🎯 Core Features

### 1. **User Authentication**
- Email/password signup and login
- JWT-based authentication
- Session persistence
- Protected routes for authenticated users
- Logout functionality

### 2. **Dashboard**
- Overview of all projects and feedback statistics
- Create, view, and delete projects
- Real-time feedback count and project metrics
- Clean, minimal SaaS-style interface

### 3. **Project & Feedback Management**
- Create and manage multiple projects
- Collect feedback with optional email and rating (1-5 stars)
- View all feedback submissions with timestamps
- Delete feedback items
- Feedback statistics (count, average rating)

### 4. **AI-Powered Insights**
- Generate automatic insights from collected feedback
- Extract key themes and topics
- Determine overall sentiment (positive/neutral/negative)
- Generate actionable suggestions
- Summary text analysis
- Mocked AI service (easily replaceable with OpenAI API)

## 🛠️ Tech Stack

**Frontend:**
- React 18.2
- React Router for navigation
- Axios for API calls
- CSS for styling

**Backend:**
- Node.js with Express.js
- MongoDB with Mongoose ODM
- JWT for authentication
- bcryptjs for password hashing
- CORS enabled for frontend communication

**Database:**
- MongoDB (local or Atlas)

## 📁 Project Structure

```
intenshiphala/
├── backend/
│   ├── models/
│   │   ├── User.js
│   │   ├── Project.js
│   │   ├── Feedback.js
│   │   └── Insight.js
│   ├── services/
│   │   ├── AuthService.js
│   │   ├── ProjectService.js
│   │   ├── FeedbackService.js
│   │   └── InsightService.js
│   ├── controllers/
│   │   ├── AuthController.js
│   │   ├── ProjectController.js
│   │   ├── FeedbackController.js
│   │   └── InsightController.js
│   ├── routes/
│   │   ├── authRoutes.js
│   │   ├── projectRoutes.js
│   │   ├── feedbackRoutes.js
│   │   └── insightRoutes.js
│   ├── middleware/
│   │   └── authMiddleware.js
│   ├── server.js
│   ├── package.json
│   └── .env
│
└── frontend/
    ├── src/
    │   ├── api/
    │   │   └── client.js
    │   ├── components/
    │   │   ├── Login.js
    │   │   ├── SignUp.js
    │   │   ├── Dashboard.js
    │   │   ├── ProjectDetail.js
    │   │   ├── ProtectedRoute.js
    │   │   ├── Auth.css
    │   │   ├── Dashboard.css
    │   │   └── ProjectDetail.css
    │   ├── context/
    │   │   └── AuthContext.js
    │   ├── App.js
    │   ├── App.css
    │   └── index.js
    ├── public/
    │   └── index.html
    ├── package.json
    └── .env
```

## 🚀 Setup & Installation

### Prerequisites
- Node.js (v14 or higher)
- MongoDB (local or Atlas cluster)
- npm or yarn

### Backend Setup

1. **Navigate to backend directory:**
```bash
cd backend
```

2. **Install dependencies:**
```bash
npm install
```

3. **Create .env file:**
```bash
cp .env.example .env
```

4. **Configure .env:**
```
PORT=5000
MONGODB_URI=mongodb://localhost:27017/clueso-feedback
JWT_SECRET=your-secret-key-change-this-in-production
NODE_ENV=development
```

5. **Start MongoDB:**
```bash
# If using local MongoDB
mongod
```

6. **Run the backend:**
```bash
# Development mode with auto-restart
npm run dev

# Or production mode
npm start
```

The backend will run on `http://localhost:5000`

### Frontend Setup

1. **Navigate to frontend directory:**
```bash
cd frontend
```

2. **Install dependencies:**
```bash
npm install
```

3. **Create .env file:**
```bash
cp .env.example .env
```

4. **The .env should contain:**
```
REACT_APP_API_URL=http://localhost:5000/api
```

5. **Start the frontend:**
```bash
npm start
```

The frontend will open at `http://localhost:3000`

## 📚 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Projects
- `GET /api/projects` - Get all user's projects
- `POST /api/projects` - Create new project
- `GET /api/projects/:projectId` - Get project details
- `PUT /api/projects/:projectId` - Update project
- `DELETE /api/projects/:projectId` - Delete project

### Feedback
- `POST /api/feedback/:projectId/feedback` - Add feedback to project
- `GET /api/feedback/:projectId/feedback` - Get all feedback for project
- `GET /api/feedback/:projectId/feedback-stats` - Get feedback statistics
- `DELETE /api/feedback/:feedbackId` - Delete feedback

### Insights
- `POST /api/insights/:projectId/insights` - Generate insights from feedback
- `GET /api/insights/:projectId/insights` - Get project insights

## 🔐 Authentication Flow

1. User signs up or logs in
2. Backend generates JWT token
3. Token stored in localStorage
4. All API requests include token in `Authorization: Bearer <token>` header
5. Middleware validates token on protected routes
6. Session persists across page refreshes

## 🧠 AI Insights Logic (Mocked)

The current implementation includes a mock AI service that:

1. **Extracts Themes:** Analyzes feedback text and identifies most common keywords
2. **Determines Sentiment:** Calculates average rating to determine overall sentiment
   - Rating >= 4: Positive
   - Rating <= 2: Negative
   - Rating 2-4: Neutral
3. **Generates Suggestions:** Creates actionable recommendations based on:
   - Feedback length and detail
   - Common themes
   - Proportion of negative feedback
4. **Summarizes:** Creates a readable summary of all insights

### Replacing with OpenAI

To integrate real AI, update `backend/services/InsightService.js`:

```javascript
const openai = new OpenAI({ apiKey: process.env.OPENAI_API_KEY });

async generateInsights(projectId) {
  const feedback = await Feedback.find({ projectId });
  const feedbackText = feedback.map(f => f.message).join('\n');
  
  const response = await openai.chat.completions.create({
    model: 'gpt-4',
    messages: [{
      role: 'system',
      content: 'Analyze this feedback and provide themes, sentiment, and suggestions.'
    }, {
      role: 'user',
      content: feedbackText
    }]
  });
  
  // Parse response and save insights...
}
```

## 🎨 UI/UX Design

- **Color Scheme:** Purple gradient (#667eea to #764ba2) for primary actions
- **Layout:** Clean, card-based design with ample whitespace
- **Responsiveness:** Mobile-friendly with flexible grid layouts
- **Typography:** System fonts for fast loading and compatibility
- **Focus:** Usability over animations

## 🔒 Security Considerations

- Passwords hashed with bcryptjs (10 salt rounds)
- JWT tokens expire after 7 days
- Protected routes require valid token
- CORS configured for frontend origin
- User data isolated per user ID

## ⚙️ Key Design Decisions

### 1. **Service Layer Architecture**
Each service (Auth, Project, Feedback, Insight) handles business logic separately from controllers. This allows:
- Easy testing of business logic
- Code reusability
- Clear separation of concerns

### 2. **Mocked AI Service**
The insight generation is mocked to:
- Allow offline development
- Reduce API costs during testing
- Provide a clear interface for OpenAI integration
- Make logic transparent and auditable

### 3. **JWT Authentication**
Chosen over session-based auth because:
- Stateless, scalable to multiple servers
- Works well with REST APIs
- Reduces database queries for auth checks
- Better for SPA applications

### 4. **React Context for Auth**
- Avoids prop drilling
- Simple state management for auth state
- Persists across page refreshes
- No external dependencies needed

## 📈 Scalability Notes

**Current Limitations:**
- Single MongoDB instance (no sharding)
- In-memory token verification (no caching)
- AI insights computed synchronously
- No pagination on feedback lists (for MVP)

**For Production:**
- Implement Redis caching for tokens
- Add pagination to feedback endpoints
- Make insight generation async with job queue
- Add database indexes on frequently queried fields
- Implement rate limiting
- Add API request logging and monitoring

## 🧪 Testing Workflow

1. **Create a test account:**
   - Navigate to signup page
   - Register with test@example.com / password123

2. **Create a project:**
   - Click "New Project" on dashboard
   - Enter project name and optional description

3. **Add feedback:**
   - Click "View" on a project
   - Click "Add Feedback"
   - Enter feedback message, rating, and optional email

4. **Generate insights:**
   - Click "Generate Insights" button
   - System analyzes feedback and displays insights

5. **Verify features:**
   - Create multiple projects
   - Add various feedback (different ratings and messages)
   - Check that insights update correctly
   - Test logout and login

## 🚨 Troubleshooting

### Backend not connecting to MongoDB
- Verify MongoDB is running: `mongosh` or `mongo`
- Check MONGODB_URI in .env
- Ensure port 27017 is not blocked

### Frontend can't connect to backend
- Verify backend is running on port 5000
- Check REACT_APP_API_URL in frontend/.env
- Check CORS is enabled in backend (server.js)

### JWT token issues
- Clear localStorage: `localStorage.clear()`
- Refresh page to re-login
- Check JWT_SECRET matches between .env

### Feedback not appearing
- Ensure project ID is correct
- Check network requests in browser DevTools
- Verify user owns the project (authorization check)

## 📝 Assumptions & Limitations

### Assumptions
- Users access the app from a single browser (session persistence via localStorage)
- MongoDB is available and connected
- Frontend and backend run on same machine (localhost)
- Email field in feedback is optional

### Limitations
- No real email verification
- No password recovery mechanism
- No pagination on feedback lists
- No collaborative features (projects are single-user)
- AI insights are mocked (not production-grade)
- No analytics or usage tracking
- No data export functionality
- Limited error messages for security

## 🎓 Learning Outcomes

This project demonstrates:

1. **Full-stack JavaScript:** React + Node.js skill integration
2. **REST API Design:** Proper endpoint structure and HTTP methods
3. **Database Modeling:** MongoDB schema design with relationships
4. **Authentication:** JWT implementation and security best practices
5. **State Management:** Context API for application state
6. **Component Architecture:** Reusable, well-organized React components
7. **Error Handling:** Graceful error handling in frontend and backend
8. **SaaS Patterns:** Multi-project, per-user data isolation

## 📄 License

This project is provided as-is for educational purposes.

## 🤝 Contributing

This is a demonstration project. Modifications are welcome for learning purposes.

---

**Built as a functional clone of Clueso.io - focusing on core workflows and product behavior.**
