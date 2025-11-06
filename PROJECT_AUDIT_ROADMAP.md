# Coffee House Blog Platform - Audit Presentation Roadmap

## Table of Contents
1. [Project Overview](#1-project-overview)
2. [Technology Stack](#2-technology-stack)
3. [System Architecture](#3-system-architecture)
4. [Database Design](#4-database-design)
5. [Security Implementation](#5-security-implementation)
6. [Backend Architecture](#6-backend-architecture)
7. [Frontend Architecture](#7-frontend-architecture)
8. [Core Features](#8-core-features)
9. [API Endpoints](#9-api-endpoints)
10. [File Management System](#10-file-management-system)
11. [Advanced Concepts](#11-advanced-concepts)
12. [Testing & Quality Assurance](#12-testing--quality-assurance)

---

## 1. Project Overview

### What to Explain:
- **Project Name**: Coffee House - A Social Blogging Platform
- **Purpose**: Full-stack social media blog application where users can create posts, interact with content, follow users, and manage their profiles
- **Type**: Monorepo with separate Backend (Spring Boot) and Frontend (Angular)

### Key Points:
- Real-world social media features (posts, comments, likes, follows, notifications)
- Admin panel for content moderation
- Responsive design with modern UI/UX
- Production-ready security features

---

## 2. Technology Stack

### Backend Technologies:
- **Framework**: Spring Boot 3.5.5
- **Language**: Java 21
- **Build Tool**: Gradle
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security + JWT
- **Password Encryption**: BCrypt

### Frontend Technologies:
- **Framework**: Angular 20.2.0
- **Language**: TypeScript 5.9.2
- **UI Library**: Bootstrap 5.3.8 + Bootstrap Icons
- **HTTP Client**: RxJS for reactive programming
- **JWT Handling**: @auth0/angular-jwt
- **Server-Side Rendering**: Angular SSR (Server-Side Rendering)

### Additional Tools:
- **Testing**: Jasmine + Karma (Frontend), JUnit (Backend)
- **Development**: Spring Boot DevTools
- **API Documentation**: RESTful principles

---

## 3. System Architecture

### Architecture Pattern:
**Three-Tier Architecture**

```
┌─────────────────────────────────────┐
│     Frontend (Angular SPA)          │
│  - Components                       │
│  - Services                         │
│  - Guards & Interceptors            │
└──────────────┬──────────────────────┘
               │ HTTP/REST API
               │ (JSON + JWT)
┌──────────────▼──────────────────────┐
│     Backend (Spring Boot)           │
│  ┌──────────────────────────────┐   │
│  │   Controller Layer           │   │
│  └──────────┬───────────────────┘   │
│  ┌──────────▼───────────────────┐   │
│  │   Service Layer (Business)   │   │
│  └──────────┬───────────────────┘   │
│  ┌──────────▼───────────────────┐   │
│  │   Repository Layer (DAO)     │   │
│  └──────────┬───────────────────┘   │
└─────────────┼───────────────────────┘
              │ JPA/Hibernate
┌─────────────▼───────────────────────┐
│     PostgreSQL Database             │
└─────────────────────────────────────┘
```

### Communication Flow:
1. User interacts with Angular frontend
2. HTTP requests intercepted (JWT token added)
3. Spring Security filters validate JWT
4. Controller receives request
5. Service layer processes business logic
6. Repository layer handles database operations
7. Response sent back through the layers

---

## 4. Database Design

### Entity Relationship Model:

#### Core Entities:
1. **User** ([User.java](backend/src/main/java/com/blog/entity/User.java))
   - Fields: id, email, firstName, lastName, userName, password, role, profileImagePath, status, uuid, createdAt
   - Relationships: One-to-Many with Posts, Comments, Likes, Reports, Follows

2. **Post** ([Post.java](backend/src/main/java/com/blog/entity/Post.java))
   - Fields: id, title, content, mediaPaths, createdAt, uuid, status
   - Relationships: Many-to-One with User, One-to-Many with Comments, Likes, Reports
   - Special: Composite index on (user_id, created_at, id) for performance

3. **Comment** ([Comment.java](backend/src/main/java/com/blog/entity/Comment.java))
   - Fields: id, content, createdAt, uuid
   - Relationships: Many-to-One with User and Post

4. **Like** ([Like.java](backend/src/main/java/com/blog/entity/Like.java))
   - Fields: id, createdAt, uuid
   - Relationships: Many-to-One with User and Post

5. **Follow** ([Follow.java](backend/src/main/java/com/blog/entity/Follow.java))
   - Fields: id, createdAt
   - Relationships: Self-referencing - tracks follower/following relationships

6. **Notification** ([Notification.java](backend/src/main/java/com/blog/entity/Notification.java))
   - Fields: id, type, content, createdAt, read status
   - Purpose: Real-time user notifications for likes, comments, follows

7. **Report** ([Report.java](backend/src/main/java/com/blog/entity/Report.java))
   - Fields: id, reason, status, createdAt
   - Purpose: Content moderation system

### Database Concepts to Explain:
- **JPA Annotations**: @Entity, @Table, @Id, @GeneratedValue
- **Relationships**: @OneToMany, @ManyToOne, @JoinColumn
- **Cascade Types**: CascadeType.ALL for automatic operations
- **Indexing**: Custom index for query optimization
- **UUID Usage**: Unique identifiers for external references
- **JsonIgnore**: Prevents circular references in JSON serialization

---

## 5. Security Implementation

### Authentication & Authorization:

#### JWT (JSON Web Token) Architecture:
Located in [config](backend/src/main/java/com/blog/config/)

1. **JwtUtils** ([JwtUtils.java](backend/src/main/java/com/blog/config/JwtUtils.java))
   - Token generation
   - Token validation
   - Claims extraction
   - HMAC-SHA256 signing algorithm

2. **JwtAuthenticationFilter** ([JwtAuthenticationFilter.java](backend/src/main/java/com/blog/config/JwtAuthenticationFilter.java))
   - Intercepts every request
   - Extracts token from Authorization header
   - Validates token and sets SecurityContext

3. **JwtAuthEntryPoint** ([JwtAuthEntryPoint.java](backend/src/main/java/com/blog/config/JwtAuthEntryPoint.java))
   - Handles authentication failures
   - Returns 401 Unauthorized

4. **SecurityConfig** ([SecurityConfig.java](backend/src/main/java/com/blog/config/SecurityConfig.java))
   - Configures security filter chain
   - CORS configuration
   - Stateless session management
   - Role-based access control (RBAC)

### Security Features:
- **Password Encryption**: BCrypt with salt
- **Stateless Sessions**: No server-side session storage
- **CORS Protection**: Configured for localhost:4200
- **CSRF Disabled**: Safe for stateless JWT authentication
- **Role-Based Access**: Admin-only endpoints protected
- **Input Validation**: Sanitization services
- **File Validation**: Security checks on uploads

### Additional Security Services:
- **FileValidationService** ([FileValidationService.java](backend/src/main/java/com/blog/security/FileValidationService.java))
  - Validates file types and sizes
  - Prevents malicious uploads

- **InputSanitizationService** ([InputSanitizationService.java](backend/src/main/java/com/blog/security/InputSanitizationService.java))
  - Sanitizes user input
  - Prevents XSS and SQL injection

---

## 6. Backend Architecture

### Layer-by-Layer Breakdown:

#### A. Controller Layer
Located: [backend/src/main/java/com/blog/controller](backend/src/main/java/com/blog/controller/)

1. **AuthController** ([AuthController.java](backend/src/main/java/com/blog/controller/AuthController.java))
   - User registration
   - Login with JWT generation
   - Token refresh

2. **PostController** ([PostController.java](backend/src/main/java/com/blog/controller/PostController.java))
   - Create, read, update, delete posts
   - Like/unlike posts
   - Comment on posts
   - Upload media files

3. **UserController** ([UserController.java](backend/src/main/java/com/blog/controller/UserController.java))
   - Profile management
   - Follow/unfollow users
   - Get user posts and followers

4. **NotificationsController** ([NotificationsController.java](backend/src/main/java/com/blog/controller/NotificationsController.java))
   - Fetch user notifications
   - Mark notifications as read

5. **ReportController** ([ReportController.java](backend/src/main/java/com/blog/controller/ReportController.java))
   - Report posts/users
   - View report status

6. **AdminPannelController** ([AdminPannelController.java](backend/src/main/java/com/blog/controller/AdminPannelController.java))
   - Content moderation
   - User management (ban/unban)
   - Report resolution

#### B. Service Layer
Located: [backend/src/main/java/com/blog/service](backend/src/main/java/com/blog/service/)

**Key Services:**
- **AuthService**: Authentication logic
- **RegistrationService**: User registration with validation
- **PostService**: Post creation and retrieval
- **EditPostService**: Post modification
- **DeletePostService**: Post deletion with cascade
- **LikePostService**: Like/unlike with spam prevention
- **CommentService**: Comment management
- **UsersServices**: User profile operations
- **NotifService**: Notification creation and delivery
- **ReportService**: Report handling
- **AdminService**: Admin operations
- **CustomUserDetailsService**: Spring Security user loading

**Service Layer Concepts:**
- Business logic separation
- Transaction management
- Data validation
- Exception handling
- Spam prevention (like/follow)

#### C. Repository Layer
Located: [backend/src/main/java/com/blog/repository](backend/src/main/java/com/blog/repository/)

**Repositories:**
- UserRepository
- PostRepository
- CommentRepository
- LikesRepository
- FollowRepository
- NotifRepository
- ReportRepository

**Repository Concepts:**
- Spring Data JPA
- Custom query methods
- Derived query names
- @Query annotations
- Pagination support

#### D. Exception Handling
Located: [backend/src/main/java/com/blog/exceptions](backend/src/main/java/com/blog/exceptions/)

**Custom Exceptions:**
- UserNotFoundException
- PostNotFoundException
- UserAlreadyExistException
- InvalidPasswordException
- UserBannedException
- LikeException
- ReportException
- CreateCommentException
- And more...

**GlobalExceptionHandler** ([GlobalExceptionHandler.java](backend/src/main/java/com/blog/exceptions/GlobalExceptionHandler.java))
- Centralized exception handling
- Custom error responses
- HTTP status code mapping

#### E. DTOs (Data Transfer Objects)
Located: [backend/src/main/java/com/blog/dto](backend/src/main/java/com/blog/dto/)

Purpose:
- Separate internal entities from API responses
- Control data exposure
- Validation rules

---

## 7. Frontend Architecture

### Angular Structure:
Located: [frontend/src/app](frontend/src/app/)

#### A. Core Components:

1. **App Component** ([app.ts](frontend/src/app/app.ts))
   - Root component
   - Layout structure
   - Navigation

2. **Authentication Module** ([auth/](frontend/src/app/auth/))
   - Login component
   - Registration component
   - Auth guards
   - JWT interceptor

3. **Home Module** ([home/](frontend/src/app/home/))
   - Feed display
   - Post list
   - Infinite scroll

4. **Post Module** ([post/](frontend/src/app/post/))
   - Post creation
   - Post detail view
   - Comment section

5. **Profile Module** ([profile/](frontend/src/app/profile/))
   - User profile view
   - Edit profile
   - Follow/unfollow functionality

6. **Notification Module** ([notification/](frontend/src/app/notification/))
   - Notification list
   - Real-time updates
   - Mark as read

7. **Admin Panel** ([admin-panel/](frontend/src/app/admin-panel/))
   - User management
   - Content moderation
   - Reports dashboard

8. **Search Bar** ([searchbar/](frontend/src/app/searchbar/))
   - User search
   - Post search

9. **Toast Notifications** ([toast/](frontend/src/app/toast/))
   - Success/error messages
   - User feedback

10. **Error Handling** ([error/](frontend/src/app/error/))
    - 404 page
    - Error boundaries

#### B. Services:
- HTTP services for API calls
- Authentication service
- State management
- Interceptor for JWT

#### C. Guards:
- AuthGuard: Protect authenticated routes
- AdminGuard: Protect admin-only routes

#### D. Interceptor ([interceptor.ts](frontend/src/app/interceptor.ts)):
- Automatically adds JWT to requests
- Handles token refresh
- Error handling

#### E. Routing ([app.routes.ts](frontend/src/app/app.routes.ts)):
- Lazy loading modules
- Route protection
- Nested routes

### Angular Concepts to Explain:
- **Components**: Reusable UI pieces
- **Services**: Business logic and data access
- **Dependency Injection**: Angular's DI system
- **RxJS Observables**: Asynchronous data streams
- **Reactive Forms**: Form handling and validation
- **HttpClient**: HTTP communication
- **Router**: Navigation and routing
- **Guards**: Route protection
- **Interceptors**: HTTP request/response modification

---

## 8. Core Features

### Feature 1: User Authentication
**Flow:**
1. User submits credentials
2. Backend validates with database
3. Password checked with BCrypt
4. JWT token generated and returned
5. Frontend stores token
6. Token sent with every request
7. Backend validates token on each request

**Files Involved:**
- Backend: [AuthController.java](backend/src/main/java/com/blog/controller/AuthController.java), [AuthService.java](backend/src/main/java/com/blog/service/AuthService.java)
- Frontend: auth module, [interceptor.ts](frontend/src/app/interceptor.ts)

### Feature 2: Post Management
**CRUD Operations:**
- **Create**: Upload with media files, validation
- **Read**: Pagination, filtering, user-specific
- **Update**: Edit with history tracking
- **Delete**: Cascade delete (comments, likes)

**Special Features:**
- Multi-image upload
- Content preview
- Edit reason tracking

**Files Involved:**
- Backend: [PostController.java](backend/src/main/java/com/blog/controller/PostController.java), [PostService.java](backend/src/main/java/com/blog/service/PostService.java)
- Frontend: post module

### Feature 3: Social Interactions
**Like System:**
- Toggle like/unlike
- Spam prevention (recent commits show fixes)
- Real-time count updates
- Notification generation

**Comment System:**
- Nested comments support
- Real-time updates
- User mentions

**Follow System:**
- Follow/unfollow users
- Spam prevention
- Follower/following lists
- Activity feed

**Files Involved:**
- [LikePostService.java](backend/src/main/java/com/blog/service/LikePostService.java)
- [CommentService.java](backend/src/main/java/com/blog/service/CommentService.java)
- [UsersServices.java](backend/src/main/java/com/blog/service/UsersServices.java) (for follows)

### Feature 4: Notification System
**Types:**
- New follower
- Post liked
- New comment
- Post reported

**Features:**
- Real-time updates
- Read/unread status
- Clickable to navigate to source

**Files Involved:**
- [NotifService.java](backend/src/main/java/com/blog/service/NotifService.java)
- [NotificationsController.java](backend/src/main/java/com/blog/controller/NotificationsController.java)

### Feature 5: Content Moderation
**Admin Features:**
- View all reports
- Review reported content
- Ban/unban users
- Delete inappropriate content
- Approve/reject reports

**User Features:**
- Report posts
- Report users
- Track report status
- Receive notification on resolution

**Files Involved:**
- [AdminService.java](backend/src/main/java/com/blog/service/AdminService.java)
- [ReportService.java](backend/src/main/java/com/blog/service/ReportService.java)
- [AdminPannelController.java](backend/src/main/java/com/blog/controller/AdminPannelController.java)

### Feature 6: File Upload System
**Capabilities:**
- Multiple file upload
- Image validation
- Size limits (100MB)
- Secure file storage
- Path management

**Security:**
- File type validation
- Size restrictions
- Sanitized file names
- Dedicated upload directory

**Configuration:**
- Max file size: 100MB
- Upload directory: `/backend/uploads/`
- Supported formats: Images (validated)

---

## 9. API Endpoints

### Authentication Endpoints:
```
POST /api/auth/register - User registration
POST /api/auth/login    - User login (returns JWT)
```

### Post Endpoints:
```
GET    /api/posts           - Get all posts (paginated)
POST   /api/posts           - Create new post
GET    /api/posts/{uuid}    - Get specific post
PUT    /api/posts/{uuid}    - Update post
DELETE /api/posts/{uuid}    - Delete post
POST   /api/posts/{uuid}/like    - Like/unlike post
POST   /api/posts/{uuid}/comment - Add comment
```

### User Endpoints:
```
GET    /api/users/me              - Get current user profile
PUT    /api/users/me              - Update profile
GET    /api/users/{uuid}          - Get user by UUID
POST   /api/users/{uuid}/follow   - Follow/unfollow user
GET    /api/users/{uuid}/posts    - Get user's posts
GET    /api/users/{uuid}/followers - Get followers
```

### Notification Endpoints:
```
GET    /api/notifications     - Get user notifications
PUT    /api/notifications/{id}/read - Mark as read
```

### Report Endpoints:
```
POST   /api/reports           - Create report
GET    /api/reports/my        - Get user's reports
```

### Admin Endpoints (Role: ADMIN):
```
GET    /api/admin/reports        - Get all reports
PUT    /api/admin/reports/{id}   - Update report status
POST   /api/admin/users/{id}/ban - Ban user
DELETE /api/admin/posts/{uuid}   - Delete post
```

### Static Files:
```
GET /uploads/**  - Access uploaded media files
```

---

## 10. File Management System

### Upload Architecture:

#### Backend Configuration:
- **Location**: [application.properties](backend/src/main/resources/application.properties)
- **Upload Directory**: `/backend/uploads/`
- **Max File Size**: 100MB
- **Max Request Size**: 100MB

#### Storage Strategy:
1. Files stored locally in filesystem
2. Database stores file paths (comma-separated for multiple files)
3. Unique file names with UUID prefix
4. Organized by entity type (e.g., `uploads/posts/`, `uploads/profiles/`)

#### Security Measures:
- File type validation ([FileValidationService.java](backend/src/main/java/com/blog/security/FileValidationService.java))
- Size restrictions
- Malicious file detection
- Sanitized file names
- Restricted access patterns

#### Serving Files:
- Static resource configuration
- Public access to `/uploads/**`
- Proper MIME types
- CDN-ready structure

---

## 11. Advanced Concepts

### A. Design Patterns Used:

1. **MVC (Model-View-Controller)**
   - Model: Entities
   - View: Angular components
   - Controller: Spring controllers

2. **Repository Pattern**
   - Data access abstraction
   - Spring Data JPA repositories

3. **Service Layer Pattern**
   - Business logic separation
   - Reusable services

4. **DTO Pattern**
   - Data transfer optimization
   - API contract definition

5. **Dependency Injection**
   - Spring IoC container
   - Angular DI system

6. **Interceptor Pattern**
   - JWT injection
   - Request/response modification

7. **Observer Pattern**
   - RxJS Observables
   - Event-driven notifications

### B. Spring Boot Concepts:

1. **Auto-Configuration**
   - Spring Boot starters
   - Convention over configuration

2. **Dependency Management**
   - Gradle build system
   - Dependency versions

3. **JPA/Hibernate**
   - ORM mapping
   - Lazy/Eager loading
   - Cascade operations
   - Entity lifecycle

4. **Spring Security**
   - Filter chain
   - Authentication manager
   - Security context

5. **Bean Management**
   - Component scanning
   - Autowiring
   - Bean lifecycle

6. **Properties Configuration**
   - application.properties
   - Environment-specific configs

### C. Angular Concepts:

1. **Component Architecture**
   - Smart vs Presentational components
   - Component lifecycle hooks
   - Change detection

2. **Reactive Programming**
   - RxJS operators
   - Observable streams
   - Subject patterns

3. **Routing**
   - Lazy loading
   - Route guards
   - Route parameters

4. **HTTP Communication**
   - HttpClient module
   - Interceptors
   - Error handling

5. **State Management**
   - Service-based state
   - Local component state

6. **Server-Side Rendering (SSR)**
   - SEO optimization
   - Initial load performance

### D. Database Optimization:

1. **Indexing**
   - Composite index on Post (user_id, created_at, id)
   - Performance optimization for common queries

2. **Relationship Management**
   - Lazy loading to prevent N+1 queries
   - @JsonIgnore to prevent circular references

3. **Query Optimization**
   - Custom query methods
   - Pagination support
   - Efficient joins

### E. Security Best Practices:

1. **Password Security**
   - BCrypt with salt
   - Never store plain text
   - Minimum strength requirements

2. **Token Security**
   - JWT with expiration (1 hour)
   - HMAC-SHA256 signing
   - Secret key management

3. **Input Validation**
   - Spring Validation annotations
   - Custom sanitization
   - XSS prevention

4. **CORS Configuration**
   - Restricted origins
   - Credential support
   - Allowed methods control

5. **Session Management**
   - Stateless architecture
   - No server-side sessions
   - Token-based authentication

6. **Error Handling**
   - No sensitive data in errors
   - Generic error messages
   - Detailed logging (server-side only)

### F. Recent Bug Fixes (From Git History):

1. **Like Spam Prevention** (commit: d811151)
   - Prevents multiple rapid likes
   - Implements rate limiting
   - Improves user experience

2. **Follow Spam Handling** (commit: d811151)
   - Prevents follow/unfollow spam
   - Debouncing mechanism

3. **Notification Bug** (commit: 640dd1f)
   - Fixed notification delivery
   - Improved notification timing

4. **Reason Preview** (commit: 895b974)
   - Edit reason display
   - Content moderation transparency

5. **Confirmation Break** (commit: a5a589f)
   - Testing confirmation flows
   - User action verification

---

## 12. Testing & Quality Assurance

### Backend Testing:
- **Framework**: JUnit Platform
- **Coverage**: Service layer tests
- **Strategy**: Unit tests for business logic

### Frontend Testing:
- **Framework**: Jasmine + Karma
- **Type**: Unit tests for components/services
- **Runner**: Chrome launcher

### Code Quality:
- **Linting**: TypeScript strict mode
- **Formatting**: Prettier (Frontend)
- **Conventions**: Java naming conventions

---

## Presentation Flow Suggestion

### Introduction (5 minutes):
1. Project overview and purpose
2. Technology stack overview
3. Show live demo of key features

### Architecture Deep Dive (10 minutes):
4. Three-tier architecture explanation
5. Database schema with ER diagram
6. Request/response flow diagram

### Backend Explanation (15 minutes):
7. Spring Boot structure
8. Security implementation (JWT flow)
9. Service layer design
10. Repository pattern
11. Exception handling

### Frontend Explanation (10 minutes):
12. Angular architecture
13. Component structure
14. Routing and guards
15. HTTP interceptor

### Feature Walkthrough (15 minutes):
16. User authentication flow
17. Post creation with file upload
18. Social interactions (like, comment, follow)
19. Notification system
20. Admin moderation panel

### Advanced Topics (10 minutes):
21. Design patterns used
22. Security best practices
23. Performance optimizations
24. Bug fixes and improvements

### Conclusion (5 minutes):
25. Challenges faced
26. Lessons learned
27. Future improvements
28. Q&A preparation

---

## Key Talking Points for Each Section

### When Explaining Security:
- "I implemented JWT-based stateless authentication"
- "Passwords are encrypted using BCrypt with salt"
- "Role-based access control protects admin endpoints"
- "Input sanitization prevents XSS and SQL injection"
- "File upload validation prevents malicious files"

### When Explaining Database:
- "I used JPA annotations for ORM mapping"
- "Composite indexes optimize query performance"
- "Cascade operations maintain data integrity"
- "UUID ensures unique external references"
- "JsonIgnore prevents circular reference errors"

### When Explaining Architecture:
- "Three-tier architecture separates concerns"
- "Service layer contains business logic"
- "Repository pattern abstracts data access"
- "DTOs control API contracts"
- "Dependency injection promotes modularity"

### When Explaining Features:
- "Real-time notifications improve user engagement"
- "Spam prevention on likes and follows"
- "Admin panel enables content moderation"
- "Multi-image upload with validation"
- "Edit tracking maintains transparency"

---

## Visual Aids to Prepare

1. **Architecture Diagram**: Show three-tier structure
2. **Database ER Diagram**: Entity relationships
3. **Authentication Flow**: JWT lifecycle
4. **Request Flow**: From frontend to database
5. **Component Tree**: Angular component hierarchy
6. **API Endpoint Map**: All available endpoints
7. **File Upload Flow**: Security and storage

---

## Common Questions to Prepare For

1. **Why did you choose Spring Boot and Angular?**
   - Industry standard, robust ecosystem, enterprise-ready

2. **How do you handle authentication?**
   - JWT tokens, stateless, role-based access control

3. **How do you prevent spam (likes, follows)?**
   - Recent commits show spam prevention implementation

4. **How do you handle file uploads securely?**
   - Validation service, size limits, type checking

5. **How is the database structured?**
   - Normalized schema, indexed, cascade operations

6. **What about scalability?**
   - Stateless design, pagination, indexing, lazy loading

7. **How do you handle errors?**
   - Global exception handler, custom exceptions, meaningful messages

8. **What security measures are in place?**
   - JWT, BCrypt, input sanitization, CORS, file validation

9. **How do notifications work?**
   - Service layer creates notifications on events, polled by frontend

10. **What's your deployment strategy?**
    - Separate backend/frontend deployments, environment configs

---

## Project Statistics to Mention

- **Backend**:
  - 6 Controllers
  - 13+ Services
  - 7 Repositories
  - 7 Entities
  - 15+ Custom Exceptions
  - Security layer with 3 filters

- **Frontend**:
  - 10+ Major modules
  - Component-based architecture
  - Interceptor for HTTP
  - Multiple guards for routing

- **Database**:
  - 7 Main tables
  - Multiple relationships
  - Custom indexes
  - UUID system

---

## Repository Information

- **Git Repository**: Active development with meaningful commits
- **Recent Work**: Bug fixes for like/follow spam, notifications
- **Commit Quality**: Clear messages, focused changes
- **Branch**: Main branch with regular updates

---

Good luck with your audit presentation! This roadmap should help you explain every aspect of your project comprehensively.
