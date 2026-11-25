Week 1 — Late August · Planning & Requirements 
I analysed the system requirements and identified the key actors and use cases for the platform.
I designed the Use Case Diagram mapping the major functionalities and user–system workflows.
I helped get the initial project/repository scaffolding in place.

Week 2 — Late Aug → Early Sept · Authentication Backend & Database Integration 
I built the Spring Boot authentication backend for signup and login.
I implemented JWT-based auth and BCrypt password hashing for secure credential handling.
I integrated the backend with PostgreSQL on Supabase and configured environment variables for the DB credentials and JWT secret (kept out of source control).
I connected the full React → Spring Boot → Supabase flow and tested signup/login and user-data persistence end-to-end.
Needs your confirmation: the initial React auth/questionnaire pages were committed by Kirandeep — my contribution here was the backend and the end-to-end integration/wiring.

Week 3 — Mid September · System Design & Diagrams 
I produced the System & Collaboration diagrams (~Sep 9).
I created the Data Flow Diagrams (DFD levels 0/1/2) and a swimlane activity diagram (~Sep 15).
I created the Activity Diagram and Sequence Diagram (~Sep 15).
I created the Class Diagram reflecting the entity/service structure (~Sep 16).
Needs your confirmation: these diagram files are on your machine and you listed them as yours — just confirm none were co-authored with a teammate before putting them in your journal.

Week 4 — Core Feature Development: Backend 
I extended the Spring Boot backend well beyond auth, adding the major app features: savings tracking & progress, gamification (streaks/points/badges), a coupons/rewards catalog, in-app notifications, educational investment recommendations, side-hustle recommendations, and an admin module.
I added role-based authorization (admin vs user) enforced on the server, per-user data isolation, and idempotency rules (no duplicate points, badges, redemptions, or notifications).

Week 5 — Feature Development: Frontend Integration 
I built the frontend pages for the new features (rewards, coupons, notifications, investments, admin dashboard, shared navigation) and wired them to the real backend APIs.
I kept them consistent with the app’s existing visual design, with proper loading/empty/error states.

Week 6 — Database & Environment Configuration 
I set up an H2 in-memory development profile with clearly-labelled demo data so the app can be run and demoed locally without the live database.
I configured Supabase for production and debugged a real connection problem: the direct endpoint was IPv6-only on an IPv4 network, so I switched the config to the Supabase session pooler and confirmed a live connection.
I handled a safe, non-destructive schema migration (adding a role column with a default so existing user rows were preserved, not reset).

Week 7 — Testing, Debugging & Documentation 
I wrote unit tests for the core business rules (streak logic, coupon redemption, notification ownership/dedupe) and got the suite passing.
I ran end-to-end verification of the main user journeys (register → login → onboarding → savings → rewards/coupons/notifications → admin) against both H2 and live Supabase, and fixed bugs found along the way (including a JDK/Mockito test issue and an admin-navigation gap).
I did a pre-push audit (secret/.gitignore checks, build/lint/test runs) and rewrote the README to document the features, database schema, API endpoints, auth model, dev/prod setup, and limitations.
