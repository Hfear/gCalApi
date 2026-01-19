# 📚 ED Group - Classroom Collaboration Platform

A full-stack web application for classroom and group management with **Google Calendar integration**. Create classrooms, form study groups, and coordinate schedules through shared calendars.

## 🎯 Overview

ED Group allows teachers and students to:
- Create and join virtual classrooms using invite codes
- Form groups within classrooms for projects or study sessions
- Automatically generate shared Google Calendars for each group
- Add, view, and manage events across personal and group calendars

## ✨ Features

### 👥 Classroom Management
- Create classrooms with auto-generated join codes
- Join existing classrooms via code
- View enrolled students
- Separate views for created vs. joined classes

### 👨‍👩‍👧‍👦 Group Collaboration
- Create groups within any classroom
- Join/leave groups freely
- View group members
- Owner-only group deletion

### 📅 Calendar Integration
- **Auto-created Google Calendar** for each group
- Add events with title, location, description, start/end times
- View upcoming events in-app
- Calendars are public for easy iframe embedding
- Personal calendar access via Google OAuth

### 🔐 Authentication
- Google OAuth 2.0 sign-in
- Session-based authentication
- Protected routes on frontend
- Secure credential storage with refresh tokens

## 🛠️ Tech Stack

### Backend
| Technology | Purpose |
|------------|---------|
| **Java 11+** | Language |
| **Spring Boot** | REST API Framework |
| **Google Calendar API** | Calendar operations |
| **Google OAuth 2.0** | Authentication |
| **Jackson** | JSON serialization |
| **ConcurrentHashMap** | Thread-safe in-memory storage |

### Frontend
| Technology | Purpose |
|------------|---------|
| **React 18** | UI Framework |
| **React Router v6** | Client-side routing |
| **Bootstrap 5** | Styling & components |
| **Vite** | Build tool & dev server |

## 📁 Project Structure

```
gCalApi/
├── src/main/java/
│   ├── Application/
│   │   └── Application.java          # Spring Boot entry point
│   ├── Controller/
│   │   ├── AuthControl.java          # OAuth endpoints
│   │   ├── CalendarController.java   # Calendar CRUD
│   │   ├── ClassroomControl.java     # Classroom endpoints
│   │   ├── GroupControl.java         # Group endpoints
│   │   └── UserControl.java          # User endpoints
│   ├── Service/
│   │   ├── ClassroomService.java     # Classroom logic
│   │   ├── GroupService.java         # Group logic
│   │   ├── GroupCalService.java      # Group calendar ops
│   │   ├── UserCalendarService.java  # Personal calendar ops
│   │   ├── EventBuilder.java         # Builder pattern for events
│   │   └── ICalendarService.java     # Calendar interface
│   ├── Model/
│   │   ├── User.java
│   │   ├── Classroom.java
│   │   ├── Group.java
│   │   └── GroupCal.java
│   ├── Database/
│   │   ├── UserDatabase.java         # User persistence
│   │   ├── ClassroomDatabase.java    # Classroom persistence
│   │   ├── GroupDatabase.java        # Group persistence
│   │   └── GroupCalDatabase.java     # Calendar persistence
│   └── ConfigAndUtil/
│       ├── Authorization.java        # OAuth flow management
│       ├── CalServiceBuilder.java    # Calendar service factory
│       ├── DateAdapter.java          # Flexible date parsing
│       └── CORSconfig.java           # CORS settings
├── src/main/resources/
│   ├── credentials.json              # Google OAuth credentials
│   ├── userdb.json                   # User data
│   ├── classroomdb.json              # Classroom data
│   ├── groupdb.json                  # Group data
│   └── groupcaldb.json               # Calendar mappings
└── frontend/
    ├── src/
    │   ├── main.jsx                  # Router setup
    │   ├── SignIn.jsx                # Google sign-in
    │   ├── MyClasses.jsx             # Class dashboard
    │   ├── MyGroups.jsx              # Group dashboard
    │   ├── Profile.jsx               # User profile
    │   ├── ProtectedRoute.jsx        # Auth guard
    │   └── ...
    └── package.json
```

## 🚀 Getting Started

### Prerequisites
- Java 11 or higher
- Node.js 16+ and npm
- Gradle
- Google Cloud Project with Calendar API enabled

### 1. Clone the Repository
```bash
git clone https://github.com/Hfear/gCalApi.git
cd gCalApi
```

### 2. Google Cloud Setup
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project
3. Enable **Google Calendar API**
4. Configure OAuth consent screen
5. Create OAuth 2.0 credentials (Web application)
6. Add authorized redirect URI: `http://localhost:8080/oauth/callback`
7. Download credentials and save as `src/main/resources/credentials.json`

### 3. Backend Setup
```bash
# Build and run with Gradle
gradle bootRun
```
Backend runs on `http://localhost:8080`

