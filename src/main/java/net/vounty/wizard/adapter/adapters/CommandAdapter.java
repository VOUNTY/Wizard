package net.vounty.wizard.adapter.adapters;

import net.vounty.wizard.adapter.Adapter;
import net.vounty.wizard.command.Command;

import java.util.List;
import java.util.Optional;

public interface CommandAdapter extends Adapter {

    void register(Command command);
    void unregister(Command command);

    Optional<Command> getCommand(String value);
    List<Command> getCommands();

}
