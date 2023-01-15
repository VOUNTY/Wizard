package net.vounty.wizard.repository.deploy;

import java.io.File;
import java.util.List;

public interface Deploy {

    File getFolder();
    List<Data> getDataList();

}
