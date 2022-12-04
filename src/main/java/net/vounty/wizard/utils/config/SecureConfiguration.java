package net.vounty.wizard.utils.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class SecureConfiguration {

    private final List<String> addresses;

    public static SecureConfiguration getDefault() {
        return new SecureConfiguration(new LinkedList<>());
    }

}
