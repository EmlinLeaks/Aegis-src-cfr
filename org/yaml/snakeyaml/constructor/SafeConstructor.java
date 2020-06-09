/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.constructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;

public class SafeConstructor
extends BaseConstructor {
    public static final ConstructUndefined undefinedConstructor = new ConstructUndefined();
    private static final Map<String, Boolean> BOOL_VALUES = new HashMap<String, Boolean>();
    private static final Pattern TIMESTAMP_REGEXP;
    private static final Pattern YMD_REGEXP;

    public SafeConstructor() {
        this.yamlConstructors.put(Tag.NULL, new ConstructYamlNull((SafeConstructor)this));
        this.yamlConstructors.put(Tag.BOOL, new ConstructYamlBool((SafeConstructor)this));
        this.yamlConstructors.put(Tag.INT, new ConstructYamlInt((SafeConstructor)this));
        this.yamlConstructors.put(Tag.FLOAT, new ConstructYamlFloat((SafeConstructor)this));
        this.yamlConstructors.put(Tag.BINARY, new ConstructYamlBinary((SafeConstructor)this));
        this.yamlConstructors.put(Tag.TIMESTAMP, new ConstructYamlTimestamp());
        this.yamlConstructors.put(Tag.OMAP, new ConstructYamlOmap((SafeConstructor)this));
        this.yamlConstructors.put(Tag.PAIRS, new ConstructYamlPairs((SafeConstructor)this));
        this.yamlConstructors.put(Tag.SET, new ConstructYamlSet((SafeConstructor)this));
        this.yamlConstructors.put(Tag.STR, new ConstructYamlStr((SafeConstructor)this));
        this.yamlConstructors.put(Tag.SEQ, new ConstructYamlSeq((SafeConstructor)this));
        this.yamlConstructors.put(Tag.MAP, new ConstructYamlMap((SafeConstructor)this));
        this.yamlConstructors.put(null, undefinedConstructor);
        this.yamlClassConstructors.put(NodeId.scalar, undefinedConstructor);
        this.yamlClassConstructors.put(NodeId.sequence, undefinedConstructor);
        this.yamlClassConstructors.put(NodeId.mapping, undefinedConstructor);
    }

    protected void flattenMapping(MappingNode node) {
        this.processDuplicateKeys((MappingNode)node);
        if (!node.isMerged()) return;
        node.setValue(this.mergeNode((MappingNode)node, (boolean)true, new HashMap<Object, Integer>(), new ArrayList<NodeTuple>()));
    }

    protected void processDuplicateKeys(MappingNode node) {
        List<NodeTuple> nodeValue = node.getValue();
        HashMap<Object, Integer> keys = new HashMap<Object, Integer>((int)nodeValue.size());
        TreeSet<Integer> toRemove = new TreeSet<Integer>();
        int i = 0;
        for (NodeTuple tuple : nodeValue) {
            Node keyNode = tuple.getKeyNode();
            if (!keyNode.getTag().equals((Object)Tag.MERGE)) {
                Integer prevIndex;
                Object key = this.constructObject((Node)keyNode);
                if (key != null) {
                    try {
                        key.hashCode();
                    }
                    catch (Exception e) {
                        throw new ConstructorException((String)"while constructing a mapping", (Mark)node.getStartMark(), (String)("found unacceptable key " + key), (Mark)tuple.getKeyNode().getStartMark(), (Throwable)e);
                    }
                }
                if ((prevIndex = keys.put(key, Integer.valueOf((int)i))) != null) {
                    if (!this.isAllowDuplicateKeys()) {
                        throw new DuplicateKeyException((Mark)node.getStartMark(), (Object)key, (Mark)tuple.getKeyNode().getStartMark());
                    }
                    toRemove.add(prevIndex);
                }
            }
            ++i;
        }
        Iterator<E> indicies2remove = toRemove.descendingIterator();
        while (indicies2remove.hasNext()) {
            nodeValue.remove((int)((Integer)indicies2remove.next()).intValue());
        }
    }

    /*
     * Exception decompiling
     */
    private List<NodeTuple> mergeNode(MappingNode node, boolean isPreffered, Map<Object, Integer> key2index, List<NodeTuple> values) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Extractable last case doesn't follow previous
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.examineSwitchContiguity(SwitchReplacer.java:478)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.rebuildSwitches(SwitchReplacer.java:328)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:466)
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

    @Override
    protected void constructMapping2ndStep(MappingNode node, Map<Object, Object> mapping) {
        this.flattenMapping((MappingNode)node);
        super.constructMapping2ndStep((MappingNode)node, mapping);
    }

    @Override
    protected void constructSet2ndStep(MappingNode node, Set<Object> set) {
        this.flattenMapping((MappingNode)node);
        super.constructSet2ndStep((MappingNode)node, set);
    }

    private Number createNumber(int sign, String number, int radix) {
        if (sign < 0) {
            number = "-" + number;
        }
        try {
            return Integer.valueOf((String)number, (int)radix);
        }
        catch (NumberFormatException e) {
            try {
                return Long.valueOf((String)number, (int)radix);
            }
            catch (NumberFormatException e1) {
                return new BigInteger((String)number, (int)radix);
            }
        }
    }

    static /* synthetic */ Map access$000() {
        return BOOL_VALUES;
    }

    static /* synthetic */ Number access$100(SafeConstructor x0, int x1, String x2, int x3) {
        return x0.createNumber((int)x1, (String)x2, (int)x3);
    }

    static /* synthetic */ Pattern access$200() {
        return YMD_REGEXP;
    }

    static /* synthetic */ Pattern access$300() {
        return TIMESTAMP_REGEXP;
    }

    static {
        BOOL_VALUES.put((String)"yes", (Boolean)Boolean.TRUE);
        BOOL_VALUES.put((String)"no", (Boolean)Boolean.FALSE);
        BOOL_VALUES.put((String)"true", (Boolean)Boolean.TRUE);
        BOOL_VALUES.put((String)"false", (Boolean)Boolean.FALSE);
        BOOL_VALUES.put((String)"on", (Boolean)Boolean.TRUE);
        BOOL_VALUES.put((String)"off", (Boolean)Boolean.FALSE);
        TIMESTAMP_REGEXP = Pattern.compile((String)"^([0-9][0-9][0-9][0-9])-([0-9][0-9]?)-([0-9][0-9]?)(?:(?:[Tt]|[ \t]+)([0-9][0-9]?):([0-9][0-9]):([0-9][0-9])(?:\\.([0-9]*))?(?:[ \t]*(?:Z|([-+][0-9][0-9]?)(?::([0-9][0-9])?)?))?)?$");
        YMD_REGEXP = Pattern.compile((String)"^([0-9][0-9][0-9][0-9])-([0-9][0-9]?)-([0-9][0-9]?)$");
    }
}

