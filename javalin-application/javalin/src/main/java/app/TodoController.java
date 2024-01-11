package app;

import io.javalin.http.Context;
import java.util.concurrent.ConcurrentHashMap;

class TodoController {
    private ConcurrentHashMap<Integer, TodoItem> todos = new ConcurrentHashMap<>();
    private int lastId = 0;

    public void getOne(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        ctx.json(todos.get(id));
    }

    public void getAll(Context ctx) {
        ctx.json(todos.values());
    }

    public void create(Context ctx) {
        TodoItem todo = ctx.bodyAsClass(TodoItem.class);
        todo.id = ++lastId;
        todos.put(todo.id, todo);
        ctx.status(201).json(todo);
    }

    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        todos.remove(id);
        ctx.status(204);
    }

    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean completed = Boolean.parseBoolean(ctx.queryParam("completed"));
        TodoItem todo = todos.get(id);
        if (todo != null) {
            todo.completed = completed;
            todos.put(id, todo);
            ctx.status(200).json(todo);
        } else {
            ctx.status(404); // Not found
        }
    }

}
