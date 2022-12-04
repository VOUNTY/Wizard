package net.vounty.wizard.adapter.adapters;

import lombok.Getter;
import net.vounty.wizard.adapter.WizardAdapter;
import net.vounty.wizard.service.Wizard;

import java.util.LinkedList;
import java.util.List;

@Getter
public class WizardSecureAdapter extends WizardAdapter implements SecureAdapter {

    private final List<String> addresses;

    public WizardSecureAdapter(Wizard wizard) {
        super(wizard);
        this.addresses = new LinkedList<>();
    }

    @Override
    public void loadFromConfiguration() {
        this.getAddresses().clear();
        this.getAddresses().addAll(this.getWizard().getConfigurationAdapter().getSecureConfiguration().getAddresses());
    }

    @Override
    public Result addAddress(String address) {
        if (this.existAddress(address))
            return Result.ALREADY_EXIST;

        this.getAddresses().add(address);
        return Result.SUCCESS;
    }

    @Override
    public Result dropAddress(String address) {
        if (!this.existAddress(address))
            return Result.NOT_EXIST;

        this.getAddresses().remove(address);
        return Result.SUCCESS;
    }

    @Override
    public Boolean existAddress(String address) {
        return this.getAddresses().contains(address);
    }

    public enum Result { SUCCESS, NOT_EXIST, ALREADY_EXIST }

}
