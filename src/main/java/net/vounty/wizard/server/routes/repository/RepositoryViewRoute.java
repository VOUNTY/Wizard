package net.vounty.wizard.server.routes.repository;

import net.vounty.wizard.server.routes.WizardRoute;
import net.vounty.wizard.service.Wizard;
import net.vounty.wizard.utils.enums.Visible;
import spark.Request;
import spark.Response;
import spark.Spark;

public class RepositoryViewRoute extends WizardRoute {

    public RepositoryViewRoute(Wizard wizard) {
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
                .replace("/v/" + repositoryName + "/", "")
                .replace("/", "//");
        if (repository.getVisible().equals(Visible.PUBLIC)) {
            final var content = repository.viewFile(pathInfo);
            if (content == null)
                Spark.halt(500);

            return content;
        }

        final var authorization = request.headers("Authorization");
        if (authorization == null)
            Spark.halt(401);

        final var optionalToken = this.getWizard().getTokenAdapter().getTokenFromAuthorization(authorization);
        if (optionalToken.isEmpty())
            Spark.halt(401);

        final var token = optionalToken.get();
        if (!repository.getTokens().contains(token.getUniqueId()))
            Spark.halt(403);

        final var content = repository.viewFile(pathInfo);
        if (content == null)
            Spark.halt(500);

        return content;
    }

}
