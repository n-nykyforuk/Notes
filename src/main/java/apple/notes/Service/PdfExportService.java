package apple.notes.Service;

import apple.notes.Entity.Note;
import apple.notes.Entity.NoteFile;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfExportService {

    public byte[] generateNotePdf(Note note) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);

        document.open();

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

        if (note.getTitle() != null && !note.getTitle().isBlank()) {
            Paragraph title = new Paragraph(note.getTitle(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);
        }

        if (note.getContent() != null && !note.getContent().isBlank()) {
            Paragraph content = new Paragraph(note.getContent(), normalFont);
            content.setSpacingAfter(10f);
            document.add(content);
        }

        if (note.getChecklist() != null && !note.getChecklist().isEmpty()) {
            document.add(new Paragraph("Checklist:", sectionFont));
            PdfContentByte cb = writer.getDirectContent();
            PdfFormField checkboxGroup = PdfFormField.createEmpty(writer);
            int y = 0;
            for (var item : note.getChecklist()) {
                float llx = document.left(), lly = document.top() - 50 - y;
                float urx = llx + 12, ury = lly + 12;
                RadioCheckField check = new RadioCheckField(writer, new Rectangle(llx, lly, urx, ury), item.getText(), "Yes");
                check.setCheckType(RadioCheckField.TYPE_CHECK);
                check.setChecked(item.isChecked());
                PdfFormField field = check.getCheckField();
                checkboxGroup.addKid(field);
                Paragraph p = new Paragraph(item.getText(), normalFont);
                p.setIndentationLeft(20f);
                document.add(p);
                y += 20;
            }
            writer.addAnnotation(checkboxGroup);
            document.add(Chunk.NEWLINE);
        }

        if (note.getFiles() != null && !note.getFiles().isEmpty()) {
            document.add(new Paragraph("Attached files:", sectionFont));
            document.add(Chunk.NEWLINE);

            for (NoteFile file : note.getFiles()) {
                if (file.getFileType() != null && file.getFileType().startsWith("image/") && file.getData() != null) {
                    try {
                        Image img = Image.getInstance(file.getData());
                        float maxWidth = PageSize.A4.getWidth() - document.leftMargin() - document.rightMargin();
                        if (img.getWidth() > maxWidth) img.scaleToFit(maxWidth, PageSize.A4.getHeight());
                        img.setAlignment(Image.ALIGN_CENTER);
                        document.add(img);
                        document.add(Chunk.NEWLINE);
                    } catch (Exception ignored) {}
                } else if (file.getData() != null) {
                    // Вкладення у PDF
                    PdfFileSpecification fs = PdfFileSpecification.fileEmbedded(writer, null, file.getFileName(), file.getData());
                    writer.addFileAttachment(file.getFileName(), fs);
                    Paragraph p = new Paragraph(file.getFileName() + " (attached)", normalFont);
                    document.add(p);
                }
            }
        }

        document.close();
        return baos.toByteArray();
    }
}
