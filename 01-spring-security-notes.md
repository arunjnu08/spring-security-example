# My Learning Journey - Spring Security

This project was my first introduction to **Spring Security**. Instead of directly learning advanced topics, I learned it step by step to understand why each feature exists.

---

## Step 1 - Adding Spring Security

Initially, I had a very simple Spring Boot application with only one controller (`HelloController`).

I added the following dependency in `pom.xml`.

```xml
spring-boot-starter-security
```

After restarting the application, something unexpected happened.

Whenever I opened:

```
http://localhost:8080
```

instead of seeing my response, Spring Boot redirected me to a **login page**.

This helped me understand that **simply adding the Spring Security dependency automatically secures all endpoints.**

Spring Boot also generated:

- Username: `user`
- A random password

The password was printed in the application logs every time the application started.

---

## Step 2 - Custom Username and Password

Instead of using the generated credentials, I configured my own credentials inside `application.properties`.

```properties
spring.security.user.name=arun
spring.security.user.password=kumar
```

Now I observed:

- Username remained fixed.
- Password remained fixed.
- Spring Boot no longer generated or printed a random password.

This showed me that Spring Boot provides default credentials only when I don't configure my own.

---

## Step 3 - Protecting REST APIs

To understand how Spring Security works with REST APIs, I created another controller:

```
StudentController
```

along with a simple `Student` class.

Initially, I exposed:

```
GET /students
```

which simply returned a hard-coded list of students.

Even though the API contained no database or business logic, Spring Security still required the user to authenticate before accessing it.

This helped me understand that Spring Security protects **all endpoints**, not just login APIs.

---

## Step 4 - Understanding CSRF Protection

Next, I added another endpoint.

```
POST /students
```

When I tried calling it from Postman, it failed.

After some investigation, I learned that Spring Security enables **CSRF (Cross-Site Request Forgery) protection** by default.

GET requests worked after authentication, but POST requests additionally required a valid CSRF token.

---

## Step 5 - Generating CSRF Token

To understand CSRF better, I created another endpoint.

```
GET /csrf-token
```

This endpoint returned the current CSRF token.

I copied the returned token and passed it as:

```
X-CSRF-TOKEN
```

in the request header while calling:

```
POST /students
```

Now the POST request worked successfully.

This helped me understand why CSRF protection exists and how Spring Security validates requests that modify data.

---

## Step 6 - Creating Custom Security Configuration

Next, I wanted to customize Spring Security.

For this, I created:

```
SecurityConfig
```

and defined my own:

```java
SecurityFilterChain
```

Once I provided this Bean, Spring Boot's default security configuration was overridden, and I became responsible for configuring security myself.

Inside this class, I configured:

- Authorization
- HTTP Basic Authentication
- Stateless Session Management
- CSRF

---

## Step 7 - Disabling CSRF

Since this project exposes REST APIs, I disabled CSRF protection.

```java
http.csrf(AbstractHttpConfigurer::disable);
```

After disabling CSRF:

- POST requests no longer required the CSRF token.
- Postman requests became much simpler.

This also helped me understand that REST APIs commonly disable CSRF because they usually use token-based authentication instead of browser sessions.

---

## Step 8 - HTTP Basic Authentication

I enabled HTTP Basic Authentication.

```java
http.httpBasic(Customizer.withDefaults());
```

Instead of using the login page, Postman now sent:

- Username
- Password

inside the HTTP Authorization header.

Spring Security authenticated every request using these credentials.

---

## Step 9 - Stateless Session

Finally, I configured:

```java
SessionCreationPolicy.STATELESS
```

This means Spring Security no longer creates or maintains an HTTP Session.

Every request must contain authentication information.

This approach is commonly used in REST APIs because each request is independent.

---

## What I Learned

Through this project, I learned:

- Adding Spring Security secures all endpoints automatically.
- Spring Boot provides a default login page.
- Default credentials can be replaced using `application.properties`.
- Spring Security protects REST APIs.
- CSRF protection is enabled by default.
- POST requests require a valid CSRF token.
- CSRF can be disabled for REST APIs.
- `SecurityFilterChain` allows complete customization of Spring Security.
- HTTP Basic Authentication authenticates requests using the Authorization header.
- Stateless session management is commonly used in REST APIs.

---

## Next Step

This project authenticated users using credentials configured in `application.properties`.

The next step is to authenticate users from a **database**, which is how most real-world applications manage users and roles.