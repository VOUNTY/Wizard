package net.vounty.wizard.server;

import net.vounty.wizard.service.Wizard;

public interface Server {

    void start();
    void stop();

    Wizard getWizard();

}
