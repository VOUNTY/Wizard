package net.vounty.wizard.adapter.adapters;

import lombok.Getter;
import net.vounty.wizard.adapter.WizardAdapter;
import net.vounty.wizard.command.Command;
import net.vounty.wizard.service.Wizard;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Getter
public class WizardCommandAdapter extends WizardAdapter implements CommandAdapter {

    private final List<Command> commands;

    public WizardCommandAdapter(Wizard wizard) {
        super(wizard);
        this.commands = new LinkedList<>();
    }

    @Override
    public void register(Command command) {
        final var optionalCommand = this.getCommand(command.getName());
        if (optionalCommand.isEmpty())
            this.getCommands().add(command.apply(this.getWizard()));
    }

    @Override
    public void unregister(Command command) {
        final var optionalCommand = this.getCommand(command.getName());
        optionalCommand.ifPresent(target ->
                this.getCommands().remove(target));
    }

    @Override
    public Optional<Command> getCommand(String value) {
        return this.getCommands().stream().filter(command ->
                command.getName().equals(value.toLowerCase()) ||
                command.getAliases().contains(value.toLowerCase()))
                .findFirst();
    }

}
