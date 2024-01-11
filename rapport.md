# Rapport mise en service
Autheurs : Nathan Rayburn, Ouweis Harun
# Table des matières

- [Rapport mise en service](#rapport-mise-en-service)
- [Table des matières](#table-des-matières)
- [Etape 0](#etape-0)
- [Etape 1](#etape-1)
  - [Create Docker File](#create-docker-file)
  - [Create nginx configuration  file](#create-nginx-configuration--file)
  - [Tailwind CSS](#tailwind-css)
    - [Install Tailwind css](#install-tailwind-css)
    - [Configurataion of the template](#configurataion-of-the-template)
    - [Add Tailwind directives to css](#add-tailwind-directives-to-css)
    - [Start the Tailwind CLI build](#start-the-tailwind-cli-build)
- [Etape 2](#etape-2)
  - [Create Docker compose file](#create-docker-compose-file)
  - [Build Docker Compose](#build-docker-compose)
  - [Run Docker Compose](#run-docker-compose)
- [Etape 3](#etape-3)
  - [Javalin To-Do List API - HTTP Requests](#javalin-to-do-list-api---http-requests)
    - [1. Get All To-Dos and a single one](#1-get-all-to-dos-and-a-single-one)
    - [2. Create a To-do Json format](#2-create-a-to-do-json-format)
    - [3. Update the To-do to done](#3-update-the-to-do-to-done)
    - [4. Delete the Todo](#4-delete-the-todo)
  - [Create Docker File](#create-docker-file-1)
  - [Create Docker Compose](#create-docker-compose)


# Etape 0
[Github dai-http-infra](https://github.com/nathanrayburn/dai-lab-http-infrastructure)

# Etape 1

## Create Docker File

Remarque

Concernant les volumes, nous devions indiquer l'emplacement local de notre en occurence dans notre configuration c'est `./dai-static-web-server` et qui sera traduit dans l'emplacement du container à `/user/share/nginx/html`.

```
services:
  myapp:
    image: nginx-custom:latest
    ports:
      - "8080:80"
    volumes:
      - ./app:/app
      - ./dai-static-web-server:/usr/share/nginx/html
    environment:
      - ENV_VARIABLE=value 
```

## Create nginx configuration  file

```
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log;
pid /var/run/nginx.pid;

# Events block
events {
    worker_connections 1024;
}

http {

    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    # Server block
    server {
        listen 80;
        server_name localhost;

        location / {
            root /usr/share/nginx/html;
            index /src/site/index.html index.htm; # absolute path to index.html
        }
    }
}
```

Build le container
```
docker build -t nginx-custom .
```
Run le container

```
docker run -p 8080:80 nginx-custom
```

## Tailwind CSS

### Install Tailwind css

```
npm install -D tailwindcss
npx tailwindcss init
```

### Configurataion of the template

`tailwind.config.js`

```
/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,js}"],
  theme: {
    extend: {},
  },
  plugins: [],
}
```
### Add Tailwind directives to css

`src/input.css`

```
@tailwind base;
@tailwind components;
@tailwind utilities;
```

### Start the Tailwind CLI build

```
npx tailwindcss -i ./src/input.css -o ./dist/output.css --watch
```
# Etape 2

## Create Docker compose file

`docker-compose.yml`

```
services:
  myapp:
    image: nginx-custom:latest
    ports:
      - "8080:80"
    volumes:
      - ./app:/app
    environment:
      - ENV_VARIABLE=value 
```

## Build Docker Compose

```
docker compose build
```

## Run Docker Compose

```
docker compose up
```

# Etape 3
## Javalin To-Do List API - HTTP Requests

### 1. Get All To-Dos and a single one

- **HTTP Method**: GET
- **Command**: 
```bash

  curl -X GET http://localhost:7000/api/todos 

  # replace id with the ID of the actual item
  curl -X GET http://localhost:7000/api/todos/{id}
  ```
### 2. Create a To-do Json format
- **HTTP Method**: POST
- **Command**: 
```bash
# Replace "New Task" and false with the desired title and completion status
curl -X POST http://localhost:7000/api/todos/ -H "Content-Type: application/json" -d '{"title":"New Task", "completed":false}'

```
### 3. Update the To-do to done
- **HTTP Method**: PUT
- **Command**: 
```bash
# Replace {id} with the ID of the To-Do item to update and set it to done or back to false
curl -X PUT http://localhost:7000/api/todos/{id}?completed=true

```

### 4. Delete the Todo
- **HTTP Method**: DELETE
- **Command**: 
```bash
# Replace {id} with the ID of the To-Do item to delete
curl -X DELETE http://localhost:7000/api/todos/{id}

```

## Create Docker File

Change the file name of the server which contains the dependencies.  I changed it to "server-todo.jar". 

```
FROM openjdk:21

WORKDIR /app

COPY ./javalin/target/server-todo.jar /app

CMD ["java", "-jar", "server-todo.jar"]

```

Build the container

```bash
docker build -t server-todo-api .
```

## Create Docker Compose


```
services:
  todo-api:
    image: server-todo-api:latest
    ports:
      - "7000:7000"
    build: .

```

Build docker image

```bash
docker compose build
```

Run docker compose
```bash
docker compose up
```