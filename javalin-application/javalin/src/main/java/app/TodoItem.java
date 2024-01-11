package app;

public class TodoItem {
    public int id;
    public String title;
    public boolean completed;

    public TodoItem() { }

    public TodoItem(int id, String title, boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }
}
