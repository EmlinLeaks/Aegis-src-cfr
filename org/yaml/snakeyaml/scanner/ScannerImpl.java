/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.scanner;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.Constant;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerException;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.scanner.SimpleKey;
import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.tokens.BlockEndToken;
import org.yaml.snakeyaml.tokens.BlockEntryToken;
import org.yaml.snakeyaml.tokens.BlockMappingStartToken;
import org.yaml.snakeyaml.tokens.BlockSequenceStartToken;
import org.yaml.snakeyaml.tokens.DirectiveToken;
import org.yaml.snakeyaml.tokens.DocumentEndToken;
import org.yaml.snakeyaml.tokens.DocumentStartToken;
import org.yaml.snakeyaml.tokens.FlowEntryToken;
import org.yaml.snakeyaml.tokens.FlowMappingEndToken;
import org.yaml.snakeyaml.tokens.FlowMappingStartToken;
import org.yaml.snakeyaml.tokens.FlowSequenceEndToken;
import org.yaml.snakeyaml.tokens.FlowSequenceStartToken;
import org.yaml.snakeyaml.tokens.KeyToken;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.StreamEndToken;
import org.yaml.snakeyaml.tokens.StreamStartToken;
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.TagTuple;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.tokens.ValueToken;
import org.yaml.snakeyaml.util.ArrayStack;
import org.yaml.snakeyaml.util.UriEncoder;

