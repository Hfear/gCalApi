
# **EdGroup - Google Calendar Integration**

## **Project Overview**

EdGroup is a web application designed to integrate Google Calendar with a Spring Boot backend and a React frontend. The project enables users to manage and share calendar events, leveraging OAuth 2.0 authentication for secure login and the Google Calendar API for event management.

### **Key Features**

* **Google Login**: Sign in with Google to access and manage your Google Calendar.
* **Calendar Events**: View, create, and modify events within the Google Calendar API.
* **Group Scheduling**: Share group schedules with classmates or colleagues.
* **Responsive UI**: Built with React and Tailwind CSS for a smooth user experience.

---

## **Tech Stack**

* **Backend**: Spring Boot, Java
* **Frontend**: React, Vite, Tailwind CSS
* **Authentication**: OAuth 2.0 with Google
* **Database**: In-memory (for tokens and user data)
* **Testing**: JUnit 5, Gradle

---

## **Installation Guide**

### **1. Prerequisites**

Make sure you have the following installed:

* [JDK 17 or higher](https://adoptopenjdk.net/) (for backend).
* [Node.js](https://nodejs.org/) and [npm](https://www.npmjs.com/) (for frontend).
* [Gradle](https://gradle.org/install/) (for building the backend).
* [Google Cloud Project](https://console.cloud.google.com/) with the Calendar API enabled for OAuth credentials.

### **2. Clone the Repository**

```bash
git clone https://github.com/your-username/edu-sync.git
cd edu-sync
```

### **3. Backend Setup**

1. **Install Dependencies**:
   Make sure you have Gradle installed and the Spring Boot application set up.

2. **Run the Backend Server**:
   In the backend directory, run the following command to start the Spring Boot server:

   ```bash
   gradle bootRun
   ```

3. **Set Up Google OAuth**:

   * Create a project on [Google Cloud Console](https://console.cloud.google.com/).
   * Enable the Google Calendar API.
   * Create OAuth 2.0 credentials and download the credentials JSON.
   * Place the credentials JSON in your backend directory (or configure it in the `application.properties`).

---

### **4. Frontend Setup**

1. **Install Dependencies**:
   In the frontend directory, run the following to install dependencies:

   ```bash
   cd frontend
   npm install
   ```

2. **Run the Frontend Server**:
   Start the React development server:

   ```bash
   npm run dev
   ```

3. **Navigate to the Application**:
   Open your browser and go to [http://localhost:3000](http://localhost:3000) to access the frontend application.

---

## **Usage**

### **1. Google Login**

* Click on the "Login with Google" button to authenticate the user with Google OAuth 2.0.
* Upon successful login, the backend exchanges the authorization code for tokens and saves them for future requests.

### **2. Calendar Events**

* View your Google Calendar events by navigating to the "My Calendar" page.
* Create new events by filling out the event details and clicking the "Create Event" button.

### **3. Group Scheduling**

* Share your calendar with group members.
* View events shared by others in the group calendar.

---

## **OAuth Flow**

### **Steps for OAuth 2.0 Authentication Flow**

1. **User logs in with Google**: The frontend redirects the user to Google OAuth for authentication.
2. **Backend exchanges the code for tokens**: After the user logs in, the backend receives an OAuth code and exchanges it for `access_token` and `refresh_token`.
3. **Backend saves tokens**: The tokens are securely stored for future API calls.
4. **API calls**: When the frontend requests calendar events, the backend includes the `access_token` in the request to Google.

---

## **Testing**

Testing is done using JUnit 5, ensuring that the backend logic functions as expected.

Run the tests with Gradle:

```bash
gradle test
```

## **Education Resources**
https://docs.google.com/document/d/1NwOoxIED40SW_OjV9eXj1MycNbeGQEmC9kDlP5ske3A/edit?usp=sharing


