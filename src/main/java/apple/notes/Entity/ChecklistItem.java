package apple.notes.Entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class ChecklistItem {

    private String text;
    private boolean checked;

    public ChecklistItem(String text, boolean checked) {
        this.text = text;
        this.checked = checked;
    }

    public ChecklistItem() {}
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
}
