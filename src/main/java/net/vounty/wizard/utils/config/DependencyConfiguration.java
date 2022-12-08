package net.vounty.wizard.utils.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.vounty.wizard.repository.content.RepositoryDependency;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class DependencyConfiguration {

    private final RepositoryDependency dependency;
    private final List<RepositoryDependency> subDependencies;

}
