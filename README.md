# 💼 TechJob - IT Job Portal

TechJob is a web application that connects IT job seekers with companies.  
The platform provides job posting, application management, and candidate profile features, supporting both recruiters and job seekers.

---

## 🚀 Features

### 👩‍💻 For Job Seekers
- User authentication & role-based authorization with **JWT**.
- Profile management: upload **avatar** and **CV** (stored in **Cloudinary**).
- AI-powered CV parsing to extract relevant information automatically.
- Apply for jobs online.
- Built a job recommendation feature that matches users’ CVs with suitable job postings using Redis Stack (vector search + embedding)

### 🏢 For Recruiters
- Post and manage job listings.
- Review candidate applications with uploaded CVs.
- Manage company profile and HR accounts.
- Dashboard with basic analytics.
- Refund handling via **VNPay** for premium services.

### ⚡ System Features
- **Redis Queue** to optimize performance and reduce response time during file uploads.
- Secure authentication with JWT tokens.
- Cloud storage integration with **Cloudinary**.

---

## 🛠️ Tech Stack

- **Backend:** Java, Spring Boot, JPA/Hibernate  
- **Frontend:** Thymeleaf, Bootstrap, JavaScript, ReactJS 
- **Database:** MySQL  
- **Authentication:** JWT  
- **Queue:** Redis  
- **Payment Integration:** VNPay  
- **Cloud Storage:** Cloudinary  
- **Integration:** AI Model

---
