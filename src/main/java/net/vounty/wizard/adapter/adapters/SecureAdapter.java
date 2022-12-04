package net.vounty.wizard.adapter.adapters;

import net.vounty.wizard.adapter.Adapter;

import java.util.List;

public interface SecureAdapter extends Adapter {

    WizardSecureAdapter.Result addAddress(String address);
    WizardSecureAdapter.Result dropAddress(String address);
    Boolean existAddress(String address);

    List<String> getAddresses();
    void loadFromConfiguration();

}
