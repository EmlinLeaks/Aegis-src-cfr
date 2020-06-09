/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.resolver.ResolverTuple;

public class Resolver {
    public static final Pattern BOOL = Pattern.compile((String)"^(?:yes|Yes|YES|no|No|NO|true|True|TRUE|false|False|FALSE|on|On|ON|off|Off|OFF)$");
    public static final Pattern FLOAT = Pattern.compile((String)"^([-+]?(\\.[0-9]+|[0-9_]+(\\.[0-9_]*)?)([eE][-+]?[0-9]+)?|[-+]?[0-9][0-9_]*(?::[0-5]?[0-9])+\\.[0-9_]*|[-+]?\\.(?:inf|Inf|INF)|\\.(?:nan|NaN|NAN))$");
    public static final Pattern INT = Pattern.compile((String)"^(?:[-+]?0b[0-1_]+|[-+]?0[0-7_]+|[-+]?(?:0|[1-9][0-9_]*)|[-+]?0x[0-9a-fA-F_]+|[-+]?[1-9][0-9_]*(?::[0-5]?[0-9])+)$");
    public static final Pattern MERGE = Pattern.compile((String)"^(?:<<)$");
    public static final Pattern NULL = Pattern.compile((String)"^(?:~|null|Null|NULL| )$");
    public static final Pattern EMPTY = Pattern.compile((String)"^$");
    public static final Pattern TIMESTAMP = Pattern.compile((String)"^(?:[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]|[0-9][0-9][0-9][0-9]-[0-9][0-9]?-[0-9][0-9]?(?:[Tt]|[ \t]+)[0-9][0-9]?:[0-9][0-9]:[0-9][0-9](?:\\.[0-9]*)?(?:[ \t]*(?:Z|[-+][0-9][0-9]?(?::[0-9][0-9])?))?)$");
    public static final Pattern VALUE = Pattern.compile((String)"^(?:=)$");
    public static final Pattern YAML = Pattern.compile((String)"^(?:!|&|\\*)$");
    protected Map<Character, List<ResolverTuple>> yamlImplicitResolvers = new HashMap<Character, List<ResolverTuple>>();

    protected void addImplicitResolvers() {
        this.addImplicitResolver((Tag)Tag.BOOL, (Pattern)BOOL, (String)"yYnNtTfFoO");
        this.addImplicitResolver((Tag)Tag.INT, (Pattern)INT, (String)"-+0123456789");
        this.addImplicitResolver((Tag)Tag.FLOAT, (Pattern)FLOAT, (String)"-+0123456789.");
        this.addImplicitResolver((Tag)Tag.MERGE, (Pattern)MERGE, (String)"<");
        this.addImplicitResolver((Tag)Tag.NULL, (Pattern)NULL, (String)"~nN\u0000");
        this.addImplicitResolver((Tag)Tag.NULL, (Pattern)EMPTY, null);
        this.addImplicitResolver((Tag)Tag.TIMESTAMP, (Pattern)TIMESTAMP, (String)"0123456789");
        this.addImplicitResolver((Tag)Tag.YAML, (Pattern)YAML, (String)"!&*");
    }

    public Resolver() {
        this.addImplicitResolvers();
    }

    public void addImplicitResolver(Tag tag, Pattern regexp, String first) {
        if (first == null) {
            List<ResolverTuple> curr = this.yamlImplicitResolvers.get(null);
            if (curr == null) {
                curr = new ArrayList<ResolverTuple>();
                this.yamlImplicitResolvers.put(null, curr);
            }
            curr.add((ResolverTuple)new ResolverTuple((Tag)tag, (Pattern)regexp));
            return;
        }
        char[] chrs = first.toCharArray();
        int i = 0;
        int j = chrs.length;
        while (i < j) {
            List<ResolverTuple> curr;
            Character theC = Character.valueOf((char)chrs[i]);
            if (theC.charValue() == '\u0000') {
                theC = null;
            }
            if ((curr = this.yamlImplicitResolvers.get((Object)theC)) == null) {
                curr = new ArrayList<ResolverTuple>();
                this.yamlImplicitResolvers.put((Character)theC, curr);
            }
            curr.add((ResolverTuple)new ResolverTuple((Tag)tag, (Pattern)regexp));
            ++i;
        }
    }

    public Tag resolve(NodeId kind, String value, boolean implicit) {
        if (kind == NodeId.scalar && implicit) {
            Tag tag;
            Pattern regexp;
            List<ResolverTuple> resolvers = value.length() == 0 ? this.yamlImplicitResolvers.get((Object)Character.valueOf((char)'\u0000')) : this.yamlImplicitResolvers.get((Object)Character.valueOf((char)value.charAt((int)0)));
            if (resolvers != null) {
                for (ResolverTuple v : resolvers) {
                    tag = v.getTag();
                    regexp = v.getRegexp();
                    if (!regexp.matcher((CharSequence)value).matches()) continue;
                    return tag;
                }
            }
            if (this.yamlImplicitResolvers.containsKey(null)) {
                for (ResolverTuple v : this.yamlImplicitResolvers.get(null)) {
                    tag = v.getTag();
                    regexp = v.getRegexp();
                    if (!regexp.matcher((CharSequence)value).matches()) continue;
                    return tag;
                }
            }
        }
        switch (1.$SwitchMap$org$yaml$snakeyaml$nodes$NodeId[kind.ordinal()]) {
            case 1: {
                return Tag.STR;
            }
            case 2: {
                return Tag.SEQ;
            }
        }
        return Tag.MAP;
    }
}

