package net.vounty.wizard.adapter.adapters;

import lombok.Getter;
import net.vounty.wizard.adapter.WizardAdapter;
import net.vounty.wizard.repository.Repository;
import net.vounty.wizard.service.Wizard;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class WizardRepositoryAdapter extends WizardAdapter implements RepositoryAdapter {

    private final List<Repository> repositories;

    public WizardRepositoryAdapter(Wizard wizard) {
        super(wizard);
        this.repositories = new LinkedList<>();
    }

    @Override
    public void loadFromConfiguration() {
        this.getRepositories().clear();
        this.getWizard().getConfigurationAdapter().getRepositoriesConfiguration().getRepositories()
                .forEach(wizardRepository -> this.getRepositories().add(wizardRepository.createFolder().updateMissingFields()));
    }

    @Override
    public Boolean registerRepository(Repository repository) {
        final var optionalRepository = this.getRepository(repository.getUniqueId());
        if (optionalRepository.isPresent())
            return false;

        this.getRepositories().add(repository);
        this.getWizard().reload();
        return true;
    }

    @Override
    public Boolean unregisterRepository(Repository repository) {
        final var optionalRepository = this.getRepository(repository.getUniqueId());
        if (optionalRepository.isEmpty())
            return false;

        final var target = optionalRepository.get();
        this.getRepositories().remove(target);
        this.getWizard().reload();
        return true;
    }

    @Override
    public Optional<Repository> getRepository(String name) {
        return this.getRepositories().stream().filter(repository ->
                repository.getName().equals(name) ||
                repository.getUniqueId().toString().equals(name)).findFirst();
    }

    @Override
    public Optional<Repository> getRepository(UUID uniqueId) {
        return this.getRepositories().stream().filter(repository ->
                repository.getUniqueId().equals(uniqueId)).findFirst();
    }

}
