package apple.notes.Controller;

import apple.notes.Entity.*;
import apple.notes.Repository.NoteRepository;
import apple.notes.Service.PdfExportService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteRepository repository;
    private final PdfExportService pdfExportService;

    public NoteController(NoteRepository repository, PdfExportService pdfExportService) {
        this.repository = repository;
        this.pdfExportService = pdfExportService;
    }

    @GetMapping
    public List<Note> getAllNotes() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/search")
    public List<Note> searchNotes(@RequestParam String query) {
        return repository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(query, query);
    }

    @GetMapping("/{id}")
    public Note getNoteById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
    }

    @PostMapping("/create")
    public Note createNote(@RequestBody Note note) {
        return repository.save(note);
    }

    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Note updateNote(@PathVariable Long id, @RequestBody Note updatedNote) {
        return repository.findById(id)
                .map(note -> {
                    note.setTitle(updatedNote.getTitle());
                    note.setContent(updatedNote.getContent());
                    note.setChecklist(updatedNote.getChecklist());
                    return repository.save(note);
                })
                .orElseThrow(() -> new RuntimeException("Note not found with id " + id));
    }

    @PostMapping("/{id}/attach")
    public ResponseEntity<Note> attachFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return repository.findById(id)
                .map(note -> {
                    try {
                        NoteFile noteFile = new NoteFile(
                                file.getOriginalFilename(),
                                file.getContentType(),
                                file.getBytes(),
                                note
                        );
                        note.getFiles().add(noteFile);
                        Note savedNote = repository.save(note);
                        return ResponseEntity.ok(savedNote);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read file", e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Note not found with id " + id));
    }


    @GetMapping("/{id}/files")
    public ResponseEntity<List<NoteFile>> getNoteFiles(@PathVariable Long id) {
        Note note = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id " + id));
        return ResponseEntity.ok(note.getFiles());
    }

    @GetMapping("/{noteId}/file/{fileId}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long noteId, @PathVariable Long fileId) {
        Note note = repository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        return note.getFiles().stream()
                .filter(f -> f.getId().equals(fileId))
                .findFirst()
                .map(file -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                        .contentType(MediaType.parseMediaType(file.getFileType()))
                        .body(file.getData()))
                .orElseThrow(() -> new RuntimeException("File not found"));
    }

    @DeleteMapping("/{noteId}/file/{fileId}")
    public ResponseEntity<Void> deleteFileFromNote(@PathVariable Long noteId, @PathVariable Long fileId) {
        Note note = repository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        boolean removed = note.getFiles().removeIf(file -> file.getId().equals(fileId));

        if (!removed) {
            throw new RuntimeException("File not found in this note");
        }

        repository.save(note);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportNoteAsPdf(@PathVariable Long id) {
        Note note = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        try {
            byte[] pdf = pdfExportService.generateNotePdf(note);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"note_" + id + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}