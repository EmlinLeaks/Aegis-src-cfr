/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import joptsimple.AbstractOptionSpec;
import joptsimple.AlternativeLongOptionSpec;
import joptsimple.ArgumentList;
import joptsimple.BuiltinHelpFormatter;
import joptsimple.HelpFormatter;
import joptsimple.MissingRequiredOptionsException;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionDeclarer;
import joptsimple.OptionDescriptor;
import joptsimple.OptionException;
import joptsimple.OptionParserState;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import joptsimple.OptionSpecTokenizer;
import joptsimple.ParserRules;
import joptsimple.UnconfiguredOptionException;
import joptsimple.internal.AbbreviationMap;
import joptsimple.util.KeyValuePair;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OptionParser
implements OptionDeclarer {
    private final AbbreviationMap<AbstractOptionSpec<?>> recognizedOptions = new AbbreviationMap<V>();
    private final ArrayList<AbstractOptionSpec<?>> trainingOrder = new ArrayList<E>();
    private final Map<List<String>, Set<OptionSpec<?>>> requiredIf = new HashMap<List<String>, Set<OptionSpec<?>>>();
    private final Map<List<String>, Set<OptionSpec<?>>> requiredUnless = new HashMap<List<String>, Set<OptionSpec<?>>>();
    private OptionParserState state = OptionParserState.moreOptions((boolean)false);
    private boolean posixlyCorrect;
    private boolean allowsUnrecognizedOptions;
    private HelpFormatter helpFormatter = new BuiltinHelpFormatter();

    public OptionParser() {
        this.recognize(new NonOptionArgumentSpec<V>());
    }

    public OptionParser(String optionSpecification) {
        this();
        new OptionSpecTokenizer((String)optionSpecification).configure((OptionParser)this);
    }

    @Override
    public OptionSpecBuilder accepts(String option) {
        return this.acceptsAll(Collections.singletonList(option));
    }

    @Override
    public OptionSpecBuilder accepts(String option, String description) {
        return this.acceptsAll(Collections.singletonList(option), (String)description);
    }

    @Override
    public OptionSpecBuilder acceptsAll(List<String> options) {
        return this.acceptsAll(options, (String)"");
    }

    @Override
    public OptionSpecBuilder acceptsAll(List<String> options, String description) {
        if (options.isEmpty()) {
            throw new IllegalArgumentException((String)"need at least one option");
        }
        ParserRules.ensureLegalOptions(options);
        return new OptionSpecBuilder((OptionParser)this, options, (String)description);
    }

    @Override
    public NonOptionArgumentSpec<String> nonOptions() {
        NonOptionArgumentSpec<String> spec = new NonOptionArgumentSpec<String>();
        this.recognize(spec);
        return spec;
    }

    @Override
    public NonOptionArgumentSpec<String> nonOptions(String description) {
        NonOptionArgumentSpec<String> spec = new NonOptionArgumentSpec<String>((String)description);
        this.recognize(spec);
        return spec;
    }

    @Override
    public void posixlyCorrect(boolean setting) {
        this.posixlyCorrect = setting;
        this.state = OptionParserState.moreOptions((boolean)setting);
    }

    boolean posixlyCorrect() {
        return this.posixlyCorrect;
    }

    @Override
    public void allowsUnrecognizedOptions() {
        this.allowsUnrecognizedOptions = true;
    }

    boolean doesAllowsUnrecognizedOptions() {
        return this.allowsUnrecognizedOptions;
    }

    @Override
    public void recognizeAlternativeLongOptions(boolean recognize) {
        if (recognize) {
            this.recognize(new AlternativeLongOptionSpec());
            return;
        }
        this.recognizedOptions.remove((String)String.valueOf((Object)"W"));
    }

    void recognize(AbstractOptionSpec<?> spec) {
        this.recognizedOptions.putAll(spec.options(), spec);
        this.trainingOrder.add(spec);
    }

    public void printHelpOn(OutputStream sink) throws IOException {
        this.printHelpOn((Writer)new OutputStreamWriter((OutputStream)sink));
    }

    public void printHelpOn(Writer sink) throws IOException {
        sink.write((String)this.helpFormatter.format(this._recognizedOptions()));
        sink.flush();
    }

    public void formatHelpWith(HelpFormatter formatter) {
        if (formatter == null) {
            throw new NullPointerException();
        }
        this.helpFormatter = formatter;
    }

    public Map<String, OptionSpec<?>> recognizedOptions() {
        return new LinkedHashMap<String, OptionSpec<?>>(this._recognizedOptions());
    }

    private Map<String, AbstractOptionSpec<?>> _recognizedOptions() {
        LinkedHashMap<String, AbstractOptionSpec<?>> options = new LinkedHashMap<String, AbstractOptionSpec<?>>();
        Iterator<AbstractOptionSpec<?>> i$ = this.trainingOrder.iterator();
        block0 : while (i$.hasNext()) {
            AbstractOptionSpec<?> spec = i$.next();
            Iterator<String> i$2 = spec.options().iterator();
            do {
                if (!i$2.hasNext()) continue block0;
                String option = i$2.next();
                options.put((String)option, spec);
            } while (true);
            break;
        }
        return options;
    }

    public OptionSet parse(String ... arguments) {
        ArgumentList argumentList = new ArgumentList((String[])arguments);
        OptionSet detected = new OptionSet(this.recognizedOptions.toJavaUtilMap());
        detected.add(this.recognizedOptions.get((String)"[arguments]"));
        do {
            if (!argumentList.hasMore()) {
                this.reset();
                this.ensureRequiredOptions((OptionSet)detected);
                return detected;
            }
            this.state.handleArgument((OptionParser)this, (ArgumentList)argumentList, (OptionSet)detected);
        } while (true);
    }

    private void ensureRequiredOptions(OptionSet options) {
        List<AbstractOptionSpec<?>> missingRequiredOptions = this.missingRequiredOptions((OptionSet)options);
        boolean helpOptionPresent = this.isHelpOptionPresent((OptionSet)options);
        if (missingRequiredOptions.isEmpty()) return;
        if (helpOptionPresent) return;
        throw new MissingRequiredOptionsException(missingRequiredOptions);
    }

    private List<AbstractOptionSpec<?>> missingRequiredOptions(OptionSet options) {
        AbstractOptionSpec<?> required;
        ArrayList<AbstractOptionSpec<?>> missingRequiredOptions = new ArrayList<AbstractOptionSpec<?>>();
        for (AbstractOptionSpec<?> each : this.recognizedOptions.toJavaUtilMap().values()) {
            if (!each.isRequired() || options.has(each)) continue;
            missingRequiredOptions.add(each);
        }
        for (Map.Entry eachEntry : this.requiredIf.entrySet()) {
            required = this.specFor((String)((String)((List)eachEntry.getKey()).iterator().next()));
            if (!this.optionsHasAnyOf((OptionSet)options, (Collection)eachEntry.getValue()) || options.has(required)) continue;
            missingRequiredOptions.add(required);
        }
        Iterator<Object> i$ = this.requiredUnless.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry eachEntry = (Map.Entry)i$.next();
            required = this.specFor((String)((String)((List)eachEntry.getKey()).iterator().next()));
            if (this.optionsHasAnyOf((OptionSet)options, (Collection)eachEntry.getValue()) || options.has(required)) continue;
            missingRequiredOptions.add(required);
        }
        return missingRequiredOptions;
    }

    private boolean optionsHasAnyOf(OptionSet options, Collection<OptionSpec<?>> specs) {
        OptionSpec<?> each;
        Iterator<OptionSpec<?>> i$ = specs.iterator();
        do {
            if (!i$.hasNext()) return false;
        } while (!options.has(each = i$.next()));
        return true;
    }

    private boolean isHelpOptionPresent(OptionSet options) {
        AbstractOptionSpec<?> each;
        boolean helpOptionPresent = false;
        Iterator<AbstractOptionSpec<?>> i$ = this.recognizedOptions.toJavaUtilMap().values().iterator();
        do {
            if (!i$.hasNext()) return helpOptionPresent;
        } while (!(each = i$.next()).isForHelp() || !options.has(each));
        return true;
    }

    void handleLongOptionToken(String candidate, ArgumentList arguments, OptionSet detected) {
        KeyValuePair optionAndArgument = OptionParser.parseLongOptionWithArgument((String)candidate);
        if (!this.isRecognized((String)optionAndArgument.key)) {
            throw OptionException.unrecognizedOption((String)optionAndArgument.key);
        }
        AbstractOptionSpec<?> optionSpec = this.specFor((String)optionAndArgument.key);
        optionSpec.handleOption((OptionParser)this, (ArgumentList)arguments, (OptionSet)detected, (String)optionAndArgument.value);
    }

    void handleShortOptionToken(String candidate, ArgumentList arguments, OptionSet detected) {
        KeyValuePair optionAndArgument = OptionParser.parseShortOptionWithArgument((String)candidate);
        if (this.isRecognized((String)optionAndArgument.key)) {
            this.specFor((String)optionAndArgument.key).handleOption((OptionParser)this, (ArgumentList)arguments, (OptionSet)detected, (String)optionAndArgument.value);
            return;
        }
        this.handleShortOptionCluster((String)candidate, (ArgumentList)arguments, (OptionSet)detected);
    }

    private void handleShortOptionCluster(String candidate, ArgumentList arguments, OptionSet detected) {
        char[] options = OptionParser.extractShortOptionsFrom((String)candidate);
        this.validateOptionCharacters((char[])options);
        int i = 0;
        while (i < options.length) {
            AbstractOptionSpec<?> optionSpec = this.specFor((char)options[i]);
            if (optionSpec.acceptsArguments() && options.length > i + 1) {
                String detectedArgument = String.valueOf((char[])options, (int)(i + 1), (int)(options.length - 1 - i));
                optionSpec.handleOption((OptionParser)this, (ArgumentList)arguments, (OptionSet)detected, (String)detectedArgument);
                return;
            }
            optionSpec.handleOption((OptionParser)this, (ArgumentList)arguments, (OptionSet)detected, null);
            ++i;
        }
    }

    void handleNonOptionArgument(String candidate, ArgumentList arguments, OptionSet detectedOptions) {
        this.specFor((String)"[arguments]").handleOption((OptionParser)this, (ArgumentList)arguments, (OptionSet)detectedOptions, (String)candidate);
    }

    void noMoreOptions() {
        this.state = OptionParserState.noMoreOptions();
    }

    boolean looksLikeAnOption(String argument) {
        if (ParserRules.isShortOptionToken((String)argument)) return true;
        if (ParserRules.isLongOptionToken((String)argument)) return true;
        return false;
    }

    boolean isRecognized(String option) {
        return this.recognizedOptions.contains((String)option);
    }

    void requiredIf(List<String> precedentSynonyms, String required) {
        this.requiredIf(precedentSynonyms, this.specFor((String)required));
    }

    void requiredIf(List<String> precedentSynonyms, OptionSpec<?> required) {
        this.putRequiredOption(precedentSynonyms, required, this.requiredIf);
    }

    void requiredUnless(List<String> precedentSynonyms, String required) {
        this.requiredUnless(precedentSynonyms, this.specFor((String)required));
    }

    void requiredUnless(List<String> precedentSynonyms, OptionSpec<?> required) {
        this.putRequiredOption(precedentSynonyms, required, this.requiredUnless);
    }

    private void putRequiredOption(List<String> precedentSynonyms, OptionSpec<?> required, Map<List<String>, Set<OptionSpec<?>>> target) {
        for (String each : precedentSynonyms) {
            AbstractOptionSpec<?> spec = this.specFor((String)each);
            if (spec != null) continue;
            throw new UnconfiguredOptionException(precedentSynonyms);
        }
        Set<OptionSpec<?>> associated = target.get(precedentSynonyms);
        if (associated == null) {
            associated = new HashSet<OptionSpec<?>>();
            target.put(precedentSynonyms, associated);
        }
        associated.add(required);
    }

    private AbstractOptionSpec<?> specFor(char option) {
        return this.specFor((String)String.valueOf((char)option));
    }

    private AbstractOptionSpec<?> specFor(String option) {
        return this.recognizedOptions.get((String)option);
    }

    private void reset() {
        this.state = OptionParserState.moreOptions((boolean)this.posixlyCorrect);
    }

    private static char[] extractShortOptionsFrom(String argument) {
        char[] options = new char[argument.length() - 1];
        argument.getChars((int)1, (int)argument.length(), (char[])options, (int)0);
        return options;
    }

    private void validateOptionCharacters(char[] options) {
        char[] arr$ = options;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            char each = arr$[i$];
            String option = String.valueOf((char)each);
            if (!this.isRecognized((String)option)) {
                throw OptionException.unrecognizedOption((String)option);
            }
            if (this.specFor((String)option).acceptsArguments()) {
                return;
            }
            ++i$;
        }
    }

    private static KeyValuePair parseLongOptionWithArgument(String argument) {
        return KeyValuePair.valueOf((String)argument.substring((int)2));
    }

    private static KeyValuePair parseShortOptionWithArgument(String argument) {
        return KeyValuePair.valueOf((String)argument.substring((int)1));
    }
}

