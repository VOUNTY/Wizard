package net.vounty.wizard.token;

import java.util.UUID;

public interface Token {

    void toggleStatus();
    WizardToken.PasswordStatus changePassword(String newPassword);
    WizardToken.NameStatus changeName(String newName);

    Boolean equalPassword(String password);

    UUID getUniqueId();
    String getUserName();
    String getPassword();
    Boolean getActive();

}
