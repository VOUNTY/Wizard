package net.vounty.wizard.server.routes.repository;

import net.vounty.wizard.server.routes.WizardRoute;
import net.vounty.wizard.service.Wizard;
import net.vounty.wizard.utils.enums.Visible;
import spark.Request;
import spark.Response;

import java.util.LinkedList;
import java.util.UUID;

public class RepositoryListRoute extends WizardRoute {

    public RepositoryListRoute(Wizard wizard) {
        super(wizard);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        response.type("application/json");
        final var list = new LinkedList<Result>();

        final var authorization = request.headers("Authorization");
        final var optionalToken = this.getWizard().getTokenAdapter().getTokenFromAuthorization(authorization);

        this.getWizard().getRepositoryAdapter().getRepositories().stream().filter(repository -> {
            if (repository.getVisible().equals(Visible.PRIVATE)) {
                if (authorization == null || optionalToken.isEmpty())
                    return false;

                final var token = optionalToken.get();
                return token.getActive() && repository.getTokens().contains(token.getUniqueId());
            }
            return !repository.getVisible().equals(Visible.HIDDEN);
        }).forEach(repository -> list.add(new Result(repository.getName(), repository.getUniqueId())));
        return list;
    }

    record Result(String name, UUID uniqueId) {}

}
