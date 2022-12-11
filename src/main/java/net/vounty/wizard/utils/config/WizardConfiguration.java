package net.vounty.wizard.utils.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WizardConfiguration {

    private final Protocol protocol;
    private final SSL ssl;
    private final Threads threads;

    public static WizardConfiguration getDefault() {
        return new WizardConfiguration(Protocol.getDefault(), SSL.getDefault(), Threads.getDefault());
    }

    @Getter
    @RequiredArgsConstructor
    public static class Protocol {

        private final String host;
        private final Integer port;

        public static Protocol getDefault() {
            return new Protocol("0.0.0.0", 4677);
        }

    }

    @Getter
    @RequiredArgsConstructor
    public static class SSL {

        private final Boolean enabled;
        private final Integer port;
        private final String keystoreFilePath, keystorePassword, truststoreFilePath, truststorePassword;

        public static SSL getDefault() {
            return new SSL(false, 443, ".//ssl-key", "YOUR_PASSWORD", ".//ssl-trust", "YOUR_PASSWORD");
        }

    }

    @Getter
    @RequiredArgsConstructor
    public static class Threads {

        private final Integer max, min, timeoutMillis;

        public static Threads getDefault() {
            return new Threads(8, 2, 30000);
        }

    }

}
