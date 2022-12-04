package net.vounty.wizard.command;

import lombok.Getter;
import lombok.Setter;
import net.vounty.wizard.log.Log;
import net.vounty.wizard.service.Wizard;

import java.util.List;

@Getter
@Setter
public abstract class WizardCommand implements Command {

    private Wizard wizard;

    private final String name, description;
    private final List<String> aliases;

    public WizardCommand() {
        if (!this.getClass().isAnnotationPresent(Builder.class))
            throw new IllegalStateException("Builder annotation is required.");

        final var builder = this.getClass().getAnnotation(Builder.class);
        this.name = builder.name();
        this.description = builder.description();
        this.aliases = List.of(builder.aliases());
    }

    @Override
    public abstract Boolean execute(List<String> arguments);

    @Override
    public abstract void displayUsage();

    @Override
    public Log getLog() {
        return this.getWizard().getLog();
    }

    @Override
    public Command apply(Wizard wizard) {
        this.setWizard(wizard);
        return this;
    }

}
