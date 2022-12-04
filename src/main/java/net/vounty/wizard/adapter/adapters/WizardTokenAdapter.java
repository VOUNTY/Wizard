package net.vounty.wizard.adapter.adapters;

import lombok.Getter;
import net.vounty.wizard.adapter.WizardAdapter;
import net.vounty.wizard.repository.Repository;
import net.vounty.wizard.service.Wizard;
import net.vounty.wizard.token.Token;

import java.util.*;

@Getter
public class WizardTokenAdapter extends WizardAdapter implements TokenAdapter {

    private final List<Token> tokens;

    public WizardTokenAdapter(Wizard wizard) {
        super(wizard);
        this.tokens = new LinkedList<>();
    }

    @Override
    public void loadFromConfiguration() {
        this.getTokens().clear();
        this.getWizard().getConfigurationAdapter().getTokensConfiguration().getTokens()
                .forEach(wizardToken -> this.getTokens().add(wizardToken));
    }

    @Override
    public Boolean registerToken(Token token) {
        final var optionalToken = this.getToken(token.getUniqueId());
        if (optionalToken.isPresent())
            return false;

        this.getTokens().add(token);
        this.getWizard().reload();
        return true;
    }

    @Override
    public Boolean unregisterToken(Token token) {
        final var optionalToken = this.getToken(token.getUniqueId());
        if (optionalToken.isEmpty())
            return false;

        final var target = optionalToken.get();
        this.getTokens().remove(target);
        this.getWizard().reload();
        return true;
    }

    @Override
    public String[] getData(String authorization) {
        final var data = authorization
                .replace("Basic ", "")
                .replace("Bearer ", "");
        final var decoded = new String(Base64.getDecoder().decode(data));
        return decoded.split(":");
    }

    @Override
    public List<Repository> getRepositoriesFromToken(Token token) {
        return this.getWizard().getRepositoryAdapter().getRepositories().stream().filter(repository ->
                repository.getTokens().contains(token.getUniqueId())).toList();
    }

    @Override
    public Optional<Token> getToken(UUID uniqueId) {
        return this.getTokens().stream().filter(token ->
                token.getUniqueId().equals(uniqueId)).findFirst();
    }

    @Override
    public Optional<Token> getToken(String userName) {
        return this.getTokens().stream().filter(token ->
                token.getUserName().equals(userName) ||
                token.getUniqueId().toString().equals(userName)).findFirst();
    }

    @Override
    public Optional<Token> getToken(String userName, String password) {
        return this.getTokens().stream().filter(token ->
                token.getUserName().equals(userName) &&
                token.equalPassword(password)).findFirst();
    }

    @Override
    public Optional<Token> getTokenFromAuthorization(String authorization) {
        if (authorization == null || authorization.isEmpty())
            return Optional.empty();

        final var values = this.getData(authorization);
        return this.getToken(values[0], values[1]);
    }

}
