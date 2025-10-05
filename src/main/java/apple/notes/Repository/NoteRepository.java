package apple.notes.Repository;

import apple.notes.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByOrderByCreatedAtDesc();
    List<Note> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
}
