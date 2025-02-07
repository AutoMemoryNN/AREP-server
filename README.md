# Web Framework for REST Services and Static File Management

## Project Statement
This project enhances an existing web server that currently supports HTML, JavaScript, CSS, and image files by transforming it into a fully functional web framework. The framework enables developers to define REST services using lambda functions, extract query values from requests, and specify the location of static files.

## Features
1. **GET Static Method for REST Services**
    - Enables defining REST services with lambda functions.
    - Example:
      ```java
      get("/hello", (req, res) -> "Hello world!");
      ```

2. **Query Value Extraction**
    - Allows retrieval of query parameters from requests.
    - Example:
      ```java
      get("/hello", (req, res) -> "Hello " + req.getValue("name"));
      ```

3. **Static File Management**
    - Developers can define the folder where static files are located.
    - Example:
      ```java
      staticfiles("webroot/public");
      ```

4. **Example Usage**
    ```java
    public static void main(String[] args) {
        staticfiles("/webroot");
        get("/hello", (req, resp) -> "Hello " + req.getValue("name"));
        get("/pi", (req, resp) -> String.valueOf(Math.PI));
    }
    ```
    - REST services respond to:
      - `http://localhost:8080/App/hello?name=Pedro`
      - `http://localhost:8080/App/pi`
    - Static files can be accessed via:
      - `http://localhost:8080/index.html`

## Installation and Setup
1. **Clone the Repository:**
   ```sh
   git clone <repository-url>
   cd <project-folder>
   ```
2. **Build the Project with Maven:**
   ```sh
   mvn clean install
   ```
3. **Run the Application:**
   ```sh
   java -jar target/webframework.jar
   ```

## Architecture
- **`WebFramework` Class:** Handles route registration and request processing.
- **Lambda-based Routing:** Uses a functional approach for defining REST services.
- **Static File Serving:** Supports serving HTML, CSS, JS, and images.
- **Built-in Query Handling:** Extracts query parameters from incoming requests.

## Creating Your Own Endpoints

Developers can register their own endpoints using the framework's `get` and `post` methods. Example:

```java
WebFramework.get("/user", (req, res) -> "User information");
WebFramework.post("/data", (req, res) -> "Processing data");
```

To retrieve parameters from a GET request:

```java
WebFramework.get("/greet", (req, res) -> {
    String name = req.getValue("name");
    return "Hello, " + (name != null ? name : "guest") + "!";
});
```

To process data from a POST request:

```java
WebFramework.post("/submit", (req, res) -> {
    String data = req.getValue("data");
    return "Received data: " + data;
});
```

## Tests
The project includes unit tests for:
- Route registration and request handling.
- Query parameter extraction.
- Static file serving.

Run tests with:
```sh
mvn test
```
