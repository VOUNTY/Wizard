package net.vounty.wizard.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemColor {

    RESET("\033[0m", "§r"),

    BLACK("\033[0;30m", "§0"),
    RED("\033[0;31m", "§4"),
    GREEN("\033[0;32m", "§2"),
    YELLOW("\033[0;33m", "§6"),
    BLUE("\033[0;34m", "§1"),
    PURPLE("\033[0;35m", "§5"),
    CYAN("\033[0;36m", "§3"),
    WHITE("\033[0;37m", "§f"),
    GRAY("\033[1;30m", "§8"),

    BLACK_BRIGHT("\033[0;90m", "§7"),
    RED_BRIGHT("\033[0;91m", "§c"),
    GREEN_BRIGHT("\033[0;92m", "§a"),
    YELLOW_BRIGHT("\033[0;93m", "§e"),
    BLUE_BRIGHT("\033[0;94m", "§b"),
    PURPLE_BRIGHT("\033[0;95m", "§d"),
    WHITE_BRIGHT("\033[0;97m", "§f");

    private final String keyCode, colorCode;

    public static String replace(String message) {
        for (final var color : SystemColor.values())
            message = message.replace(color.getColorCode(), color.getKeyCode());

        return message;
    }

    public static String convert(String message) {
        for (final var color : SystemColor.values())
            message = message.replace(color.getColorCode(), "");
        return message;
    }

    public static SystemColor get(String name) {
        for (final var color : SystemColor.values()) {
            if (color.name().equals(name.toUpperCase())) {
                return color;
            }
        }
        return SystemColor.YELLOW;
    }

}