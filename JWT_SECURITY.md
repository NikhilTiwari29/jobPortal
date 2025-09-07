# 🔐 JWT Security Documentation - Job Portal Project

This document explains how **JWT-based authentication** is implemented in the `jobPortal` project.  
It covers the **authentication flow**, the **purpose of each class**, and the **recommended order to create them**.

---

## 🔑 JWT Authentication Flow

1. **User Login**
    - User sends credentials to `/users/login`.
    - If valid, the server generates a **JWT token** and returns it.

2. **Client Stores Token**
    - The client (e.g., frontend) stores this token (in `localStorage` or an HTTP-only cookie).

3. **Client Sends Token on Requests**
    - For each request to a protected endpoint, the client attaches the token in the header:
      ```
      Authorization: Bearer <token>
      ```

4. **JwtAuthFilter Validates Token**
    - Extracts and validates the token.
    - If valid → sets the authenticated user in Spring Security’s `SecurityContext`.
    - If invalid/expired → request is blocked.

5. **Spring Security Authorization**
    - Uses the authenticated user + roles to decide whether the request is allowed.

---

## 🏗️ Classes & Responsibilities

### 1. `SecurityConfig`
- Defines security rules:
    - Public endpoints (`/users/register`, `/users/login`).
    - Protected endpoints (all others).
- Registers the `JwtAuthFilter`.
- Configures exception handling (`JwtAuthenticationEntryPoint`).
- Sets session as **stateless**.

👉 *Think of it as the security "gatekeeper".*

---

### 2. `ApplicationConfig`
- Defines beans like:
    - `PasswordEncoder` (for hashing passwords).
    - `AuthenticationManager` (to process login attempts).

👉 *Think of it as the "toolbox" for authentication-related beans.*

---

### 3. `JwtService`
- Handles **all token operations**:
    - Generate tokens.
    - Extract claims (username, roles).
    - Validate tokens.

👉 *Think of it as the "token factory + validator".*

---

### 4. `JwtAuthFilter`
- Runs **on every request**.
- Extracts token from `Authorization` header.
- Uses `JwtService` to validate it.
- If valid → sets authentication in `SecurityContext`.

👉 *Think of it as the "bouncer" that checks tickets before entry.*

---

### 5. `JwtAuthenticationEntryPoint`
- Handles unauthorized requests.
- Sends a **401 Unauthorized** response with a JSON error message.

👉 *Think of it as the "error handler" when someone tries to enter without a valid ticket.*

---

### 6. `JwtUserDetailService`
- Implements Spring Security’s `UserDetailsService`.
- Loads user data (username, password, roles) from the database.
- Used during token validation to confirm the user still exists.

👉 *Think of it as the "bridge" between our database and Spring Security.*

---

### 7. `JwtUserDetails` (Optional)
- Custom implementation of `UserDetails`.
- Wraps our `User` entity to adapt it to Spring Security.
- Alternative: make the `User` entity itself implement `UserDetails`.

👉 *Think of it as the "adapter" between our user model and Spring Security.*

---

## 📌 Order to Create Classes

When setting up JWT security, follow this order:

1. **JwtService** → token generation & validation.
2. **JwtUserDetailService** (and optionally `JwtUserDetails`) → load users from DB.
3. **JwtAuthFilter** → validate token on each request.
4. **JwtAuthenticationEntryPoint** → handle unauthorized requests.
5. **SecurityConfig** → wire everything together.
6. **ApplicationConfig** → define reusable beans.

---

## 📊 Visual Flow

```text
 User → [Login] → JwtService (create token)
        ↓
   (Token returned)
        ↓
   Client stores token
        ↓
   Client → [Protected API Request with JWT]
        ↓
   JwtAuthFilter → JwtService (validate) → JwtUserDetailService (load user)
        ↓
   SecurityContext (authenticated user)
        ↓
   Controller executes → Response
