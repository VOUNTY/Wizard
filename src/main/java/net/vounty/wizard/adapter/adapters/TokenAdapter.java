package net.vounty.wizard.adapter.adapters;

import net.vounty.wizard.adapter.Adapter;
import net.vounty.wizard.repository.Repository;
import net.vounty.wizard.token.Token;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenAdapter extends Adapter {

    void loadFromConfiguration();

    Boolean registerToken(Token token);
    Boolean unregisterToken(Token token);

    String[] getData(String authorization);
    List<Repository> getRepositoriesFromToken(Token token);

    Optional<Token> getToken(UUID uniqueId);
    Optional<Token> getToken(String userName);
    Optional<Token> getToken(String userName, String password);
    Optional<Token> getTokenFromAuthorization(String authorization);
    List<Token> getTokens();

}
