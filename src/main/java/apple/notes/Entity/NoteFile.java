package apple.notes.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "note_files")
public class NoteFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] data;

    private String fileName;
    private String fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    @JsonBackReference
    private Note note;

    public NoteFile(String fileName, String fileType, byte[] data, Note note) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
        this.note = note;
    }

    public NoteFile() {}
    public Long getId() { return id; }
    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public Note getNote() { return note; }
    public void setNote(Note note) { this.note = note; }
}
