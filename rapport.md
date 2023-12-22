# Rapport mise en service
# Table des matières

- [Etape 0](#etape-0)
- [Etape 1](#etape-1)
    - [Create Docker File](#create-docker-file)
  - [Create nginx configuration file](#create-nginx-configuration-file)
  - [Build le container](#build-le-container)
  - [Run le container](#run-le-container)
  - [Tailwind CSS](#tailwind-css)
    - [Install Tailwind css](#install-tailwind-css)
    - [Configuration of the template](#configuration-of-the-template)
    - [Add Tailwind directives to css](#add-tailwind-directives-to-css)
    - [Start the Tailwind CLI build](#start-the-tailwind-cli-build)
- [Etape 2](#etape-2)
  - [Create Docker compose file](#create-docker-compose-file)
  - [Build Docker Compose](#build-docker-compose)
  - [Run Docker Compose](#run-docker-compose)
- [Etape 3](#etape-3)

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