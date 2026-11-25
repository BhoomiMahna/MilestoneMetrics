### Week 1 — Late August · Planning & System Architecture

Analysed the project requirements and explored the existing MilestoneMetrics architecture, including the React frontend, Spring Boot backend, PostgreSQL database, and authentication workflow. Studied the communication between frontend components, REST APIs, backend layers, and database entities.

Designed the **System Diagram** to represent the overall application structure and the **Collaboration Diagram** to illustrate interactions between system components.

### Week 2 — Late Aug → Early Sept · Data Flow & Workflow Modelling

Prepared the **Data Flow Diagrams (DFD Levels 0, 1 and 2)** to document the movement of information between users, system processes, and application modules at different levels of detail.

Also designed the **Swimlane Activity Diagram** to represent workflows across different actors and the **Activity Diagram** to illustrate process execution, decisions, and control flow.

### Week 3 — Mid September · System Design & Object-Oriented Modelling

Created the **Sequence Diagram** to represent time-ordered interactions between users and system components. Designed the **Class Diagram** to document the application's classes, attributes, methods, and relationships.

Also completed the **Component Diagram** to represent software modules and their dependencies, and the **Deployment Diagram** to illustrate the frontend, backend, database, and deployment architecture.

### Week 4 — Core Feature Development: Financial Questionnaire Backend

Developed the financial questionnaire backend using Java, Spring Boot, and Spring Data JPA, following a layered architecture comprising Controller, Service, Repository, DTO, and Entity components.

Created the `Questionnaire` entity with a one-to-one relationship with the User entity, along with request/response DTOs, repository, service, and controller classes.

Implemented three REST APIs for questionnaire creation, retrieval, and updating. Integrated the existing JWT authentication mechanism to associate questionnaire records with the currently authenticated user.

Added backend validation using Jakarta Bean Validation, covering financial inputs, mandatory age group, positive goal amount, and a timeline restricted to 1–100 years. Implemented duplicate questionnaire prevention through user-specific checks and a database-level unique constraint.

### Week 5 — Financial Logic & Database Integration

Implemented the questionnaire's financial calculation logic in the service layer, including timeline conversion into months, monthly savings target calculation using `BigDecimal` with ceiling rounding, monthly surplus calculation, and goal feasibility assessment.

Included current investment information in the user's financial profile and integrated the backend with the team's Supabase PostgreSQL database. Configured database credentials through environment variables (`DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`) instead of hardcoding sensitive information.

Verified questionnaire creation, retrieval, and updates against the connected PostgreSQL database.

### Week 6 — Frontend–Backend Integration & Cloud Deployment

Worked on deploying the React frontend and Spring Boot backend separately using Render. Successfully deployed the frontend and configured the backend deployment using Docker, a free Render instance in the Singapore region, Supabase database credentials, and dynamic port configuration through the `PORT` environment variable.

Identified deployment connectivity issues caused by local API URLs and corrected the authentication API URL in `Auth.jsx` through a separate feature branch and pull request. Also addressed a backend CORS configuration issue to enable communication between the hosted frontend and backend.

### Week 7 — Security, Admin Module, Testing & GitHub Collaboration

Explored the existing security implementation, including JWT authentication, BCrypt password hashing, `USER` and `ADMIN` role-based authorization, and Spring Security restrictions on administrator-only endpoints. Studied the administrator bootstrap mechanism and explored the existing Admin Dashboard, covering analytics, user management, coupon management, and side-hustle opportunities.

Compiled and executed the Spring Boot application using the Maven wrapper. Tested questionnaire creation, retrieval, updates, authenticated requests, input validation, and duplicate prevention. Verified data persistence in Supabase PostgreSQL and checked the deployed frontend's availability.

Used Git and GitHub for collaborative development through separate feature branches, descriptive commits, and pull requests. Successfully merged the questionnaire backend into the shared `backend` branch through Pull Request #1 and contributed separate pull requests for the authentication API URL correction and backend CORS fix.
