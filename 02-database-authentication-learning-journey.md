# My Learning Journey - Spring Security (Database Authentication)

In my previous project, I learned the basics of Spring Security. The application authenticated users using credentials configured in `application.properties`. Although this helped me understand how Spring Security works, the real-world applications do not store usernames and passwords inside configuration files.

The next logical step was to authenticate users from a database.

---

## Step 1 - In-Memory Authentication

Before connecting a database, I first learned another approach provided by Spring Security.

I implemented a `UserDetailsService` Bean and returned an `InMemoryUserDetailsManager`.

```java
@Bean
public UserDetailsService userDetailsService() {
    ...
    return new InMemoryUserDetailsManager(user1, user2);
}
```

Here, I created two users using hard-coded values.

This helped me understand that Spring Security does not necessarily require a database. As long as it receives user information through a `UserDetailsService`, it can authenticate users.

However, this approach is only suitable for learning or small demo applications because the user information disappears whenever the application stops.

---

## Step 2 - Why Database Authentication?

Hard-coded users are not practical.

In real applications:

* User information is stored in a database.
* New users can register.
* Passwords can change.
* User roles can be updated.

Therefore, instead of returning an `InMemoryUserDetailsManager`, I commented that code and decided to fetch user details from a PostgreSQL database.

---

## Step 3 - Setting up PostgreSQL

To store users permanently, I installed:

* PostgreSQL
* pgAdmin 4

I created a database and then created a table named:

```
users
```

Finally, I inserted a few sample users into the table.

Unlike the previous implementation, authentication would now use database records instead of hard-coded values.

---

## Step 4 - Configuring Database Access

To communicate with PostgreSQL, I added the required dependencies.

* Spring Data JPA
* PostgreSQL Driver

I also configured the database connection inside `application.properties`.

Once the application started successfully, Spring Boot was able to connect to PostgreSQL.

---

## Step 5 - Creating Entity and Repository

Next, I created a model class:

```
Users
```

This class represents one row of the `users` table.

To fetch users from the database, I created:

```
UsersRepo
```

which extends `JpaRepository`.

I also added a custom query method:

```java
findByUsername(...)
```

This repository became responsible for retrieving user information whenever authentication is required.

---

## Step 6 - Implementing UserDetailsService

Spring Security expects a Bean that implements the `UserDetailsService` interface.

So I created:

```
MyUserDetailsService
```

and implemented:

```java
loadUserByUsername(String username)
```

Whenever a user attempts to log in, Spring Security automatically calls this method.

Inside this method, I used `UsersRepo` to fetch the user from the PostgreSQL database.

At this stage, I realized that the responsibility of `UserDetailsService` is only to **load user details**. It does not verify passwords.

---

## Step 7 - Understanding UserDetails

While implementing `loadUserByUsername()`, I discovered another interface named:

```
UserDetails
```

Although I already had my `Users` entity, Spring Security could not work with it directly.

Instead, it expected an object implementing `UserDetails`.

To solve this, I created another class:

```
UserPrincipal
```

which wraps my `Users` entity and exposes the methods required by Spring Security, such as username, password, and authorities.

This helped me understand that `UserPrincipal` acts as an adapter between my database entity and Spring Security.

---

## Step 8 - Configuring AuthenticationProvider

Earlier, Spring Security authenticated users using the in-memory implementation.

Now I wanted Spring Security to authenticate users using my own database logic.

For this, I configured a `DaoAuthenticationProvider`.

Inside it, I configured:

* `UserDetailsService`
* `PasswordEncoder`

This provider became responsible for authenticating users.

Whenever authentication is required, it asks my `MyUserDetailsService` to load the user and then verifies the supplied password.

---

## Step 9 - Password Encoder

For learning purposes, I configured:

```java
NoOpPasswordEncoder
```

This compares passwords as plain text.

Although this is useful for understanding the authentication flow, I learned that production applications should never store plain-text passwords.

Instead, Spring Security commonly uses:

```
BCryptPasswordEncoder
```

which stores encrypted password hashes.

---

## Step 10 - Understanding the Complete Authentication Flow

After implementing all the components, the authentication process became much clearer to me.

```
Client
        │
Username + Password
        │
        ▼
Spring Security Filter Chain
        │
        ▼
AuthenticationManager
        │
        ▼
DaoAuthenticationProvider
        │
        ▼
MyUserDetailsService
        │
loadUserByUsername()
        │
        ▼
UsersRepo
        │
        ▼
PostgreSQL Database
        │
        ▼
Users Entity
        │
        ▼
UserPrincipal
        │
        ▼
DaoAuthenticationProvider
        │
Password Verification
        │
        ▼
Controller
```

# 🔐 Authentication Flow Explained

The following diagram shows the overall authentication flow.

