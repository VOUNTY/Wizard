package net.vounty.wizard;

import net.vounty.wizard.service.WizardService;
import net.vounty.wizard.utils.OptionSet;
import net.vounty.wizard.utils.enums.JavaVersion;

import java.util.List;
import java.util.Locale;

public class Bootstrap {

    public static void main(String[] args) {
        final var optionSet = OptionSet.of(List.of(args));

        var ignoreJavaVersion = false;
        if (optionSet.hasOption("ignoreJavaVersion")) {
            ignoreJavaVersion = optionSet.getOption("ignoreJavaVersion", Boolean.class, false);
            if (ignoreJavaVersion)
                System.out.println("Wizard may not start with the recommended Java version. \r\n Errors that occur for this reason are up to you.");
        }
        final var javaVersion = JavaVersion.getVersion();
        if (javaVersion == null) {
            System.out.println("How can you start this software without Java installed?");
            return;
        }
        if (javaVersion.equals(JavaVersion.UNKNOWN)) {
            System.out.println("Cannot detect Java version. Please re-run this program or make sure to correctly install Java on your system.");
            return;
        }
        if (!ignoreJavaVersion && !javaVersion.isHigherThan(JavaVersion.JAVA_16)) {
            System.out.println("Wizard requires Java 16 or above.");
            System.out.println("Detected version: Java " + javaVersion.getName());
            System.out.println("!! YOUR RISK !! With the parameter 'ignoreJavaVersion=true' it is possible to ignore this check.");
            return;
        }

        var ignoreRoot = false;
        if (optionSet.hasOption("ignoreRoot"))
            ignoreRoot = optionSet.getOption("ignoreRoot", Boolean.class, false);

        if (!ignoreRoot && System.getProperty("user.name").toLowerCase(Locale.ROOT).equals("root")) {
            System.out.println("Never launch software's with the authorized 'root' account. " + "\r\n " +
                    "If you still want to run this software with the 'root' account, " + "\n\n " +
                    "specify the parameter 'ignoreRoot=true' after the file.");
            return;
        }

        final var wizard = new WizardService();
        wizard.initialize(optionSet);
    }

}
