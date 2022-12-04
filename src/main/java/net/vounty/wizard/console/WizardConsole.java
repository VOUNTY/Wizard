package net.vounty.wizard.console;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.vounty.wizard.service.Wizard;
import net.vounty.wizard.utils.enums.OperationSystem;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@RequiredArgsConstructor
public class WizardConsole implements Console {

    private final Wizard wizard;

    private Terminal terminal;
    private LineReader lineReader;

    private ScheduledFuture<?> task;

    @Override
    public void initialize() {
        try {
            if (this.getTerminal() == null) {
                this.setTerminal(TerminalBuilder.builder()
                        .system(true)
                        .dumb(true)
                        .streams(System.in, System.out)
                        .encoding(StandardCharsets.UTF_8)
                        .build());
                this.setLineReader(LineReaderBuilder.builder()
                        .terminal(this.terminal)
                        .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                        .option(LineReader.Option.AUTO_REMOVE_SLASH, false)
                        .option(LineReader.Option.INSERT_TAB, false)
                        .option(LineReader.Option.INSERT_BRACKET, true)
                        .variable(LineReader.INDENTATION, 2)
                        .build());
                this.execute();
                System.setOut(new PrintStream(this.getTerminal().output()));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void execute() {
        this.setTask(Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {

            final var message = this.readLine();
            if (message == null)
                return;

            final var arguments = message.split(" ");
            final var name = arguments[0];
            if (name.length() == 0)
                return;

            final var optionalCommand = this.getWizard().getCommandAdapter().getCommand(name);
            optionalCommand.ifPresentOrElse(command -> {
                if (arguments.length >= 2 && arguments[1].equalsIgnoreCase("-h")) {
                    command.displayUsage();
                    return;
                }
                final var result = command.execute(List.of(this.buildArray(arguments)));
                if (result)
                    this.getWizard().getLog().warn("Please use '§b{0} -h§r' for more help.", name);
            }, () -> this.getWizard().getLog().error("Command '§b{0}§r' not found", name));

        }, 0, 100, TimeUnit.MILLISECONDS));
    }

    @Override
    public void terminate() {
        this.getTask().cancel(false);
    }

    @Override
    public void clearConsole() {
        try {
            final var arguments = OperationSystem.isWindows() ? List.of("cmd", "/c", "cls") : List.of("clear");
            new ProcessBuilder(arguments).inheritIO().start().waitFor();
            this.printBig();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void printBig() {
        this.getWizard().getLog().info(" _       ___                      __");
        this.getWizard().getLog().info("| |     / (_)___  ____ __________/ /");
        this.getWizard().getLog().info("| | /| / / /_  / / __ `/ ___/ __  /");
        this.getWizard().getLog().info("| |/ |/ / / / /_/ /_/ / /  / /_/ /");
        this.getWizard().getLog().info("|__/|__/_/ /___/\\__,_/_/   \\__,_/");
        this.getWizard().getLog().info("Repository Management by §bVountyNetwork§r");
        this.getWizard().getLog().info("Use '§bhelp§r' for more help.");
    }

    private String[] buildArray(String[] array) {
        final var elements = new String[array.length - 1];
        System.arraycopy(array, 1, elements, 0, array.length - 1);
        return elements;
    }

    @Override
    public String readLine() {
        return this.getLineReader().readLine(" > ", null, (MaskingCallback) null, null);
    }

}
