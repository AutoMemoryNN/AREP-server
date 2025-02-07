package arep.basic.app;

@FunctionalInterface
public interface RouteHandler {
    /**
     * Método que procesa la petición y define la respuesta.
     * @param req Objeto HttpRequest con información de la petición.
     * @param res Objeto HttpResponse para configurar la respuesta.
     * @return El cuerpo de la respuesta (opcional, se puede configurar en res).
     */
    String handle(HttpRequest req, HttpResponse res);
}