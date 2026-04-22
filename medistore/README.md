# MediStore — Component 01: User Management
### Online Medical Store Management System · Spring Boot

---

## 📦 Project Structure

```
medistore/
├── pom.xml
└── src/main/
    ├── java/com/medistore/
    │   ├── MediStoreApplication.java          ← Entry point
    │   ├── model/
    │   │   ├── User.java                      ← Abstract base class (OOP)
    │   │   ├── Customer.java                  ← Extends User (Inheritance)
    │   │   └── Admin.java                     ← Extends User (Inheritance)
    │   ├── repository/
    │   │   └── UserRepository.java            ← CRUD with users.txt
    │   ├── service/
    │   │   └── UserService.java               ← Business logic
    │   ├── controller/
    │   │   ├── AuthController.java            ← Login & Register
    │   │   ├── ProfileController.java         ← Profile page
    │   │   └── AdminController.java           ← Admin user list
    │   └── config/
    │       ├── SecurityConfig.java            ← Spring Security
    │       └── DataInitializer.java           ← Seeds default admin
    └── resources/
        ├── application.properties
        └── templates/
            ├── auth/
            │   ├── login.html                 ← Page 1: Login
            │   └── register.html              ← Page 2: Registration
            ├── user/
            │   └── profile.html               ← Page 3: Profile
            └── admin/
                ├── user-list.html             ← Page 4: Admin User List
                ├── user-edit.html             ← Edit user
                └── user-form.html             ← Create new user
```

---

## 🧠 OOP Concepts Used

| Concept         | Where Applied                                              |
|-----------------|------------------------------------------------------------|
| **Encapsulation** | All `User` fields are `private`; accessed via getters/setters |
| **Inheritance**  | `Customer` and `Admin` both extend abstract `User` class   |
| **Abstraction**  | `User` has abstract methods: `getRole()`, `validateLogin()`, `getDisplayName()` |
| **Polymorphism** | Each subclass overrides these methods with different behaviour |

---

## 🗂️ CRUD Operations

| Operation | Method                        | Storage       |
|-----------|-------------------------------|---------------|
| **Create**  | `UserRepository.save(user)`    | `data/users.txt` |
| **Read**    | `findAll()`, `findById()`, `findByEmail()`, `searchUsers()` | `data/users.txt` |
| **Update**  | `UserRepository.update(user)`  | `data/users.txt` |
| **Delete**  | `UserRepository.deleteById(id)` | `data/users.txt` |

---

## 🖥️ UI Pages

| Page              | URL               | Access     |
|-------------------|-------------------|------------|
| Login             | `/login`          | Public     |
| Registration      | `/register`       | Public     |
| Profile           | `/profile`        | Logged-in  |
| Admin User List   | `/admin/users`    | ADMIN only |
| Add User (Admin)  | `/admin/users/new`| ADMIN only |
| Edit User (Admin) | `/admin/users/{id}/edit` | ADMIN only |

---

## 🚀 How to Run

### Prerequisites
- Java 17+
- Maven 3.6+

### Steps

```bash
# 1. Navigate into the project
cd medistore

# 2. Build the project
mvn clean package -DskipTests

# 3. Run the application
mvn spring-boot:run
```

### Access the App
Open your browser: **http://localhost:8080**

---

## 🔑 Default Login Credentials

| Role  | Email                    | Password   |
|-------|--------------------------|------------|
| Admin | `admin@medistore.com`    | `Admin@123` |

> A default admin is automatically created on first run if `data/users.txt` is empty.

---

## 📁 Data Storage

Users are stored in `data/users.txt` in pipe-delimited format:

```
id|username|email|hashedPassword|address|phone|role|active|createdAt
```

Example:
```
A1B2C3D4|SuperAdmin|admin@medistore.com|$2a$10$...|Colombo|0771234567|ADMIN|true|2026-01-01T10:00
```

---

## 🛡️ Security Features

- **BCrypt** password hashing (Spring Security)
- **Role-based access control** — ADMIN vs CUSTOMER
- **Spring Security** form login with CSRF protection
- Session management with secure logout

---

## 📝 Dependencies

- Spring Boot 3.2 (Web, Security, Thymeleaf, Validation)
- Lombok
- Font Awesome 6.4 (UI icons)
- Google Fonts — Inter
