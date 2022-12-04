package net.vounty.wizard.utils.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.vounty.wizard.repository.WizardRepository;
import net.vounty.wizard.token.WizardToken;

import java.util.LinkedList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class RepositoriesConfiguration {

    private final List<WizardRepository> repositories;

    public static RepositoriesConfiguration getDefault() {
        return new RepositoriesConfiguration(List.of(
                new WizardRepository("releases"),
                new WizardRepository("snapshots")
        ));
    }

}
