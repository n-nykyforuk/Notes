package apple.notes.Repository;

import apple.notes.Entity.NoteFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteFileRepository extends JpaRepository<NoteFile, Long> {
}
