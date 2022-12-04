package net.vounty.wizard.adapter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.vounty.wizard.log.Log;
import net.vounty.wizard.service.Wizard;

@Getter
@RequiredArgsConstructor
public class WizardAdapter implements Adapter {

    private final Wizard wizard;

    public Log getLog() {
        return this.getWizard().getLog();
    }

}
