package com.example.todo;

// import com.example.todo.config.DatabaseConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Application {

  public static HttpServer createServer(int port) {
    URI baseUri = URI.create("http://127.0.0.1:" + port + "/");

    ResourceConfig config = new ResourceConfig()
        .packages("com.example.todo")
        .register(JacksonFeature.class);

    return GrizzlyHttpServerFactory.createHttpServer(
        baseUri,
        config);
  }

  public static void main(String[] args) {
    int port = Integer.parseInt(
        System.getProperty("server.port", "8080"));

    HttpServer server = createServer(port);

    System.out.println(
        "Todo API running at http://127.0.0.1:" + port);
    System.out.println("Press CTRL+C to stop.");

    Runtime.getRuntime().addShutdownHook(
        new Thread(server::shutdownNow));
  }
}
