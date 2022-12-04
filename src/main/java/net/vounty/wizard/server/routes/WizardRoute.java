package net.vounty.wizard.server.routes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.vounty.wizard.service.Wizard;
import spark.Request;
import spark.Response;
import spark.Route;

@Getter
@RequiredArgsConstructor
public abstract class WizardRoute implements Route {

    private final Wizard wizard;

    @Override
    public abstract Object handle(Request request, Response response) throws Exception;

}
