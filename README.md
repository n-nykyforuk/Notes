# Notes API

**Notes** is a RESTful backend service built with **Spring Boot** that powers a notes management application.  
It allows users to create, update, delete, and organize notes, attach files, and even **export notes as PDFs**.  
The API is designed to be consumed by a **React frontend**.

---

## Features

-  Full CRUD operations for notes  
-  Attach and manage files with notes  
-  Checklist item support  
-  Export notes to PDF  
-  Search and filter notes  
-  CORS configuration for React frontend  
-  Persistent storage with JPA and database support  

---

## Technologies Used

- **Java 17+**  
- **Spring Boot**  
- **Spring Web (REST API)**  
- **Spring Data JPA / Hibernate**  
- **MySQL**  
- **Maven**
- **React**  

---

## Project Structure

src/
├─ main/
│ ├─ java/apple/notes/
│ │ ├─ Config/ # Configuration classes (CORS setup)
│ │ ├─ Controller/ # REST controllers (e.g., NoteController)
│ │ ├─ Entity/ # JPA entities (Note, ChecklistItem, NoteFile)
│ │ ├─ Repository/ # JPA repositories
│ │ ├─ Service/ # Business logic (e.g., PdfExportService)
│ │ └─ NotesApplication.java
│ └─ resources/
│ ├─ application.properties # Environment configuration
│ └─ static/ (optional frontend resources)
└─ test/ # Unit and integration tests

---

## Setup and Run

### 1. Clone the repository

```bash
git clone https://github.com/n-nykyforuk/Notes.git
cd Notes