```text
Client
        │
Username + Password
        │
        ▼
Spring Security Filter Chain
        │
        ▼
AuthenticationManager
        │
        ▼
DaoAuthenticationProvider
        │
        ▼
MyUserDetailsService
        │
loadUserByUsername()
        │
        ▼
UsersRepo
        │
        ▼
PostgreSQL Database
        │
        ▼
Users Entity
        │
        ▼
UserPrincipal
(UserDetails)
        │
        ▼
DaoAuthenticationProvider
        │
Password Verification
        │
        ▼
Controller
```

---

## 📖 Step-by-Step Story

### 🟢 Step 1 - Client Sends Request

A client calls a secured API, for example:

```http
GET /students
```

Since the API is protected, the client also sends a **username** and **password**.

---

### 🛡️ Step 2 - Spring Security Intercepts the Request

Before the request reaches the controller, it is intercepted by the **Spring Security Filter Chain**.

Spring Security checks whether the requested endpoint requires authentication.

Since the endpoint is secured, the request **cannot** directly reach the controller.

---

### 🔍 Step 3 - Authentication Starts

Spring Security delegates the authentication process to the configured:

```text
DaoAuthenticationProvider
```

At this point, the provider has the username and password sent by the client.

However, it does **not** know where user information is stored.

It simply follows one rule:

> **"If I need user details, I must ask a `UserDetailsService`."**

---

### 👤 Step 4 - Loading User Details

Since I configured:

```java
provider.setUserDetailsService(userDetailsService);
```

Spring injects my implementation:

```text
MyUserDetailsService
```

The provider then calls:

```java
loadUserByUsername(username)
```

Its responsibility is **only to fetch user details**.

It does **not** verify passwords.

---

### 🗄️ Step 5 - Fetching User from Database

Inside `loadUserByUsername()`,

I use:

```text
UsersRepo
```

to query PostgreSQL.

The repository searches the **users** table and returns a:

```text
Users
```

entity.

---

### 🔄 Step 6 - Converting Entity into UserDetails

Spring Security cannot directly understand my `Users` entity.

Instead, it expects an object implementing:

```text
UserDetails
```

Therefore, I created:

```text
UserPrincipal
```

which wraps the `Users` entity.

Now Spring Security receives user information in the format it understands.

---

### ✅ Step 7 - Password Verification

`MyUserDetailsService` returns the `UserPrincipal` object back to the:

```text
DaoAuthenticationProvider
```

Now the provider has everything it needs.

It compares:

- Password entered by the client
- Password stored inside `UserPrincipal`

using the configured:

```text
PasswordEncoder
```

---

### 🎯 Step 8 - Authentication Result

If both passwords match,

✅ Authentication succeeds.

The request is allowed to continue to the controller.

Otherwise,

❌ Spring Security rejects the request and returns an authentication error.

---

# 📌 Responsibility of Each Component

| 🧩 Component | 🎯 Responsibility |
|-------------|-------------------|
| **Spring Security Filter Chain** | Intercepts every secured request before it reaches the controller. |
| **AuthenticationManager** | Delegates authentication to the configured `AuthenticationProvider`. |
| **DaoAuthenticationProvider** | Authenticates users by loading user details and verifying passwords. |
| **MyUserDetailsService** | Loads user details based on the supplied username. |
| **UsersRepo** | Fetches user information from the PostgreSQL database. |
| **Users Entity** | Represents a row from the `users` table. |
| **UserPrincipal** | Wraps the `Users` entity and converts it into a `UserDetails` object understood by Spring Security. |
| **PasswordEncoder** | Compares the entered password with the stored password. |
| **Controller** | Executes the requested API only after successful authentication. |

---

# 💡 Key Points to Remember

> 🔹 `UserDetailsService` **loads** user details.

> 🔹 `DaoAuthenticationProvider` **authenticates** users.

> 🔹 `UsersRepo` **fetches** data from the database.

> 🔹 `UserPrincipal` **adapts** the `Users` entity into a `UserDetails` object.

> 🔹 `PasswordEncoder` **verifies** passwords.

> 🔹 Only after successful authentication does the request reach the controller.

---

# 🧠 One-Line Summary

> **Spring Security intercepts every secured request, asks `UserDetailsService` to load the user from the database, verifies the password using `DaoAuthenticationProvider` and `PasswordEncoder`, and only then allows the request to reach the controller.**

Understanding this flow helped me connect all the interfaces and classes that initially seemed confusing.

---

## What I Learned

Through this project, I learned:

* Why `InMemoryUserDetailsManager` is useful for learning.
* Why production applications authenticate users from a database.
* How `UserDetailsService` loads user information.
* How `JpaRepository` is used to fetch users.
* Why Spring Security requires a `UserDetails` implementation.
* How `UserPrincipal` acts as a wrapper around the database entity.
* The responsibility of `DaoAuthenticationProvider`.
* The purpose of `PasswordEncoder`.
* The complete authentication flow used by Spring Security.

---

## Next Step

This project authenticates users from a database using Spring Security.

The next step is to understand **JWT (JSON Web Tokens)** so that authentication becomes completely stateless and suitable for modern REST APIs.
