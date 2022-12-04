package net.vounty.wizard.command.commands;

import net.vounty.wizard.command.Builder;
import net.vounty.wizard.command.WizardCommand;

import java.util.List;

@Builder(
        name = "reload",
        description = "Reload the current Wizard service.",
        aliases = { "refresh", "rl" }
)
public class ReloadCommand extends WizardCommand {

    @Override
    public Boolean execute(List<String> arguments) {
        this.getWizard().reload();
        return false;
    }

    @Override
    public void displayUsage() {
        this.getLog().usage("Use '§breload§r'");
    }

}
