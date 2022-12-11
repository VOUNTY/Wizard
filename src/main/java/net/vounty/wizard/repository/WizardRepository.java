package net.vounty.wizard.repository;

import lombok.Getter;
import lombok.Setter;
import net.vounty.wizard.repository.content.Content;
import net.vounty.wizard.repository.content.RepositoryContent;
import net.vounty.wizard.repository.content.RepositoryDependency;
import net.vounty.wizard.server.routes.repository.RepositoryContentRoute;
import net.vounty.wizard.service.Wizard;
import net.vounty.wizard.utils.config.DependencyConfiguration;
import net.vounty.wizard.utils.enums.PathState;
import net.vounty.wizard.utils.enums.Visible;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class WizardRepository implements Repository {

    private final UUID uniqueId;
    private final List<UUID> tokens;
    private String name;
    private Visible visible;

    public WizardRepository(String name) {
        this.uniqueId = UUID.randomUUID();
        this.tokens = new LinkedList<>();
        this.name = name;
        this.visible = Visible.PUBLIC;
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
    public void download(HttpServletRequest request) throws Exception {
        final var path = request.getPathInfo();
        final var paths = path.split("/");
        final var fileName = paths[paths.length - 1];

        final var folderPath = path
                .replace(this.getName(), "")
                .replace(fileName, "");

        final var folder = new File(this.getFolder() + folderPath);
        folder.mkdirs();

        final var filePath = this.getFolder() + path.replace(this.getName(), "");
        final var file = new File(filePath);
        final var inputStream = request.getInputStream();
        final var outputStream = new FileOutputStream(file);
        inputStream.transferTo(outputStream);

        outputStream.close();
        inputStream.close();
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
