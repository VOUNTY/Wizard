package net.vounty.wizard.repository;

import net.vounty.wizard.server.routes.repository.RepositoryContentRoute;
import net.vounty.wizard.token.Token;
import net.vounty.wizard.utils.enums.Framework;
import net.vounty.wizard.utils.enums.Visible;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

public interface Repository {

    Repository updateMissingFields();
    void download(HttpServletRequest request, Token token, Framework framework) throws Exception;
    String viewFile(String path);

    WizardRepository.NameStatus changeName(String newName);
    WizardRepository.TokenStatus pushToken(UUID uniqueId);
    WizardRepository.TokenStatus dropToken(UUID uniqueId);
    void toggleVisible();
    void toggleMultipleDeployments();

    RepositoryContentRoute.Result getRouteResult(String path);

    UUID getUniqueId();
    String getName();
    Visible getVisible();
    Boolean getMultipleDeployments();
    List<UUID> getTokens();
    String getFolder();

}
