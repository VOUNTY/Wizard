package net.vounty.wizard.server;

import net.vounty.wizard.service.Wizard;

import java.util.function.Consumer;

public interface Server {

    void start(Consumer<String> host);
    void stop();

    Wizard getWizard();

}
