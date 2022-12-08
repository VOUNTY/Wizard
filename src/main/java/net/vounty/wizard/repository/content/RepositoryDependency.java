package net.vounty.wizard.repository.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class RepositoryDependency implements Dependency {

    private final String groupId, artifactId, version;

}
