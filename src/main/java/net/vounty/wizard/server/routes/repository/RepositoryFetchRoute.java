package net.vounty.wizard.server.routes.repository;

import net.vounty.wizard.server.routes.WizardRoute;
import net.vounty.wizard.service.Wizard;
import net.vounty.wizard.utils.enums.Visible;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.File;
import java.io.FileInputStream;

public class RepositoryFetchRoute extends WizardRoute {

    public RepositoryFetchRoute(Wizard wizard) {
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
        if (!repository.getVisible().equals(Visible.PUBLIC)) {
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
        }

        final var xRealIp = request.headers("X-Real-IP");
        if (this.getWizard().getSecureAdapter().existAddress(xRealIp))
            Spark.halt(405);

        final var pathInfo = request.pathInfo();
        final var filePath = repository.getFolder() + pathInfo
                .replace("/" + repositoryParam + "/", "")
                .replace("/", "//");

        final var file = new File(filePath);
        if (!file.exists())
            Spark.halt(404);

        if (file.isDirectory())
            Spark.halt(400);

        response.type("application/octet-stream");
        response.status(200);
        response.raw().setCharacterEncoding("utf-8");

        final var inputStream = new FileInputStream(file);
        final var outputStream = response.raw().getOutputStream();
        inputStream.transferTo(outputStream);

        outputStream.close();
        inputStream.close();
        return null;
    }

}
