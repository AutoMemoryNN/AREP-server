package arep.basic.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WebFrameworkTest {

    @BeforeEach
    void setUp() {
        WebFramework.get("/testGet", (req, res) -> "GET Response");
        WebFramework.post("/testPost", (req, res) -> "POST Response");
    }

    @Test
    void testGetRouteRegistration() {
        HttpRequest req = new HttpRequest("/testGet", "");
        HttpResponse res = new HttpResponse();
        String response = WebFramework.getRoutes.get("/testGet").handle(req, res);
        assertEquals("GET Response", response);
    }

    @Test
    void testPostRouteRegistration() {
        HttpRequest req = new HttpRequest("/testPost", "");
        HttpResponse res = new HttpResponse();
        String response = WebFramework.postRoutes.get("/testPost").handle(req, res);
        assertEquals("POST Response", response);
    }

    @Test
    void testStaticFilesConfiguration() {
        WebFramework.staticfiles("src/static");
        assertEquals("src/static", WebFramework.staticFolder);
    }

    @Test
    void testGetNonExistentRoute() {
        HttpRequest req = new HttpRequest("/notFound", "");
        HttpResponse res = new HttpResponse();
        assertNull(WebFramework.getRoutes.get("/notFound"));
    }
}
