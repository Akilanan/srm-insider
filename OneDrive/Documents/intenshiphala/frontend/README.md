# Clueso Frontend

React application for feedback collection and AI insights dashboard.

## 🚀 Quick Start

### Installation

```bash
npm install
```

### Environment Setup

Create `.env` file:
```
REACT_APP_API_URL=http://localhost:5000/api
```

### Run App

```bash
npm start
```

App opens at `http://localhost:3000`

## 📱 Pages

### Login
- Email/password authentication
- Redirects to dashboard on success
- Link to signup page

### Sign Up
- Create new account with name, email, password
- Password confirmation validation
- Redirects to dashboard on success
- Link to login page

### Dashboard
- Overview stats (projects count, total feedback)
- List all projects
- Create new project
- Delete projects
- Quick links to project details

### Project Detail
- View all feedback for project
- Add new feedback with message, rating (1-5), email
- View feedback statistics
- Generate AI insights
- View extracted themes, sentiment, and suggestions
- Delete feedback items

## 🔐 Authentication

- **AuthContext** - Global auth state management
- **Protected Routes** - Only accessible when logged in
- **Token Storage** - JWT stored in localStorage
- **Session Persistence** - Auto-login on page reload

### Auth Flow
1. User fills signup/login form
2. API validates credentials
3. Backend returns token
4. Token stored in localStorage
5. Redirect to dashboard

## 🎨 Components

### ProtectedRoute.js
Wraps routes that require authentication. Redirects to login if not authenticated.

### Login.js
Login form with email/password inputs. Calls AuthService and redirects on success.

### SignUp.js
Signup form with name, email, password, confirmation. Creates account and logs in.

### Dashboard.js
Main app screen with projects list, stats, and new project form.

### ProjectDetail.js
Project view with feedback list, add feedback form, and insights display.

## 🎯 Features

- ✅ User authentication
- ✅ Create/delete projects
- ✅ Submit feedback with ratings
- ✅ View feedback list
- ✅ Generate AI insights
- ✅ Responsive mobile design
- ✅ Session persistence

## 🌐 API Integration

All API calls via `api/client.js`:

```javascript
// Auth
authAPI.register(email, password, name)
authAPI.login(email, password)

// Projects
projectsAPI.getAll()
projectsAPI.create(data)
projectsAPI.getById(id)
projectsAPI.delete(id)

// Feedback
feedbackAPI.add(projectId, data)
feedbackAPI.getAll(projectId)
feedbackAPI.delete(feedbackId)

// Insights
insightsAPI.generate(projectId)
insightsAPI.get(projectId)
```

## 🎨 Styling

- CSS Grid and Flexbox layouts
- Responsive breakpoints at 768px
- Purple gradient theme (#667eea to #764ba2)
- Consistent spacing and typography

## 📦 Dependencies

- **react** - UI framework
- **react-router-dom** - Page routing
- **axios** - HTTP client
- **react-scripts** - Build tools

## 🧪 Testing Workflow

1. Sign up with test account
2. Create a project
3. Add feedback with different ratings
4. Generate insights
5. View insights analysis
6. Delete feedback/project
7. Logout and login again

## 🐛 Troubleshooting

**Can't connect to backend:**
- Check REACT_APP_API_URL is correct
- Verify backend is running on port 5000
- Check network tab in DevTools

**Login fails:**
- Verify backend is running
- Check credentials are correct
- Clear localStorage: `localStorage.clear()`

**Insights not generating:**
- Ensure project has feedback
- Check browser console for errors
- Verify backend is responding

**Page refreshes and logs out:**
- Check localStorage for token
- Verify AuthContext provider wraps app
- Check token hasn't expired

---

See [main README](../README.md) for full documentation.
