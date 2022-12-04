package net.vounty.wizard.command;

import net.vounty.wizard.log.Log;
import net.vounty.wizard.service.Wizard;

import java.util.List;

public interface Command {

    Boolean execute(List<String> arguments);
    void displayUsage();

    Command apply(Wizard wizard);

    String getName();
    String getDescription();
    List<String> getAliases();

    Log getLog();
    Wizard getWizard();

}
