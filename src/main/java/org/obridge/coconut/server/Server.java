package org.obridge.coconut.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

@Slf4j
@RequiredArgsConstructor
public class Server {

    private final ServerConfiguration conf;

    private Method getCallMethod(Object service) {
        Method m = null;
        for (Method method : service.getClass().getMethods()) {
            if (method.getName().equals("call")) {
                m = method;
            }
        }
        if (m == null) {
            throw new IllegalStateException("Call method not found in " + service.getClass().getSimpleName());
        }
        return m;
    }

    private String camelToSnake(String str) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        str = str.replaceAll(regex, replacement).toLowerCase();
        return str;
    }

    public Server register(Type typ, Route route) {

        final String url = "/api/" + camelToSnake(route.getClass().getSimpleName()).replaceAll("_", "/").replaceAll("/api", "");
        log.info("Registering {} for URL {}", route.getClass().getSimpleName(), url);

        switch (typ) {
            case GET:
                Spark.get(url, route);
                break;
            case POST:
                Spark.post(url, route);
                break;
        }

        return this;

    }

    public Server register(Object service) {
        final String url = "/api/" + camelToSnake(service.getClass().getSimpleName()).replaceAll("_", "/").replaceAll("/api", "");
        log.info("Registering {} for URL {}", service.getClass().getSimpleName(), url);
        Method m = getCallMethod(service);

        if (m.getParameterCount() > 1) {
            throw new IllegalArgumentException("Too many arguments: " + m.getName());
        }


        boolean hasOutput = !m.getReturnType().getSimpleName().equals("void");
        boolean hasInput = m.getParameterCount() == 1;

        if (hasOutput && !hasInput) {

            log.info("GET " + url);

            Spark.get(url, (request, response) -> {
                response.header("Content-Type", "application/json; charset=utf-8");
                return conf.getObjectMapper().writeValueAsString(m.invoke(service));
            });

        } else if (!hasOutput && !hasInput) {

            log.info("GET " + url);

            Spark.get(url, (request, response) -> {
                m.invoke(service);
                return "";
            });

        } else if (!hasOutput && hasInput) {

            final Class<?> inputType = m.getParameters()[0].getType();

            if (inputType != spark.Request.class) {

                log.info("POST " + url);

                Spark.post(url, "application/json", (request, response) -> {
                    final Object o = conf.getObjectMapper().readValue(request.body(), inputType);
                    m.invoke(service, o);
                    return "";
                });
            } else {

                log.info("GET " + url);

                Spark.get(url, "application/json", (request, response) -> {
                    m.invoke(service, request);
                    return "";
                });

            }

        } else if (hasOutput && hasInput) {

            final Class<?> inputType = m.getParameters()[0].getType();

            if (inputType != spark.Request.class) {

                log.info("POST " + url);

                Spark.post(url, "application/json", (request, response) -> {
                    final Object o = conf.getObjectMapper().readValue(request.body(), inputType);
                    response.header("Content-Type", "application/json; charset=utf-8");
                    return conf.getObjectMapper().writeValueAsString(m.invoke(service, o));
                });
            } else {

                log.info("GET " + url);

                Spark.get(url, "application/json", (request, response) -> {
                    response.header("Content-Type", "application/json; charset=utf-8");
                    return conf.getObjectMapper().writeValueAsString(m.invoke(service, request));
                });
            }


        } else {
            throw new UnsupportedOperationException("Not implemented yet.");
        }

        return this;
    }


    public void finish() {
        Spark.exception(PermissionDeniedException.class, (e, request, response) -> {
            response.status(403);
            response.body(e.getMessage());
        });
        //Spark.get("/*", this::handleVueStatic);
    }

    private String handleVueStatic(Request request, Response response) throws IOException {
        final InputStream resourceAsStream = this.getClass().getResourceAsStream("/static/index.html");
        return IOUtils.toString(resourceAsStream);
    }

    public Server init() {
        Spark.staticFileLocation("/static");
        Spark.port(conf.getPort());
        return this;
    }

    public enum Type {

        GET,
        POST

    }
}
