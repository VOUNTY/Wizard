package net.vounty.wizard.adapter.adapters;

import com.google.gson.Gson;
import net.vounty.wizard.adapter.Adapter;
import net.vounty.wizard.utils.config.*;
import net.vounty.wizard.utils.enums.PathState;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;

public interface ConfigurationAdapter extends Adapter {

    void generateConfigurations();
    void loadConfigurations();
    void saveConfigurations();

    void saveConfiguration(PathState path, Object document, Boolean override);
    void saveConfiguration(String path, Object document, Boolean override);

    <Value> Value readConfiguration(String path, Class<Value> type);
    <Value> Value readConfiguration(PathState path, Class<Value> type);
    <Value> Value readConfiguration(File file, Class<Value> type);
    <Value> Value readConfiguration(InputStream inputStream, Class<Value> type);
    Type readConfiguration(InputStream inputStream, Type type);
    Type readConfiguration(File file, Type type);

    Gson generateBuilder();
    WizardConfiguration getWizardConfiguration();
    TokensConfiguration getTokensConfiguration();
    RepositoriesConfiguration getRepositoriesConfiguration();
    WebsiteConfiguration getWebsiteConfiguration();
    SecureConfiguration getSecureConfiguration();

}
