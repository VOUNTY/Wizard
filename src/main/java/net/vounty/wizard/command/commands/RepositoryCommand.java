package net.vounty.wizard.command.commands;

import net.vounty.wizard.command.Builder;
import net.vounty.wizard.command.WizardCommand;
import net.vounty.wizard.repository.WizardRepository;
import net.vounty.wizard.utils.enums.LogState;

import java.util.List;

@Builder(
        name = "repository",
        description = "Manage the repositories of this service.",
        aliases = { "r", "repositories", "repo", "repos" }
)
public class RepositoryCommand extends WizardCommand {

    @Override
    public Boolean execute(List<String> arguments) {

        switch (arguments.size()) {
            case 1 -> {
                switch (arguments.get(0).toLowerCase()) {
                    case "list", "l" -> {
                        final var repositories = this.getWizard().getRepositoryAdapter().getRepositories();
                        if (repositories.size() == 0) {
                            this.getLog().warn("Unable to find any repositories on this service.");
                            break;
                        }
                        this.getLog().info("List all §b{0}§r repositories:", repositories.size());
                        repositories.forEach(repository ->
                                this.getLog().info(" §b{0}§r (ID: §1{1}§r, Visible: §1{2}§r, MultipleDeployments: §1{3}§r)", repository.getName(), repository.getUniqueId(), repository.getVisible(), repository.getMultipleDeployments()));
                    }
                    default -> { return true; }
                }
            }
            case 2 -> {
                final var name = arguments.get(1);
                final var optionalRepository = this.getWizard().getRepositoryAdapter().getRepository(name);
                switch (arguments.get(0).toLowerCase()) {
                    case "about", "a" -> {
                        optionalRepository.ifPresentOrElse(repository -> {
                            this.getLog().info("Information about §1{0}§r:", repository.getName());
                            this.getLog().info("ID: §b{0}§r", repository.getUniqueId());
                            this.getLog().info("Visible: §b{0}§r", repository.getVisible());
                            final var tokens = repository.getTokens();
                            if (tokens.size() == 0) {
                                this.getLog().info("Tokens: §cEmpty§r");
                                return;
                            }
                            this.getLog().info("Tokens (§1{0}§r)", tokens.size());
                            tokens.forEach(uuid -> this.getLog().info(" §b{0}§r", uuid));
                        }, () -> this.getLog().error("Cannot find repository with name or id '§b{0}§r'", name));
                    }
                    case "create", "c" -> {
                        optionalRepository.ifPresentOrElse(repository -> this.getLog().warn("Repository with name '{0}' already exist.", name), () -> {
                            final var newRepository = new WizardRepository(name);
                            final var result = this.getWizard().getRepositoryAdapter().registerRepository(newRepository);
                            if (result)
                                this.getLog().info("Repository §b{0}§r was successfully created.", name);
                            else this.getLog().error("Unable to create repository. Try again later.");
                        });
                    }
                    case "drop", "d" -> {
                        optionalRepository.ifPresentOrElse(repository -> {
                            final var result = this.getWizard().getRepositoryAdapter().unregisterRepository(repository);
                            this.getLog().log(result ? LogState.INFO : LogState.ERROR,
                                    result ? "Repository §b{0}§r was successfully removed." : "Unable to remove repository §b{0}§r. Try again later.", name);
                        }, () -> this.getLog().error("Cannot find repository with name or id '§b{0}§r'", name));
                    }
                    default -> { return true; }
                }
            }
            case 3 -> {
                final var name = arguments.get(1);
                final var optionalRepository = this.getWizard().getRepositoryAdapter().getRepository(name);
                switch (arguments.get(0).toLowerCase()) {
                    case "toggle", "t" -> {
                        optionalRepository.ifPresentOrElse(repository -> {
                            switch (arguments.get(2).toLowerCase()) {
                                case "visible", "v" -> {
                                    repository.toggleVisible();
                                    this.getLog().info("The repository §b{0}§r is now §b{1}§r.", name, repository.getVisible());
                                }
                                case "multipledeployments", "multideploy", "multipledeploy", "multideployments", "md" -> {
                                    repository.toggleMultipleDeployments();
                                    this.getLog().info("Multiple deployments of repository §b{0}§r are now §b{1}§r.", name, repository.getMultipleDeployments() ? "enabled" : "disabled");
                                }
                                default -> this.getLog().warn("Use '§brepository§r' for more help.");
                            }
                        }, () -> this.getLog().error("Cannot find repository with name or id '§b{0}§r'", name));
                    }
                    default -> { return true; }
                }
            }
            case 4 -> {
                final var name = arguments.get(1);
                final var action = arguments.get(2);
                final var value = arguments.get(3);
                final var optionalRepository = this.getWizard().getRepositoryAdapter().getRepository(name);
                switch (arguments.get(0).toLowerCase()) {
                    case "modify", "m", "edit", "e" -> {
                        optionalRepository.ifPresentOrElse(repository -> {

                            switch (action.toLowerCase()) {
                                case "name", "n" -> {
                                    final var nameResult = repository.changeName(value);
                                    switch (nameResult) {
                                        case SUCCESS -> this.getLog().info("Name has been successfully changed.");
                                        case EQUAL_NAME -> this.getLog().warn("This name is already set.");
                                        case ALREADY_EXIST -> this.getLog().warn("Repository with name '{0}' already exist.", repository.getName());
                                    }
                                }
                                default -> this.getLog().warn("Use '§brepository§r' for more help.");
                            }

                        }, () -> this.getLog().error("Cannot find repository with name or id '§b{0}§r'", name));
                    }
                    case "token", "t" -> {
                        optionalRepository.ifPresentOrElse(repository -> {

                            final var optionalToken = this.getWizard().getTokenAdapter().getToken(value);
                            optionalToken.ifPresentOrElse(token -> {

                                switch (action.toLowerCase()) {
                                    case "add", "a" -> {
                                        final var tokenResult = repository.pushToken(token.getUniqueId());
                                        switch (tokenResult) {
                                            case SUCCESS -> this.getLog().info("Token §b{0}§r was added to repository §b{1}§r.", value, repository.getName());
                                            case ALREADY_EXIST -> this.getLog().warn("Token §b{0}§r already exist in repository §b{1}§r.", value, repository.getName());
                                        }
                                    }
                                    case "drop", "d" -> {
                                        final var tokenResult = repository.dropToken(token.getUniqueId());
                                        switch (tokenResult) {
                                            case SUCCESS -> this.getLog().info("Token §b{0}§r was removed from repository §b{1}§r.", value, repository.getName());
                                            case NOT_EXIST -> this.getLog().warn("Token §b{0}§r does not exist in repository §b{1}§r.", value, repository.getName());
                                        }
                                    }
                                    default -> this.getLog().warn("Use '§brepository§r' for more help.");
                                }

                            }, () -> this.getLog().error("Cannot find token with name or id '§b{0}§r'", value));

                        }, () -> this.getLog().error("Cannot find repository with name or id '§b{0}§r'", name));
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
        this.getLog().usage("Use '§brepository§r'");
        this.getLog().usage("Use '§brepository list§r'");
        this.getLog().usage("Use '§brepository toggle (§1Name§b) visible,multiDeployments§r'");
        this.getLog().usage("Use '§brepository about, create, drop (§1Name§b)§r'");
        this.getLog().usage("Use '§brepository modify (§1Name§b) name (§1Value§b)§r'");
        this.getLog().usage("Use '§brepository token (§1Name§b) add, drop (§1Token§b)§r'");
    }

}
