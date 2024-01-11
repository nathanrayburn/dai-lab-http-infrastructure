package app;

import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        System.out.println("Server started on port 7000");
        TodoController todoController = new TodoController();
        app.get("/api/todos", todoController::getAll);
        app.get("/api/todos/{id}", todoController::getOne);
        app.post("/api/todos/", todoController::create);
        app.put("/api/todos/{id}", todoController::update);
        app.delete("/api/todos/{id}", todoController::delete);
    }
}
