# 🔐 Secure Web App — OWASP Top 10 Practice (2021)

This is a secure full-stack web application developed as part of a security-focused assignment.
---

## 🛠️ Tech Stack

- **Frontend:** HTML, CSS  
- **Backend:** Java Spring Boot  
- **Database:** MySQL  

---

## ✅ Security Practices Implemented — OWASP Top 10 (2021)

| OWASP Risk | Description |
|------------|-------------|
| **1. Broken Access Control** | Role-based access control with Spring Security — users cannot access admin-level routes. |
| **2. Injection** | SQL Injection prevented via JPA and parameterized queries. |
| **3. Insecure Design** | Input validation, CSRF protection, and secure form handling are implemented throughout. |
| **4. Security Misconfiguration** | Configured secure headers, CORS policies, and CSRF tokens via Spring Security. |
| **5. Vulnerable and Outdated Components** | Dependencies managed and scanned using OWASP Dependency-Check. |
| **6. Identification and Authentication Failures** | Secure login with password policies, session handling, and no detailed error messages. |
| **7. Software and Data Integrity Failures** | Safe data-handling practices and deployment assumptions are followed. |



---

## 💡 Features

- 🔐 Secure User Registration & Login  
- 👨‍💼 Role-Based Access Control (User/Admin)  
- 🛡️ Spring Security Integration  
- 🧪 Vulnerability Fixes Aligned with OWASP Top 10  

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven
- MySQL

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/secure-banking-owasp-practice.git
   cd secure-banking-owasp-practice
