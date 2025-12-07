# 🎯 EventSphereX — Event Management System (Java + Swing + MySQL)

**Internship:** CodeClause – Java Development Intern  
**Author:** Mahipal Mali  
**Project Duration:** 01 December 2025 – 31 December 2025  
**Tech Stack:** Java • Swing • JDBC • MySQL • Maven  

---

## 📌 Project Description

EventSphereX is a modern desktop application designed to simplify **event planning and attendee management**.

It enables users to:

- Create, edit, and schedule events  
- Manage attendees for each event  
- View a dashboard of all upcoming activities  
- Visualize analytics like total attendees & events  
- Use a calendar view similar to Google Calendar  

---

## ✨ Features

- 📝 **Create and manage events**
- 👥 **Add and manage attendees**
- 📅 **Calendar-based event visualization**
- 📊 **Analytics dashboard**
- 🔐 **Secure login & profile settings**
- 🎨 **Modern UI (Material + Gradient)**
- 💾 **Database integration (MySQL)**

---

## 📁 Project Structure
```
src/
└── main/
├── java/
│ ├── dao/
│ │ ├── DBConnection.java
│ │ ├── EventDAO.java
│ │ └── UserDAO.java
│ ├── model/
│ │ ├── Event.java
│ │ └── User.java
│ └── ui/
│ ├── LoginFrame.java
│ └── DashboardFrame.java
└── resources/
└── logo.png

pom.xml
README.md
```

---

## 🖥 Requirements

- Java **JDK 17+**  
- MySQL **(running locally or on server)**  
- Maven Installed  
- Internet not required (offline system)

---

## 🗄 Database Setup

```sql
CREATE DATABASE event_db;
USE event_db;

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100),
    password VARCHAR(100)
);

CREATE TABLE events (
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100),
    description TEXT,
    location VARCHAR(100),
    event_date DATETIME,
    created_by INT
);

CREATE TABLE attendees (
    attendee_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    email VARCHAR(100),
    contact VARCHAR(15)
);

CREATE TABLE event_attendees (
    link_id INT PRIMARY KEY AUTO_INCREMENT,
    event_id INT,
    attendee_id INT
);
```
🚀 Running the Application

Build using Maven:
```bash
mvn clean install
```

Run the JAR:
```bash
java -jar target/EventSphereX-1.0.jar
```
## 📸 Application Preview (Screenshots)
<img width="960" height="600" alt="image" src="https://github.com/user-attachments/assets/171383ce-8144-4878-b4d1-0b93b539a96a" /><img width="960" height="600" alt="image" src="https://github.com/user-attachments/assets/be19fe7c-ed18-450c-b883-7951610c2764" />
<img width="960" height="600" alt="image" src="https://github.com/user-attachments/assets/8d42293f-a1bd-47fd-93ba-807d5335cd82" /><img width="960" height="600" alt="image" src="https://github.com/user-attachments/assets/7084d963-21f6-4437-8d7a-9acb67beb62c" />



## 📚 Learnings from Project

Java Swing UI designing

JDBC + SQL CRUD operations

Managing relational database models

Calendar UI logic implementation

Clean UI UX with custom gradients

Application-level architecture development

## 📌 Author

Mahipal Mali
Java Developer Intern — CodeClause
