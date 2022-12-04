package net.vounty.wizard.service;

import net.vounty.wizard.adapter.adapters.*;
import net.vounty.wizard.console.Console;
import net.vounty.wizard.log.Log;
import net.vounty.wizard.utils.OptionSet;

public interface Wizard {

    void initialize(OptionSet optionSet);
    void reload();
    void terminate();

    Boolean isIPv4(String address);

    Log getLog();
    Console getConsole();

    CommandAdapter getCommandAdapter();
    ConfigurationAdapter getConfigurationAdapter();
    TokenAdapter getTokenAdapter();
    RepositoryAdapter getRepositoryAdapter();
    SecureAdapter getSecureAdapter();

    static Wizard getService() {
        return WizardService.getWizardService();
    }

}
