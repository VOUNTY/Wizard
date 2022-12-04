package net.vounty.wizard.command.commands;

import net.vounty.wizard.command.Builder;
import net.vounty.wizard.command.WizardCommand;

import java.util.List;

@Builder(
        name = "secure",
        description = "Manage the security system.",
        aliases = { "s", "security" }
)
public class SecureCommand extends WizardCommand {

    @Override
    public Boolean execute(List<String> arguments) {

        switch (arguments.size()) {
            case 1 -> {
                switch (arguments.get(0).toLowerCase()) {
                    case "list", "l" -> {
                        final var addresses = this.getWizard().getSecureAdapter().getAddresses();
                        if (addresses.size() == 0) {
                            this.getLog().warn("Unable to find any addresses on this service.");
                            break;
                        }
                        this.getLog().info("List all §b{0}§r blocked addresses:", addresses.size());
                        addresses.forEach(address -> this.getLog().info(" §b{0}§r", address));
                    }
                    default -> { return true; }
                }
            }
            case 2 -> {
                final var address = arguments.get(1);
                if (!this.getWizard().isIPv4(address)) {
                    this.getLog().error("§b{0}§r is not a valid IPv4 address.", address);
                    return false;
                }
                switch (arguments.get(0).toLowerCase()) {
                    case "add", "a" -> {
                        final var result = this.getWizard().getSecureAdapter().addAddress(address);
                        switch (result) {
                            case SUCCESS -> this.getLog().info("Address §b{0}§r was successfully added.", address);
                            case ALREADY_EXIST -> this.getLog().warn("Address §b{0}§r already exist.", address);
                        }
                    }
                    case "drop", "d" -> {
                        final var result = this.getWizard().getSecureAdapter().dropAddress(address);
                        switch (result) {
                            case SUCCESS -> this.getLog().info("Address §b{0}§r was successfully removed.", address);
                            case NOT_EXIST -> this.getLog().warn("Address §b{0}§r does not exist.", address);
                        }
                    }
                    default -> { return true; }
                }
            }
            default -> { return true; }
        }

        return false;
    }

    @Override
    public void displayUsage() {
        this.getLog().usage("Use '§bsecure§r'");
        this.getLog().usage("Use '§bsecure list§r'");
        this.getLog().usage("Use '§bsecure add, drop (§1IPv4§b)§r'");
    }

}
