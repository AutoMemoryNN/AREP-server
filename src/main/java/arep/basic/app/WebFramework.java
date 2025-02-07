package arep.basic.app;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class WebFramework {

    // Mapas para almacenar las rutas registradas para GET y POST
    private static Map<String, RouteHandler> getRoutes = new HashMap<>();
    private static Map<String, RouteHandler> postRoutes = new HashMap<>();

    // Directorio raíz para archivos estáticos
    private static String staticFolder = "src/view"; // valor por defecto

    /**
     * Método para registrar rutas GET.
     * Ejemplo de uso: get("/hello", (req, res) -> "Hello world!");
     */
    public static void get(String path, RouteHandler handler) {
        getRoutes.put(path, handler);
    }

    /**
     * Método para registrar rutas POST.
     */
    public static void post(String path, RouteHandler handler) {
        postRoutes.put(path, handler);
    }

    /**
     * Método para especificar la carpeta donde se encuentran los archivos estáticos.
     * Ejemplo: staticfiles("webroot/public");
     */
    public static void staticfiles(String folder) {
        staticFolder = folder;
    }

    public static void main(String[] args) throws IOException {
        // Ejemplo de registro de rutas REST
        get("/", (req, res) -> {
            // Se intenta servir un archivo estático: index.html
            File file = new File(staticFolder + "/index.html");
            System.out.println(staticFolder + "/index.html");
            if (file.exists()) {
                try {
                    res.setHeader("Content-Type", "text/html");
                    return new String(Files.readAllBytes(file.toPath()));
                } catch (IOException e) {
                    res.setStatusCode(500);
                    return "Error al leer el archivo.";
                }
            } else {
                res.setStatusCode(404);
                return "404 Not Found, locura";
            }
        });

        get("/hello", (req, res) -> {
            String name = req.getValue("name");
            if (name == null || name.isEmpty()) {
                name = "Guest";
            }
            return "Hello, " + name + "!";
        });

        post("/hellopost", (req, res) -> {
            String name = req.getValue("name");
            if (name == null || name.isEmpty()) {
                name = "Guest";
            }
            return "Hello from POST, " + name + "!";
        });
        
        // Iniciar el servidor en el puerto 8080
        int port = 8080;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor HTTP corriendo en http://localhost:" + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     OutputStream out = clientSocket.getOutputStream()) {

                    // Leer la línea de petición
                    String requestLine = in.readLine();
                    if (requestLine == null || requestLine.trim().isEmpty()) {
                        continue;
                    }
                    String[] tokens = requestLine.split(" ");
                    if (tokens.length < 2) continue;
                    String method = tokens[0];
                    String fullPath = tokens[1];

                    // Separa la ruta y la query string
                    String path = fullPath;
                    String query = "";
                    if (fullPath.contains("?")) {
                        String[] parts = fullPath.split("\\?", 2);
                        path = parts[0];
                        query = parts[1];
                    }

                    // Se procesan las cabeceras (por ejemplo, Content-Length)
                    Map<String, String> headers = new HashMap<>();
                    String line;
                    int contentLength = 0;
                    while (!(line = in.readLine()).isEmpty()) {
                        String[] headerTokens = line.split(":");
                        if (headerTokens.length >= 2) {
                            String key = headerTokens[0].trim();
                            String value = line.substring(line.indexOf(":") + 1).trim();
                            headers.put(key, value);
                            if (key.equalsIgnoreCase("Content-Length")) {
                                try {
                                    contentLength = Integer.parseInt(value);
                                } catch (NumberFormatException e) {
                                    contentLength = 0;
                                }
                            }
                        }
                    }

                    // Si es POST y tiene body, leerlo
                    String bodyData = "";
                    if ("POST".equalsIgnoreCase(method) && contentLength > 0) {
                        char[] bodyChars = new char[contentLength];
                        in.read(bodyChars);
                        bodyData = new String(bodyChars);
                        // Para simplificar, se asume que el body está en formato de query string (key1=val1&key2=val2)
                        if (!query.isEmpty()) {
                            query += "&" + bodyData;
                        } else {
                            query = bodyData;
                        }
                    }

                    // Crear objeto HttpRequest con la información obtenida
                    HttpRequest req = new HttpRequest(path, query);
                    HttpResponse res = new HttpResponse();

                    String responseBody = null;
                    // Despachar según el método y la ruta
                    if ("GET".equalsIgnoreCase(method)) {
                        if (getRoutes.containsKey(path)) {
                            // Ejecutar la función lambda registrada
                            responseBody = getRoutes.get(path).handle(req, res);
                        } else {
                            // Intentar servir archivo estático
                            File staticFile = new File(staticFolder + path);
                            if (staticFile.exists() && staticFile.isFile()) {
                                try {
                                    // Se determina el Content-Type según la extensión (muy básico)
                                    if (path.endsWith(".html")) {
                                        res.setHeader("Content-Type", "text/html");
                                    } else if (path.endsWith(".css")) {
                                        res.setHeader("Content-Type", "text/css");
                                    } else if (path.endsWith(".js")) {
                                        res.setHeader("Content-Type", "application/javascript");
                                    } else if (path.matches(".*\\.(png|jpg|jpeg|gif)$")) {
                                        res.setHeader("Content-Type", "image/*");
                                    }
                                    responseBody = new String(Files.readAllBytes(staticFile.toPath()));
                                } catch (IOException e) {
                                    res.setStatusCode(500);
                                    responseBody = "Error al leer el archivo.";
                                }
                            } else {
                                res.setStatusCode(404);
                                responseBody = "404 Not Found";
                            }
                        }
                    } else if ("POST".equalsIgnoreCase(method)) {
                        if (postRoutes.containsKey(path)) {
                            responseBody = postRoutes.get(path).handle(req, res);
                        } else {
                            res.setStatusCode(404);
                            responseBody = "404 Not Found";
                        }
                    } else {
                        res.setStatusCode(405);
                        responseBody = "405 Method Not Allowed";
                    }

                    // Establece el cuerpo de la respuesta y envía la respuesta completa
                    res.setBody(responseBody);
                    String httpResponse = res.generateResponse();
                    out.write(httpResponse.getBytes());
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
