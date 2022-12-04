package net.vounty.wizard.server.routes.config;

import net.vounty.wizard.server.routes.WizardRoute;
import net.vounty.wizard.service.Wizard;
import spark.Request;
import spark.Response;

public class ConfigGetRoute extends WizardRoute {

    public ConfigGetRoute(Wizard wizard) {
        super(wizard);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        response.type("application/json");
        return this.getWizard().getConfigurationAdapter().getWebsiteConfiguration();
    }

}
