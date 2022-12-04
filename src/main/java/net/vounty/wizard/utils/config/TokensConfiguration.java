package net.vounty.wizard.utils.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.vounty.wizard.token.WizardToken;

import java.util.LinkedList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class TokensConfiguration {

    private final List<WizardToken> tokens;

    public static TokensConfiguration getDefault() {
        return new TokensConfiguration(new LinkedList<>());
    }

}
