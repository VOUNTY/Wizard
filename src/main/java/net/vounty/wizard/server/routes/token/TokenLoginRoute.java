package net.vounty.wizard.server.routes.token;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.vounty.wizard.server.routes.WizardRoute;
import net.vounty.wizard.service.Wizard;
import spark.Request;
import spark.Response;
import spark.Spark;

public class TokenLoginRoute extends WizardRoute {

    public TokenLoginRoute(Wizard wizard) {
        super(wizard);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        final var tokenBody = new Gson().fromJson(request.body(), TokenBody.class);
        if (tokenBody == null)
            Spark.halt(500);

        final var optionalToken = this.getWizard().getTokenAdapter().getToken(tokenBody.getUser(), tokenBody.getPassword());
        if (optionalToken.isEmpty())
            Spark.halt(401);

        Spark.halt(204);
        return null;
    }

    @Getter
    @AllArgsConstructor
    public class TokenBody {

        private final String user, password;

    }

}
