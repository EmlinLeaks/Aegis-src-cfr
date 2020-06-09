/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.event;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface EventHandler {
    public byte priority() default 0;
}