public final class ScannerImpl
implements Scanner {
    private static final Pattern NOT_HEXA = Pattern.compile((String)"[^0-9A-Fa-f]");
    public static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<Character, String>();
    public static final Map<Character, Integer> ESCAPE_CODES = new HashMap<Character, Integer>();
    private final StreamReader reader;
    private boolean done = false;
    private int flowLevel = 0;
    private List<Token> tokens;
    private int tokensTaken = 0;
    private int indent = -1;
    private ArrayStack<Integer> indents;
    private boolean allowSimpleKey = true;
    private Map<Integer, SimpleKey> possibleSimpleKeys;

    public ScannerImpl(StreamReader reader) {
        this.reader = reader;
        this.tokens = new ArrayList<Token>((int)100);
        this.indents = new ArrayStack<T>((int)10);
        this.possibleSimpleKeys = new LinkedHashMap<Integer, SimpleKey>();
        this.fetchStreamStart();
    }

    @Override
    public boolean checkToken(Token.ID ... choices) {
        while (this.needMoreTokens()) {
            this.fetchMoreTokens();
        }
        if (this.tokens.isEmpty()) return false;
        if (choices.length == 0) {
            return true;
        }
        Token.ID first = this.tokens.get((int)0).getTokenId();
        int i = 0;
        while (i < choices.length) {
            if (first == choices[i]) {
                return true;
            }
            ++i;
        }
        return false;
    }

    @Override
    public Token peekToken() {
        while (this.needMoreTokens()) {
            this.fetchMoreTokens();
        }
        return this.tokens.get((int)0);
    }

    @Override
    public Token getToken() {
        ++this.tokensTaken;
        return this.tokens.remove((int)0);
    }

    private boolean needMoreTokens() {
        if (this.done) {
            return false;
        }
        if (this.tokens.isEmpty()) {
            return true;
        }
        this.stalePossibleSimpleKeys();
        if (this.nextPossibleSimpleKey() != this.tokensTaken) return false;
        return true;
    }

    private void fetchMoreTokens() {
        this.scanToNextToken();
        this.stalePossibleSimpleKeys();
        this.unwindIndent((int)this.reader.getColumn());
        int c = this.reader.peek();
        switch (c) {
            case 0: {
                this.fetchStreamEnd();
                return;
            }
            case 37: {
                if (!this.checkDirective()) break;
                this.fetchDirective();
                return;
            }
            case 45: {
                if (this.checkDocumentStart()) {
                    this.fetchDocumentStart();
                    return;
                }
                if (!this.checkBlockEntry()) break;
                this.fetchBlockEntry();
                return;
            }
            case 46: {
                if (!this.checkDocumentEnd()) break;
                this.fetchDocumentEnd();
                return;
            }
            case 91: {
                this.fetchFlowSequenceStart();
                return;
            }
            case 123: {
                this.fetchFlowMappingStart();
                return;
            }
            case 93: {
                this.fetchFlowSequenceEnd();
                return;
            }
            case 125: {
                this.fetchFlowMappingEnd();
                return;
            }
            case 44: {
                this.fetchFlowEntry();
                return;
            }
            case 63: {
                if (!this.checkKey()) break;
                this.fetchKey();
                return;
            }
            case 58: {
                if (!this.checkValue()) break;
                this.fetchValue();
                return;
            }
            case 42: {
                this.fetchAlias();
                return;
            }
            case 38: {
                this.fetchAnchor();
                return;
            }
            case 33: {
                this.fetchTag();
                return;
            }
            case 124: {
                if (this.flowLevel != 0) break;
                this.fetchLiteral();
                return;
            }
            case 62: {
                if (this.flowLevel != 0) break;
                this.fetchFolded();
                return;
            }
            case 39: {
                this.fetchSingle();
                return;
            }
            case 34: {
                this.fetchDouble();
                return;
            }
        }
        if (this.checkPlain()) {
            this.fetchPlain();
            return;
        }
        String chRepresentation = String.valueOf((char[])Character.toChars((int)c));
        for (Character s : ESCAPE_REPLACEMENTS.keySet()) {
            String v = ESCAPE_REPLACEMENTS.get((Object)s);
            if (!v.equals((Object)chRepresentation)) continue;
            chRepresentation = "\\" + s;
            break;
        }
        if (c == 9) {
            chRepresentation = chRepresentation + "(TAB)";
        }
        String text = String.format((String)"found character '%s' that cannot start any token. (Do not use %s for indentation)", (Object[])new Object[]{chRepresentation, chRepresentation});
        throw new ScannerException((String)"while scanning for the next token", null, (String)text, (Mark)this.reader.getMark());
    }

    private int nextPossibleSimpleKey() {
        if (this.possibleSimpleKeys.isEmpty()) return -1;
        return this.possibleSimpleKeys.values().iterator().next().getTokenNumber();
    }

    private void stalePossibleSimpleKeys() {
        if (this.possibleSimpleKeys.isEmpty()) return;
        Iterator<SimpleKey> iterator = this.possibleSimpleKeys.values().iterator();
        while (iterator.hasNext()) {
            SimpleKey key = iterator.next();
            if (key.getLine() == this.reader.getLine() && this.reader.getIndex() - key.getIndex() <= 1024) continue;
            if (key.isRequired()) {
                throw new ScannerException((String)"while scanning a simple key", (Mark)key.getMark(), (String)"could not find expected ':'", (Mark)this.reader.getMark());
            }
            iterator.remove();
        }
    }

    private void savePossibleSimpleKey() {
        boolean required;
        boolean bl = required = this.flowLevel == 0 && this.indent == this.reader.getColumn();
        if (!this.allowSimpleKey) {
            if (required) throw new YAMLException((String)"A simple key is required only if it is the first token in the current line");
        }
        if (!this.allowSimpleKey) return;
        this.removePossibleSimpleKey();
        int tokenNumber = this.tokensTaken + this.tokens.size();
        SimpleKey key = new SimpleKey((int)tokenNumber, (boolean)required, (int)this.reader.getIndex(), (int)this.reader.getLine(), (int)this.reader.getColumn(), (Mark)this.reader.getMark());
        this.possibleSimpleKeys.put((Integer)Integer.valueOf((int)this.flowLevel), (SimpleKey)key);
    }

    private void removePossibleSimpleKey() {
        SimpleKey key = this.possibleSimpleKeys.remove((Object)Integer.valueOf((int)this.flowLevel));
        if (key == null) return;
        if (!key.isRequired()) return;
        throw new ScannerException((String)"while scanning a simple key", (Mark)key.getMark(), (String)"could not find expected ':'", (Mark)this.reader.getMark());
    }

    private void unwindIndent(int col) {
        if (this.flowLevel != 0) {
            return;
        }
        while (this.indent > col) {
            Mark mark = this.reader.getMark();
            this.indent = this.indents.pop().intValue();
            this.tokens.add((Token)new BlockEndToken((Mark)mark, (Mark)mark));
        }
    }

    private boolean addIndent(int column) {
        if (this.indent >= column) return false;
        this.indents.push((Integer)Integer.valueOf((int)this.indent));
        this.indent = column;
        return true;
    }

    private void fetchStreamStart() {
        Mark mark = this.reader.getMark();
        StreamStartToken token = new StreamStartToken((Mark)mark, (Mark)mark);
        this.tokens.add((Token)token);
    }

    private void fetchStreamEnd() {
        this.unwindIndent((int)-1);
        this.removePossibleSimpleKey();
        this.allowSimpleKey = false;
        this.possibleSimpleKeys.clear();
        Mark mark = this.reader.getMark();
        StreamEndToken token = new StreamEndToken((Mark)mark, (Mark)mark);
        this.tokens.add((Token)token);
        this.done = true;
    }

    private void fetchDirective() {
        this.unwindIndent((int)-1);
        this.removePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = this.scanDirective();
        this.tokens.add((Token)tok);
    }

    private void fetchDocumentStart() {
        this.fetchDocumentIndicator((boolean)true);
    }

    private void fetchDocumentEnd() {
        this.fetchDocumentIndicator((boolean)false);
    }

    private void fetchDocumentIndicator(boolean isDocumentStart) {
        this.unwindIndent((int)-1);
        this.removePossibleSimpleKey();
        this.allowSimpleKey = false;
        Mark startMark = this.reader.getMark();
        this.reader.forward((int)3);
        Mark endMark = this.reader.getMark();
        Token token = isDocumentStart ? new DocumentStartToken((Mark)startMark, (Mark)endMark) : new DocumentEndToken((Mark)startMark, (Mark)endMark);
        this.tokens.add((Token)token);
    }

    private void fetchFlowSequenceStart() {
        this.fetchFlowCollectionStart((boolean)false);
    }

    private void fetchFlowMappingStart() {
        this.fetchFlowCollectionStart((boolean)true);
    }

    private void fetchFlowCollectionStart(boolean isMappingStart) {
        this.savePossibleSimpleKey();
        ++this.flowLevel;
        this.allowSimpleKey = true;
        Mark startMark = this.reader.getMark();
        this.reader.forward((int)1);
        Mark endMark = this.reader.getMark();
        Token token = isMappingStart ? new FlowMappingStartToken((Mark)startMark, (Mark)endMark) : new FlowSequenceStartToken((Mark)startMark, (Mark)endMark);
        this.tokens.add((Token)token);
    }

    private void fetchFlowSequenceEnd() {
        this.fetchFlowCollectionEnd((boolean)false);
    }

    private void fetchFlowMappingEnd() {
        this.fetchFlowCollectionEnd((boolean)true);
    }

    private void fetchFlowCollectionEnd(boolean isMappingEnd) {
        this.removePossibleSimpleKey();
        --this.flowLevel;
        this.allowSimpleKey = false;
        Mark startMark = this.reader.getMark();
        this.reader.forward();
        Mark endMark = this.reader.getMark();
        Token token = isMappingEnd ? new FlowMappingEndToken((Mark)startMark, (Mark)endMark) : new FlowSequenceEndToken((Mark)startMark, (Mark)endMark);
        this.tokens.add((Token)token);
    }

    private void fetchFlowEntry() {
        this.allowSimpleKey = true;
        this.removePossibleSimpleKey();
        Mark startMark = this.reader.getMark();
        this.reader.forward();
        Mark endMark = this.reader.getMark();
        FlowEntryToken token = new FlowEntryToken((Mark)startMark, (Mark)endMark);
        this.tokens.add((Token)token);
    }

    private void fetchBlockEntry() {
        if (this.flowLevel == 0) {
            if (!this.allowSimpleKey) {
                throw new ScannerException(null, null, (String)"sequence entries are not allowed here", (Mark)this.reader.getMark());
            }
            if (this.addIndent((int)this.reader.getColumn())) {
                Mark mark = this.reader.getMark();
                this.tokens.add((Token)new BlockSequenceStartToken((Mark)mark, (Mark)mark));
            }
        }
        this.allowSimpleKey = true;
        this.removePossibleSimpleKey();
        Mark startMark = this.reader.getMark();
        this.reader.forward();
        Mark endMark = this.reader.getMark();
        BlockEntryToken token = new BlockEntryToken((Mark)startMark, (Mark)endMark);
        this.tokens.add((Token)token);
    }

    private void fetchKey() {
        if (this.flowLevel == 0) {
            if (!this.allowSimpleKey) {
                throw new ScannerException(null, null, (String)"mapping keys are not allowed here", (Mark)this.reader.getMark());
            }
            if (this.addIndent((int)this.reader.getColumn())) {
                Mark mark = this.reader.getMark();
                this.tokens.add((Token)new BlockMappingStartToken((Mark)mark, (Mark)mark));
            }
        }
        this.allowSimpleKey = this.flowLevel == 0;
        this.removePossibleSimpleKey();
        Mark startMark = this.reader.getMark();
        this.reader.forward();
        Mark endMark = this.reader.getMark();
        KeyToken token = new KeyToken((Mark)startMark, (Mark)endMark);
        this.tokens.add((Token)token);
    }

    private void fetchValue() {
        SimpleKey key = this.possibleSimpleKeys.remove((Object)Integer.valueOf((int)this.flowLevel));
        if (key != null) {
            this.tokens.add((int)(key.getTokenNumber() - this.tokensTaken), (Token)new KeyToken((Mark)key.getMark(), (Mark)key.getMark()));
            if (this.flowLevel == 0 && this.addIndent((int)key.getColumn())) {
                this.tokens.add((int)(key.getTokenNumber() - this.tokensTaken), (Token)new BlockMappingStartToken((Mark)key.getMark(), (Mark)key.getMark()));
            }
            this.allowSimpleKey = false;
        } else {
            if (this.flowLevel == 0 && !this.allowSimpleKey) {
                throw new ScannerException(null, null, (String)"mapping values are not allowed here", (Mark)this.reader.getMark());
            }
            if (this.flowLevel == 0 && this.addIndent((int)this.reader.getColumn())) {
                Mark mark = this.reader.getMark();
                this.tokens.add((Token)new BlockMappingStartToken((Mark)mark, (Mark)mark));
            }
            this.allowSimpleKey = this.flowLevel == 0;
            this.removePossibleSimpleKey();
        }
        Mark startMark = this.reader.getMark();
        this.reader.forward();
        Mark endMark = this.reader.getMark();
        ValueToken token = new ValueToken((Mark)startMark, (Mark)endMark);
        this.tokens.add((Token)token);
    }

    private void fetchAlias() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = this.scanAnchor((boolean)false);
        this.tokens.add((Token)tok);
    }

    private void fetchAnchor() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = this.scanAnchor((boolean)true);
        this.tokens.add((Token)tok);
    }

    private void fetchTag() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = this.scanTag();
        this.tokens.add((Token)tok);
    }

    private void fetchLiteral() {
        this.fetchBlockScalar((char)'|');
    }

    private void fetchFolded() {
        this.fetchBlockScalar((char)'>');
    }

    private void fetchBlockScalar(char style) {
        this.allowSimpleKey = true;
        this.removePossibleSimpleKey();
        Token tok = this.scanBlockScalar((char)style);
        this.tokens.add((Token)tok);
    }

    private void fetchSingle() {
        this.fetchFlowScalar((char)'\'');
    }

    private void fetchDouble() {
        this.fetchFlowScalar((char)'\"');
    }

    private void fetchFlowScalar(char style) {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = this.scanFlowScalar((char)style);
        this.tokens.add((Token)tok);
    }

    private void fetchPlain() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = this.scanPlain();
        this.tokens.add((Token)tok);
    }

    private boolean checkDirective() {
        if (this.reader.getColumn() != 0) return false;
        return true;
    }

    private boolean checkDocumentStart() {
        if (this.reader.getColumn() != 0) return false;
        if (!"---".equals((Object)this.reader.prefix((int)3))) return false;
        if (!Constant.NULL_BL_T_LINEBR.has((int)this.reader.peek((int)3))) return false;
        return true;
    }

    private boolean checkDocumentEnd() {
        if (this.reader.getColumn() != 0) return false;
        if (!"...".equals((Object)this.reader.prefix((int)3))) return false;
        if (!Constant.NULL_BL_T_LINEBR.has((int)this.reader.peek((int)3))) return false;
        return true;
    }

    private boolean checkBlockEntry() {
        return Constant.NULL_BL_T_LINEBR.has((int)this.reader.peek((int)1));
    }

    private boolean checkKey() {
        if (this.flowLevel == 0) return Constant.NULL_BL_T_LINEBR.has((int)this.reader.peek((int)1));
        return true;
    }

    private boolean checkValue() {
        if (this.flowLevel == 0) return Constant.NULL_BL_T_LINEBR.has((int)this.reader.peek((int)1));
        return true;
    }

    private boolean checkPlain() {
        int c = this.reader.peek();
        if (Constant.NULL_BL_T_LINEBR.hasNo((int)c, (String)"-?:,[]{}#&*!|>'\"%@`")) return true;
        if (!Constant.NULL_BL_T_LINEBR.hasNo((int)this.reader.peek((int)1))) return false;
        if (c == 45) return true;
        if (this.flowLevel != 0) return false;
        if ("?:".indexOf((int)c) == -1) return false;
        return true;
    }

    private void scanToNextToken() {
        if (this.reader.getIndex() == 0 && this.reader.peek() == 65279) {
            this.reader.forward();
        }
        boolean found = false;
        while (!found) {
            int ff = 0;
            while (this.reader.peek((int)ff) == 32) {
                ++ff;
            }
            if (ff > 0) {
                this.reader.forward((int)ff);
            }
            if (this.reader.peek() == 35) {
                ff = 0;
                while (Constant.NULL_OR_LINEBR.hasNo((int)this.reader.peek((int)ff))) {
                    ++ff;
                }
                if (ff > 0) {
                    this.reader.forward((int)ff);
                }
            }
            if (this.scanLineBreak().length() != 0) {
                if (this.flowLevel != 0) continue;
                this.allowSimpleKey = true;
                continue;
            }
            found = true;
        }
    }

    private Token scanDirective() {
        Mark endMark;
        Mark startMark = this.reader.getMark();
        this.reader.forward();
        String name = this.scanDirectiveName((Mark)startMark);
        List<Object> value = null;
        if ("YAML".equals((Object)name)) {
            value = this.scanYamlDirectiveValue((Mark)startMark);
            endMark = this.reader.getMark();
        } else if ("TAG".equals((Object)name)) {
            value = this.scanTagDirectiveValue((Mark)startMark);
            endMark = this.reader.getMark();
        } else {
            endMark = this.reader.getMark();
            int ff = 0;
            while (Constant.NULL_OR_LINEBR.hasNo((int)this.reader.peek((int)ff))) {
                ++ff;
            }
            if (ff > 0) {
                this.reader.forward((int)ff);
            }
        }
        this.scanDirectiveIgnoredLine((Mark)startMark);
        return new DirectiveToken<Integer>((String)name, value, (Mark)startMark, (Mark)endMark);
    }

    private String scanDirectiveName(Mark startMark) {
        int length = 0;
        int c = this.reader.peek((int)length);
        while (Constant.ALPHA.has((int)c)) {
            c = this.reader.peek((int)(++length));
        }
        if (length == 0) {
            String s = String.valueOf((char[])Character.toChars((int)c));
            throw new ScannerException((String)"while scanning a directive", (Mark)startMark, (String)("expected alphabetic or numeric character, but found " + s + "(" + c + ")"), (Mark)this.reader.getMark());
        }
        String value = this.reader.prefixForward((int)length);
        c = this.reader.peek();
        if (!Constant.NULL_BL_LINEBR.hasNo((int)c)) return value;
        String s = String.valueOf((char[])Character.toChars((int)c));
        throw new ScannerException((String)"while scanning a directive", (Mark)startMark, (String)("expected alphabetic or numeric character, but found " + s + "(" + c + ")"), (Mark)this.reader.getMark());
    }

    private List<Integer> scanYamlDirectiveValue(Mark startMark) {
        while (this.reader.peek() == 32) {
            this.reader.forward();
        }
        Integer major = this.scanYamlDirectiveNumber((Mark)startMark);
        int c = this.reader.peek();
        if (c != 46) {
            String s = String.valueOf((char[])Character.toChars((int)c));
            throw new ScannerException((String)"while scanning a directive", (Mark)startMark, (String)("expected a digit or '.', but found " + s + "(" + c + ")"), (Mark)this.reader.getMark());
        }
        this.reader.forward();
        Integer minor = this.scanYamlDirectiveNumber((Mark)startMark);
        c = this.reader.peek();
        if (Constant.NULL_BL_LINEBR.hasNo((int)c)) {
            String s = String.valueOf((char[])Character.toChars((int)c));
            throw new ScannerException((String)"while scanning a directive", (Mark)startMark, (String)("expected a digit or ' ', but found " + s + "(" + c + ")"), (Mark)this.reader.getMark());
        }
        ArrayList<Integer> result = new ArrayList<Integer>((int)2);
        result.add(major);
        result.add(minor);
        return result;
    }

    private Integer scanYamlDirectiveNumber(Mark startMark) {
        int c = this.reader.peek();
        if (!Character.isDigit((int)c)) {
            String s = String.valueOf((char[])Character.toChars((int)c));
            throw new ScannerException((String)"while scanning a directive", (Mark)startMark, (String)("expected a digit, but found " + s + "(" + c + ")"), (Mark)this.reader.getMark());
        }
        int length = 0;
        while (Character.isDigit((int)this.reader.peek((int)length))) {
            ++length;
        }
        return Integer.valueOf((int)Integer.parseInt((String)this.reader.prefixForward((int)length)));
    }

    private List<String> scanTagDirectiveValue(Mark startMark) {
        while (this.reader.peek() == 32) {
            this.reader.forward();
        }
        String handle = this.scanTagDirectiveHandle((Mark)startMark);
        do {
            if (this.reader.peek() != 32) {
                String prefix = this.scanTagDirectivePrefix((Mark)startMark);
                ArrayList<String> result = new ArrayList<String>((int)2);
                result.add(handle);
                result.add(prefix);
                return result;
            }
            this.reader.forward();
        } while (true);
    }

    private String scanTagDirectiveHandle(Mark startMark) {
        String value = this.scanTagHandle((String)"directive", (Mark)startMark);
        int c = this.reader.peek();
        if (c == 32) return value;
        String s = String.valueOf((char[])Character.toChars((int)c));
        throw new ScannerException((String)"while scanning a directive", (Mark)startMark, (String)("expected ' ', but found " + s + "(" + c + ")"), (Mark)this.reader.getMark());
    }

    private String scanTagDirectivePrefix(Mark startMark) {
        String value = this.scanTagUri((String)"directive", (Mark)startMark);
        int c = this.reader.peek();
        if (!Constant.NULL_BL_LINEBR.hasNo((int)c)) return value;
        String s = String.valueOf((char[])Character.toChars((int)c));
        throw new ScannerException((String)"while scanning a directive", (Mark)startMark, (String)("expected ' ', but found " + s + "(" + c + ")"), (Mark)this.reader.getMark());
    }

    private void scanDirectiveIgnoredLine(Mark startMark) {
        while (this.reader.peek() == 32) {
            this.reader.forward();
        }
        if (this.reader.peek() == 35) {
            while (Constant.NULL_OR_LINEBR.hasNo((int)this.reader.peek())) {
                this.reader.forward();
            }
        }
        int c = this.reader.peek();
        String lineBreak = this.scanLineBreak();
        if (lineBreak.length() != 0) return;
        if (c == 0) return;
        String s = String.valueOf((char[])Character.toChars((int)c));
        throw new ScannerException((String)"while scanning a directive", (Mark)startMark, (String)("expected a comment or a line break, but found " + s + "(" + c + ")"), (Mark)this.reader.getMark());
    }

    private Token scanAnchor(boolean isAnchor) {
        Mark startMark = this.reader.getMark();
        int indicator = this.reader.peek();
        String name = indicator == 42 ? "alias" : "anchor";
        this.reader.forward();
        int length = 0;
        int c = this.reader.peek((int)length);
        while (Constant.ALPHA.has((int)c)) {
            c = this.reader.peek((int)(++length));
        }
        if (length == 0) {
            String s = String.valueOf((char[])Character.toChars((int)c));
            throw new ScannerException((String)("while scanning an " + name), (Mark)startMark, (String)("expected alphabetic or numeric character, but found " + s + "(" + c + ")"), (Mark)this.reader.getMark());
        }
        String value = this.reader.prefixForward((int)length);
        c = this.reader.peek();
        if (Constant.NULL_BL_T_LINEBR.hasNo((int)c, (String)"?:,]}%@`")) {
            String s = String.valueOf((char[])Character.toChars((int)c));
            throw new ScannerException((String)("while scanning an " + name), (Mark)startMark, (String)("expected alphabetic or numeric character, but found " + s + "(" + c + ")"), (Mark)this.reader.getMark());
        }
        Mark endMark = this.reader.getMark();
        if (!isAnchor) return new AliasToken((String)value, (Mark)startMark, (Mark)endMark);
        return new AnchorToken((String)value, (Mark)startMark, (Mark)endMark);
    }

    private Token scanTag() {
        Mark startMark = this.reader.getMark();
        int c = this.reader.peek((int)1);
        String handle = null;
        String suffix = null;
        if (c == 60) {
            this.reader.forward((int)2);
            suffix = this.scanTagUri((String)"tag", (Mark)startMark);
            c = this.reader.peek();
            if (c != 62) {
                String s = String.valueOf((char[])Character.toChars((int)c));
                throw new ScannerException((String)"while scanning a tag", (Mark)startMark, (String)("expected '>', but found '" + s + "' (" + c + ")"), (Mark)this.reader.getMark());
            }
            this.reader.forward();
        } else if (Constant.NULL_BL_T_LINEBR.has((int)c)) {
            suffix = "!";
            this.reader.forward();
        } else {
            int length = 1;
            boolean useHandle = false;
            while (Constant.NULL_BL_LINEBR.hasNo((int)c)) {
                if (c == 33) {
                    useHandle = true;
                    break;
                }
                c = this.reader.peek((int)(++length));
            }
            if (useHandle) {
                handle = this.scanTagHandle((String)"tag", (Mark)startMark);
            } else {
                handle = "!";
                this.reader.forward();
            }
            suffix = this.scanTagUri((String)"tag", (Mark)startMark);
        }
        c = this.reader.peek();
        if (Constant.NULL_BL_LINEBR.hasNo((int)c)) {
            String s = String.valueOf((char[])Character.toChars((int)c));
            throw new ScannerException((String)"while scanning a tag", (Mark)startMark, (String)("expected ' ', but found '" + s + "' (" + c + ")"), (Mark)this.reader.getMark());
        }
        TagTuple value = new TagTuple((String)handle, (String)suffix);
        Mark endMark = this.reader.getMark();
        return new TagToken((TagTuple)value, (Mark)startMark, (Mark)endMark);
    }

    private Token scanBlockScalar(char style) {
        int indent;
        Mark endMark;
        Object[] brme;
        String breaks;
        boolean folded = style == '>';
        StringBuilder chunks = new StringBuilder();
        Mark startMark = this.reader.getMark();
        this.reader.forward();
        Chomping chompi = this.scanBlockScalarIndicators((Mark)startMark);
        int increment = chompi.getIncrement();
        this.scanBlockScalarIgnoredLine((Mark)startMark);
        int minIndent = this.indent + 1;
        if (minIndent < 1) {
            minIndent = 1;
        }
        if (increment == -1) {
            brme = this.scanBlockScalarIndentation();
            breaks = (String)brme[0];
            int maxIndent = ((Integer)brme[1]).intValue();
            endMark = (Mark)brme[2];
            indent = Math.max((int)minIndent, (int)maxIndent);
        } else {
            indent = minIndent + increment - 1;
            brme = this.scanBlockScalarBreaks((int)indent);
            breaks = (String)brme[0];
            endMark = (Mark)brme[1];
        }
        String lineBreak = "";
        while (this.reader.getColumn() == indent && this.reader.peek() != 0) {
            chunks.append((String)breaks);
            boolean leadingNonSpace = " \t".indexOf((int)this.reader.peek()) == -1;
            int length = 0;
            while (Constant.NULL_OR_LINEBR.hasNo((int)this.reader.peek((int)length))) {
                ++length;
            }
            chunks.append((String)this.reader.prefixForward((int)length));
            lineBreak = this.scanLineBreak();
            Object[] brme2 = this.scanBlockScalarBreaks((int)indent);
            breaks = (String)brme2[0];
            endMark = (Mark)brme2[1];
            if (this.reader.getColumn() != indent || this.reader.peek() == 0) break;
            if (folded && "\n".equals((Object)lineBreak) && leadingNonSpace && " \t".indexOf((int)this.reader.peek()) == -1) {
                if (breaks.length() != 0) continue;
                chunks.append((String)" ");
                continue;
            }
            chunks.append((String)lineBreak);
        }
        if (chompi.chompTailIsNotFalse()) {
            chunks.append((String)lineBreak);
        }
        if (!chompi.chompTailIsTrue()) return new ScalarToken((String)chunks.toString(), (boolean)false, (Mark)startMark, (Mark)endMark, (DumperOptions.ScalarStyle)DumperOptions.ScalarStyle.createStyle((Character)Character.valueOf((char)style)));
        chunks.append((String)breaks);
        return new ScalarToken((String)chunks.toString(), (boolean)false, (Mark)startMark, (Mark)endMark, (DumperOptions.ScalarStyle)DumperOptions.ScalarStyle.createStyle((Character)Character.valueOf((char)style)));
    }

    private Chomping scanBlockScalarIndicators(Mark startMark) {
        String s;
        Boolean chomping = null;
        int increment = -1;
        int c = this.reader.peek();
        if (c == 45 || c == 43) {
            chomping = c == 43 ? Boolean.TRUE : Boolean.FALSE;
            this.reader.forward();
            c = this.reader.peek();
            if (Character.isDigit((int)c)) {
                s = String.valueOf((char[])Character.toChars((int)c));
                increment = Integer.parseInt((String)s);
                if (increment == 0) {
                    throw new ScannerException((String)"while scanning a block scalar", (Mark)startMark, (String)"expected indentation indicator in the range 1-9, but found 0", (Mark)this.reader.getMark());
                }
                this.reader.forward();
            }
        } else if (Character.isDigit((int)c)) {
            s = String.valueOf((char[])Character.toChars((int)c));
            increment = Integer.parseInt((String)s);
            if (increment == 0) {
                throw new ScannerException((String)"while scanning a block scalar", (Mark)startMark, (String)"expected indentation indicator in the range 1-9, but found 0", (Mark)this.reader.getMark());
            }
            this.reader.forward();
            c = this.reader.peek();
            if (c == 45 || c == 43) {
                chomping = c == 43 ? Boolean.TRUE : Boolean.FALSE;
                this.reader.forward();
            }
        }
        if (!Constant.NULL_BL_LINEBR.hasNo((int)(c = this.reader.peek()))) return new Chomping((Boolean)chomping, (int)increment);
        s = String.valueOf((char[])Character.toChars((int)c));
        throw new ScannerException((String)"while scanning a block scalar", (Mark)startMark, (String)("expected chomping or indentation indicators, but found " + s + "(" + c + ")"), (Mark)this.reader.getMark());
    }

    private String scanBlockScalarIgnoredLine(Mark startMark) {
        while (this.reader.peek() == 32) {
            this.reader.forward();
        }
        if (this.reader.peek() == 35) {
            while (Constant.NULL_OR_LINEBR.hasNo((int)this.reader.peek())) {
                this.reader.forward();
            }
        }
        int c = this.reader.peek();
        String lineBreak = this.scanLineBreak();
        if (lineBreak.length() != 0) return lineBreak;
        if (c == 0) return lineBreak;
        String s = String.valueOf((char[])Character.toChars((int)c));
        throw new ScannerException((String)"while scanning a block scalar", (Mark)startMark, (String)("expected a comment or a line break, but found " + s + "(" + c + ")"), (Mark)this.reader.getMark());
    }

    private Object[] scanBlockScalarIndentation() {
        StringBuilder chunks = new StringBuilder();
        int maxIndent = 0;
        Mark endMark = this.reader.getMark();
        while (Constant.LINEBR.has((int)this.reader.peek(), (String)" \r")) {
            if (this.reader.peek() != 32) {
                chunks.append((String)this.scanLineBreak());
                endMark = this.reader.getMark();
                continue;
            }
            this.reader.forward();
            if (this.reader.getColumn() <= maxIndent) continue;
            maxIndent = this.reader.getColumn();
        }
        return new Object[]{chunks.toString(), Integer.valueOf((int)maxIndent), endMark};
    }

    private Object[] scanBlockScalarBreaks(int indent) {
        int col;
        StringBuilder chunks = new StringBuilder();
        Mark endMark = this.reader.getMark();
        for (col = this.reader.getColumn(); col < indent && this.reader.peek() == 32; ++col) {
            this.reader.forward();
        }
        String lineBreak = null;
        block1 : while ((lineBreak = this.scanLineBreak()).length() != 0) {
            chunks.append((String)lineBreak);
            endMark = this.reader.getMark();
            col = this.reader.getColumn();
            do {
                if (col >= indent || this.reader.peek() != 32) continue block1;
                this.reader.forward();
                ++col;
            } while (true);
            break;
        }
        return new Object[]{chunks.toString(), endMark};
    }

    private Token scanFlowScalar(char style) {
        boolean _double = style == '\"';
        StringBuilder chunks = new StringBuilder();
        Mark startMark = this.reader.getMark();
        int quote = this.reader.peek();
        this.reader.forward();
        chunks.append((String)this.scanFlowScalarNonSpaces((boolean)_double, (Mark)startMark));
        do {
            if (this.reader.peek() == quote) {
                this.reader.forward();
                Mark endMark = this.reader.getMark();
                return new ScalarToken((String)chunks.toString(), (boolean)false, (Mark)startMark, (Mark)endMark, (DumperOptions.ScalarStyle)DumperOptions.ScalarStyle.createStyle((Character)Character.valueOf((char)style)));
            }
            chunks.append((String)this.scanFlowScalarSpaces((Mark)startMark));
            chunks.append((String)this.scanFlowScalarNonSpaces((boolean)_double, (Mark)startMark));
        } while (true);
    }

    private String scanFlowScalarNonSpaces(boolean doubleQuoted, Mark startMark) {
        StringBuilder chunks = new StringBuilder();
        do {
            int length = 0;
            while (Constant.NULL_BL_T_LINEBR.hasNo((int)this.reader.peek((int)length), (String)"'\"\\")) {
                ++length;
            }
            if (length != 0) {
                chunks.append((String)this.reader.prefixForward((int)length));
            }
            int c = this.reader.peek();
            if (!doubleQuoted && c == 39 && this.reader.peek((int)1) == 39) {
                chunks.append((String)"'");
                this.reader.forward((int)2);
                continue;
            }
            if (doubleQuoted && c == 39 || !doubleQuoted && "\"\\".indexOf((int)c) != -1) {
                chunks.appendCodePoint((int)c);
                this.reader.forward();
                continue;
            }
            if (!doubleQuoted) return chunks.toString();
            if (c != 92) return chunks.toString();
            this.reader.forward();
            c = this.reader.peek();
            if (!Character.isSupplementaryCodePoint((int)c) && ESCAPE_REPLACEMENTS.containsKey((Object)Character.valueOf((char)((char)c)))) {
                chunks.append((String)ESCAPE_REPLACEMENTS.get((Object)Character.valueOf((char)((char)c))));
                this.reader.forward();
                continue;
            }
            if (!Character.isSupplementaryCodePoint((int)c) && ESCAPE_CODES.containsKey((Object)Character.valueOf((char)((char)c)))) {
                length = ESCAPE_CODES.get((Object)Character.valueOf((char)((char)c))).intValue();
                this.reader.forward();
                String hex = this.reader.prefix((int)length);
                if (NOT_HEXA.matcher((CharSequence)hex).find()) {
                    throw new ScannerException((String)"while scanning a double-quoted scalar", (Mark)startMark, (String)("expected escape sequence of " + length + " hexadecimal numbers, but found: " + hex), (Mark)this.reader.getMark());
                }
                int decimal = Integer.parseInt((String)hex, (int)16);
                String unicode = new String((char[])Character.toChars((int)decimal));
                chunks.append((String)unicode);
                this.reader.forward((int)length);
                continue;
            }
            if (this.scanLineBreak().length() == 0) {
                String s = String.valueOf((char[])Character.toChars((int)c));
                throw new ScannerException((String)"while scanning a double-quoted scalar", (Mark)startMark, (String)("found unknown escape character " + s + "(" + c + ")"), (Mark)this.reader.getMark());
            }
            chunks.append((String)this.scanFlowScalarBreaks((Mark)startMark));
        } while (true);
    }

    private String scanFlowScalarSpaces(Mark startMark) {
        StringBuilder chunks = new StringBuilder();
        int length = 0;
        while (" \t".indexOf((int)this.reader.peek((int)length)) != -1) {
            ++length;
        }
        String whitespaces = this.reader.prefixForward((int)length);
        int c = this.reader.peek();
        if (c == 0) {
            throw new ScannerException((String)"while scanning a quoted scalar", (Mark)startMark, (String)"found unexpected end of stream", (Mark)this.reader.getMark());
        }
        String lineBreak = this.scanLineBreak();
        if (lineBreak.length() == 0) {
            chunks.append((String)whitespaces);
            return chunks.toString();
        }
        String breaks = this.scanFlowScalarBreaks((Mark)startMark);
        if (!"\n".equals((Object)lineBreak)) {
            chunks.append((String)lineBreak);
        } else if (breaks.length() == 0) {
            chunks.append((String)" ");
        }
        chunks.append((String)breaks);
        return chunks.toString();
    }

    private String scanFlowScalarBreaks(Mark startMark) {
        StringBuilder chunks = new StringBuilder();
        do {
            String prefix;
            if (("---".equals((Object)(prefix = this.reader.prefix((int)3))) || "...".equals((Object)prefix)) && Constant.NULL_BL_T_LINEBR.has((int)this.reader.peek((int)3))) {
                throw new ScannerException((String)"while scanning a quoted scalar", (Mark)startMark, (String)"found unexpected document separator", (Mark)this.reader.getMark());
            }
            while (" \t".indexOf((int)this.reader.peek()) != -1) {
                this.reader.forward();
            }
            String lineBreak = this.scanLineBreak();
            if (lineBreak.length() == 0) return chunks.toString();
            chunks.append((String)lineBreak);
        } while (true);
    }

    private Token scanPlain() {
        Mark startMark;
        StringBuilder chunks = new StringBuilder();
        Mark endMark = startMark = this.reader.getMark();
        int indent = this.indent + 1;
        String spaces = "";
        do {
            int c;
            int length = 0;
            if (this.reader.peek() == 35) {
                return new ScalarToken((String)chunks.toString(), (Mark)startMark, (Mark)endMark, (boolean)true);
            }
            while (!(Constant.NULL_BL_T_LINEBR.has((int)(c = this.reader.peek((int)length))) || c == 58 && Constant.NULL_BL_T_LINEBR.has((int)this.reader.peek((int)(length + 1)), (String)(this.flowLevel != 0 ? ",[]{}" : "")) || this.flowLevel != 0 && ",?[]{}".indexOf((int)c) != -1)) {
                ++length;
            }
            if (length == 0) {
                return new ScalarToken((String)chunks.toString(), (Mark)startMark, (Mark)endMark, (boolean)true);
            }
            this.allowSimpleKey = false;
            chunks.append((String)spaces);
            chunks.append((String)this.reader.prefixForward((int)length));
            endMark = this.reader.getMark();
            spaces = this.scanPlainSpaces();
            if (spaces.length() == 0) return new ScalarToken((String)chunks.toString(), (Mark)startMark, (Mark)endMark, (boolean)true);
            if (this.reader.peek() == 35) return new ScalarToken((String)chunks.toString(), (Mark)startMark, (Mark)endMark, (boolean)true);
        } while (this.flowLevel != 0 || this.reader.getColumn() >= indent);
        return new ScalarToken((String)chunks.toString(), (Mark)startMark, (Mark)endMark, (boolean)true);
    }

    private String scanPlainSpaces() {
        int length = 0;
        while (this.reader.peek((int)length) == 32 || this.reader.peek((int)length) == 9) {
            ++length;
        }
        String whitespaces = this.reader.prefixForward((int)length);
        String lineBreak = this.scanLineBreak();
        if (lineBreak.length() == 0) return whitespaces;
        this.allowSimpleKey = true;
        String prefix = this.reader.prefix((int)3);
        if ("---".equals((Object)prefix)) return "";
        if ("...".equals((Object)prefix) && Constant.NULL_BL_T_LINEBR.has((int)this.reader.peek((int)3))) {
            return "";
        }
        StringBuilder breaks = new StringBuilder();
        do {
            if (this.reader.peek() == 32) {
                this.reader.forward();
                continue;
            }
            String lb = this.scanLineBreak();
            if (lb.length() == 0) break;
            breaks.append((String)lb);
            prefix = this.reader.prefix((int)3);
            if ("---".equals((Object)prefix)) return "";
            if ("...".equals((Object)prefix) && Constant.NULL_BL_T_LINEBR.has((int)this.reader.peek((int)3))) return "";
        } while (true);
        if (!"\n".equals((Object)lineBreak)) {
            return lineBreak + breaks;
        }
        if (breaks.length() != 0) return breaks.toString();
        return " ";
    }

    private String scanTagHandle(String name, Mark startMark) {
        int c = this.reader.peek();
        if (c != 33) {
            String s = String.valueOf((char[])Character.toChars((int)c));
            throw new ScannerException((String)("while scanning a " + name), (Mark)startMark, (String)("expected '!', but found " + s + "(" + c + ")"), (Mark)this.reader.getMark());
        }
        int length = 1;
        c = this.reader.peek((int)length);
        if (c == 32) return this.reader.prefixForward((int)length);
        while (Constant.ALPHA.has((int)c)) {
            c = this.reader.peek((int)(++length));
        }
        if (c != 33) {
            this.reader.forward((int)length);
            String s = String.valueOf((char[])Character.toChars((int)c));
            throw new ScannerException((String)("while scanning a " + name), (Mark)startMark, (String)("expected '!', but found " + s + "(" + c + ")"), (Mark)this.reader.getMark());
        }
        ++length;
        return this.reader.prefixForward((int)length);
    }

    private String scanTagUri(String name, Mark startMark) {
        StringBuilder chunks = new StringBuilder();
        int length = 0;
        int c = this.reader.peek((int)length);
        while (Constant.URI_CHARS.has((int)c)) {
            if (c == 37) {
                chunks.append((String)this.reader.prefixForward((int)length));
                length = 0;
                chunks.append((String)this.scanUriEscapes((String)name, (Mark)startMark));
            } else {
                ++length;
            }
            c = this.reader.peek((int)length);
        }
        if (length != 0) {
            chunks.append((String)this.reader.prefixForward((int)length));
        }
        if (chunks.length() != 0) return chunks.toString();
        String s = String.valueOf((char[])Character.toChars((int)c));
        throw new ScannerException((String)("while scanning a " + name), (Mark)startMark, (String)("expected URI, but found " + s + "(" + c + ")"), (Mark)this.reader.getMark());
    }

    private String scanUriEscapes(String name, Mark startMark) {
        int length = 1;
        while (this.reader.peek((int)(length * 3)) == 37) {
            ++length;
        }
        Mark beginningMark = this.reader.getMark();
        ByteBuffer buff = ByteBuffer.allocate((int)length);
        while (this.reader.peek() == 37) {
            this.reader.forward();
            try {
                byte code = (byte)Integer.parseInt((String)this.reader.prefix((int)2), (int)16);
                buff.put((byte)code);
            }
            catch (NumberFormatException nfe) {
                int c1 = this.reader.peek();
                String s1 = String.valueOf((char[])Character.toChars((int)c1));
                int c2 = this.reader.peek((int)1);
                String s2 = String.valueOf((char[])Character.toChars((int)c2));
                throw new ScannerException((String)("while scanning a " + name), (Mark)startMark, (String)("expected URI escape sequence of 2 hexadecimal numbers, but found " + s1 + "(" + c1 + ") and " + s2 + "(" + c2 + ")"), (Mark)this.reader.getMark());
            }
            this.reader.forward((int)2);
        }
        buff.flip();
        try {
            return UriEncoder.decode((ByteBuffer)buff);
        }
        catch (CharacterCodingException e) {
            throw new ScannerException((String)("while scanning a " + name), (Mark)startMark, (String)("expected URI in UTF-8: " + e.getMessage()), (Mark)beginningMark);
        }
    }

    private String scanLineBreak() {
        int c = this.reader.peek();
        if (c == 13 || c == 10 || c == 133) {
            if (c == 13 && 10 == this.reader.peek((int)1)) {
                this.reader.forward((int)2);
                return "\n";
            }
            this.reader.forward();
            return "\n";
        }
        if (c != 8232) {
            if (c != 8233) return "";
        }
        this.reader.forward();
        return String.valueOf((char[])Character.toChars((int)c));
    }

    static {
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'0'), (String)"\u0000");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'a'), (String)"\u0007");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'b'), (String)"\b");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'t'), (String)"\t");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'n'), (String)"\n");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'v'), (String)"\u000b");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'f'), (String)"\f");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'r'), (String)"\r");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'e'), (String)"\u001b");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)' '), (String)" ");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\"'), (String)"\"");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'\\'), (String)"\\");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'N'), (String)"\u0085");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'_'), (String)"\u00a0");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'L'), (String)"\u2028");
        ESCAPE_REPLACEMENTS.put((Character)Character.valueOf((char)'P'), (String)"\u2029");
        ESCAPE_CODES.put((Character)Character.valueOf((char)'x'), (Integer)Integer.valueOf((int)2));
        ESCAPE_CODES.put((Character)Character.valueOf((char)'u'), (Integer)Integer.valueOf((int)4));
        ESCAPE_CODES.put((Character)Character.valueOf((char)'U'), (Integer)Integer.valueOf((int)8));
    }
}

