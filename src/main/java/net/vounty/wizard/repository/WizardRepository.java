package net.vounty.wizard.repository;

import lombok.Getter;
import lombok.Setter;
import net.vounty.wizard.repository.content.Content;
import net.vounty.wizard.repository.content.RepositoryContent;
import net.vounty.wizard.service.Wizard;
import net.vounty.wizard.utils.enums.PathState;
import net.vounty.wizard.utils.enums.Visible;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
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

        final var file = new File(this.getFolder() + path.replace(this.getName(), ""));
        final var inputStream = request.getInputStream();
        final var outputStream = new FileOutputStream(file);
        inputStream.transferTo(outputStream);

        outputStream.close();
        inputStream.close();
    }

    @Override
    public List<Content> getContents(String path) {
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
                array.add(RepositoryContent.of(content.getName(), content.isFile(), size));
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
