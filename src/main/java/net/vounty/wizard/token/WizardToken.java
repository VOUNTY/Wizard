package net.vounty.wizard.token;

import lombok.Getter;
import lombok.Setter;
import net.vounty.wizard.service.Wizard;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

@Getter
@Setter
public class WizardToken implements Token {

    private final UUID uniqueId;
    private Boolean active;
    private String userName, password;

    public WizardToken(String userName) {
        this.uniqueId = UUID.randomUUID();
        this.active = true;
        this.userName = userName;
    }

    @Override
    public Boolean equalPassword(String password) {
        if (this.getPassword() == null || this.getPassword().isEmpty())
            return false;

        return BCrypt.checkpw(password, this.getPassword());
    }

    @Override
    public void toggleStatus() {
        this.setActive(!this.getActive());
    }

    @Override
    public PasswordStatus changePassword(String newPassword) {
        final var password = newPassword.trim();
        if (this.equalPassword(newPassword))
            return PasswordStatus.EQUAL_PASSWORD;

        this.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));
        return PasswordStatus.SUCCESS;
    }

    @Override
    public NameStatus changeName(String newName) {
        final var name = newName.trim();
        if (this.getUserName().equals(name))
            return NameStatus.EQUAL_NAME;

        final var optionalToken = Wizard.getService().getTokenAdapter().getToken(name);
        if (optionalToken.isPresent())
            return NameStatus.ALREADY_EXIST;

        this.setUserName(name);
        return NameStatus.SUCCESS;
    }

    public enum PasswordStatus { SUCCESS, EQUAL_PASSWORD }
    public enum NameStatus { SUCCESS, EQUAL_NAME, ALREADY_EXIST }

}
