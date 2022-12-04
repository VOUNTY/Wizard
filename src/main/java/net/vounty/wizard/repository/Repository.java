package net.vounty.wizard.repository;

import net.vounty.wizard.repository.content.Content;
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
    List<Content> getContents(String path);

    UUID getUniqueId();
    String getName();
    Visible getVisible();
    List<UUID> getTokens();
    String getFolder();

}
