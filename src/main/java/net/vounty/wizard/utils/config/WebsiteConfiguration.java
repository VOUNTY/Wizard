package net.vounty.wizard.utils.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class WebsiteConfiguration {

    private final String title, logo, favicon, description, color, keywords, repositoryName;
    private final Mantine mantine;

    public static WebsiteConfiguration getDefault() {
        return new WebsiteConfiguration(
                "| Wizard - Repository Management",
                "https://cdn.vounty.net/Logo.png",
                "https://cdn.vounty.net/favicons/favicon-96x96.png",
                "Powered by VountyWizard",
                "#2c4696",
                "wizard,repository,repository management",
                "Wizard",
                Mantine.getDefault());
    }

    @Getter
    @RequiredArgsConstructor
    public static class Mantine {

        private final List<String> primary, secondary;

        public static Mantine getDefault() {
            return new Mantine(
                    List.of("#fff", "#2c4696", "#2c4696", "#2c4696", "#2c4696", "#2c4696", "#2c4696", "#2c4696", "#2c4696", "#2c4696"),
                    List.of("#fff", "#4761b3", "#4761b3", "#4761b3", "#4761b3", "#4761b3", "#4761b3", "#4761b3", "#4761b3", "#4761b3")
            );
        }

    }

}
