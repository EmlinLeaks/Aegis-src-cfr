/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentList;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.ValueConverter;
import joptsimple.internal.Objects;
import joptsimple.internal.Reflection;
import joptsimple.internal.Strings;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ArgumentAcceptingOptionSpec<V>
extends AbstractOptionSpec<V> {
    private static final char NIL_VALUE_SEPARATOR = '\u0000';
    private boolean optionRequired;
    private final boolean argumentRequired;
    private ValueConverter<V> converter;
    private String argumentDescription = "";
    private String valueSeparator = String.valueOf((char)'\u0000');
    private final List<V> defaultValues = new ArrayList<V>();

    ArgumentAcceptingOptionSpec(String option, boolean argumentRequired) {
        super((String)option);
        this.argumentRequired = argumentRequired;
    }

    ArgumentAcceptingOptionSpec(List<String> options, boolean argumentRequired, String description) {
        super(options, (String)description);
        this.argumentRequired = argumentRequired;
    }

    public final <T> ArgumentAcceptingOptionSpec<T> ofType(Class<T> argumentType) {
        return this.withValuesConvertedBy(Reflection.findConverter(argumentType));
    }

    public final <T> ArgumentAcceptingOptionSpec<T> withValuesConvertedBy(ValueConverter<T> aConverter) {
        if (aConverter == null) {
            throw new NullPointerException((String)"illegal null converter");
        }
        this.converter = aConverter;
        return this;
    }

    public final ArgumentAcceptingOptionSpec<V> describedAs(String description) {
        this.argumentDescription = description;
        return this;
    }

    public final ArgumentAcceptingOptionSpec<V> withValuesSeparatedBy(char separator) {
        if (separator == '\u0000') {
            throw new IllegalArgumentException((String)"cannot use U+0000 as separator");
        }
        this.valueSeparator = String.valueOf((char)separator);
        return this;
    }

    public final ArgumentAcceptingOptionSpec<V> withValuesSeparatedBy(String separator) {
        if (separator.indexOf((int)0) != -1) {
            throw new IllegalArgumentException((String)"cannot use U+0000 in separator");
        }
        this.valueSeparator = separator;
        return this;
    }

    public ArgumentAcceptingOptionSpec<V> defaultsTo(V value, V ... values) {
        this.addDefaultValue(value);
        this.defaultsTo(values);
        return this;
    }

    public ArgumentAcceptingOptionSpec<V> defaultsTo(V[] values) {
        V[] arr$ = values;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            V each = arr$[i$];
            this.addDefaultValue(each);
            ++i$;
        }
        return this;
    }

    public ArgumentAcceptingOptionSpec<V> required() {
        this.optionRequired = true;
        return this;
    }

    @Override
    public boolean isRequired() {
        return this.optionRequired;
    }

    private void addDefaultValue(V value) {
        Objects.ensureNotNull(value);
        this.defaultValues.add(value);
    }

    @Override
    final void handleOption(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions, String detectedArgument) {
        if (Strings.isNullOrEmpty((String)detectedArgument)) {
            this.detectOptionArgument((OptionParser)parser, (ArgumentList)arguments, (OptionSet)detectedOptions);
            return;
        }
        this.addArguments((OptionSet)detectedOptions, (String)detectedArgument);
    }

    protected void addArguments(OptionSet detectedOptions, String detectedArgument) {
        StringTokenizer lexer = new StringTokenizer((String)detectedArgument, (String)this.valueSeparator);
        if (!lexer.hasMoreTokens()) {
            detectedOptions.addWithArgument(this, (String)detectedArgument);
            return;
        }
        while (lexer.hasMoreTokens()) {
            detectedOptions.addWithArgument(this, (String)lexer.nextToken());
        }
    }

    protected abstract void detectOptionArgument(OptionParser var1, ArgumentList var2, OptionSet var3);

    @Override
    protected final V convert(String argument) {
        return (V)this.convertWith(this.converter, (String)argument);
    }

    protected boolean canConvertArgument(String argument) {
        StringTokenizer lexer = new StringTokenizer((String)argument, (String)this.valueSeparator);
        try {
            while (lexer.hasMoreTokens()) {
                this.convert((String)lexer.nextToken());
            }
            return true;
        }
        catch (OptionException ignored) {
            return false;
        }
    }

    protected boolean isArgumentOfNumberType() {
        if (this.converter == null) return false;
        if (!Number.class.isAssignableFrom(this.converter.valueType())) return false;
        return true;
    }

    @Override
    public boolean acceptsArguments() {
        return true;
    }

    @Override
    public boolean requiresArgument() {
        return this.argumentRequired;
    }

    @Override
    public String argumentDescription() {
        return this.argumentDescription;
    }

    @Override
    public String argumentTypeIndicator() {
        return this.argumentTypeIndicatorFrom(this.converter);
    }

    public List<V> defaultValues() {
        return Collections.unmodifiableList(this.defaultValues);
    }

    @Override
    public boolean equals(Object that) {
        if (!super.equals((Object)that)) {
            return false;
        }
        ArgumentAcceptingOptionSpec other = (ArgumentAcceptingOptionSpec)that;
        if (this.requiresArgument() != other.requiresArgument()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int n;
        if (this.argumentRequired) {
            n = 0;
            return super.hashCode() ^ n;
        }
        n = 1;
        return super.hashCode() ^ n;
    }
}

