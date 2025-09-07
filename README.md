# 🏢 Job Portal Backend API

A **full-featured Job Portal backend** built with **Spring Boot**, implementing **JWT authentication**, **OTP verification**, **user profiles**, **job management**, and **notifications**.  
Designed to demonstrate **real-world backend development skills**, **security best practices**, and **clean architecture**.

---

## ✨ Features

### User Management
- Register new users
- Login with JWT authentication
- Change password with notification

### Profile Management
- Get & update user profile
- Manage skills, experiences, certifications
- Save jobs for later

### Job Management
- Post single or multiple jobs
- View all jobs or jobs posted by a specific user
- Apply to jobs and manage application status

### Notifications
- Send notifications to users
- View unread notifications
- Mark notifications as read

### Authentication & Security
- JWT-based authentication for all protected APIs
- OTP-based email verification
- Password hashing using BCrypt

### Testing
- Unit tests (Service & Repository layers)
- Integration tests for Controllers
- JUnit 5 + Mockito

---

## ⚙️ Tech Stack

| Layer | Technology |
|-------|------------|
| Backend Framework | Spring Boot |
| Security | Spring Security, JWT, BCrypt, OTP |
| Database | MySQL |
| ORM | Spring Data JPA |
| Mapping | ModelMapper |
| Email Service | JavaMailSender |
| Testing | JUnit 5, Mockito |
| Logging | Lombok + SLF4J |
| Build Tool | Gradle (Kotlin DSL) |

---

## 🏗️ Architecture Overview

```
Client (React / Angular / Postman)
        |
        v
   [Controllers]  --> handle HTTP requests, validation
        |
        v
   [Services] --> business logic, security, notifications
        |
        v
   [Repositories] --> JPA + MySQL persistence
        |
        v
     Database
```

- **Controllers**: Handle API requests & validation  
- **Services**: Implement business logic, JWT & OTP handling  
- **Repositories**: Interface with database using Spring Data JPA  
- **Notifications**: Service layer integration for email or in-app alerts  

---

## 🔐 Security

- **JWT-based authentication** for all protected endpoints
- **Public endpoints**:  
  ```
  /users/register
  /users/login
  ```
- **Protected endpoints**: All others
- **Send JWT token in header**:  
  ```
  Authorization: Bearer <your_jwt_token>
  ```

> For detailed JWT flow & class responsibilities, see JWT_SECURITY.md

---

## 🚀 Setup & Installation

1. **Clone the repository**

```bash
git clone https://github.com/NikhilTiwari29/jobPortal.git
cd jobPortal
```

2. **Configure database & JWT in `application.properties`**

```properties
# Database Configuration
# ===============================
#   DATASOURCE (MySQL + HikariCP)
# ===============================
spring.datasource.url=jdbc:mysql://localhost:3306/job_portal?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ===============================
#   JPA / HIBERNATE
# ===============================
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# ===============================
#   FLYWAY (Database Migrations)
# ===============================
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration

# ===============================
#   LOGGING
# ===============================
logging.level.root=INFO
logging.level.org.hibernate.SQL=DEBUG

# ==========================
# Spring Mail Configuration
# ==========================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL
spring.mail.password=${GMAIL_APP_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.debug=true
```

3. **Build and run**

```bash
./gradlew bootRun
```

4. **Access APIs** at `http://localhost:8080`

---

## 📡 API Endpoints (Summary)

<details>
<summary>1️⃣ User Module</summary>

| Endpoint | Method | Description |
|----------|--------|-------------|
| /users/register | POST | Register a new user |
| /users/login | POST | Login and get JWT token |
| /users/change-password | POST | Change password (requires JWT) |

</details>

<details>
<summary>2️⃣ Profile Module</summary>

| Endpoint | Method | Description |
|----------|--------|-------------|
| /profile/{id} | GET | Fetch profile by user id |
| /profile/update/{id} | PUT | Update profile |
| /profile/{profileId}/save-job/{jobId} | POST | Save a job for a profile |

</details>

<details>
<summary>3️⃣ Job Module</summary>

| Endpoint | Method | Description |
|----------|--------|-------------|
| /jobs/post | POST | Post a single job |
| /jobs/postAll | POST | Post multiple jobs |
| /jobs/getAll | GET | Fetch all jobs |
| /jobs/{id} | GET | Fetch job by id |
| /jobs/apply/{id} | POST | Apply to a job |
| /jobs/postedBy/{id} | GET | Jobs posted by a user |
| /jobs/history/{id}/{status} | GET | Application history by status |
| /jobs/changeAppStatus | POST | Change application status |

</details>

<details>
<summary>4️⃣ Notification Module</summary>

| Endpoint | Method | Description |
|----------|--------|-------------|
| /notification/send | POST | Send notification |
| /notification/{userId} | GET | Fetch unread notifications |
| /notification/read/{id} | PUT | Mark notification as read |

</details>

<details>
<summary>5️⃣ Auth (OTP) Module</summary>

| Endpoint | Method | Description |
|----------|--------|-------------|
| /auth/send-otp | POST | Send OTP to email |
| /auth/verify/otp | POST | Verify OTP |

</details>

---

## 🧪 Testing

- **Unit Tests**: Service & Repository layers using **JUnit 5 + Mockito**  
- **Integration Tests**: Controller endpoints using **Spring Boot Test**  

**Run all tests:**

```bash
./gradlew test
```

