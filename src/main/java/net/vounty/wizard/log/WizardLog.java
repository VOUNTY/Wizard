package net.vounty.wizard.log;

import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.vounty.wizard.service.Wizard;
import net.vounty.wizard.utils.config.LoggerConfiguration;
import net.vounty.wizard.utils.enums.LogState;
import net.vounty.wizard.utils.enums.PathState;
import net.vounty.wizard.utils.enums.SystemColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Getter
@RequiredArgsConstructor
public class WizardLog implements Log {

    private final Wizard wizard;

    @Override
    public void info(String message) {
        this.info(message, Collections.emptyList());
    }

    @Override
    public void info(String message, Object... replacements) {
        this.log(LogState.INFO, message, replacements);
    }

    @Override
    public void warn(String message) {
        this.warn(message, Collections.emptyList());
    }

    @Override
    public void warn(String message, Object... replacements) {
        this.log(LogState.WARN, message, replacements);
    }

    @Override
    public void error(String message) {
        this.error(message, Collections.emptyList());
    }

    @Override
    public void error(String message, Object... replacements) {
        this.log(LogState.ERROR, message, replacements);
    }

    @Override
    public void debug(String message) {
        this.debug(message, Collections.emptyList());
    }

    @Override
    public void debug(String message, Object... replacements) {
        this.log(LogState.DEBUG, message, replacements);
    }

    @Override
    public void usage(String message) {
        this.usage(message, Collections.emptyList());
    }

    @Override
    public void usage(String message, Object... replacements) {
        this.log(LogState.USAGE, message, replacements);
    }

    @Override
    public void secure(String message) {
        this.secure(message, Collections.emptyList());
    }

    @Override
    public void secure(String message, Object... replacements) {
        this.log(LogState.SECURE, message, replacements);
    }

    @Override
    public void trace(Throwable throwable) {
        this.trace(LogState.TRACE, throwable);
    }

    @Override
    public void trace(LogState state, Throwable throwable) {
        System.out.println(this.format(state, throwable.getMessage()));
        final var traceElements = new LinkedList<>(Arrays.stream(throwable.getStackTrace()).toList());
        if (throwable.getCause() != null) traceElements.addAll(Arrays.stream(throwable.getCause().getStackTrace()).toList());
        if (throwable.getSuppressed() != null) {
            Arrays.stream(throwable.getSuppressed()).forEach(suppressedThrows -> {
                traceElements.addAll(Arrays.stream(suppressedThrows.getStackTrace()).toList());
            });
        }
        this.writeConfiguration(this.convertStackTraceToStrings(throwable.getMessage() != null ? throwable.getMessage() : "-/-", traceElements));
    }

    @Override
    public void log(LogState state, String message) {
        this.log(state, message, Collections.emptyList());
    }

    @Override
    public void log(LogState state, String message, Object... replacements) {
        final var replacedContent = this.replacePlaceHolders(message, replacements);
        final var endResult = this.format(state, replacements.length > 0 ? replacedContent : message);

//        this.getWizard().getConsole().getTerminal().writer().println(endResult);
        System.out.println(endResult);
        this.writeConfiguration(Collections.singletonList(replacedContent));
    }

    private void writeConfiguration(List<String> array) {
        final var folder = PathState.LOGS.getTarget();
        if (!folder.exists()) folder.mkdirs();

        final var fileFormat = new SimpleDateFormat("'//'MM-d-yyyy'.json'");
        final var file = new File(folder.getAbsolutePath() + fileFormat.format(new Date()));

        try {
            final var gsonInstance = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                    .setPrettyPrinting()
                    .setLenient()
                    .disableHtmlEscaping()
                    .serializeNulls()
                    .serializeSpecialFloatingPointValues()
                    .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                    .create();

            FileInputStream fileInputStream = null;
            InputStreamReader inputStreamReader = null;
            LoggerConfiguration configuration;
            if (!file.exists()) {
                configuration = new LoggerConfiguration(new LinkedList<>());
                file.createNewFile();
            } else {
                fileInputStream = new FileInputStream(file);
                inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                configuration = gsonInstance.fromJson(inputStreamReader, LoggerConfiguration.class);
            }

            array.forEach(message -> configuration.getMessages().add(SystemColor.convert(message)));

            final var writer = new PrintWriter(file, StandardCharsets.UTF_8);
            gsonInstance.toJson(configuration, writer);
            writer.close();

            if (fileInputStream != null)
                fileInputStream.close();

            if (inputStreamReader != null)
                inputStreamReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private List<String> convertStackTraceToStrings(String message, List<StackTraceElement> stackTraceElements) {
        final var array = new LinkedList<String>();
        array.add("********** TRACE [" + message + "] **********");
        stackTraceElements.forEach(stackTraceElement -> array.add(stackTraceElement.toString()));
        array.add("********** TRACE [" + message + "] **********");
        return array;
    }

    private String format(LogState state, String message) {
        final var format = " %clock% §7- " + state.format() + " §7>§r ";
        final var dateFormat = new SimpleDateFormat("hh:mm:ss a", Locale.US);
        return SystemColor.replace(format + message).replace("%clock%", dateFormat.format(new Date()));
    }

    private String replacePlaceHolders(String input, Object... replacements) {
        for (var count = 0; count < replacements.length; count++)
            input = input.replaceAll("\\{" + count + "}", String.valueOf(replacements[count]));
        return input;
    }

}
