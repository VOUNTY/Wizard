package net.vounty.wizard.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Getter
@AllArgsConstructor
public enum OperationSystem {

    WINDOWS(Collections.singletonList("win")),
    MACOS(Collections.singletonList("mac")),
    SOLARIS(Collections.singletonList("sunos")),
    UNIX(Arrays.asList("nix", "nux", "aix")),
    UNDEFINED(Collections.emptyList());

    private final List<String> names;
    public static OperationSystem cachedSystem;

    public static Boolean isWindows() {
        return OperationSystem.is(OperationSystem.WINDOWS);
    }

    public static Boolean isMacOs() {
        return OperationSystem.is(OperationSystem.MACOS);
    }

    public static Boolean isSolaris() {
        return OperationSystem.is(OperationSystem.SOLARIS);
    }

    public static Boolean isUnix() {
        return OperationSystem.is(OperationSystem.UNIX);
    }

    public static Boolean is(OperationSystem operationSystem) {
        return OperationSystem.getOperationSystem().equals(operationSystem);
    }

    public static OperationSystem getOperationSystem() {
        final var operationSystemName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (OperationSystem.cachedSystem != null)
            return OperationSystem.cachedSystem;

        for (final var operationSystem : OperationSystem.values()) {
            for (final var name : operationSystem.names) {
                if (operationSystemName.contains(name))
                    return OperationSystem.cachedSystem = operationSystem;
            }
        }
        return OperationSystem.UNDEFINED;
    }

}