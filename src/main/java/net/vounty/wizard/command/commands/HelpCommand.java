package net.vounty.wizard.command.commands;

import net.vounty.wizard.command.Builder;
import net.vounty.wizard.command.WizardCommand;

import java.util.List;

@Builder(
        name = "help",
        description = "List of all available commands.",
        aliases = { "?", "h" }
)
public class HelpCommand extends WizardCommand {

    @Override
    public Boolean execute(List<String> arguments) {
        this.getLog().info("Write '§b-H§r' after a command to receive detailed usage.");
        this.getWizard().getCommandAdapter().getCommands().forEach(command ->
            this.getLog().info(" §b{0}§r - {1} {2}", command.getName(), command.getDescription(), command.getAliases()));
        return false;
    }

    @Override
    public void displayUsage() {
        this.getLog().usage("Use '§bhelp§r'");
    }

}
