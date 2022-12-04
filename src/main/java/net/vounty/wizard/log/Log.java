package net.vounty.wizard.log;

import net.vounty.wizard.utils.enums.LogState;
import net.vounty.wizard.service.Wizard;

public interface Log {

    void info(String message);
    void info(String message, Object... replacements);

    void warn(String message);
    void warn(String message, Object... replacements);

    void error(String message);
    void error(String message, Object... replacements);

    void debug(String message);
    void debug(String message, Object... replacements);

    void usage(String message);
    void usage(String message, Object... replacements);

    void secure(String message);
    void secure(String message, Object... replacements);

    void trace(Throwable throwable);
    void trace(LogState state, Throwable throwable);

    void log(LogState state, String message);
    void log(LogState state, String message, Object... replacements);

    Wizard getWizard();

}
