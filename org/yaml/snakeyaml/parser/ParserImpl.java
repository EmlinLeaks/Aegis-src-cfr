/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.yaml.snakeyaml.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.parser.Production;
import org.yaml.snakeyaml.parser.VersionTagsTuple;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.tokens.DirectiveToken;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.TagTuple;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.util.ArrayStack;

public class ParserImpl
implements Parser {
    private static final Map<String, String> DEFAULT_TAGS = new HashMap<String, String>();
    protected final Scanner scanner;
    private Event currentEvent;
    private final ArrayStack<Production> states;
    private final ArrayStack<Mark> marks;
    private Production state;
    private VersionTagsTuple directives;

    public ParserImpl(StreamReader reader) {
        this((Scanner)new ScannerImpl((StreamReader)reader));
    }

    public ParserImpl(Scanner scanner) {
        this.scanner = scanner;
        this.currentEvent = null;
        this.directives = new VersionTagsTuple(null, new HashMap<String, String>(DEFAULT_TAGS));
        this.states = new ArrayStack<T>((int)100);
        this.marks = new ArrayStack<T>((int)10);
        this.state = new ParseStreamStart((ParserImpl)this, null);
    }

    @Override
    public boolean checkEvent(Event.ID choice) {
        this.peekEvent();
        if (this.currentEvent == null) return false;
        if (!this.currentEvent.is((Event.ID)choice)) return false;
        return true;
    }

    @Override
    public Event peekEvent() {
        if (this.currentEvent != null) return this.currentEvent;
        if (this.state == null) return this.currentEvent;
        this.currentEvent = this.state.produce();
        return this.currentEvent;
    }

    @Override
    public Event getEvent() {
        this.peekEvent();
        Event value = this.currentEvent;
        this.currentEvent = null;
        return value;
    }

    /*
     * Unable to fully structure code
     */
    private VersionTagsTuple processDirectives() {
        yamlVersion = null;
        tagHandles = new HashMap<String, String>();
        while (this.scanner.checkToken((Token.ID[])new Token.ID[]{Token.ID.Directive})) {
            token = (DirectiveToken)this.scanner.getToken();
            if (token.getName().equals((Object)"YAML")) {
                if (yamlVersion != null) {
                    throw new ParserException(null, null, (String)"found duplicate YAML directive", (Mark)token.getStartMark());
                }
                value = token.getValue();
                major = (Integer)value.get((int)0);
                if (major.intValue() != 1) {
                    throw new ParserException(null, null, (String)"found incompatible YAML document (version 1.* is required)", (Mark)token.getStartMark());
                }
                minor = (Integer)value.get((int)1);
                switch (minor.intValue()) {
                    case 0: {
                        yamlVersion = DumperOptions.Version.V1_0;
                        ** break;
                    }
                }
                yamlVersion = DumperOptions.Version.V1_1;
                ** break;
lbl19: // 2 sources:
                continue;
            }
            if (!token.getName().equals((Object)"TAG")) continue;
            value = token.getValue();
            handle = (String)value.get((int)0);
            prefix = (String)value.get((int)1);
            if (tagHandles.containsKey((Object)handle)) {
                throw new ParserException(null, null, (String)("duplicate tag handle " + handle), (Mark)token.getStartMark());
            }
            tagHandles.put((String)handle, (String)prefix);
        }
        if (yamlVersion == null) {
            if (tagHandles.isEmpty() != false) return this.directives;
        }
        token = ParserImpl.DEFAULT_TAGS.keySet().iterator();
        do {
            if (!token.hasNext()) {
                this.directives = new VersionTagsTuple(yamlVersion, tagHandles);
                return this.directives;
            }
            key = (String)token.next();
            if (tagHandles.containsKey((Object)key)) continue;
            tagHandles.put((String)key, (String)ParserImpl.DEFAULT_TAGS.get((Object)key));
        } while (true);
    }

    private Event parseFlowNode() {
        return this.parseNode((boolean)false, (boolean)false);
    }

    private Event parseBlockNodeOrIndentlessSequence() {
        return this.parseNode((boolean)true, (boolean)true);
    }

    private Event parseNode(boolean block, boolean indentlessSequence) {
        NodeEvent event;
        boolean implicit;
        Mark startMark = null;
        Mark endMark = null;
        Mark tagMark = null;
        if (this.scanner.checkToken((Token.ID[])new Token.ID[]{Token.ID.Alias})) {
            AliasToken token = (AliasToken)this.scanner.getToken();
            event = new AliasEvent((String)token.getValue(), (Mark)token.getStartMark(), (Mark)token.getEndMark());
            this.state = this.states.pop();
            return event;
        }
        String anchor = null;
        TagTuple tagTokenTag = null;
        if (this.scanner.checkToken((Token.ID[])new Token.ID[]{Token.ID.Anchor})) {
            AnchorToken token = (AnchorToken)this.scanner.getToken();
            startMark = token.getStartMark();
            endMark = token.getEndMark();
            anchor = token.getValue();
            if (this.scanner.checkToken((Token.ID[])new Token.ID[]{Token.ID.Tag})) {
                TagToken tagToken = (TagToken)this.scanner.getToken();
                tagMark = tagToken.getStartMark();
                endMark = tagToken.getEndMark();
                tagTokenTag = tagToken.getValue();
            }
        } else if (this.scanner.checkToken((Token.ID[])new Token.ID[]{Token.ID.Tag})) {
            TagToken tagToken = (TagToken)this.scanner.getToken();
            tagMark = startMark = tagToken.getStartMark();
            endMark = tagToken.getEndMark();
            tagTokenTag = tagToken.getValue();
            if (this.scanner.checkToken((Token.ID[])new Token.ID[]{Token.ID.Anchor})) {
                AnchorToken token = (AnchorToken)this.scanner.getToken();
                endMark = token.getEndMark();
                anchor = token.getValue();
            }
        }
        String tag = null;
        if (tagTokenTag != null) {
            String handle = tagTokenTag.getHandle();
            String suffix = tagTokenTag.getSuffix();
            if (handle != null) {
                if (!this.directives.getTags().containsKey((Object)handle)) {
                    throw new ParserException((String)"while parsing a node", (Mark)startMark, (String)("found undefined tag handle " + handle), (Mark)tagMark);
                }
                tag = this.directives.getTags().get((Object)handle) + suffix;
            } else {
                tag = suffix;
            }
        }
        if (startMark == null) {
            endMark = startMark = this.scanner.peekToken().getStartMark();
        }
        event = null;
        boolean bl = implicit = tag == null || tag.equals((Object)"!");
        if (indentlessSequence) {
            if (this.scanner.checkToken((Token.ID[])new Token.ID[]{Token.ID.BlockEntry})) {
                endMark = this.scanner.peekToken().getEndMark();
                event = new SequenceStartEvent((String)anchor, (String)tag, (boolean)implicit, (Mark)startMark, (Mark)endMark, (DumperOptions.FlowStyle)DumperOptions.FlowStyle.BLOCK);
                this.state = new ParseIndentlessSequenceEntry((ParserImpl)this, null);
                return event;
            }
        }
        if (this.scanner.checkToken((Token.ID[])new Token.ID[]{Token.ID.Scalar})) {
            ScalarToken token = (ScalarToken)this.scanner.getToken();
            endMark = token.getEndMark();
            ImplicitTuple implicitValues = token.getPlain() && tag == null || "!".equals((Object)tag) ? new ImplicitTuple((boolean)true, (boolean)false) : (tag == null ? new ImplicitTuple((boolean)false, (boolean)true) : new ImplicitTuple((boolean)false, (boolean)false));
            event = new ScalarEvent((String)anchor, (String)tag, (ImplicitTuple)implicitValues, (String)token.getValue(), (Mark)startMark, (Mark)endMark, (DumperOptions.ScalarStyle)token.getStyle());
            this.state = this.states.pop();
            return event;
        }
        if (this.scanner.checkToken((Token.ID[])new Token.ID[]{Token.ID.FlowSequenceStart})) {
            endMark = this.scanner.peekToken().getEndMark();
            event = new SequenceStartEvent((String)anchor, (String)tag, (boolean)implicit, (Mark)startMark, (Mark)endMark, (DumperOptions.FlowStyle)DumperOptions.FlowStyle.FLOW);
            this.state = new ParseFlowSequenceFirstEntry((ParserImpl)this, null);
            return event;
        }
        if (this.scanner.checkToken((Token.ID[])new Token.ID[]{Token.ID.FlowMappingStart})) {
            endMark = this.scanner.peekToken().getEndMark();
            event = new MappingStartEvent((String)anchor, (String)tag, (boolean)implicit, (Mark)startMark, (Mark)endMark, (DumperOptions.FlowStyle)DumperOptions.FlowStyle.FLOW);
            this.state = new ParseFlowMappingFirstKey((ParserImpl)this, null);
            return event;
        }
        if (block) {
            if (this.scanner.checkToken((Token.ID[])new Token.ID[]{Token.ID.BlockSequenceStart})) {
                endMark = this.scanner.peekToken().getStartMark();
                event = new SequenceStartEvent((String)anchor, (String)tag, (boolean)implicit, (Mark)startMark, (Mark)endMark, (DumperOptions.FlowStyle)DumperOptions.FlowStyle.BLOCK);
                this.state = new ParseBlockSequenceFirstEntry((ParserImpl)this, null);
                return event;
            }
        }
        if (block) {
            if (this.scanner.checkToken((Token.ID[])new Token.ID[]{Token.ID.BlockMappingStart})) {
                endMark = this.scanner.peekToken().getStartMark();
                event = new MappingStartEvent((String)anchor, (String)tag, (boolean)implicit, (Mark)startMark, (Mark)endMark, (DumperOptions.FlowStyle)DumperOptions.FlowStyle.BLOCK);
                this.state = new ParseBlockMappingFirstKey((ParserImpl)this, null);
                return event;
            }
        }
        if (anchor != null || tag != null) {
            event = new ScalarEvent((String)anchor, (String)tag, (ImplicitTuple)new ImplicitTuple((boolean)implicit, (boolean)false), (String)"", (Mark)startMark, (Mark)endMark, (DumperOptions.ScalarStyle)DumperOptions.ScalarStyle.PLAIN);
            this.state = this.states.pop();
            return event;
        }
        String node = block ? "block" : "flow";
        Token token = this.scanner.peekToken();
        throw new ParserException((String)("while parsing a " + node + " node"), (Mark)startMark, (String)("expected the node content, but found '" + (Object)((Object)token.getTokenId()) + "'"), (Mark)token.getStartMark());
    }

    private Event processEmptyScalar(Mark mark) {
        return new ScalarEvent(null, null, (ImplicitTuple)new ImplicitTuple((boolean)true, (boolean)false), (String)"", (Mark)mark, (Mark)mark, (DumperOptions.ScalarStyle)DumperOptions.ScalarStyle.PLAIN);
    }

    static /* synthetic */ Production access$102(ParserImpl x0, Production x1) {
        x0.state = x1;
        return x0.state;
    }

    static /* synthetic */ VersionTagsTuple access$302(ParserImpl x0, VersionTagsTuple x1) {
        x0.directives = x1;
        return x0.directives;
    }

    static /* synthetic */ Map access$400() {
        return DEFAULT_TAGS;
    }

    static /* synthetic */ ArrayStack access$600(ParserImpl x0) {
        return x0.states;
    }

    static /* synthetic */ VersionTagsTuple access$900(ParserImpl x0) {
        return x0.processDirectives();
    }

    static /* synthetic */ ArrayStack access$1100(ParserImpl x0) {
        return x0.marks;
    }

    static /* synthetic */ Event access$1200(ParserImpl x0, Mark x1) {
        return x0.processEmptyScalar((Mark)x1);
    }

    static /* synthetic */ Event access$1300(ParserImpl x0, boolean x1, boolean x2) {
        return x0.parseNode((boolean)x1, (boolean)x2);
    }

    static /* synthetic */ Event access$2200(ParserImpl x0) {
        return x0.parseBlockNodeOrIndentlessSequence();
    }

    static /* synthetic */ Event access$2400(ParserImpl x0) {
        return x0.parseFlowNode();
    }

    static {
        DEFAULT_TAGS.put((String)"!", (String)"!");
        DEFAULT_TAGS.put((String)"!!", (String)"tag:yaml.org,2002:");
    }
}

