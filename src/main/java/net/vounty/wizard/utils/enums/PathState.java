package net.vounty.wizard.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum PathState {

    WIZARD(".//", true),
    LOGS(WIZARD.getPath() + "logs//", true),
    MODULES(WIZARD.getPath() + "logs//", true),
    REPOSITORIES(WIZARD.getPath() + "repositories//", true),
    CONFIGS(WIZARD.getPath() + "configs//", true),

    REPOSITORY(REPOSITORIES.getPath() + "%a//", false),

    WIZARD_CONFIG(WIZARD.getPath() + "configuration.json", false),

    TOKENS_CONFIG(CONFIGS.getPath() + "tokens.json", false),
    REPOSITORIES_CONFIG(CONFIGS.getPath() + "repositories.json", false),
    WEBSITE_CONFIG(CONFIGS.getPath() + "website.json", false),
    SECURE_CONFIG(CONFIGS.getPath() + "secure.json", false),

    ;

    private final String path;
    private final Boolean create;

    public File getTarget() {
        return new File(this.getPath());
    }

    public static void createPaths() {
        Arrays.stream(PathState.values())
                .filter(PathState::getCreate)
                .forEach(state -> state.getTarget().mkdirs());
    }

}
