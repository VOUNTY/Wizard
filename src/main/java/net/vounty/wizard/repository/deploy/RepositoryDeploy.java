package net.vounty.wizard.repository.deploy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class RepositoryDeploy implements Deploy {

    private final File folder;
    private final List<Data> dataList;

}
