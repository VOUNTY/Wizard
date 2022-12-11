package net.vounty.wizard.server.routes.repository;

import net.vounty.wizard.server.routes.WizardRoute;
import net.vounty.wizard.service.Wizard;
import net.vounty.wizard.utils.enums.Framework;
import net.vounty.wizard.utils.enums.Visible;
import spark.Request;
import spark.Response;
import spark.Spark;

public class RepositoryDeployRoute extends WizardRoute {

    public RepositoryDeployRoute(Wizard wizard) {
        super(wizard);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        response.type("application/json");
        final var repositoryParam = request.params(":repository");
        if (repositoryParam == null)
            Spark.halt(400);

        final var optionalRepository = this.getWizard().getRepositoryAdapter().getRepository(repositoryParam);
        if (optionalRepository.isEmpty())
            Spark.halt(404);

        final var repository = optionalRepository.get();
        final var authorization = request.headers("Authorization");
        if (authorization == null)
            Spark.halt(401);

        final var optionalToken = this.getWizard().getTokenAdapter().getTokenFromAuthorization(authorization);
        if (optionalToken.isEmpty())
            Spark.halt(401);

        final var token = optionalToken.get();
        if ((repository.getVisible().equals(Visible.PRIVATE) || repository.getVisible().equals(Visible.HIDDEN))
            && !repository.getTokens().contains(token.getUniqueId()))
            Spark.halt(403);

        final var xRealIp = request.headers("X-Real-IP");
        if (this.getWizard().getSecureAdapter().existAddress(xRealIp))
            Spark.halt(405);

        final var userAgent = request.headers("User-Agent");
        final var framework = Framework.fetch(userAgent);

        repository.download(request.raw());
        this.getWizard().getLog().info("User §b{0}§r (§1{1}§r) deploying on §b{2}§r via §1{3}§r",
                token.getUserName(), xRealIp, repository.getName(), framework);
        return null;
    }

}
