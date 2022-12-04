package net.vounty.wizard.utils.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LoggerConfiguration {

    private final List<String> messages;

}
