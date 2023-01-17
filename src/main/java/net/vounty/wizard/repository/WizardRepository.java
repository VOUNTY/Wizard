package net.vounty.wizard.repository;

import lombok.Getter;
import lombok.Setter;
import net.vounty.wizard.repository.content.Content;
import net.vounty.wizard.repository.content.RepositoryContent;
import net.vounty.wizard.repository.content.RepositoryDependency;
import net.vounty.wizard.repository.deploy.Deploy;
import net.vounty.wizard.repository.deploy.RepositoryData;
import net.vounty.wizard.repository.deploy.RepositoryDeploy;
import net.vounty.wizard.server.routes.repository.RepositoryContentRoute;
import net.vounty.wizard.service.Wizard;
import net.vounty.wizard.token.Token;
import net.vounty.wizard.utils.config.DependencyConfiguration;
import net.vounty.wizard.utils.enums.Framework;
import net.vounty.wizard.utils.enums.PathState;
import net.vounty.wizard.utils.enums.Visible;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import spark.Spark;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class WizardRepository implements Repository {

    private final UUID uniqueId;
    private final List<UUID> tokens;
    private String name;
    private Visible visible;
    private Boolean multipleDeployments;

    private transient Map<String, Long> lastDeployment;
    private transient Map<String, Deploy> deployments;

    public WizardRepository(String name) {
        this.uniqueId = UUID.randomUUID();
        this.tokens = new LinkedList<>();
        this.name = name;
        this.visible = Visible.PUBLIC;
        this.multipleDeployments = false;
    }

    @Override
    public Repository updateMissingFields() {
        this.deployments = new LinkedHashMap<>();
        this.lastDeployment = new LinkedHashMap<>();

        if (this.multipleDeployments == null) this.multipleDeployments = false;
        if (this.visible == null) this.visible = Visible.PUBLIC;
        return this;
    }

    public Repository createFolder() {
        final var folder = new File(PathState.REPOSITORY.getPath().replace("%a", this.getName()));
        if (!folder.exists())
            folder.mkdirs();
        return this;
    }

    @Override
    public String getFolder() {
        return PathState.REPOSITORY.getPath().replace("%a", this.getName());
    }

    @Override
    public void download(HttpServletRequest request, Token token, Framework framework) throws IOException {
        final var path = request.getPathInfo();
        final var paths = path.split("/");
        final var fileName = paths[paths.length - 1];

        final var folderPath = path
                .replace(this.getName(), "")
                .replace(fileName, "");

        final var folder = new File(this.getFolder() + folderPath);
        final var filePath = this.getFolder() + path.replace(this.getName(), "");
        final var inputStream = request.getInputStream();

        final var address = request.getHeader("X-Real-IP");

        if (folder.exists() && !this.getMultipleDeployments()) {
            Wizard.getService().getLog().warn("User §b{0}§r (§1{1}§r) tried to deploy on §b{2}§r but multi deployments are disabled.",
                    token.getUserName(), address, this.getName());
            Spark.halt(403);
        }

        if (!this.lastDeployment.containsKey(address))
            Wizard.getService().getLog().info("User §b{0}§r (§1{1}§r) deploying to repository §b{2}§r...",
                    token.getUserName(), address, this.getName());

        final var deploy = this.deployments.getOrDefault(address, new RepositoryDeploy(folder, new LinkedList<>()));
        final var data = new RepositoryData(filePath, inputStream);
        final var list = deploy.getDataList();
        list.add(data);

        this.lastDeployment.put(address, System.currentTimeMillis());
        this.deployments.put(address, deploy);

        Executors.newSingleThreadScheduledExecutor().schedule(() -> {

            final var lastDeployed = this.lastDeployment.getOrDefault(address, System.currentTimeMillis());
            if (System.currentTimeMillis() - lastDeployed >= 1000) {
                this.flushDeployment(deploy, address, token, framework);
                this.deployments.remove(address);
                this.lastDeployment.remove(address);
            }

        }, 2, TimeUnit.SECONDS);
    }

    private void flushDeployment(Deploy deploy, String address, Token token, Framework framework) {
        final var folder = deploy.getFolder();
        final var list = deploy.getDataList();

        if (!folder.exists())
            folder.mkdirs();

        list.forEach(data -> {
            final var path = data.getPath();
            final var inputStream = data.getInputStream();

            try {
                final var file = new File(path);
                final var outputStream = new FileOutputStream(file);
                inputStream.transferTo(outputStream);
                outputStream.close();
                inputStream.close();
            } catch (Exception exception) {
                Wizard.getService().getLog().trace(exception);
            }
        });

        Wizard.getService().getLog().info("User §b{0}§r (§1{1}§r) deployed on §b{2}§r via §1{3}§r",
                token.getUserName(), address, this.getName(), framework);
    }

    @Override
    public RepositoryContentRoute.Result getRouteResult(String path) {
        return new RepositoryContentRoute.Result(
                this.getDependencyConfiguration(this.hasPomFile(path)),
                this.getContents(path)
        );
    }

    @Override
    public String viewFile(String path) {
        try {
            final var file = new File(this.getFolder() + path);
            if (!file.exists() || file.isDirectory())
                return null;

            final var names = List.of("pom", "xml");
            final var values = file.getName().split("\\.");
            if (!names.contains(values[values.length - 1].toLowerCase()))
                return null;

            final var inputReader = new FileReader(file);
            final var reader = new BufferedReader(inputReader);
            final var builder = new StringBuilder();

            var line = "";
            while ((line = reader.readLine()) != null)
                builder.append(line).append("\n");

            return builder.toString();
        } catch (Exception ignored) {
            return null;
        }
    }

    private DependencyConfiguration getDependencyConfiguration(File file) {
        final var document = this.readProjectObjectModel(file);
        if (Objects.isNull(document))
            return null;

        final var dependency = this.readDependency(document.getDocumentElement());
        return new DependencyConfiguration(dependency, this.readSubDependencies(document));
    }

    private RepositoryDependency readDependency(Element element) {
        final var groupId = element.getElementsByTagName("groupId").item(0).getTextContent();
        final var artifactId = element.getElementsByTagName("artifactId").item(0).getTextContent();
        final var version = element.getElementsByTagName("version").item(0).getTextContent();
        if (Objects.isNull(groupId) || Objects.isNull(artifactId) || Objects.isNull(version))
            throw new RuntimeException("Unable to parse dependency.");

        return RepositoryDependency.of(groupId, artifactId, version);
    }

    private List<RepositoryDependency> readSubDependencies(Document document) {
        final var list = new LinkedList<RepositoryDependency>();
        final var dependencies = (Element) document.getElementsByTagName("dependencies").item(0);
        final var items = dependencies.getElementsByTagName("dependency");
        for (int count = 0; count < items.getLength(); count++) {
            final var item = (Element) items.item(count);
            list.add(this.readDependency(item));
        }
        return list;
    }

    private Document readProjectObjectModel(File file) {
        try {
            if (!file.getName().endsWith(".pom"))
                return null;

            final var factory = DocumentBuilderFactory.newInstance();
            final var builder = factory.newDocumentBuilder();
            return builder.parse(file);
        } catch (Exception ignored) {
            return null;
        }
    }

    private File hasPomFile(String path) {
        final var folder = new File(this.getFolder() + path);
        if (!folder.exists())
            return null;

        final var contents = folder.listFiles();
        if (contents == null)
            return null;

        for (final var content : contents) {
            if (content.getName().endsWith(".pom"))
                return content;
        }
        return null;
    }

    private List<Content> getContents(String path) {
        try {
            final var array = new LinkedList<Content>();
            final var folder = new File(this.getFolder() + path);
            if (!folder.exists())
                return null;

            final var contents = folder.listFiles();
            if (contents == null)
                return null;

            for (final var content : contents) {
                final var size = content.isDirectory() ? -1 : Files.size(content.toPath());
                array.add(RepositoryContent.of(content.getName(), path, content.isFile(), size));
            }
            return array;
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public NameStatus changeName(String newName) {
        final var name = newName.trim();
        if (this.getName().equals(name))
            return NameStatus.EQUAL_NAME;

        final var optionalRepository = Wizard.getService().getRepositoryAdapter().getRepository(name);
        if (optionalRepository.isPresent())
            return NameStatus.ALREADY_EXIST;

        this.setName(name);
        return NameStatus.SUCCESS;
    }

    @Override
    public void toggleVisible() {
        switch (this.getVisible()) {
            case PUBLIC -> this.setVisible(Visible.HIDDEN);
            case HIDDEN -> this.setVisible(Visible.PRIVATE);
            case PRIVATE -> this.setVisible(Visible.PUBLIC);
        }
    }

    @Override
    public void toggleMultipleDeployments() {
        if (this.getMultipleDeployments() == null) {
            this.setMultipleDeployments(true);
            return;
        }
        this.setMultipleDeployments(!this.getMultipleDeployments());
    }

    @Override
    public TokenStatus pushToken(UUID uniqueId) {
        if (this.getTokens().contains(uniqueId))
            return TokenStatus.ALREADY_EXIST;

        this.getTokens().add(uniqueId);
        return TokenStatus.SUCCESS;
    }

    @Override
    public TokenStatus dropToken(UUID uniqueId) {
        if (!this.getTokens().contains(uniqueId))
            return TokenStatus.NOT_EXIST;

        this.getTokens().remove(uniqueId);
        return TokenStatus.SUCCESS;
    }

    public enum NameStatus { SUCCESS, EQUAL_NAME, ALREADY_EXIST }
    public enum TokenStatus { SUCCESS, ALREADY_EXIST, NOT_EXIST }

}
