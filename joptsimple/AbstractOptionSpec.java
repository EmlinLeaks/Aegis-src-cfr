/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import joptsimple.ArgumentList;
import joptsimple.OptionArgumentConversionException;
import joptsimple.OptionDescriptor;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import joptsimple.internal.Reflection;
import joptsimple.internal.ReflectionException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class AbstractOptionSpec<V>
implements OptionSpec<V>,
OptionDescriptor {
    private final List<String> options = new ArrayList<String>();
    private final String description;
    private boolean forHelp;

    protected AbstractOptionSpec(String option) {
        this(Collections.singletonList(option), (String)"");
    }

    protected AbstractOptionSpec(List<String> options, String description) {
        this.arrangeOptions(options);
        this.description = description;
    }

    @Override
    public final List<String> options() {
        return Collections.unmodifiableList(this.options);
    }

    @Override
    public final List<V> values(OptionSet detectedOptions) {
        return detectedOptions.valuesOf(this);
    }

    @Override
    public final V value(OptionSet detectedOptions) {
        return (V)detectedOptions.valueOf(this);
    }

    @Override
    public String description() {
        return this.description;
    }

    public final AbstractOptionSpec<V> forHelp() {
        this.forHelp = true;
        return this;
    }

    @Override
    public final boolean isForHelp() {
        return this.forHelp;
    }

    @Override
    public boolean representsNonOptions() {
        return false;
    }

    protected abstract V convert(String var1);

    protected V convertWith(ValueConverter<V> converter, String argument) {
        try {
            return (V)Reflection.convertWith(converter, (String)argument);
        }
        catch (ReflectionException ex) {
            throw new OptionArgumentConversionException(this, (String)argument, (Throwable)ex);
        }
        catch (ValueConversionException ex) {
            throw new OptionArgumentConversionException(this, (String)argument, (Throwable)ex);
        }
    }

    protected String argumentTypeIndicatorFrom(ValueConverter<V> converter) {
        String string;
        if (converter == null) {
            return null;
        }
        String pattern = converter.valuePattern();
        if (pattern == null) {
            string = converter.valueType().getName();
            return string;
        }
        string = pattern;
        return string;
    }

    abstract void handleOption(OptionParser var1, ArgumentList var2, OptionSet var3, String var4);

    private void arrangeOptions(List<String> unarranged) {
        if (unarranged.size() == 1) {
            this.options.addAll(unarranged);
            return;
        }
        ArrayList<String> shortOptions = new ArrayList<String>();
        ArrayList<String> longOptions = new ArrayList<String>();
        Iterator<String> i$ = unarranged.iterator();
        do {
            if (!i$.hasNext()) {
                Collections.sort(shortOptions);
                Collections.sort(longOptions);
                this.options.addAll(shortOptions);
                this.options.addAll(longOptions);
                return;
            }
            String each = i$.next();
            if (each.length() == 1) {
                shortOptions.add(each);
                continue;
            }
            longOptions.add(each);
        } while (true);
    }

    public boolean equals(Object that) {
        if (!(that instanceof AbstractOptionSpec)) {
            return false;
        }
        AbstractOptionSpec other = (AbstractOptionSpec)that;
        return this.options.equals(other.options);
    }

    public int hashCode() {
        return this.options.hashCode();
    }

    public String toString() {
        return this.options.toString();
    }
}

