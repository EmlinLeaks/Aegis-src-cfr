/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.md_5.bungee.module;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.module.JenkinsModuleSource;
import net.md_5.bungee.module.ModuleSource;
import net.md_5.bungee.module.ModuleSpec;
import net.md_5.bungee.module.ModuleVersion;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class ModuleManager {
    private final Map<String, ModuleSource> knownSources = new HashMap<String, ModuleSource>();

    public ModuleManager() {
        this.knownSources.put((String)"jenkins", (ModuleSource)new JenkinsModuleSource());
    }

    @SuppressFBWarnings(value={"SF_SWITCH_FALLTHROUGH", "SF_SWITCH_NO_DEFAULT"})
    public void load(ProxyServer proxy, File moduleDirectory) throws Exception {
        CaseInsensitiveMap<Serializable> config;
        moduleDirectory.mkdir();
        ModuleVersion bungeeVersion = ModuleVersion.parse((String)proxy.getVersion());
        if (bungeeVersion == null) {
            System.out.println((String)"Couldn't detect bungee version. Custom build?");
            return;
        }
        ArrayList<ModuleSpec> modules = new ArrayList<ModuleSpec>();
        File configFile = new File((String)"modules.yml");
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle((DumperOptions.FlowStyle)DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml((DumperOptions)options);
        configFile.createNewFile();
        FileInputStream is = new FileInputStream((File)configFile);
        Throwable throwable = null;
        try {
            config = (CaseInsensitiveMap<Serializable>)yaml.load((InputStream)is);
        }
        catch (Throwable throwable2) {
            throwable = throwable2;
            throw throwable2;
        }
        finally {
            ModuleManager.$closeResource((Throwable)throwable, (AutoCloseable)is);
        }
        config = config == null ? new CaseInsensitiveMap<Serializable>() : new CaseInsensitiveMap<V>(config);
        ArrayList<String> defaults = new ArrayList<String>();
        V readModules = config.get((Object)"modules");
        if (readModules != null) {
            defaults.addAll((Collection)readModules);
        }
        int version = config.containsKey((Object)"version") ? ((Integer)config.get((Object)"version")).intValue() : 0;
        switch (version) {
            case 0: {
                defaults.add("jenkins://cmd_alert");
                defaults.add("jenkins://cmd_find");
                defaults.add("jenkins://cmd_list");
                defaults.add("jenkins://cmd_send");
                defaults.add("jenkins://cmd_server");
            }
            case 1: {
                defaults.add("jenkins://reconnect_yaml");
            }
        }
        config.put("modules", defaults);
        config.put("version", Integer.valueOf((int)2));
        Iterator<E> wr = new FileWriter((File)configFile);
        Throwable throwable3 = null;
        try {
            yaml.dump(config, (Writer)((Object)wr));
        }
        catch (Throwable throwable4) {
            throwable3 = throwable4;
            throw throwable4;
        }
        finally {
            ModuleManager.$closeResource((Throwable)throwable3, (AutoCloseable)((Object)wr));
        }
        for (String s : (List)config.get((Object)"modules")) {
            URI uri = new URI((String)s);
            ModuleSource source = this.knownSources.get((Object)uri.getScheme());
            if (source == null) {
                System.out.println((String)("Unknown module source: " + s));
                continue;
            }
            String name = uri.getAuthority();
            if (name == null) {
                System.out.println((String)("Unknown module host: " + s));
                continue;
            }
            ModuleSpec spec = new ModuleSpec((String)name, (File)new File((File)moduleDirectory, (String)(name + ".jar")), (ModuleSource)source);
            modules.add(spec);
            System.out.println((String)("Discovered module: " + spec));
        }
        wr = modules.iterator();
        while (wr.hasNext()) {
            ModuleSpec module = (ModuleSpec)wr.next();
            ModuleVersion moduleVersion = module.getFile().exists() ? this.getVersion((File)module.getFile()) : null;
            if (bungeeVersion.equals((Object)moduleVersion)) continue;
            System.out.println((String)("Attempting to update plugin from " + moduleVersion + " to " + bungeeVersion));
            module.getProvider().retrieve((ModuleSpec)module, (ModuleVersion)bungeeVersion);
        }
    }

    /*
     * Exception decompiling
     */
    @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"})
    private ModuleVersion getVersion(File file) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 4[TRYBLOCK]
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

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
            return;
        }
        catch (Throwable throwable) {
            x0.addSuppressed((Throwable)throwable);
            return;
        }
    }
}

