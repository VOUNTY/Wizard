package net.vounty.wizard.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JavaVersion {

    UNKNOWN(-1F, "Unknown"),
    JAVA_1_2(46F, "1.2"),
    JAVA_1_3(47F, "1.3"),
    JAVA_1_4(48F, "1.4"),
    JAVA_5(49F, "5"),
    JAVA_6(50F, "6"),
    JAVA_7(51F, "7"),
    JAVA_8(52F, "8"),
    JAVA_9(53F, "9"),
    JAVA_10(54F, "10"),
    JAVA_11(55F, "11"),
    JAVA_12(56F, "12"),
    JAVA_13(57F, "13"),
    JAVA_14(58F, "14"),
    JAVA_15(59F, "15"),
    JAVA_16(60F, "16"),
    JAVA_17(61F, "17"),
    JAVA_18(62F, "18"),
    JAVA_19(63F, "19");

    private final Float classVersion;
    private final String name;

    private static JavaVersion cachedJavaVersion;

    public Boolean isHigherThan(JavaVersion version) {
        final var currentVersion = JavaVersion.getVersion();
        return currentVersion.getClassVersion() >= version.getClassVersion();
    }

    public static JavaVersion getVersion() {
        if (JavaVersion.cachedJavaVersion != null)
            return JavaVersion.cachedJavaVersion;

        final var classVersion = Float.parseFloat(System.getProperty("java.class.version"));
        for (final var version : JavaVersion.values()) {
            if (version.getClassVersion().equals(classVersion)) {
                JavaVersion.cachedJavaVersion = version;
                return version;
            }
        }

        return JavaVersion.UNKNOWN;
    }

}
