package apple.notes.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    @CollectionTable(name = "note_checklists", joinColumns = @JoinColumn(name = "note_id"))
    private List<ChecklistItem> checklist = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = true)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<NoteFile> files = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.createdAt = LocalDateTime.now();
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Note() {}
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<ChecklistItem> getChecklist() { return checklist; }
    public void setChecklist(List<ChecklistItem> checklist) { this.checklist = checklist; }
    public List<NoteFile> getFiles() { return files; }
    public void setFiles(List<NoteFile> files) { this.files = files; }
}