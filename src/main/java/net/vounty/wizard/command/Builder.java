package net.vounty.wizard.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Builder {

    String name();
    String description();
    String[] aliases() default {};

}
