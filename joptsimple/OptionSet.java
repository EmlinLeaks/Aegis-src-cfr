/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import joptsimple.AbstractOptionSpec;
import joptsimple.MultipleArgumentsForOptionException;
import joptsimple.OptionSpec;
import joptsimple.internal.Objects;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OptionSet {
    private final List<OptionSpec<?>> detectedSpecs = new ArrayList<OptionSpec<?>>();
    private final Map<String, AbstractOptionSpec<?>> detectedOptions = new HashMap<String, AbstractOptionSpec<?>>();
    private final Map<AbstractOptionSpec<?>, List<String>> optionsToArguments = new IdentityHashMap<AbstractOptionSpec<?>, List<String>>();
    private final Map<String, AbstractOptionSpec<?>> recognizedSpecs;
    private final Map<String, List<?>> defaultValues;

    OptionSet(Map<String, AbstractOptionSpec<?>> recognizedSpecs) {
        this.defaultValues = OptionSet.defaultValues(recognizedSpecs);
        this.recognizedSpecs = recognizedSpecs;
    }

    public boolean hasOptions() {
        if (this.detectedOptions.size() != 1) return true;
        if (!this.detectedOptions.values().iterator().next().representsNonOptions()) return true;
        return false;
    }

    public boolean has(String option) {
        return this.detectedOptions.containsKey((Object)option);
    }

    public boolean has(OptionSpec<?> option) {
        return this.optionsToArguments.containsKey(option);
    }

    public boolean hasArgument(String option) {
        AbstractOptionSpec<?> spec = this.detectedOptions.get((Object)option);
        if (spec == null) return false;
        if (!this.hasArgument(spec)) return false;
        return true;
    }

    public boolean hasArgument(OptionSpec<?> option) {
        Objects.ensureNotNull(option);
        List<String> values = this.optionsToArguments.get(option);
        if (values == null) return false;
        if (values.isEmpty()) return false;
        return true;
    }

    public Object valueOf(String option) {
        Objects.ensureNotNull((Object)option);
        AbstractOptionSpec<?> spec = this.detectedOptions.get((Object)option);
        if (spec != null) return this.valueOf(spec);
        List<V> defaults = this.defaultValuesFor((String)option);
        if (defaults.isEmpty()) {
            return null;
        }
        Object v0 = defaults.get((int)0);
        return v0;
    }

    public <V> V valueOf(OptionSpec<V> option) {
        Objects.ensureNotNull(option);
        List<V> values = this.valuesOf(option);
        switch (values.size()) {
            case 0: {
                return (V)null;
            }
            case 1: {
                return (V)values.get((int)0);
            }
        }
        throw new MultipleArgumentsForOptionException(option);
    }

    public List<?> valuesOf(String option) {
        List<Object> list;
        Objects.ensureNotNull((Object)option);
        AbstractOptionSpec<?> spec = this.detectedOptions.get((Object)option);
        if (spec == null) {
            list = this.defaultValuesFor((String)option);
            return list;
        }
        list = this.valuesOf(spec);
        return list;
    }

    public <V> List<V> valuesOf(OptionSpec<V> option) {
        Objects.ensureNotNull(option);
        List<String> values = this.optionsToArguments.get(option);
        if (values == null) return this.defaultValueFor(option);
        if (values.isEmpty()) {
            return this.defaultValueFor(option);
        }
        AbstractOptionSpec spec = (AbstractOptionSpec)option;
        ArrayList<V> convertedValues = new ArrayList<V>();
        Iterator<String> i$ = values.iterator();
        while (i$.hasNext()) {
            String each = i$.next();
            convertedValues.add(spec.convert((String)each));
        }
        return Collections.unmodifiableList(convertedValues);
    }

    public List<OptionSpec<?>> specs() {
        List<OptionSpec<AbstractOptionSpec<?>>> specs = this.detectedSpecs;
        specs.removeAll(Collections.singletonList(this.detectedOptions.get((Object)"[arguments]")));
        return Collections.unmodifiableList(specs);
    }

    public Map<OptionSpec<?>, List<?>> asMap() {
        HashMap<AbstractOptionSpec<?>, List<?>> map = new HashMap<AbstractOptionSpec<?>, List<?>>();
        Iterator<AbstractOptionSpec<?>> i$ = this.recognizedSpecs.values().iterator();
        while (i$.hasNext()) {
            AbstractOptionSpec<?> spec = i$.next();
            if (spec.representsNonOptions()) continue;
            map.put(spec, this.valuesOf(spec));
        }
        return Collections.unmodifiableMap(map);
    }

    public List<?> nonOptionArguments() {
        return Collections.unmodifiableList(this.valuesOf((OptionSpec)this.detectedOptions.get((Object)"[arguments]")));
    }

    void add(AbstractOptionSpec<?> spec) {
        this.addWithArgument(spec, null);
    }

    void addWithArgument(AbstractOptionSpec<?> spec, String argument) {
        this.detectedSpecs.add(spec);
        for (String each : spec.options()) {
            this.detectedOptions.put((String)each, spec);
        }
        List<String> optionArguments = this.optionsToArguments.get(spec);
        if (optionArguments == null) {
            optionArguments = new ArrayList<String>();
            this.optionsToArguments.put(spec, optionArguments);
        }
        if (argument == null) return;
        optionArguments.add((String)argument);
    }

    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) return false;
        if (!this.getClass().equals(that.getClass())) {
            return false;
        }
        OptionSet other = (OptionSet)that;
        HashMap<AbstractOptionSpec<?>, List<String>> thisOptionsToArguments = new HashMap<AbstractOptionSpec<?>, List<String>>(this.optionsToArguments);
        HashMap<AbstractOptionSpec<?>, List<String>> otherOptionsToArguments = new HashMap<AbstractOptionSpec<?>, List<String>>(other.optionsToArguments);
        if (!this.detectedOptions.equals(other.detectedOptions)) return false;
        if (!thisOptionsToArguments.equals(otherOptionsToArguments)) return false;
        return true;
    }

    public int hashCode() {
        HashMap<AbstractOptionSpec<?>, List<String>> thisOptionsToArguments = new HashMap<AbstractOptionSpec<?>, List<String>>(this.optionsToArguments);
        return this.detectedOptions.hashCode() ^ thisOptionsToArguments.hashCode();
    }

    private <V> List<V> defaultValuesFor(String option) {
        if (!this.defaultValues.containsKey((Object)option)) return Collections.emptyList();
        return this.defaultValues.get((Object)option);
    }

    private <V> List<V> defaultValueFor(OptionSpec<V> option) {
        return this.defaultValuesFor((String)option.options().iterator().next());
    }

    private static Map<String, List<?>> defaultValues(Map<String, AbstractOptionSpec<?>> recognizedSpecs) {
        HashMap<String, List<?>> defaults = new HashMap<String, List<?>>();
        Iterator<Map.Entry<String, AbstractOptionSpec<?>>> i$ = recognizedSpecs.entrySet().iterator();
        while (i$.hasNext()) {
            Map.Entry<String, AbstractOptionSpec<?>> each = i$.next();
            defaults.put((String)each.getKey(), each.getValue().defaultValues());
        }
        return defaults;
    }
}

