package net.vounty.wizard.repository.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class RepositoryContent implements Content {

    private final String name, folder;
    private final Boolean isFile;
    private final Long size;

}
