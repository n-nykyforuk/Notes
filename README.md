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
```

### 2. Configure the application
In `src/main/resources/application.properties`, set up your database and other settings:
spring.datasource.url=jdbc:mysql://localhost:3306/notes_db
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

### 3. Build and run the backend
mvn clean install
mvn spring-boot:run
Once started, the backend will be available at:
http://localhost:8080

---

##  API Endpoints (Examples)
| Method | Endpoint | Description |
|--------|-----------|-------------|
| GET | /api/notes | Get all notes |
| GET | /api/notes/{id} | Get a single note by ID |
| POST | /api/notes | Create a new note |
| PUT | /api/notes/{id} | Update a note |
| DELETE | /api/notes/{id} | Delete a note |
| GET | /api/notes/export/{id} | Export note as PDF |
> For full endpoint documentation, see `NoteController.java`.

---

## Frontend Integration
This API is designed to be used by a **React** frontend.
Make sure to enable CORS for your frontend URL in `CorsConfig.java`, for example:
@Configuration
public class CorsConfig {
@Bean
public WebMvcConfigurer corsConfigurer() {
return new WebMvcConfigurer() {
@Override
public void addCorsMappings(CorsRegistry registry) {
registry.addMapping("/**")
.allowedOrigins("http://localhost:3000")
.allowedMethods("*");
}
};
}
} 

---  

##  Notes
- PDF export logic is implemented in `PdfExportService.java`
- Data persistence handled via `NoteRepository` and `NoteFileRepository`
- API base package: `apple.notes`

---

## Author
**Nazar Nykyforuk**
■ GitHub: https://github.com/n-nykyforuk

---

## License
This project is licensed under the **MIT License**.


