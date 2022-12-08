package net.vounty.wizard.server.routes.repository;

import net.vounty.wizard.repository.content.Content;
import net.vounty.wizard.server.routes.WizardRoute;
import net.vounty.wizard.service.Wizard;
import net.vounty.wizard.utils.config.DependencyConfiguration;
import net.vounty.wizard.utils.enums.Visible;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.List;

public class RepositoryContentRoute extends WizardRoute {

    public RepositoryContentRoute(Wizard wizard) {
        super(wizard);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        response.type("application/json");
        final var repositoryName = request.params(":repository");
        if (repositoryName == null)
            Spark.halt(400);

        final var optionalRepository = this.getWizard().getRepositoryAdapter().getRepository(repositoryName);
        if (optionalRepository.isEmpty())
            Spark.halt(404);

        final var repository = optionalRepository.get();
        final var pathInfo = request.pathInfo()
                .replace("/s/" + repositoryName + "/", "")
                .replace("/", "//");
        if (repository.getVisible().equals(Visible.PUBLIC))
            return repository.getRouteResult(pathInfo);

        if (repository.getVisible().equals(Visible.PRIVATE)) {
            final var authorization = request.headers("Authorization");
            if (authorization == null)
                Spark.halt(401);

            final var optionalToken = this.getWizard().getTokenAdapter().getTokenFromAuthorization(authorization);
            if (optionalToken.isEmpty())
                Spark.halt(401);

            final var token = optionalToken.get();
            if (!repository.getTokens().contains(token.getUniqueId()))
                Spark.halt(403);

            return repository.getRouteResult(pathInfo);
        }
        Spark.halt(204);
        return null;
    }

    public record Result(DependencyConfiguration dependency, List<Content> contents) {}

}
