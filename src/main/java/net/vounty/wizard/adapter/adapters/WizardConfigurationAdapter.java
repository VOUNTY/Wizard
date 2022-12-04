package net.vounty.wizard.adapter.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import lombok.Getter;
import lombok.Setter;
import net.vounty.wizard.adapter.WizardAdapter;
import net.vounty.wizard.repository.WizardRepository;
import net.vounty.wizard.token.WizardToken;
import net.vounty.wizard.utils.config.*;
import net.vounty.wizard.utils.enums.PathState;
import net.vounty.wizard.service.Wizard;

import java.io.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
public class WizardConfigurationAdapter extends WizardAdapter implements ConfigurationAdapter {

    private WizardConfiguration wizardConfiguration;
    private TokensConfiguration tokensConfiguration;
    private RepositoriesConfiguration repositoriesConfiguration;
    private WebsiteConfiguration websiteConfiguration;
    private SecureConfiguration secureConfiguration;

    public WizardConfigurationAdapter(Wizard wizard) {
        super(wizard);
    }

    @Override
    public void generateConfigurations() {
        this.saveConfiguration(PathState.WIZARD_CONFIG, WizardConfiguration.getDefault(), false);
        this.saveConfiguration(PathState.TOKENS_CONFIG, TokensConfiguration.getDefault(), false);
        this.saveConfiguration(PathState.REPOSITORIES_CONFIG, RepositoriesConfiguration.getDefault(), false);
        this.saveConfiguration(PathState.WEBSITE_CONFIG, WebsiteConfiguration.getDefault(), false);
        this.saveConfiguration(PathState.SECURE_CONFIG, SecureConfiguration.getDefault(), false);
    }

    @Override
    public void loadConfigurations() {
        this.setWizardConfiguration(this.readConfiguration(PathState.WIZARD_CONFIG, WizardConfiguration.class));
        this.setTokensConfiguration(this.readConfiguration(PathState.TOKENS_CONFIG, TokensConfiguration.class));
        this.setRepositoriesConfiguration(this.readConfiguration(PathState.REPOSITORIES_CONFIG, RepositoriesConfiguration.class));
        this.setWebsiteConfiguration(this.readConfiguration(PathState.WEBSITE_CONFIG, WebsiteConfiguration.class));
        this.setSecureConfiguration(this.readConfiguration(PathState.SECURE_CONFIG, SecureConfiguration.class));
    }

    @Override
    public void saveConfigurations() {
        this.saveConfiguration(PathState.TOKENS_CONFIG, new TokensConfiguration(this.getWizard().getTokenAdapter().getTokens().stream().map(token -> (WizardToken) token).toList()), true);
        this.saveConfiguration(PathState.REPOSITORIES_CONFIG, new RepositoriesConfiguration(this.getWizard().getRepositoryAdapter().getRepositories().stream().map(repository -> (WizardRepository) repository).toList()), true);
        this.saveConfiguration(PathState.SECURE_CONFIG, new SecureConfiguration(this.getWizard().getSecureAdapter().getAddresses()), true);
    }

    @Override
    public void saveConfiguration(PathState path, Object document, Boolean override) {
        this.saveConfiguration(path.getPath(), document, override);
    }

    @Override
    public void saveConfiguration(String path, Object document, Boolean override) {
        try {
            final var currentFile = new File(path);
            if (override) {
                if (!currentFile.exists()) currentFile.createNewFile();
                final var writer = new PrintWriter(currentFile, StandardCharsets.UTF_8);
                this.generateBuilder().toJson(document, writer);
                writer.close();
                return;
            }
            if (!currentFile.exists()) {
                currentFile.createNewFile();
                final var writer = new PrintWriter(currentFile, StandardCharsets.UTF_8);
                this.generateBuilder().toJson(document, writer);
                writer.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public <Value> Value readConfiguration(String filePath, Class<Value> type) {
        try {
            return this.readConfiguration(new FileInputStream(filePath), type);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public <Value> Value readConfiguration(PathState path, Class<Value> type) {
        try {
            return this.readConfiguration(new FileInputStream(path.getPath()), type);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public <Value> Value readConfiguration(InputStream inputStream, Class<Value> type) {
        try {
            final var inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            return generateBuilder().fromJson(inputStreamReader, type);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public <Value> Value readConfiguration(File file, Class<Value> type) {
        try {
            final var inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            return generateBuilder().fromJson(inputStreamReader, type);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Type readConfiguration(InputStream inputStream, Type type) {
        try {
            final var inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            return generateBuilder().fromJson(inputStreamReader, type);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Type readConfiguration(File file, Type type) {
        try {
            final var inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            return generateBuilder().fromJson(inputStreamReader, type);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Gson generateBuilder() {
        return new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .serializeNulls()
                .serializeSpecialFloatingPointValues()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .create();
    }

}
