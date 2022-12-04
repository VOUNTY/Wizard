package net.vounty.wizard.service;

import lombok.Getter;
import lombok.Setter;
import net.vounty.wizard.adapter.adapters.*;
import net.vounty.wizard.command.commands.*;
import net.vounty.wizard.console.Console;
import net.vounty.wizard.console.WizardConsole;
import net.vounty.wizard.log.Log;
import net.vounty.wizard.log.WizardLog;
import net.vounty.wizard.server.Server;
import net.vounty.wizard.server.WebServer;
import net.vounty.wizard.utils.OptionSet;
import net.vounty.wizard.utils.enums.PathState;

import java.util.Objects;

@Getter
@Setter
public class WizardService implements Wizard {

    private static WizardService service;
    private final Thread shutdownHook = new Thread(this::terminate);
    private OptionSet cachedOptionSet;

    private Log log;
    private Console console;
    private Server server;

    private CommandAdapter commandAdapter;
    private ConfigurationAdapter configurationAdapter;
    private TokenAdapter tokenAdapter;
    private RepositoryAdapter repositoryAdapter;
    private SecureAdapter secureAdapter;

    @Override
    public void initialize(OptionSet optionSet) {
        if (Objects.nonNull(WizardService.service))
            throw new IllegalStateException("Wizard is already initialized.");

        WizardService.service = this;

        PathState.createPaths();
        this.setCachedOptionSet(optionSet);
        this.setLog(new WizardLog(this));
        this.setConsole(new WizardConsole(this));
        this.setServer(new WebServer(this));

        this.setCommandAdapter(new WizardCommandAdapter(this));
        this.setConfigurationAdapter(new WizardConfigurationAdapter(this));
        this.setTokenAdapter(new WizardTokenAdapter(this));
        this.setRepositoryAdapter(new WizardRepositoryAdapter(this));
        this.setSecureAdapter(new WizardSecureAdapter(this));

        this.getConfigurationAdapter().generateConfigurations();
        this.getConfigurationAdapter().loadConfigurations();

        this.getRepositoryAdapter().loadFromConfiguration();
        this.getTokenAdapter().loadFromConfiguration();
        this.getSecureAdapter().loadFromConfiguration();

        this.getConsole().clearConsole();
        this.getConsole().initialize();
        this.getServer().start();

        this.getCommandAdapter().register(new HelpCommand());
        this.getCommandAdapter().register(new ExitCommand());
        this.getCommandAdapter().register(new ReloadCommand());
        this.getCommandAdapter().register(new ClearCommand());
        this.getCommandAdapter().register(new RepositoryCommand());
        this.getCommandAdapter().register(new TokenCommand());
        this.getCommandAdapter().register(new SecureCommand());

        Runtime.getRuntime().addShutdownHook(this.shutdownHook);
    }

    @Override
    public void reload() {
        if (Objects.isNull(WizardService.service))
            throw new IllegalStateException("Wizard is not initialized.");

        this.getConfigurationAdapter().generateConfigurations();
        this.getConfigurationAdapter().saveConfigurations();
        this.getConfigurationAdapter().loadConfigurations();

        this.getRepositoryAdapter().loadFromConfiguration();
        this.getTokenAdapter().loadFromConfiguration();
    }

    @Override
    public void terminate() {
        if (Objects.isNull(WizardService.service))
            throw new IllegalStateException("Wizard is not initialized.");

        this.getServer().stop();
        this.getConfigurationAdapter().saveConfigurations();
        this.getConsole().terminate();
        Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
        WizardService.service = null;
        System.exit(0);
    }

    @Override
    public Boolean isIPv4(String address) {
        return address.matches("^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$");
    }

    public static WizardService getWizardService() {
        return WizardService.service;
    }

}
