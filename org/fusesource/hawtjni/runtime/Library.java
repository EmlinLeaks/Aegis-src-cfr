/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.fusesource.hawtjni.runtime;

import java.io.Closeable;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Library {
    static final String SLASH = System.getProperty((String)"file.separator");
    private final String name;
    private final String version;
    private final ClassLoader classLoader;
    private boolean loaded;

    public Library(String name) {
        this((String)name, null, null);
    }

    public Library(String name, Class<?> clazz) {
        this((String)name, (String)Library.version(clazz), (ClassLoader)clazz.getClassLoader());
    }

    public Library(String name, String version) {
        this((String)name, (String)version, null);
    }

    public Library(String name, String version, ClassLoader classLoader) {
        if (name == null) {
            throw new IllegalArgumentException((String)"name cannot be null");
        }
        this.name = name;
        this.version = version;
        this.classLoader = classLoader;
    }

    private static String version(Class<?> clazz) {
        try {
            return clazz.getPackage().getImplementationVersion();
        }
        catch (Throwable e) {
            return null;
        }
    }

    public static String getOperatingSystem() {
        String name = System.getProperty((String)"os.name").toLowerCase().trim();
        if (name.startsWith((String)"linux")) {
            return "linux";
        }
        if (name.startsWith((String)"mac os x")) {
            return "osx";
        }
        if (!name.startsWith((String)"win")) return name.replaceAll((String)"\\W+", (String)"_");
        return "windows";
    }

    public static String getPlatform() {
        return Library.getOperatingSystem() + Library.getBitModel();
    }

    public static int getBitModel() {
        String prop = System.getProperty((String)"sun.arch.data.model");
        if (prop == null) {
            prop = System.getProperty((String)"com.ibm.vm.bitmode");
        }
        if (prop == null) return -1;
        return Integer.parseInt((String)prop);
    }

    public synchronized void load() {
        if (this.loaded) {
            return;
        }
        this.doLoad();
        this.loaded = true;
    }

    private void doLoad() {
        String version = System.getProperty((String)("library." + this.name + ".version"));
        if (version == null) {
            version = this.version;
        }
        ArrayList<String> errors = new ArrayList<String>();
        String customPath = System.getProperty((String)("library." + this.name + ".path"));
        if (customPath != null) {
            if (version != null) {
                if (this.load(errors, (File)this.file((String[])new String[]{customPath, this.map((String)(this.name + "-" + version))}))) {
                    return;
                }
            }
            if (this.load(errors, (File)this.file((String[])new String[]{customPath, this.map((String)this.name)}))) {
                return;
            }
        }
        if (version != null && this.load(errors, (String)(this.name + Library.getBitModel() + "-" + version))) {
            return;
        }
        if (version != null && this.load(errors, (String)(this.name + "-" + version))) {
            return;
        }
        if (this.load(errors, (String)this.name)) {
            return;
        }
        if (this.classLoader == null) throw new UnsatisfiedLinkError((String)("Could not load library. Reasons: " + errors.toString()));
        if (this.exractAndLoad(errors, (String)version, (String)customPath, (String)this.getPlatformSpecifcResourcePath())) {
            return;
        }
        if (this.exractAndLoad(errors, (String)version, (String)customPath, (String)this.getOperatingSystemSpecifcResourcePath())) {
            return;
        }
        if (!this.exractAndLoad(errors, (String)version, (String)customPath, (String)this.getResorucePath())) throw new UnsatisfiedLinkError((String)("Could not load library. Reasons: " + errors.toString()));
    }

    public final String getOperatingSystemSpecifcResourcePath() {
        return this.getPlatformSpecifcResourcePath((String)Library.getOperatingSystem());
    }

    public final String getPlatformSpecifcResourcePath() {
        return this.getPlatformSpecifcResourcePath((String)Library.getPlatform());
    }

    public final String getPlatformSpecifcResourcePath(String platform) {
        return "META-INF/native/" + platform + "/" + this.map((String)this.name);
    }

    public final String getResorucePath() {
        return "META-INF/native/" + this.map((String)this.name);
    }

    public final String getLibraryFileName() {
        return this.map((String)this.name);
    }

    private boolean exractAndLoad(ArrayList<String> errors, String version, String customPath, String resourcePath) {
        File target;
        URL resource = this.classLoader.getResource((String)resourcePath);
        if (resource == null) return false;
        String libName = this.name + "-" + Library.getBitModel();
        if (version != null) {
            libName = libName + "-" + version;
        }
        String[] libNameParts = this.map((String)libName).split((String)"\\.");
        String prefix = libNameParts[0] + "-";
        String suffix = "." + libNameParts[1];
        if (customPath != null) {
            target = this.extract(errors, (URL)resource, (String)prefix, (String)suffix, (File)this.file((String[])new String[]{customPath}));
            if (target != null && this.load(errors, (File)target)) {
                return true;
            }
        }
        customPath = System.getProperty((String)"java.io.tmpdir");
        target = this.extract(errors, (URL)resource, (String)prefix, (String)suffix, (File)this.file((String[])new String[]{customPath}));
        if (target == null) return false;
        if (!this.load(errors, (File)target)) return false;
        return true;
    }

    private File file(String ... paths) {
        File rc = null;
        String[] arr$ = paths;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            String path = arr$[i$];
            rc = rc == null ? new File((String)path) : new File((File)rc, (String)path);
            ++i$;
        }
        return rc;
    }

    private String map(String libName) {
        String ext;
        if (!(libName = System.mapLibraryName((String)libName)).endsWith((String)(ext = ".dylib"))) return libName;
        return libName.substring((int)0, (int)(libName.length() - ext.length())) + ".jnilib";
    }

    /*
     * Exception decompiling
     */
    private File extract(ArrayList<String> errors, URL source, String prefix, String suffix, File directory) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 3[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    private static void close(Closeable file) {
        if (file == null) return;
        try {
            file.close();
            return;
        }
        catch (Exception ignore) {
            // empty catch block
        }
    }

    private void chmod(String permision, File path) {
        if (Library.getPlatform().startsWith((String)"windows")) {
            return;
        }
        try {
            Runtime.getRuntime().exec((String[])new String[]{"chmod", permision, path.getCanonicalPath()}).waitFor();
            return;
        }
        catch (Throwable e) {
            // empty catch block
        }
    }

    private boolean load(ArrayList<String> errors, File lib) {
        try {
            System.load((String)lib.getPath());
            return true;
        }
        catch (UnsatisfiedLinkError e) {
            errors.add((String)e.getMessage());
            return false;
        }
    }

    private boolean load(ArrayList<String> errors, String lib) {
        try {
            System.loadLibrary((String)lib);
            return true;
        }
        catch (UnsatisfiedLinkError e) {
            errors.add((String)e.getMessage());
            return false;
        }
    }
}

