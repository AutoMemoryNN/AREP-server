package arep.basic.app;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private int statusCode = 200;
    private String body = "";
    private Map<String, String> headers = new HashMap<>();

    public HttpResponse() {
        // Cabecera por defecto para permitir CORS
        headers.put("Access-Control-Allow-Origin", "*");
    }

    public int getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(int code) {
        this.statusCode = code;
    }

    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }
    
    /**
     * Genera la respuesta HTTP en formato String para enviar por el socket.
     */
    public String generateResponse() {
        // Si no se ha definido Content-Type, se asume texto plano
        if (!headers.containsKey("Content-Type")) {
            headers.put("Content-Type", "text/plain");
        }
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(statusCode).append(" OK\r\n");
        headers.put("Content-Length", String.valueOf(body.getBytes().length));
        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey()).append(": ")
                    .append(header.getValue()).append("\r\n");
        }
        response.append("\r\n").append(body);
        return response.toString();
    }
}
