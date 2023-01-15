package net.vounty.wizard.repository.deploy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@Getter
@RequiredArgsConstructor
public class RepositoryData implements Data {

    private final String path;
    private final InputStream inputStream;

}
