# Service Commissioning Report
Authors: Nathan Rayburn, Ouweis Harun

# Table of Contents
- [Service Commissioning Report](#service-commissioning-report)
- [Table of Contents](#table-of-contents)
- [Step 0](#step-0)
- [Step 1](#step-1)
  - [Create Docker File](#create-docker-file)
  - [Create nginx configuration  file](#create-nginx-configuration--file)
  - [Tailwind CSS](#tailwind-css)
    - [Install Tailwind css](#install-tailwind-css)
    - [Configurataion of the template](#configurataion-of-the-template)
    - [Add Tailwind directives to css](#add-tailwind-directives-to-css)
    - [Start the Tailwind CLI build](#start-the-tailwind-cli-build)
- [Step 2](#step-2)
  - [Create Docker compose file](#create-docker-compose-file)
  - [Build Docker Compose](#build-docker-compose)
  - [Run Docker Compose](#run-docker-compose)
- [Step 3](#step-3)
  - [Javalin To-Do List API - HTTP Requests](#javalin-to-do-list-api---http-requests)
    - [1. Get All To-Dos and a single one](#1-get-all-to-dos-and-a-single-one)
    - [2. Create a To-do Json format](#2-create-a-to-do-json-format)
    - [3. Update the To-do to done](#3-update-the-to-do-to-done)
    - [4. Delete the Todo](#4-delete-the-todo)
  - [Create Docker File](#create-docker-file-1)
  - [Create Docker Compose](#create-docker-compose)
  - [Step 4: Reverse Proxy with Traefik](#step-4-reverse-proxy-with-traefik)
    - [Modifications Made](#modifications-made)
    - [Testing Procedure](#testing-procedure)
    - [Results and Observations from Demonstration Images](#results-and-observations-from-demonstration-images)
  - [Step 5: Scalability and Load Balancing](#step-5-scalability-and-load-balancing)
    - [Overview](#overview)
    - [Modifications Made](#modifications-made-1)
    - [Testing Procedure](#testing-procedure-1)
    - [Results and Observations](#results-and-observations)
  - [Step 6: Implementing Sticky Sessions](#step-6-implementing-sticky-sessions)
    - [Configuration Changes](#configuration-changes)
    - [Demonstration and Testing](#demonstration-and-testing)
    - [Results and Observations](#results-and-observations-1)
  - [](#)
  - [Step 7: Securing Traefik with HTTPS](#step-7-securing-traefik-with-https)
    - [Configuration Changes](#configuration-changes-1)
    - [Testing and Validation](#testing-and-validation)
  - [Optional Step 1: Management UI with Portainer](#optional-step-1-management-ui-with-portainer)
    - [Docker Compose Configuration for Portainer](#docker-compose-configuration-for-portainer)
    - [Setting Up and Using Portainer](#setting-up-and-using-portainer)
      - [Configuration:](#configuration)
      - [Usage:](#usage)
    - [Validation and Conclusion](#validation-and-conclusion)
  - [Optional Step 2: Integration of API with Static Website](#optional-step-2-integration-of-api-with-static-website)
    - [Description](#description)
    - [Implementation Details](#implementation-details)
    - [Testing and Validation](#testing-and-validation-1)

---


# Step 0
[Github repository](https://github.com/nathanrayburn/dai-lab-http-infrastructure)

---
# Step 1

## Create Docker File

Note:

Regarding volumes, we had to specify the local location, which in our configuration is `./dai-static-web-server`. This will be mapped to the container location at `/usr/share/nginx/html`.

```yaml
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

Build the container:

```bash
docker build -t nginx-custom .
```
Run the container:

```bash
docker run -p 8080:80 nginx-custom
```

## Tailwind CSS

### Install Tailwind css

```bash
npm install -D tailwindcss
npx tailwindcss init
```

### Configurataion of the template

`tailwind.config.js`

```javascript
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

```css
@tailwind base;
@tailwind components;
@tailwind utilities;
```

### Start the Tailwind CLI build

```bash
npx tailwindcss -i ./src/input.css -o ./dist/output.css --watch
```

---


# Step 2

## Create Docker compose file

`docker-compose.yml`

```yaml
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

```bash
docker compose build
```

## Run Docker Compose

```bash
docker compose up
```

---

# Step 3
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

```Dockerfile
FROM openjdk:21

WORKDIR /app

COPY ./javalin/target/server-todo.jar /app

CMD ["java", "-jar", "server-todo.jar"]

```

Build the container:

```bash
docker build -t server-todo-api .
```

## Create Docker Compose

`docker-compose.yml` snippet for the `todo-api` service:

```yaml
services:
  todo-api:
    image: server-todo-api:latest
    ports:
      - "7000:7000"
    build: .

```

Build docker image:

```bash
docker compose build
```

Run docker compose:
```bash
docker compose up
```
---

## Step 4: Reverse Proxy with Traefik

### Modifications Made

In this step, we have integrated Traefik as a reverse proxy to manage the traffic to our static web server (webapp) and dynamic API server (todo-api). The `docker-compose.yml` file has been updated as follows:

1. **Traefik Service Added:**
   - A new service named `traefik` was added with the Traefik image version 2.10.
   - Configuration commands were set to enable an insecure API, Docker provider, and define entrypoints for HTTP (`:80`) and HTTPS (`:443`).
   - Ports 80 (HTTP) and 8080 (Traefik dashboard) were exposed.
   - Labels for Traefik configuration were added to define routing rules and service details.

2. **Todo-API Service:**
   - Labels added to enable Traefik integration, with routing rules specifying that any request to `localhost` with a path prefix of `/api` should be directed to this service.
   - The `expose` directive was used to make the service available on port 7000 within the Docker network.

3. **Webapp Service:**
   - Labels added to enable Traefik integration, with routing rules specifying that requests to `localhost` should be directed to this service.
   - The `expose` directive was used for port 80, similar to the todo-api service.

### Testing Procedure

To test the updated configuration, the following steps were undertaken:

1. **Starting the Services:**
   - Ran the command `docker compose up` to start all services, including Traefik, todo-api, and webapp.
   - Ensured that no errors occurred during the startup and all services were running correctly.

2. **Verifying Traefik Dashboard:**
   - Accessed the Traefik dashboard on `http://localhost:8080` to check if the services were properly detected and routes were correctly configured.
   - Confirmed that the routing rules for both webapp and todo-api were displayed and correctly pointing to their respective entrypoints and ports.

3. **Testing Webapp Service:**
   - Accessed the webapp service by navigating to `http://localhost` in a browser.
   - Verified that the static website was served correctly without any issues.

4. **Testing Todo-API Service:**
   - Accessed the todo-api service by sending requests to `http://localhost/api`.
   - Performed various API operations (CRUD) to ensure that the API was responding correctly and the data manipulation was as expected.

5. **Checking Load Distribution:**
   - Sent multiple requests to both webapp and todo-api to observe the load handling and routing effectiveness of Traefik.

By completing these tests, we were able to confirm that the integration of Traefik as a reverse proxy was successful and both the static and dynamic services were functioning correctly under the new configuration.

### Results and Observations from Demonstration Images

1. **Webapp Service Access:**
   - The first image depicts the webapp service's login screen accessible at `http://localhost`. The presence of the sign-in interface indicates that the static content is being correctly served through the Traefik reverse proxy. The styling and layout, including input fields for the email address and password, are properly rendered, confirming that the static files are being delivered as expected.

2. **API Service Response:**
   - The second image shows the result of a GET request made to the `http://localhost/api/todos` endpoint. The browser displays an empty array `[]`, which, while not showing any data, does indicate a successful HTTP response from the API server. This response confirms that the API route is correctly configured in Traefik and that the server is running and capable of handling requests.


![Alt text](Reverse-proxy-1.png)

![Alt text](Reverse-proxy-final-1.png)


---

## Step 5: Scalability and Load Balancing

### Overview

In this step, we focused on enhancing the scalability and load-balancing capabilities of our web infrastructure. To achieve this, we configured our services to run multiple replicas, allowing for better distribution of incoming traffic and improved reliability.

### Modifications Made

1. **Configuration for Multiple Replicas:**
   - For both the `todo-api` and `webapp` services, we added a `deploy` section in our `docker-compose.yml` file.
   - Specified `replicas: 5` for each service, enabling the deployment of 5 instances of each service.
   - This configuration was intended to provide an initial setup for load balancing and to test the scalability.

2. **Dynamic Scaling:**
   - To test the dynamic scalability, we used the command `sudo docker compose up --scale [service-name]=[number-of-replicas] -d`.
   - We experimented with scaling up and down, increasing the number of instances to 10 and then reducing them to 3, for the `froom-static` service as an example.

### Testing Procedure

1. **Scaling Up:**
   - We executed the command `sudo docker compose up --scale froom-static=10 -d` to scale up the `froom-static` service to 10 instances.
   - Verified by running `sudo docker compose ps`, which showed all 10 instances running as expected.

2. **Observing Load Distribution:**
   - We monitored the load distribution across the 10 instances to ensure that the traffic was evenly spread, indicating successful load balancing.

3. **Scaling Down:**
   - To test the downscaling, we executed `sudo docker compose up --scale froom-static=3 -d`.
   - Again, we used `sudo docker compose ps` to confirm that the number of running instances was successfully reduced to 3.

### Results and Observations


- **Even Distribution of Load:**
  - The server logs indicated that the load was evenly distributed among the available instances. For instance, the API requests were consistently served by `dai-lab-http-infrastructure-todo-api-1`, demonstrating the sticky session configuration in action.
  - In contrast, requests to the static website were handled by different instances, such as `dai-lab-http-infrastructure-webapp-1`, `dai-lab-http-infrastructure-webapp-3`, and `dai-lab-http-infrastructure-webapp-2`. This variety in request handling confirms that round-robin distribution was functioning correctly.

- **Sticky Sessions Confirmation:**
  - The repeated GET requests on `/api/todos` showcase the sticky sessions at work, with all requests being directed to the same API instance (`dai-lab-http-infrastructure-todo-api-1`). This behavior is crucial for maintaining user session state and ensuring a consistent experience for clients.

- **Round-Robin Load Balancing Validation:**
  - The logs from the static website served by different instances on consecutive requests validate the active round-robin load balancing. The distribution of these requests across various instances illustrates Traefik's dynamic response to incoming traffic and its effective load balancing strategy.

- **Scalability in Action:**
  - The system's response to scaling commands was immediate and effective. The ability to increase and decrease the number of service instances on-demand demonstrated the agility of our deployment and the robustness of the Docker and Traefik configuration.

- **Reliability and Performance:**
  - Throughout the scaling process, no service interruptions were noted. This resilience is indicative of a well-configured distributed system capable of handling varying loads and is a testament to the stability of our infrastructure.


![Alt text](Round-robin-1.png)




---

## Step 6: Implementing Sticky Sessions

### Configuration Changes

We modified our `docker-compose.yml` file to implement sticky sessions for the `froom-api` service. The following labels were added to the service configuration:

1. **Activation of Sticky Sessions:**
   - Added `"traefik.http.services.froom-api.loadbalancer.sticky=true"` to enable sticky sessions.

2. **Cookie Configuration:**
   - Specified a cookie name for the sticky session using `"traefik.http.services.froom-api.loadbalancer.sticky.cookie.name=StickyCookie"`.
   - Enhanced security by adding `"traefik.http.services.froom-api.loadbalancer.sticky.cookie.secure=true"`.

### Demonstration and Testing

To demonstrate the effectiveness of the sticky session configuration, we conducted the following tests:

1. **Sticky Sessions for API Service:**
   - Performed 9 consecutive page refreshes on the API endpoint.
   - Monitored the server logs to verify that all requests were consistently handled by the same instance of the `froom-api` service (`dai-lab-http-infrastructure-froom-api-1`).
   - This consistent routing to the same instance confirmed that sticky sessions were working as intended.

2. **Round-Robin for Static Website:**
   - Executed 5 page refreshes on the static website.
   - Observed the server logs to check the distribution of requests across different instances of the `froom-static` service.
   - The logs indicated requests being handled by various instances (`dai-lab-http-infrastructure-froom-static-1`, `froom-static-2`, etc.), demonstrating that round-robin load balancing was still active for the static server.

### Results and Observations


- **Consistency in API Service:**
  - The server logs demonstrated that all the GET requests on `/api/todos` were serviced by the same instance of the `todo-api` service, specifically `dai-lab-http-infrastructure-todo-api-1`. This was consistent across 9 separate requests, which clearly indicates that sticky sessions were effectively maintained.
  
- **Persistent Session Management:**
  - The logs also showed the `getAll` function call being repeatedly handled by the same API instance, reinforcing the effectiveness of the sticky sessions. This consistency is essential for stateful interactions, where the client's session state needs to persist across multiple requests.

- **Graceful Shutdown:**
  - Upon initiating a graceful stop of the services, we observed that the system handled the termination signals as expected, allowing for a controlled shutdown of the active sessions. This is crucial to ensure data integrity and a smooth user experience even during service interruptions.


![Alt text](Sticky-sessions-1.png)
---

## Step 7: Securing Traefik with HTTPS

### Configuration Changes

We updated our `docker-compose.yml` file to enable HTTPS in our Traefik setup. The key modifications include:

1. **Traefik Service Configuration:**
   - Updated the Traefik service to expose port 443 for HTTPS.
   - Mounted two volumes:
     - `./certificates:/etc/traefik/certificates` to provide access to SSL certificates.
     - `./traefik.yaml:/etc/traefik/traefik.yaml` for Traefik's main configuration.

2. **Todo-API Service:**
   - Updated labels to configure the `todo-api` service for HTTPS.
   - Specified `traefik.http.routers.todo-api.tls=true` and set the entrypoint to `websecure`.
   - Configured the router rule for HTTPS and kept the sticky session settings.
   - Defined the service's load balancer server port as 7000.

3. **Webapp Service:**
   - Configured labels for the `webapp` service to use HTTPS.
   - Set `traefik.http.routers.froom-static.tls=true` and the entrypoint to `websecure`.

### Testing and Validation

To validate our HTTPS setup, the following steps were carried out:

1. **Starting the Services:**
   - Ran `docker compose up` to start the updated services, including Traefik with the new HTTPS configuration.

2. **Accessing the Services via HTTPS:**
   - Accessed the `todo-api` service using `https://localhost/api/todos`.
   - Accessed the `webapp` service via `https://localhost`.
   - Verified that both services were accessible over HTTPS, indicating successful routing through Traefik.

3. **Certificate Verification:**
   - Inspected the SSL certificate information in the browser to confirm that our self-signed certificates were being used.
   - Checked for any SSL-related errors or warnings.

4. **Monitoring Traefik Dashboard:**
   - Accessed the Traefik dashboard on `https://localhost:8080` to ensure that HTTPS endpoints were correctly configured and operational.


---

## Optional Step 1: Management UI with Portainer

For the first optional step, we chose to integrate Portainer into our web infrastructure. Portainer is a user-friendly tool that simplifies the management and monitoring of Docker containers. Its ease of use and stability make it an ideal choice for our project.

### Docker Compose Configuration for Portainer

To include Portainer in our setup, we updated the `docker-compose.yml` file with the following configuration:

```yaml
# Portainer service configuration
portainer:
  image: portainer/portainer-ce:latest
  ports:
    - "9443:9443"
  volumes:
    - /var/run/docker.sock:/var/run/docker.sock
  restart: unless-stopped
```

This configuration ensures that Portainer is set up to manage the Docker containers running as part of our web infrastructure.

### Setting Up and Using Portainer

#### Configuration:

1. **Accessing Portainer:**
   - Open a browser and navigate to `https://localhost:9443/`.
   - Set up a user account as per the on-screen instructions.

![Alt text](Login.png)

#### Usage:

1. **Selecting the Environment:**
   - Initially, select the appropriate Docker environment to manage within Portainer.

2. **Managing Containers:**
   - Navigate to the 'Containers' section to view all active containers.
   - Containers can be managed directly from this interface, with options to start, stop, and modify settings.

![Alt text](Manage_container.png)
![Alt text](manage_container_2.png)

3. **Duplicating a Container:**
   - To duplicate a container, select it and click on 'Duplicate/Edit'.
   - In the duplication interface, rename the new container and adjust settings as required.
   - Disable the 'Always pull image' option if not needed.
   - Additional configurations can be added at this stage.

![Alt text](Duplicate.png)
![Alt text](Container_config.png)

4. **Observing the New Container:**
   - Once the duplication process is complete, the new container appears in the cluster.
   - It's now operational and can be interacted with like any other container in the environment.

![Alt text](Finish.png)

### Validation and Conclusion

As a validation step, users can interact with the newly added container to ensure it functions as intended within the infrastructure. The integration of Portainer significantly simplifies the management of our Docker containers, providing a visual and intuitive interface for controlling and monitoring our web infrastructure.

---

## Optional Step 2: Integration of API with Static Website

### Description

The aim of this step was to bridge our static web page with the dynamic content served by our API. We achieved this by implementing AJAX (Asynchronous JavaScript and XML) calls to the API server, using JavaScript, to periodically fetch and display data on the static webpage.

### Implementation Details

1. **JavaScript Integration:**
   - Added a new JavaScript file, `script.js`, to our project structure.
   - Included `script.js` in the HTML page to handle the API requests and data rendering.

2. **HTML Page Modifications:**
   - Enhanced the existing HTML page to include a section for displaying TODO list items.
   - Used Alpine.js, a minimal framework for composing JavaScript behavior in markup, to facilitate dynamic content rendering.

3. **JavaScript Functionality:**
   - Implemented an `init` function within Alpine.js's `todoList` component to perform a `GET` request to our API server.
   - Utilized the Fetch API to make asynchronous requests to the API endpoint `https://localhost/api/todos`.
   - Dynamically rendered the fetched TODO items on the webpage, displaying their ID, title, and completion status.

### Testing and Validation

1. **Functionality Testing:**
   - Verified the correct inclusion of the JavaScript file and Alpine.js in the HTML page.
   - Ensured that the `init` function was properly fetching data from the API upon loading the webpage.

2. **Data Rendering:**
   - Checked the webpage to observe the dynamic rendering of the TODO items.
   - Ensured that each TODO item's details (ID, title, and completion status) were accurately displayed.

![Alt text](Last_picture-1.png)