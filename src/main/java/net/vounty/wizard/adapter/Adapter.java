package net.vounty.wizard.adapter;

import net.vounty.wizard.log.Log;
import net.vounty.wizard.service.Wizard;

public interface Adapter {

    Log getLog();
    Wizard getWizard();

}
