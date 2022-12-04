package net.vounty.wizard.command.commands;

import net.vounty.wizard.command.Builder;
import net.vounty.wizard.command.WizardCommand;

import java.util.List;

@Builder(
        name = "clear",
        description = "Clear the console window.",
        aliases = { "clean", "c" }
)
public class ClearCommand extends WizardCommand {

    @Override
    public Boolean execute(List<String> arguments) {
        this.getWizard().getConsole().clearConsole();
        return false;
    }

    @Override
    public void displayUsage() {
        this.getLog().usage("Use '§bclear§r'");
    }

}
