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
