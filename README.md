# Spring Boot Security Demo

> Learning Project: Built while learning Spring Boot Security before moving to database authentication and JWT.

## Overview

This project demonstrates the fundamentals of **Spring Security** using a simple Spring Boot REST application.

The project starts with Spring Boot's default security configuration and gradually customizes it to understand how Spring Security works internally.

The application exposes simple REST APIs and secures them using authentication, CSRF protection, HTTP Basic Authentication, and a custom `SecurityFilterChain`.

---

## Technologies Used

* Java 17
* Spring Boot
* Spring Security
* Maven
* Bruno (API Testing)

---

## Features

* Default Spring Security Login
* Custom Username & Password
* Secured REST APIs
* CSRF Token Generation
* POST Request Protection
* Custom `SecurityFilterChain`
* HTTP Basic Authentication
* Stateless Session Management

---

## Project Structure

```
src
 ├── controller
 │      ├── HelloController
 │      └── StudentController
 │
 ├── model
 │      └── Student
 │
 └── config
        └── SecurityConfig
```

---

## Concepts Covered

### Spring Security

* Adding `spring-boot-starter-security`
* Default login page
* Default generated password
* Custom username and password

### REST API Security

* Securing GET and POST endpoints
* Authentication
* Authorization basics

### CSRF Protection

* Default CSRF behaviour
* Generating CSRF token
* Calling POST APIs using CSRF token
* Disabling CSRF for REST APIs

### Custom Security Configuration

* `SecurityFilterChain`
* `HttpSecurity`
* `authorizeHttpRequests()`
* `httpBasic()`
* `sessionManagement()`
* `SessionCreationPolicy.STATELESS`

---

## Request Flow

```
Client
    │
    ▼
Spring Security Filter Chain
    │
Authentication
    │
Controller
    │
Response
```

---

## APIs

| Method | Endpoint      | Description                |
| ------ | ------------- | -------------------------- |
| GET    | `/`           | Sample Hello endpoint      |
| GET    | `/students`   | Returns a list of students |
| POST   | `/students`   | Adds a student (secured)   |
| GET    | `/csrf-token` | Returns CSRF token         |

---

## What I Learned

Through this project I learned:

* How Spring Boot automatically secures applications.
* How the default login mechanism works.
* How to configure custom credentials.
* Why CSRF protection exists.
* How to call secured POST APIs.
* How to customize Spring Security using `SecurityFilterChain`.
* How HTTP Basic Authentication works.
* Why REST APIs are usually stateless.

---

## Future Improvements

The next version of this project will include:

* Database Authentication
* UserDetailsService
* BCrypt Password Encoding
* JWT Authentication
* Role-Based Authorization

---

## Learning Notes

Detailed step-by-step learning notes are available in **NOTES.md**, where each concept is explained as it was learned during the project.
