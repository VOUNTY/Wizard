package net.vounty.wizard.command.commands;

import net.vounty.wizard.command.Builder;
import net.vounty.wizard.command.WizardCommand;
import net.vounty.wizard.token.WizardToken;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

@Builder(
        name = "token",
        description = "Manage the tokens of this service.",
        aliases = { "t", "tokens" }
)
public class TokenCommand extends WizardCommand {

    @Override
    public Boolean execute(List<String> arguments) {

        switch (arguments.size()) {
            case 1 -> {
                switch (arguments.get(0).toLowerCase()) {
                    case "list", "l" -> {
                        final var tokens = this.getWizard().getTokenAdapter().getTokens();
                        if (tokens.size() == 0) {
                            this.getLog().warn("Unable to find any tokens on this service.");
                            break;
                        }
                        this.getLog().info("List all §b{0}§r tokens:", tokens.size());
                        tokens.forEach(token ->
                                this.getLog().info(" §b{0}§r (ID: {1}, Active: {2})", token.getUserName(), token.getUniqueId(), token.getActive()));
                    }
                    default -> { return true; }
                }
            }
            case 2 -> {
                final var name = arguments.get(1);
                final var optionalToken = this.getWizard().getTokenAdapter().getToken(name);
                switch (arguments.get(0).toLowerCase()) {
                    case "about", "a" -> {
                        optionalToken.ifPresentOrElse(token -> {
                            this.getLog().info("Information about §1{0}§r:", token.getUserName());
                            this.getLog().info("ID: §b{0}§r", token.getUniqueId());
                            this.getLog().info("Active: §b{0}§r", token.getActive() ? "Yes" : "No");
                            final var repositories = this.getWizard().getTokenAdapter().getRepositoriesFromToken(token);
                            if (repositories.size() == 0) {
                                this.getLog().info("Repositories: §cEmpty§r");
                                return;
                            }
                            this.getLog().info("Repositories (§1{0}§r)", repositories.size());
                            repositories.forEach(repository -> this.getLog().info(" §b{0}§r", repository.getName()));
                        }, () -> this.getLog().error("Cannot find token with name or id '§b{0}§r'", name));
                    }
                    case "create", "c" -> {
                        optionalToken.ifPresentOrElse(token -> this.getLog().warn("Token with name/id '{0}' already exist.", name), () -> {
                            final var password = RandomStringUtils.randomAlphabetic(75);
                            final var newToken = new WizardToken(name, password);
                            final var result = this.getWizard().getTokenAdapter().registerToken(newToken);
                            if (result)
                                this.getLog().info("Token §b{0}§r was successfully created. (Password: §c{1}§r)", name, password);
                            else this.getLog().error("Unable to create token. Try again later.");
                        });
                    }
                    case "drop", "d" -> {
                        optionalToken.ifPresentOrElse(token -> {
                            final var result = this.getWizard().getTokenAdapter().unregisterToken(token);
                            if (result)
                                this.getLog().info("Token §b{0}§r was successfully removed.", name);
                            else this.getLog().error("Unable to remove token. Try again later.");
                        }, () -> this.getLog().error("Cannot find token with name or id '§b{0}§r'", name));
                    }
                    case "toggle", "t" -> {
                        optionalToken.ifPresentOrElse(token -> {
                            token.toggleStatus();
                            this.getLog().info("The token §b{0}§r is now §b{1}§r.", name, token.getActive() ? "active" : "inactive");
                        }, () -> this.getLog().error("Cannot find token with name or id '§b{0}§r'", name));
                    }
                    default -> { return true; }
                }
            }
            case 4 -> {
                final var name = arguments.get(1);
                final var action = arguments.get(2);
                final var value = new String[] { arguments.get(3) };
                final var optionalToken = this.getWizard().getTokenAdapter().getToken(name);
                switch (arguments.get(0).toLowerCase()) {
                    case "modify", "m", "edit", "e" -> {
                        optionalToken.ifPresentOrElse(token -> {

                            switch (action.toLowerCase()) {
                                case "name", "n" -> {
                                    final var nameResult = token.changeName(value[0]);
                                    switch (nameResult) {
                                        case SUCCESS -> this.getLog().info("Name was successfully changed.");
                                        case EQUAL_NAME -> this.getLog().warn("This name is already set.");
                                        case ALREADY_EXIST -> this.getLog().warn("Token with name '{0}' already exist.", token.getUserName());
                                    }
                                }
                                case "password", "p" -> {
                                    final var isGenerate = value[0].equalsIgnoreCase("--generate");
                                    if (isGenerate) value[0] = RandomStringUtils.randomAlphabetic(75);
                                    final var passwordResult = token.changePassword(value[0]);
                                    switch (passwordResult) {
                                        case SUCCESS -> {
                                            this.getLog().info("Password was successfully changed.");
                                            if (isGenerate) this.getLog().info("Generated password§8: §c{0}§r", value[0]);
                                        }
                                        case EQUAL_PASSWORD -> this.getLog().warn("This password is already set.");
                                    }
                                }
                                default -> this.getLog().warn("Use '§btoken§r' for more help.");
                            }

                        }, () -> this.getLog().error("Cannot find token with name/id '{0}'", name));
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
        this.getLog().usage("Use '§btoken§r'");
        this.getLog().usage("Use '§btoken list§r'");
        this.getLog().usage("Use '§btoken about, create, drop, toggle (§1Name§b)§r'");
        this.getLog().usage("Use '§btoken modify (§1Name§b) name, password (§1Value§b)§r'");
    }

}
