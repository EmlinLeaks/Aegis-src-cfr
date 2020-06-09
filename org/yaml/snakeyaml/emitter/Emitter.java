/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.emitter;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.emitter.Emitable;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.emitter.EmitterException;
import org.yaml.snakeyaml.emitter.EmitterState;
import org.yaml.snakeyaml.emitter.ScalarAnalysis;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.CollectionEndEvent;
import org.yaml.snakeyaml.events.CollectionStartEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.Constant;
import org.yaml.snakeyaml.util.ArrayStack;

public final class Emitter
implements Emitable {
    private static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<Character, String>();
    public static final int MIN_INDENT = 1;
    public static final int MAX_INDENT = 10;
    private static final char[] SPACE = new char[]{' '};
    private static final Map<String, String> DEFAULT_TAG_PREFIXES;
    private final Writer stream;
    private final ArrayStack<EmitterState> states;
    private EmitterState state;
    private final Queue<Event> events;
    private Event event;
    private final ArrayStack<Integer> indents;
    private Integer indent;
    private int flowLevel;
    private boolean rootContext;
    private boolean mappingContext;
    private boolean simpleKeyContext;
    private int column;
    private boolean whitespace;
    private boolean indention;
    private boolean openEnded;
    private Boolean canonical;
    private Boolean prettyFlow;
    private boolean allowUnicode;
    private int bestIndent;
    private int indicatorIndent;
    private int bestWidth;
    private char[] bestLineBreak;
    private boolean splitLines;
    private int maxSimpleKeyLength;
    private Map<String, String> tagPrefixes;
    private String preparedAnchor;
    private String preparedTag;
    private ScalarAnalysis analysis;
    private DumperOptions.ScalarStyle style;
    private static final Pattern HANDLE_FORMAT;
    private static final Pattern ANCHOR_FORMAT;

    public Emitter(Writer stream, DumperOptions opts) {
        this.stream = stream;
        this.states = new ArrayStack<T>((int)100);
        this.state = new ExpectStreamStart((Emitter)this, null);
        this.events = new ArrayBlockingQueue<Event>((int)100);
        this.event = null;
        this.indents = new ArrayStack<T>((int)10);
        this.indent = null;
        this.flowLevel = 0;
        this.mappingContext = false;
        this.simpleKeyContext = false;
        this.column = 0;
        this.whitespace = true;
        this.indention = true;
        this.openEnded = false;
        this.canonical = Boolean.valueOf((boolean)opts.isCanonical());
        this.prettyFlow = Boolean.valueOf((boolean)opts.isPrettyFlow());
        this.allowUnicode = opts.isAllowUnicode();
        this.bestIndent = 2;
        if (opts.getIndent() > 1 && opts.getIndent() < 10) {
            this.bestIndent = opts.getIndent();
        }
        this.indicatorIndent = opts.getIndicatorIndent();
        this.bestWidth = 80;
        if (opts.getWidth() > this.bestIndent * 2) {
            this.bestWidth = opts.getWidth();
        }
        this.bestLineBreak = opts.getLineBreak().getString().toCharArray();
        this.splitLines = opts.getSplitLines();
        this.maxSimpleKeyLength = opts.getMaxSimpleKeyLength();
        this.tagPrefixes = new LinkedHashMap<String, String>();
        this.preparedAnchor = null;
        this.preparedTag = null;
        this.analysis = null;
        this.style = null;
    }

    @Override
    public void emit(Event event) throws IOException {
        this.events.add((Event)event);
        while (!this.needMoreEvents()) {
            this.event = this.events.poll();
            this.state.expect();
            this.event = null;
        }
    }

    private boolean needMoreEvents() {
        if (this.events.isEmpty()) {
            return true;
        }
        Event event = this.events.peek();
        if (event instanceof DocumentStartEvent) {
            return this.needEvents((int)1);
        }
        if (event instanceof SequenceStartEvent) {
            return this.needEvents((int)2);
        }
        if (!(event instanceof MappingStartEvent)) return false;
        return this.needEvents((int)3);
    }

    private boolean needEvents(int count) {
        int level = 0;
        Iterator<E> iter = this.events.iterator();
        iter.next();
        while (iter.hasNext()) {
            Event event = (Event)iter.next();
            if (event instanceof DocumentStartEvent || event instanceof CollectionStartEvent) {
                ++level;
            } else if (event instanceof DocumentEndEvent || event instanceof CollectionEndEvent) {
                --level;
            } else if (event instanceof StreamEndEvent) {
                level = -1;
            }
            if (level >= 0) continue;
            return false;
        }
        if (this.events.size() >= count + 1) return false;
        return true;
    }

    private void increaseIndent(boolean flow, boolean indentless) {
        this.indents.push((Integer)this.indent);
        if (this.indent != null) {
            if (indentless) return;
            Emitter emitter = this;
            emitter.indent = Integer.valueOf((int)(emitter.indent.intValue() + this.bestIndent));
            return;
        }
        if (flow) {
            this.indent = Integer.valueOf((int)this.bestIndent);
            return;
        }
        this.indent = Integer.valueOf((int)0);
    }

    private void expectNode(boolean root, boolean mapping, boolean simpleKey) throws IOException {
        this.rootContext = root;
        this.mappingContext = mapping;
        this.simpleKeyContext = simpleKey;
        if (this.event instanceof AliasEvent) {
            this.expectAlias();
            return;
        }
        if (!(this.event instanceof ScalarEvent)) {
            if (!(this.event instanceof CollectionStartEvent)) throw new EmitterException((String)("expected NodeEvent, but got " + this.event));
        }
        this.processAnchor((String)"&");
        this.processTag();
        if (this.event instanceof ScalarEvent) {
            this.expectScalar();
            return;
        }
        if (this.event instanceof SequenceStartEvent) {
            if (!(this.flowLevel != 0 || this.canonical.booleanValue() || ((SequenceStartEvent)this.event).isFlow() || this.checkEmptySequence())) {
                this.expectBlockSequence();
                return;
            }
            this.expectFlowSequence();
            return;
        }
        if (!(this.flowLevel != 0 || this.canonical.booleanValue() || ((MappingStartEvent)this.event).isFlow() || this.checkEmptyMapping())) {
            this.expectBlockMapping();
            return;
        }
        this.expectFlowMapping();
    }

    private void expectAlias() throws IOException {
        if (((NodeEvent)this.event).getAnchor() == null) {
            throw new EmitterException((String)"anchor is not specified for alias");
        }
        this.processAnchor((String)"*");
        this.state = this.states.pop();
    }

    private void expectScalar() throws IOException {
        this.increaseIndent((boolean)true, (boolean)false);
        this.processScalar();
        this.indent = this.indents.pop();
        this.state = this.states.pop();
    }

    private void expectFlowSequence() throws IOException {
        this.writeIndicator((String)"[", (boolean)true, (boolean)true, (boolean)false);
        ++this.flowLevel;
        this.increaseIndent((boolean)true, (boolean)false);
        if (this.prettyFlow.booleanValue()) {
            this.writeIndent();
        }
        this.state = new ExpectFirstFlowSequenceItem((Emitter)this, null);
    }

    private void expectFlowMapping() throws IOException {
        this.writeIndicator((String)"{", (boolean)true, (boolean)true, (boolean)false);
        ++this.flowLevel;
        this.increaseIndent((boolean)true, (boolean)false);
        if (this.prettyFlow.booleanValue()) {
            this.writeIndent();
        }
        this.state = new ExpectFirstFlowMappingKey((Emitter)this, null);
    }

    private void expectBlockSequence() throws IOException {
        boolean indentless = this.mappingContext && !this.indention;
        this.increaseIndent((boolean)false, (boolean)indentless);
        this.state = new ExpectFirstBlockSequenceItem((Emitter)this, null);
    }

    private void expectBlockMapping() throws IOException {
        this.increaseIndent((boolean)false, (boolean)false);
        this.state = new ExpectFirstBlockMappingKey((Emitter)this, null);
    }

    private boolean checkEmptySequence() {
        if (!(this.event instanceof SequenceStartEvent)) return false;
        if (this.events.isEmpty()) return false;
        if (!(this.events.peek() instanceof SequenceEndEvent)) return false;
        return true;
    }

    private boolean checkEmptyMapping() {
        if (!(this.event instanceof MappingStartEvent)) return false;
        if (this.events.isEmpty()) return false;
        if (!(this.events.peek() instanceof MappingEndEvent)) return false;
        return true;
    }

    private boolean checkEmptyDocument() {
        if (!(this.event instanceof DocumentStartEvent)) return false;
        if (this.events.isEmpty()) {
            return false;
        }
        Event event = this.events.peek();
        if (!(event instanceof ScalarEvent)) return false;
        ScalarEvent e = (ScalarEvent)event;
        if (e.getAnchor() != null) return false;
        if (e.getTag() != null) return false;
        if (e.getImplicit() == null) return false;
        if (e.getValue().length() != 0) return false;
        return true;
    }

    private boolean checkSimpleKey() {
        int length = 0;
        if (this.event instanceof NodeEvent && ((NodeEvent)this.event).getAnchor() != null) {
            if (this.preparedAnchor == null) {
                this.preparedAnchor = Emitter.prepareAnchor((String)((NodeEvent)this.event).getAnchor());
            }
            length += this.preparedAnchor.length();
        }
        String tag = null;
        if (this.event instanceof ScalarEvent) {
            tag = ((ScalarEvent)this.event).getTag();
        } else if (this.event instanceof CollectionStartEvent) {
            tag = ((CollectionStartEvent)this.event).getTag();
        }
        if (tag != null) {
            if (this.preparedTag == null) {
                this.preparedTag = this.prepareTag((String)tag);
            }
            length += this.preparedTag.length();
        }
        if (this.event instanceof ScalarEvent) {
            if (this.analysis == null) {
                this.analysis = this.analyzeScalar((String)((ScalarEvent)this.event).getValue());
            }
            length += this.analysis.scalar.length();
        }
        if (length >= this.maxSimpleKeyLength) return false;
        if (this.event instanceof AliasEvent) return true;
        if (this.event instanceof ScalarEvent && !this.analysis.empty) {
            if (!this.analysis.multiline) return true;
        }
        if (this.checkEmptySequence()) return true;
        if (!this.checkEmptyMapping()) return false;
        return true;
    }

    private void processAnchor(String indicator) throws IOException {
        NodeEvent ev = (NodeEvent)this.event;
        if (ev.getAnchor() == null) {
            this.preparedAnchor = null;
            return;
        }
        if (this.preparedAnchor == null) {
            this.preparedAnchor = Emitter.prepareAnchor((String)ev.getAnchor());
        }
        this.writeIndicator((String)(indicator + this.preparedAnchor), (boolean)true, (boolean)false, (boolean)false);
        this.preparedAnchor = null;
    }

    private void processTag() throws IOException {
        String tag = null;
        if (this.event instanceof ScalarEvent) {
            ScalarEvent ev = (ScalarEvent)this.event;
            tag = ev.getTag();
            if (this.style == null) {
                this.style = this.chooseScalarStyle();
            }
            if ((!this.canonical.booleanValue() || tag == null) && (this.style == null && ev.getImplicit().canOmitTagInPlainScalar() || this.style != null && ev.getImplicit().canOmitTagInNonPlainScalar())) {
                this.preparedTag = null;
                return;
            }
            if (ev.getImplicit().canOmitTagInPlainScalar() && tag == null) {
                tag = "!";
                this.preparedTag = null;
            }
        } else {
            CollectionStartEvent ev = (CollectionStartEvent)this.event;
            tag = ev.getTag();
            if ((!this.canonical.booleanValue() || tag == null) && ev.getImplicit()) {
                this.preparedTag = null;
                return;
            }
        }
        if (tag == null) {
            throw new EmitterException((String)"tag is not specified");
        }
        if (this.preparedTag == null) {
            this.preparedTag = this.prepareTag((String)tag);
        }
        this.writeIndicator((String)this.preparedTag, (boolean)true, (boolean)false, (boolean)false);
        this.preparedTag = null;
    }

    private DumperOptions.ScalarStyle chooseScalarStyle() {
        ScalarEvent ev = (ScalarEvent)this.event;
        if (this.analysis == null) {
            this.analysis = this.analyzeScalar((String)ev.getValue());
        }
        if (!ev.isPlain()) {
            if (ev.getScalarStyle() == DumperOptions.ScalarStyle.DOUBLE_QUOTED) return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
        }
        if (this.canonical.booleanValue()) {
            return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
        }
        if (ev.isPlain() && ev.getImplicit().canOmitTagInPlainScalar() && (!this.simpleKeyContext || !this.analysis.empty && !this.analysis.multiline)) {
            if (this.flowLevel != 0) {
                if (this.analysis.allowFlowPlain) return null;
            }
            if (this.flowLevel == 0 && this.analysis.allowBlockPlain) {
                return null;
            }
        }
        if (!(ev.isPlain() || ev.getScalarStyle() != DumperOptions.ScalarStyle.LITERAL && ev.getScalarStyle() != DumperOptions.ScalarStyle.FOLDED || this.flowLevel != 0 || this.simpleKeyContext || !this.analysis.allowBlock)) {
            return ev.getScalarStyle();
        }
        if (!ev.isPlain()) {
            if (ev.getScalarStyle() != DumperOptions.ScalarStyle.SINGLE_QUOTED) return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
        }
        if (!this.analysis.allowSingleQuoted) return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
        if (!this.simpleKeyContext) return DumperOptions.ScalarStyle.SINGLE_QUOTED;
        if (this.analysis.multiline) return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
        return DumperOptions.ScalarStyle.SINGLE_QUOTED;
    }

    /*
     * Unable to fully structure code
     */
    private void processScalar() throws IOException {
        ev = (ScalarEvent)this.event;
        if (this.analysis == null) {
            this.analysis = this.analyzeScalar((String)ev.getValue());
        }
        if (this.style == null) {
            this.style = this.chooseScalarStyle();
        }
        v0 = split = this.simpleKeyContext == false && this.splitLines != false;
        if (this.style == null) {
            this.writePlain((String)this.analysis.scalar, (boolean)split);
        } else {
            switch (1.$SwitchMap$org$yaml$snakeyaml$DumperOptions$ScalarStyle[this.style.ordinal()]) {
                case 1: {
                    this.writeDoubleQuoted((String)this.analysis.scalar, (boolean)split);
                    ** break;
                }
                case 2: {
                    this.writeSingleQuoted((String)this.analysis.scalar, (boolean)split);
                    ** break;
                }
                case 3: {
                    this.writeFolded((String)this.analysis.scalar, (boolean)split);
                    ** break;
                }
                case 4: {
                    this.writeLiteral((String)this.analysis.scalar);
                    ** break;
                }
            }
            throw new YAMLException((String)("Unexpected style: " + (Object)this.style));
        }
lbl24: // 5 sources:
        this.analysis = null;
        this.style = null;
    }

    private String prepareVersion(DumperOptions.Version version) {
        if (version.major() == 1) return version.getRepresentation();
        throw new EmitterException((String)("unsupported YAML version: " + (Object)((Object)version)));
    }

    private String prepareTagHandle(String handle) {
        if (handle.length() == 0) {
            throw new EmitterException((String)"tag handle must not be empty");
        }
        if (handle.charAt((int)0) != '!') throw new EmitterException((String)("tag handle must start and end with '!': " + handle));
        if (handle.charAt((int)(handle.length() - 1)) != '!') {
            throw new EmitterException((String)("tag handle must start and end with '!': " + handle));
        }
        if ("!".equals((Object)handle)) return handle;
        if (HANDLE_FORMAT.matcher((CharSequence)handle).matches()) return handle;
        throw new EmitterException((String)("invalid character in the tag handle: " + handle));
    }

    private String prepareTagPrefix(String prefix) {
        if (prefix.length() == 0) {
            throw new EmitterException((String)"tag prefix must not be empty");
        }
        StringBuilder chunks = new StringBuilder();
        int start = 0;
        int end = 0;
        if (prefix.charAt((int)0) == '!') {
            end = 1;
        }
        do {
            if (end >= prefix.length()) {
                if (start >= end) return chunks.toString();
                chunks.append((String)prefix.substring((int)start, (int)end));
                return chunks.toString();
            }
            ++end;
        } while (true);
    }

    private String prepareTag(String tag) {
        int end;
        if (tag.length() == 0) {
            throw new EmitterException((String)"tag must not be empty");
        }
        if ("!".equals((Object)tag)) {
            return tag;
        }
        String handle = null;
        String suffix = tag;
        for (String prefix : this.tagPrefixes.keySet()) {
            if (!tag.startsWith((String)prefix) || !"!".equals((Object)prefix) && prefix.length() >= tag.length()) continue;
            handle = prefix;
        }
        if (handle != null) {
            suffix = tag.substring((int)handle.length());
            handle = this.tagPrefixes.get((Object)handle);
        }
        String suffixText = (end = suffix.length()) > 0 ? suffix.substring((int)0, (int)end) : "";
        if (handle == null) return "!<" + suffixText + ">";
        return handle + suffixText;
    }

    static String prepareAnchor(String anchor) {
        if (anchor.length() == 0) {
            throw new EmitterException((String)"anchor must not be empty");
        }
        if (ANCHOR_FORMAT.matcher((CharSequence)anchor).matches()) return anchor;
        throw new EmitterException((String)("invalid character in the anchor: " + anchor));
    }

    private ScalarAnalysis analyzeScalar(String scalar) {
        if (scalar.length() == 0) {
            return new ScalarAnalysis((String)scalar, (boolean)true, (boolean)false, (boolean)false, (boolean)true, (boolean)true, (boolean)false);
        }
        boolean blockIndicators = false;
        boolean flowIndicators = false;
        boolean lineBreaks = false;
        boolean specialCharacters = false;
        boolean leadingSpace = false;
        boolean leadingBreak = false;
        boolean trailingSpace = false;
        boolean trailingBreak = false;
        boolean breakSpace = false;
        boolean spaceBreak = false;
        if (scalar.startsWith((String)"---") || scalar.startsWith((String)"...")) {
            blockIndicators = true;
            flowIndicators = true;
        }
        boolean preceededByWhitespace = true;
        boolean followedByWhitespace = scalar.length() == 1 || Constant.NULL_BL_T_LINEBR.has((int)scalar.codePointAt((int)1));
        boolean previousSpace = false;
        boolean previousBreak = false;
        int index = 0;
        while (index < scalar.length()) {
            int nextIndex;
            boolean isLineBreak;
            int c = scalar.codePointAt((int)index);
            if (index == 0) {
                if ("#,[]{}&*!|>'\"%@`".indexOf((int)c) != -1) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
                if (c == 63 || c == 58) {
                    flowIndicators = true;
                    if (followedByWhitespace) {
                        blockIndicators = true;
                    }
                }
                if (c == 45 && followedByWhitespace) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
            } else {
                if (",?[]{}".indexOf((int)c) != -1) {
                    flowIndicators = true;
                }
                if (c == 58) {
                    flowIndicators = true;
                    if (followedByWhitespace) {
                        blockIndicators = true;
                    }
                }
                if (c == 35 && preceededByWhitespace) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
            }
            if (isLineBreak = Constant.LINEBR.has((int)c)) {
                lineBreaks = true;
            }
            if (c != 10 && (32 > c || c > 126)) {
                if (c == 133 || c >= 160 && c <= 55295 || c >= 57344 && c <= 65533 || c >= 65536 && c <= 1114111) {
                    if (!this.allowUnicode) {
                        specialCharacters = true;
                    }
                } else {
                    specialCharacters = true;
                }
            }
            if (c == 32) {
                if (index == 0) {
                    leadingSpace = true;
                }
                if (index == scalar.length() - 1) {
                    trailingSpace = true;
                }
                if (previousBreak) {
                    breakSpace = true;
                }
                previousSpace = true;
                previousBreak = false;
            } else if (isLineBreak) {
                if (index == 0) {
                    leadingBreak = true;
                }
                if (index == scalar.length() - 1) {
                    trailingBreak = true;
                }
                if (previousSpace) {
                    spaceBreak = true;
                }
                previousSpace = false;
                previousBreak = true;
            } else {
                previousSpace = false;
                previousBreak = false;
            }
            preceededByWhitespace = Constant.NULL_BL_T.has((int)c) || isLineBreak;
            followedByWhitespace = true;
            if ((index += Character.charCount((int)c)) + 1 >= scalar.length() || (nextIndex = index + Character.charCount((int)scalar.codePointAt((int)index))) >= scalar.length()) continue;
            followedByWhitespace = Constant.NULL_BL_T.has((int)scalar.codePointAt((int)nextIndex)) || isLineBreak;
        }
        boolean allowFlowPlain = true;
        boolean allowBlockPlain = true;
        boolean allowSingleQuoted = true;
        boolean allowBlock = true;
        if (leadingSpace || leadingBreak || trailingSpace || trailingBreak) {
            allowBlockPlain = false;
            allowFlowPlain = false;
        }
        if (trailingSpace) {
            allowBlock = false;
        }
        if (breakSpace) {
            allowSingleQuoted = false;
            allowBlockPlain = false;
            allowFlowPlain = false;
        }
        if (spaceBreak || specialCharacters) {
            allowBlock = false;
            allowSingleQuoted = false;
            allowBlockPlain = false;
            allowFlowPlain = false;
        }
        if (lineBreaks) {
            allowFlowPlain = false;
        }
        if (flowIndicators) {
            allowFlowPlain = false;
        }
        if (!blockIndicators) return new ScalarAnalysis((String)scalar, (boolean)false, (boolean)lineBreaks, (boolean)allowFlowPlain, (boolean)allowBlockPlain, (boolean)allowSingleQuoted, (boolean)allowBlock);
        allowBlockPlain = false;
        return new ScalarAnalysis((String)scalar, (boolean)false, (boolean)lineBreaks, (boolean)allowFlowPlain, (boolean)allowBlockPlain, (boolean)allowSingleQuoted, (boolean)allowBlock);
    }

    void flushStream() throws IOException {
        this.stream.flush();
    }

    void writeStreamStart() {
    }

    void writeStreamEnd() throws IOException {
        this.flushStream();
    }

    void writeIndicator(String indicator, boolean needWhitespace, boolean whitespace, boolean indentation) throws IOException {
        if (!this.whitespace && needWhitespace) {
            ++this.column;
            this.stream.write((char[])SPACE);
        }
        this.whitespace = whitespace;
        this.indention = this.indention && indentation;
        this.column += indicator.length();
        this.openEnded = false;
        this.stream.write((String)indicator);
    }

    void writeIndent() throws IOException {
        int indent = this.indent != null ? this.indent.intValue() : 0;
        if (!this.indention || this.column > indent || this.column == indent && !this.whitespace) {
            this.writeLineBreak(null);
        }
        this.writeWhitespace((int)(indent - this.column));
    }

    private void writeWhitespace(int length) throws IOException {
        if (length <= 0) {
            return;
        }
        this.whitespace = true;
        char[] data = new char[length];
        int i = 0;
        do {
            if (i >= data.length) {
                this.column += length;
                this.stream.write((char[])data);
                return;
            }
            data[i] = 32;
            ++i;
        } while (true);
    }

    private void writeLineBreak(String data) throws IOException {
        this.whitespace = true;
        this.indention = true;
        this.column = 0;
        if (data == null) {
            this.stream.write((char[])this.bestLineBreak);
            return;
        }
        this.stream.write((String)data);
    }

    void writeVersionDirective(String versionText) throws IOException {
        this.stream.write((String)"%YAML ");
        this.stream.write((String)versionText);
        this.writeLineBreak(null);
    }

    void writeTagDirective(String handleText, String prefixText) throws IOException {
        this.stream.write((String)"%TAG ");
        this.stream.write((String)handleText);
        this.stream.write((char[])SPACE);
        this.stream.write((String)prefixText);
        this.writeLineBreak(null);
    }

    private void writeSingleQuoted(String text, boolean split) throws IOException {
        this.writeIndicator((String)"'", (boolean)true, (boolean)false, (boolean)false);
        boolean spaces = false;
        boolean breaks = false;
        int start = 0;
        int end = 0;
        do {
            char ch;
            block13 : {
                char[] arrc;
                int n;
                block15 : {
                    int len;
                    block14 : {
                        block12 : {
                            if (end > text.length()) {
                                this.writeIndicator((String)"'", (boolean)false, (boolean)false, (boolean)false);
                                return;
                            }
                            ch = '\u0000';
                            if (end < text.length()) {
                                ch = text.charAt((int)end);
                            }
                            if (!spaces) break block12;
                            if (ch == '\u0000' || ch != ' ') {
                                if (start + 1 == end && this.column > this.bestWidth && split && start != 0 && end != text.length()) {
                                    this.writeIndent();
                                } else {
                                    len = end - start;
                                    this.column += len;
                                    this.stream.write((String)text, (int)start, (int)len);
                                }
                                start = end;
                            }
                            break block13;
                        }
                        if (!breaks) break block14;
                        if (ch != '\u0000' && !Constant.LINEBR.hasNo((int)ch)) break block13;
                        if (text.charAt((int)start) == '\n') {
                            this.writeLineBreak(null);
                        }
                        String data = text.substring((int)start, (int)end);
                        arrc = data.toCharArray();
                        n = arrc.length;
                        break block15;
                    }
                    if (Constant.LINEBR.has((int)ch, (String)"\u0000 '") && start < end) {
                        len = end - start;
                        this.column += len;
                        this.stream.write((String)text, (int)start, (int)len);
                        start = end;
                    }
                    break block13;
                }
                for (int i = 0; i < n; ++i) {
                    char br = arrc[i];
                    if (br == '\n') {
                        this.writeLineBreak(null);
                        continue;
                    }
                    this.writeLineBreak((String)String.valueOf((char)br));
                }
                this.writeIndent();
                start = end;
            }
            if (ch == '\'') {
                this.column += 2;
                this.stream.write((String)"''");
                start = end + 1;
            }
            if (ch != '\u0000') {
                spaces = ch == ' ';
                breaks = Constant.LINEBR.has((int)ch);
            }
            ++end;
        } while (true);
    }

    private void writeDoubleQuoted(String text, boolean split) throws IOException {
        this.writeIndicator((String)"\"", (boolean)true, (boolean)false, (boolean)false);
        int start = 0;
        int end = 0;
        do {
            if (end > text.length()) {
                this.writeIndicator((String)"\"", (boolean)false, (boolean)false, (boolean)false);
                return;
            }
            Character ch = null;
            if (end < text.length()) {
                ch = Character.valueOf((char)text.charAt((int)end));
            }
            if (ch == null || "\"\\\u0085\u2028\u2029\ufeff".indexOf((int)ch.charValue()) != -1 || ' ' > ch.charValue() || ch.charValue() > '~') {
                if (start < end) {
                    int len = end - start;
                    this.column += len;
                    this.stream.write((String)text, (int)start, (int)len);
                    start = end;
                }
                if (ch != null) {
                    String data;
                    if (ESCAPE_REPLACEMENTS.containsKey((Object)ch)) {
                        data = "\\" + ESCAPE_REPLACEMENTS.get((Object)ch);
                    } else if (!this.allowUnicode || !StreamReader.isPrintable((int)ch.charValue())) {
                        String s;
                        if (ch.charValue() <= '\u00ff') {
                            s = "0" + Integer.toString((int)ch.charValue(), (int)16);
                            data = "\\x" + s.substring((int)(s.length() - 2));
                        } else if (ch.charValue() >= '\ud800' && ch.charValue() <= '\udbff') {
                            if (end + 1 < text.length()) {
                                Character ch2 = Character.valueOf((char)text.charAt((int)(++end)));
                                String s2 = "000" + Long.toHexString((long)((long)Character.toCodePoint((char)ch.charValue(), (char)ch2.charValue())));
                                data = "\\U" + s2.substring((int)(s2.length() - 8));
                            } else {
                                s = "000" + Integer.toString((int)ch.charValue(), (int)16);
                                data = "\\u" + s.substring((int)(s.length() - 4));
                            }
                        } else {
                            s = "000" + Integer.toString((int)ch.charValue(), (int)16);
                            data = "\\u" + s.substring((int)(s.length() - 4));
                        }
                    } else {
                        data = String.valueOf((Object)ch);
                    }
                    this.column += data.length();
                    this.stream.write((String)data);
                    start = end + 1;
                }
            }
            if (0 < end && end < text.length() - 1 && (ch.charValue() == ' ' || start >= end) && this.column + (end - start) > this.bestWidth && split) {
                String data = start >= end ? "\\" : text.substring((int)start, (int)end) + "\\";
                if (start < end) {
                    start = end;
                }
                this.column += data.length();
                this.stream.write((String)data);
                this.writeIndent();
                this.whitespace = false;
                this.indention = false;
                if (text.charAt((int)start) == ' ') {
                    data = "\\";
                    this.column += data.length();
                    this.stream.write((String)data);
                }
            }
            ++end;
        } while (true);
    }

    private String determineBlockHints(String text) {
        char ch1;
        StringBuilder hints = new StringBuilder();
        if (Constant.LINEBR.has((int)text.charAt((int)0), (String)" ")) {
            hints.append((int)this.bestIndent);
        }
        if (Constant.LINEBR.hasNo((int)(ch1 = text.charAt((int)(text.length() - 1))))) {
            hints.append((String)"-");
            return hints.toString();
        }
        if (text.length() != 1) {
            if (!Constant.LINEBR.has((int)text.charAt((int)(text.length() - 2)))) return hints.toString();
        }
        hints.append((String)"+");
        return hints.toString();
    }

    void writeFolded(String text, boolean split) throws IOException {
        String hints = this.determineBlockHints((String)text);
        this.writeIndicator((String)(">" + hints), (boolean)true, (boolean)false, (boolean)false);
        if (hints.length() > 0 && hints.charAt((int)(hints.length() - 1)) == '+') {
            this.openEnded = true;
        }
        this.writeLineBreak(null);
        boolean leadingSpace = true;
        boolean spaces = false;
        boolean breaks = true;
        int start = 0;
        int end = 0;
        while (end <= text.length()) {
            char ch;
            block16 : {
                int n;
                char[] arrc;
                block17 : {
                    block15 : {
                        ch = '\u0000';
                        if (end < text.length()) {
                            ch = text.charAt((int)end);
                        }
                        if (!breaks) break block15;
                        if (ch != '\u0000' && !Constant.LINEBR.hasNo((int)ch)) break block16;
                        if (!leadingSpace && ch != '\u0000' && ch != ' ' && text.charAt((int)start) == '\n') {
                            this.writeLineBreak(null);
                        }
                        leadingSpace = ch == ' ';
                        String data = text.substring((int)start, (int)end);
                        arrc = data.toCharArray();
                        n = arrc.length;
                        break block17;
                    }
                    if (spaces) {
                        if (ch != ' ') {
                            if (start + 1 == end && this.column > this.bestWidth && split) {
                                this.writeIndent();
                            } else {
                                int len = end - start;
                                this.column += len;
                                this.stream.write((String)text, (int)start, (int)len);
                            }
                            start = end;
                        }
                    } else if (Constant.LINEBR.has((int)ch, (String)"\u0000 ")) {
                        int len = end - start;
                        this.column += len;
                        this.stream.write((String)text, (int)start, (int)len);
                        if (ch == '\u0000') {
                            this.writeLineBreak(null);
                        }
                        start = end;
                    }
                    break block16;
                }
                for (int i = 0; i < n; ++i) {
                    char br = arrc[i];
                    if (br == '\n') {
                        this.writeLineBreak(null);
                        continue;
                    }
                    this.writeLineBreak((String)String.valueOf((char)br));
                }
                if (ch != '\u0000') {
                    this.writeIndent();
                }
                start = end;
            }
            if (ch != '\u0000') {
                breaks = Constant.LINEBR.has((int)ch);
                spaces = ch == ' ';
            }
            ++end;
        }
    }

    void writeLiteral(String text) throws IOException {
        String hints = this.determineBlockHints((String)text);
        this.writeIndicator((String)("|" + hints), (boolean)true, (boolean)false, (boolean)false);
        if (hints.length() > 0 && hints.charAt((int)(hints.length() - 1)) == '+') {
            this.openEnded = true;
        }
        this.writeLineBreak(null);
        boolean breaks = true;
        int start = 0;
        int end = 0;
        while (end <= text.length()) {
            char ch;
            block10 : {
                int n;
                char[] arrc;
                block11 : {
                    block9 : {
                        ch = '\u0000';
                        if (end < text.length()) {
                            ch = text.charAt((int)end);
                        }
                        if (!breaks) break block9;
                        if (ch != '\u0000' && !Constant.LINEBR.hasNo((int)ch)) break block10;
                        String data = text.substring((int)start, (int)end);
                        arrc = data.toCharArray();
                        n = arrc.length;
                        break block11;
                    }
                    if (ch == '\u0000' || Constant.LINEBR.has((int)ch)) {
                        this.stream.write((String)text, (int)start, (int)(end - start));
                        if (ch == '\u0000') {
                            this.writeLineBreak(null);
                        }
                        start = end;
                    }
                    break block10;
                }
                for (int i = 0; i < n; ++i) {
                    char br = arrc[i];
                    if (br == '\n') {
                        this.writeLineBreak(null);
                        continue;
                    }
                    this.writeLineBreak((String)String.valueOf((char)br));
                }
                if (ch != '\u0000') {
                    this.writeIndent();
                }
                start = end;
            }
            if (ch != '\u0000') {
                breaks = Constant.LINEBR.has((int)ch);
            }
            ++end;
        }
    }

    void writePlain(String text, boolean split) throws IOException {
        if (this.rootContext) {
            this.openEnded = true;
        }
        if (text.length() == 0) {
            return;
        }
        if (!this.whitespace) {
            ++this.column;
            this.stream.write((char[])SPACE);
        }
        this.whitespace = false;
        this.indention = false;
        boolean spaces = false;
        boolean breaks = false;
        int start = 0;
        int end = 0;
        while (end <= text.length()) {
            char ch;
            block14 : {
                char[] arrc;
                int n;
                block16 : {
                    int len;
                    block15 : {
                        block13 : {
                            ch = '\u0000';
                            if (end < text.length()) {
                                ch = text.charAt((int)end);
                            }
                            if (!spaces) break block13;
                            if (ch != ' ') {
                                if (start + 1 == end && this.column > this.bestWidth && split) {
                                    this.writeIndent();
                                    this.whitespace = false;
                                    this.indention = false;
                                } else {
                                    len = end - start;
                                    this.column += len;
                                    this.stream.write((String)text, (int)start, (int)len);
                                }
                                start = end;
                            }
                            break block14;
                        }
                        if (!breaks) break block15;
                        if (!Constant.LINEBR.hasNo((int)ch)) break block14;
                        if (text.charAt((int)start) == '\n') {
                            this.writeLineBreak(null);
                        }
                        String data = text.substring((int)start, (int)end);
                        arrc = data.toCharArray();
                        n = arrc.length;
                        break block16;
                    }
                    if (Constant.LINEBR.has((int)ch, (String)"\u0000 ")) {
                        len = end - start;
                        this.column += len;
                        this.stream.write((String)text, (int)start, (int)len);
                        start = end;
                    }
                    break block14;
                }
                for (int i = 0; i < n; ++i) {
                    char br = arrc[i];
                    if (br == '\n') {
                        this.writeLineBreak(null);
                        continue;
                    }
                    this.writeLineBreak((String)String.valueOf((char)br));
                }
                this.writeIndent();
                this.whitespace = false;
                this.indention = false;
                start = end;
            }
            if (ch != '\u0000') {
                spaces = ch == ' ';
                breaks = Constant.LINEBR.has((int)ch);
            }
            ++end;
        }
    }

    static /* synthetic */ Event access$100(Emitter x0) {
        return x0.event;
    }

    static /* synthetic */ EmitterState access$202(Emitter x0, EmitterState x1) {
        x0.state = x1;
        return x0.state;
    }

    static /* synthetic */ boolean access$400(Emitter x0) {
        return x0.openEnded;
    }

    static /* synthetic */ String access$500(Emitter x0, DumperOptions.Version x1) {
        return x0.prepareVersion((DumperOptions.Version)x1);
    }

    static /* synthetic */ Map access$602(Emitter x0, Map x1) {
        x0.tagPrefixes = x1;
        return x0.tagPrefixes;
    }

    static /* synthetic */ Map access$700() {
        return DEFAULT_TAG_PREFIXES;
    }

    static /* synthetic */ Map access$600(Emitter x0) {
        return x0.tagPrefixes;
    }

    static /* synthetic */ String access$800(Emitter x0, String x1) {
        return x0.prepareTagHandle((String)x1);
    }

    static /* synthetic */ String access$900(Emitter x0, String x1) {
        return x0.prepareTagPrefix((String)x1);
    }

    static /* synthetic */ Boolean access$1000(Emitter x0) {
        return x0.canonical;
    }

    static /* synthetic */ boolean access$1100(Emitter x0) {
        return x0.checkEmptyDocument();
    }

    static /* synthetic */ ArrayStack access$1500(Emitter x0) {
        return x0.states;
    }

    static /* synthetic */ void access$1600(Emitter x0, boolean x1, boolean x2, boolean x3) throws IOException {
        x0.expectNode((boolean)x1, (boolean)x2, (boolean)x3);
    }

    static /* synthetic */ Integer access$1802(Emitter x0, Integer x1) {
        x0.indent = x1;
        return x0.indent;
    }

    static /* synthetic */ ArrayStack access$1900(Emitter x0) {
        return x0.indents;
    }

    static /* synthetic */ int access$2010(Emitter x0) {
        return x0.flowLevel--;
    }

    static /* synthetic */ int access$2100(Emitter x0) {
        return x0.column;
    }

    static /* synthetic */ int access$2200(Emitter x0) {
        return x0.bestWidth;
    }

    static /* synthetic */ boolean access$2300(Emitter x0) {
        return x0.splitLines;
    }

    static /* synthetic */ Boolean access$2400(Emitter x0) {
        return x0.prettyFlow;
    }

    static /* synthetic */ boolean access$2700(Emitter x0) {
        return x0.checkSimpleKey();
    }

    static /* synthetic */ int access$3200(Emitter x0) {
        return x0.indicatorIndent;
    }

    static /* synthetic */ void access$3300(Emitter x0, int x1) throws IOException {
        x0.writeWhitespace((int)x1);
    }

    static {
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\u0000'), (String)"0");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\u0007'), (String)"a");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\b'), (String)"b");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\t'), (String)"t");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\n'), (String)"n");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\u000b'), (String)"v");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\f'), (String)"f");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\r'), (String)"r");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\u001b'), (String)"e");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\"'), (String)"\"");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\\'), (String)"\\");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\u0085'), (String)"N");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\u00a0'), (String)"_");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\u2028'), (String)"L");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\u2029'), (String)"P");
        DEFAULT_TAG_PREFIXES = new LinkedHashMap<String, String>();
        DEFAULT_TAG_PREFIXES.put((String)"!", (String)"!");
        DEFAULT_TAG_PREFIXES.put((String)"tag:yaml.org,2002:", (String)"!!");
        HANDLE_FORMAT = Pattern.compile((String)"^![-_\\w]*!$");
        ANCHOR_FORMAT = Pattern.compile((String)"^[-_\\w]*$");
    }
}

