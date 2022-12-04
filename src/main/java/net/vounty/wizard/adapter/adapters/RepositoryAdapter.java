package net.vounty.wizard.adapter.adapters;

import net.vounty.wizard.adapter.Adapter;
import net.vounty.wizard.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepositoryAdapter extends Adapter {

    void loadFromConfiguration();

    Boolean registerRepository(Repository repository);
    Boolean unregisterRepository(Repository repository);

    Optional<Repository> getRepository(String name);
    Optional<Repository> getRepository(UUID uniqueId);
    List<Repository> getRepositories();

}
