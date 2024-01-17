document.addEventListener('alpine:init', () => {
    Alpine.data('todoList', () => ({
        todos: [],

        async init() {
            this.todos = await (await fetch('https://localhost/api/todos')).json();
            console.log(this.todos);
        },
    }));
});

