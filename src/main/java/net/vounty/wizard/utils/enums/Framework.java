package net.vounty.wizard.utils.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Framework {

    UNKNOWN(""),
    GRADLE("Gradle"),
    MAVEN("Maven"),
    SBT("SBT"),
    LEININGEN("Leiningen"),

    ;

    private final String name;

    public static Framework fetch(String content) {
        for (final var framework : Arrays.stream(Framework.values()).filter(framework ->
                !framework.equals(UNKNOWN)).toList()) {
            if (content.toLowerCase().contains(framework.getName().toLowerCase()))
                return framework;
        }
        return Framework.UNKNOWN;
    }

}
