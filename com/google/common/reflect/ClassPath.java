/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

@Beta
public final class ClassPath {
    private static final Logger logger = Logger.getLogger((String)ClassPath.class.getName());
    private static final Predicate<ClassInfo> IS_TOP_LEVEL = new Predicate<ClassInfo>(){

        public boolean apply(ClassInfo info) {
            if (ClassInfo.access$000((ClassInfo)info).indexOf((int)36) != -1) return false;
            return true;
        }
    };
    private static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on((String)" ").omitEmptyStrings();
    private static final String CLASS_FILE_NAME_EXTENSION = ".class";
    private final ImmutableSet<ResourceInfo> resources;

    private ClassPath(ImmutableSet<ResourceInfo> resources) {
        this.resources = resources;
    }

    public static ClassPath from(ClassLoader classloader) throws IOException {
        DefaultScanner scanner = new DefaultScanner();
        scanner.scan((ClassLoader)classloader);
        return new ClassPath(scanner.getResources());
    }

    public ImmutableSet<ResourceInfo> getResources() {
        return this.resources;
    }

    public ImmutableSet<ClassInfo> getAllClasses() {
        return FluentIterable.from(this.resources).filter(ClassInfo.class).toSet();
    }

    public ImmutableSet<ClassInfo> getTopLevelClasses() {
        return FluentIterable.from(this.resources).filter(ClassInfo.class).filter(IS_TOP_LEVEL).toSet();
    }

    public ImmutableSet<ClassInfo> getTopLevelClasses(String packageName) {
        Preconditions.checkNotNull(packageName);
        ImmutableSet.Builder<E> builder = ImmutableSet.builder();
        Iterator i$ = this.getTopLevelClasses().iterator();
        while (i$.hasNext()) {
            ClassInfo classInfo = (ClassInfo)i$.next();
            if (!classInfo.getPackageName().equals((Object)packageName)) continue;
            builder.add((Object)classInfo);
        }
        return builder.build();
    }

    public ImmutableSet<ClassInfo> getTopLevelClassesRecursive(String packageName) {
        Preconditions.checkNotNull(packageName);
        String packagePrefix = packageName + '.';
        ImmutableSet.Builder<E> builder = ImmutableSet.builder();
        Iterator i$ = this.getTopLevelClasses().iterator();
        while (i$.hasNext()) {
            ClassInfo classInfo = (ClassInfo)i$.next();
            if (!classInfo.getName().startsWith((String)packagePrefix)) continue;
            builder.add((Object)classInfo);
        }
        return builder.build();
    }

    @VisibleForTesting
    static String getClassName(String filename) {
        int classNameEnd = filename.length() - CLASS_FILE_NAME_EXTENSION.length();
        return filename.substring((int)0, (int)classNameEnd).replace((char)'/', (char)'.');
    }

    static /* synthetic */ Logger access$100() {
        return logger;
    }

    static /* synthetic */ Splitter access$200() {
        return CLASS_PATH_ATTRIBUTE_SEPARATOR;
    }
}

