package net.vounty.wizard.command.commands;

import net.vounty.wizard.command.Builder;
import net.vounty.wizard.command.WizardCommand;

import java.util.List;

@Builder(
        name = "exit",
        description = "Terminate this service.",
        aliases = { "terminate", "stop", "end" }
)
public class ExitCommand extends WizardCommand {

    @Override
    public Boolean execute(List<String> arguments) {
        this.getWizard().terminate();
        return false;
    }

    @Override
    public void displayUsage() {
        this.getLog().usage("Use '§bexit§r'");
    }

}
