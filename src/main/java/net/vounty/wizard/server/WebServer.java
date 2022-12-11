package net.vounty.wizard.server;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.vounty.wizard.server.jetty.EmptyLogger;
import net.vounty.wizard.server.routes.config.ConfigGetRoute;
import net.vounty.wizard.server.routes.repository.*;
import net.vounty.wizard.server.routes.token.TokenLoginRoute;
import net.vounty.wizard.server.transform.JsonTransformer;
import net.vounty.wizard.service.Wizard;
import org.eclipse.jetty.util.log.Log;
import spark.Spark;

import java.util.function.Consumer;

@Getter
@Setter
@RequiredArgsConstructor
public class WebServer implements Server {

    private final Wizard wizard;

    @Override
    public void start(Consumer<String> host) {
        Log.setLog(new EmptyLogger());

        final var gson = new Gson();
        final var configuration = this.getWizard().getConfigurationAdapter().getWizardConfiguration();
        Spark.ipAddress(configuration.getProtocol().getHost());
        Spark.port(configuration.getSsl().getEnabled() ? configuration.getSsl().getPort() : configuration.getProtocol().getPort());
        if (configuration.getSsl().getEnabled())
            Spark.secure(configuration.getSsl().getKeystoreFilePath(), configuration.getSsl().getKeystorePassword(), configuration.getSsl().getTruststoreFilePath(), configuration.getSsl().getTruststorePassword());

        Spark.threadPool(configuration.getThreads().getMax(), configuration.getThreads().getMin(), configuration.getThreads().getTimeoutMillis());
        Spark.staticFiles.location("/web");

        Spark.defaultResponseTransformer(new JsonTransformer());
        Spark.initExceptionHandler(exception -> this.getWizard().getLog().trace(exception));

        Spark.notFound((request, response) -> {
            response.type("application/json");
            return gson.toJson(new Status(404, "Not found."));
        });

        Spark.internalServerError((request, response) -> {
            response.type("application/json");
            return gson.toJson(new Status(500, "Server error"));
        });

        Spark.path("/api/", () -> {
            Spark.get("repository/list", new RepositoryListRoute(this.getWizard()));
            Spark.post("token/login", new TokenLoginRoute(this.getWizard()));
            Spark.get("config/website", new ConfigGetRoute(this.getWizard()));
        });
        Spark.get("/s/:repository/*", new RepositoryContentRoute(this.getWizard()));
        Spark.get("/v/:repository/*", new RepositoryViewRoute(this.getWizard()));
        Spark.get(":repository/*", new RepositoryFetchRoute(this.getWizard()));
        Spark.put(":repository/*", new RepositoryDeployRoute(this.getWizard()));

        Spark.init();
        host.accept("§b" + configuration.getProtocol().getHost() + "§r:§b" + Spark.port() + "§r");
    }

    @Override
    public void stop() {
        Spark.awaitStop();
    }

    record Status(Integer code, String message) {}

}
