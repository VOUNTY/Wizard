package net.vounty.wizard.repository;

import net.vounty.wizard.server.routes.repository.RepositoryContentRoute;
import net.vounty.wizard.utils.enums.Visible;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

public interface Repository {

    void download(HttpServletRequest request) throws Exception;

    WizardRepository.NameStatus changeName(String newName);
    WizardRepository.TokenStatus pushToken(UUID uniqueId);
    WizardRepository.TokenStatus dropToken(UUID uniqueId);
    void toggleVisible();

    RepositoryContentRoute.Result getRouteResult(String path);

    UUID getUniqueId();
    String getName();
    Visible getVisible();
    List<UUID> getTokens();
    String getFolder();

}
