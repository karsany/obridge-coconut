package org.obridge.coconut.incubator.commandserver;

import lombok.extern.slf4j.Slf4j;
import org.obridge.coconut.command.Command;
import org.obridge.coconut.command.CommandHandler;
import org.obridge.coconut.command.CommandResult;
import org.obridge.coconut.command.bus.CommandBus;
import org.obridge.coconut.converter.exception.ConverterNotFoundException;
import org.obridge.coconut.json.JsonObject;
import org.obridge.coconut.json.JsonString;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CommandServer<T extends Command & JsonString> implements CommandBus<T> {

    private final int port;
    private final NamingStrategy ns;
    private final Map<String, CommandHandler<T>> handlers = new HashMap<>();
    private final Map<String, Class<T>> commands = new HashMap<>();

    public CommandServer(int port, NamingStrategy ns) {
        this.port = port;
        this.ns = ns;
        Spark.port(this.port);

    }

    public CommandServer(int port) {
        this.port = port;
        this.ns = commandHandler -> "/" + commandHandler.getSimpleName().toLowerCase();
        Spark.port(this.port);

    }

    public void run() {
        Spark.get("/alive", (request, response) -> "OK");
    }

    @Override
    public void registerHandler(CommandHandler<T> ch) {
        this.registerHandler(ns.route(ch.getClass()), ch);
    }

    public void registerHandler(String route, CommandHandler<T> ch) {
        final Class<? extends CommandHandler> aClass = ch.getClass();
        final ParameterizedType genericSuperclass = (ParameterizedType) aClass.getGenericInterfaces()[0];
        final Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();
        final Class actualTypeArgument = (Class) actualTypeArguments[0];

        handlers.put(route, ch);
        commands.put(route, actualTypeArgument);
        Spark.post(route, (request, response) -> this.handle(route, request, response));
        log.info("Registetred: " + actualTypeArgument.getCanonicalName() + " to url " + route + " (POST)");
    }

    private String handle(String route, Request request, Response response) {
        final Class<T> aClass = this.commands.get(route);

        System.out.println(request.body());

        try {
            final CommandHandler<T> commandHandler = this.handlers.get(route);

            final CommandResult<T> cr = commandHandler.handle(JsonObject.create(aClass, request.body()));

            if (cr.result()) {
                response.status(200);
            } else {
                response.status(500);
            }

            return new JsonCommandResult(cr).toJson();


        } catch (ConverterNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public CommandResult handle(Command command) {
        throw new UnsupportedOperationException();
    }

}