### 4. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on `http://localhost:5173` or `http://localhost:5174`

### 5. Environment Configuration
Create `application.properties`:
```properties
frontend.redirect-uri=http://localhost:5174/homepage
```

## 🔄 OAuth 2.0 Flow

```
┌──────────┐     1. Login Request      ┌──────────┐
│          │ ────────────────────────► │          │
│  React   │                           │  Google  │
│ Frontend │ ◄──────────────────────── │  OAuth   │
│          │     2. Auth Code          │          │
└────┬─────┘                           └──────────┘
     │                                       │
     │ 3. Send code to backend               │
     ▼                                       │
┌──────────┐     4. Exchange code      ┌─────┴────┐
│  Spring  │ ────────────────────────► │  Google  │
│   Boot   │ ◄──────────────────────── │  Token   │
│ Backend  │   5. Access + Refresh     │ Endpoint │
└────┬─────┘       Tokens              └──────────┘
     │
     │ 6. Store tokens, create session
     │ 7. Redirect to homepage
     ▼
┌──────────┐
│  User    │
│ Dashboard│
└──────────┘
```

**Step-by-step:**
1. **User clicks "Login with Google"** → Frontend redirects to Google OAuth
2. **User authenticates** → Google returns authorization code to callback URL
3. **Backend receives code** → Exchanges it for `access_token` and `refresh_token`
4. **Tokens stored** → User info extracted from ID token and saved to database
5. **Session created** → User email stored in HTTP session
6. **API calls** → Backend includes `access_token` in Google Calendar API requests

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/auth/url` | Get Google OAuth URL |
| `GET` | `/oauth/callback` | OAuth callback handler |
| `GET` | `/oauth/isAuth` | Check authentication status |
| `GET` | `/currentUser` | Get current user ID |
| `POST` | `/logout` | Logout and invalidate session |

### User
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/profile` | Get current user profile |
| `GET` | `/my-classes` | Get user's enrolled classes |
| `GET` | `/my-groups` | Get user's groups |
| `GET` | `/user/{id}` | Get user by ID |

### Classrooms
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/class/create` | Create new classroom |
| `POST` | `/join` | Join classroom by code |
| `GET` | `/class/{code}` | Get classroom by code |
| `POST` | `/class/student-ids` | Get student IDs for class |

### Groups
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/class/{classCode}/groups/listgroups` | List all groups in class |
| `POST` | `/class/{classCode}/groups/creategroup` | Create new group |
| `POST` | `/class/{classCode}/groups/group/{groupCode}/join` | Join group |
| `POST` | `/class/{classCode}/groups/group/{groupCode}/leave` | Leave group |
| `GET` | `/class/{classCode}/groups/{groupCode}/members` | List group members |
| `GET` | `/class/{classCode}/groups/{groupCode}/details` | Get group details |
| `DELETE` | `/class/{classCode}/groups/group/{groupCode}` | Delete group (owner only) |

### Calendar
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/calendar/group/{groupCode}/display` | Get group calendar events |
| `POST` | `/calendar/group/{groupCode}/addevent` | Add event to group calendar |
| `POST` | `/calendar/group/{groupCode}/removeevent` | Remove group event |
| `GET` | `/calendar/group/{groupCode}/info` | Get group calendar info |
| `GET` | `/calendar/user/display` | Get user's personal events |
| `POST` | `/calendar/user/addevent` | Add personal event |
| `POST` | `/calendar/user/removeevent` | Remove personal event |

## 🎨 Design Patterns

| Pattern | Implementation | Purpose |
|---------|----------------|---------|
| **Builder** | `EventBuilder.java` | Fluent API for creating Google Calendar events |
| **Service Layer** | `*Service.java` classes | Separates business logic from controllers |
| **Interface Segregation** | `ICalendarService` | Common interface for user and group calendars |
| **Repository** | `*Database.java` classes | Abstracts data persistence |

## 🧪 Testing

Run backend tests with Gradle:
```bash
gradle test
```

## 📖 Documentation & Resources

- [Project Documentation](https://docs.google.com/document/d/1NwOoxIED40SW_OjV9eXj1MycNbeGQEmC9kDlP5ske3A/edit?usp=sharing)
- [Google Calendar API Docs](https://developers.google.com/calendar/api)
- [Spring Boot Reference](https://spring.io/projects/spring-boot)

## 🔮 Future Enhancements

- [ ] PostgreSQL/MongoDB for production database
- [ ] Schedule conflict detection
- [ ] Email notifications for events
- [ ] Embedded calendar view in frontend
- [ ] Role-based permissions (teacher/student)
- [ ] Group chat integration
- [ ] Mobile app (React Native)

## 📄 License

MIT License

---

*Built with ☕ Java and ⚛️ React*
