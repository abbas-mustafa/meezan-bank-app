# ✨ Digital Banking Full-Stack Project ✨

Welcome to the **Digital Banking Full-Stack Project**! 🏦  
This application was developed as part of the **FSI OBDX (Oracle Banking Digital Experience)** training, simulating a modern, secure digital banking portal.  
It demonstrates robust user authentication, account management, and a secure password recovery system.

---

## 🚀 Features at a Glance

- **User & Account Management:** 🧑‍💻 Comprehensive CRUD (Create, Read, Update, Delete) operations for users and their linked bank accounts.  
- **Secure Authentication:** 🔐 End-to-end secure login and registration flows to protect user data.  
- **Password Recovery:** 🔄 A multi-step "Forgot Password" workflow, ensuring identity verification via CNIC and account number before reset.  
- **Robust API:** 💪 A REST API built with Spring Boot handling all core business logic and data interactions.  
- **Dynamic Frontend:** 🎨 A responsive, intuitive Single-Page Application (SPA) built with Oracle JET for a seamless user experience.

---

## 🛠️ Technologies Under the Hood

This project leverages a modern, enterprise-grade tech stack for both backend and frontend components.

### 🌐 Backend (Spring Boot)

- **Java 17 & Spring Boot 3** – Robust, scalable, and secure REST API foundation.  
- **Spring Security** – Authentication and fine-grained authorization.  
- **PostgreSQL** – Reliable database for storing banking and user data.  
- **JPA (Hibernate)** – Object-Relational Mapping (ORM) for smooth database operations.  
- **SQL Stored Procedures** – Encapsulated, atomic DB operations for performance and integrity.  
- **Maven** – Build automation and dependency management.

### 🎨 Frontend (Oracle JET)

- **Oracle JET (OJET) v19** – A modern JavaScript toolkit for rich, responsive web apps.  
- **Knockout.js** – MVVM framework for dynamic UI binding.  
- **HTML5 & CSS3** – For structure and custom styling.

### 🔒 Security Highlights

- **Client-Side Encryption:** Passwords are **RSA-encrypted** 🔑 directly in the frontend using `jsencrypt` *before* transmission.  
- **Server-Side Hashing:** Backend decrypts the RSA-encrypted password, then securely hashes it using **BCrypt** before saving — providing multi-layered protection.

---

## ▶️ Getting Started: How to Run

The project is organized into two main components — the **Spring Boot backend** and the **Oracle JET frontend**.

---

### 1. Backend Setup (Spring Boot)

📁 Located in: `backend/forgetpassword.mbl`

#### Step 1: Database Configuration ⚙️

Open  
`backend/forgetpassword.mbl/src/main/resources/application.properties`  
(or create one from `application.properties.example`) and update:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
spring.datasource.username=your_db_username
spring.datasource.password=YOUR_DB_PASSWORD

app.security.rsa.public-key=-----BEGIN PUBLIC KEY-----...
app.security.rsa.private-key=-----BEGIN PRIVATE KEY-----...
```

#### Step 2: Run the Application ▶️

- Open `backend/forgetpassword.mbl` as a Maven project in IntelliJ, VS Code, or Eclipse.  
- Run the `Application.java` file.  
- The backend will start at:  
  👉 `http://localhost:8080`

---

### 2. Frontend Setup (Oracle JET)

📁 Located in: `frontend/your_ojet_project_name_here`

#### Step 1: Navigate to Project Directory 📁

```bash
cd path/to/your/ojet_project_folder
```

#### Step 2: Install Dependencies 📦

```bash
npm install
```

#### Step 3: Configure RSA Public Key 🔑

Open `src/js/config.js` (or rename `config.js.example`) and paste your RSA **public key** in:

```javascript
const publicKey = `-----BEGIN PUBLIC KEY-----
YOUR_PUBLIC_KEY_CONTENT
-----END PUBLIC KEY-----`;
```

*(Note: This file is ignored by Git for security reasons.)*

#### Step 4: Serve the Application 🌐

```bash
ojet serve
```

Your app will typically open at:  
👉 `http://localhost:8000`

---

## 💡 Notes

- Keep your **RSA keys** private — never commit them to GitHub.  
- Ensure PostgreSQL is running before starting the backend.  
- Frontend and backend must run simultaneously for full functionality.

---

## 🎉 Conclusion

This project showcases a **realistic full-stack digital banking system** emphasizing **security, modularity, and modern web practices**.  
Perfect for understanding enterprise-level **Spring Boot + Oracle JET** integration.

**Happy Coding! 🌟**
