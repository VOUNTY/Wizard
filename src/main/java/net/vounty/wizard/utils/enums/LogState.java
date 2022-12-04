package net.vounty.wizard.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogState {

    INFO("§1"),
    WARN("§6"),
    USAGE("§b"),
    ERROR("§4"),
    DEBUG("§d"),
    TRACE("§5"),
    SECURE("§2");

    private final String color;

    public String format() {
        return this.getColor() + this.name();
    }

}