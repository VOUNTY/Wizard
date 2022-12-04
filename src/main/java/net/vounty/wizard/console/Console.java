package net.vounty.wizard.console;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

public interface Console {

    void initialize();
    void terminate();
    void execute();
    void clearConsole();

    String readLine();

    Terminal getTerminal();
    LineReader getLineReader();

}
