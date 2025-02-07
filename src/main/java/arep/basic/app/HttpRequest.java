package arep.basic.app;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String path;
    private String query;
    private Map<String, String> queryParams;

    public HttpRequest(String path, String query) {
        this.path = path;
        this.query = query;
        this.queryParams = parseQueryParams(query);
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    /**
     * Devuelve el valor del parámetro solicitado. Si no existe, retorna null.
     */
    public String getValue(String key) {
        return queryParams.get(key);
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.trim().isEmpty()) {
            return params;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            try {
                if (keyValue.length == 2) {
                    params.put(URLDecoder.decode(keyValue[0], "UTF-8"),
                               URLDecoder.decode(keyValue[1], "UTF-8"));
                } else if (keyValue.length == 1) {
                    params.put(URLDecoder.decode(keyValue[0], "UTF-8"), "");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return params;
    }
}
