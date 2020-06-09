/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import joptsimple.BuiltinHelpFormatter;
import joptsimple.HelpFormatter;
import joptsimple.OptionDescriptor;
import joptsimple.ParserRules;
import joptsimple.internal.Classes;
import joptsimple.internal.Messages;
import joptsimple.internal.Rows;
import joptsimple.internal.Strings;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BuiltinHelpFormatter
implements HelpFormatter {
    private final Rows nonOptionRows;
    private final Rows optionRows;

    BuiltinHelpFormatter() {
        this((int)80, (int)2);
    }

    public BuiltinHelpFormatter(int desiredOverallWidth, int desiredColumnSeparatorWidth) {
        this.nonOptionRows = new Rows((int)(desiredOverallWidth * 2), (int)0);
        this.optionRows = new Rows((int)desiredOverallWidth, (int)desiredColumnSeparatorWidth);
    }

    @Override
    public String format(Map<String, ? extends OptionDescriptor> options) {
        Comparator<OptionDescriptor> comparator = new Comparator<OptionDescriptor>((BuiltinHelpFormatter)this){
            final /* synthetic */ BuiltinHelpFormatter this$0;
            {
                this.this$0 = builtinHelpFormatter;
            }

            public int compare(OptionDescriptor first, OptionDescriptor second) {
                return first.options().iterator().next().compareTo((String)second.options().iterator().next());
            }
        };
        TreeSet<OptionDescriptor> sorted = new TreeSet<OptionDescriptor>(comparator);
        sorted.addAll(options.values());
        this.addRows(sorted);
        return this.formattedHelpOutput();
    }

    protected void addOptionRow(String single) {
        this.addOptionRow((String)single, (String)"");
    }

    protected void addOptionRow(String left, String right) {
        this.optionRows.add((String)left, (String)right);
    }

    protected void addNonOptionRow(String single) {
        this.nonOptionRows.add((String)single, (String)"");
    }

    protected void fitRowsToWidth() {
        this.nonOptionRows.fitToWidth();
        this.optionRows.fitToWidth();
    }

    protected String nonOptionOutput() {
        return this.nonOptionRows.render();
    }

    protected String optionOutput() {
        return this.optionRows.render();
    }

    protected String formattedHelpOutput() {
        StringBuilder formatted = new StringBuilder();
        String nonOptionDisplay = this.nonOptionOutput();
        if (!Strings.isNullOrEmpty((String)nonOptionDisplay)) {
            formatted.append((String)nonOptionDisplay).append((String)Strings.LINE_SEPARATOR);
        }
        formatted.append((String)this.optionOutput());
        return formatted.toString();
    }

    protected void addRows(Collection<? extends OptionDescriptor> options) {
        this.addNonOptionsDescription(options);
        if (options.isEmpty()) {
            this.addOptionRow((String)this.message((String)"no.options.specified", (Object[])new Object[0]));
        } else {
            this.addHeaders(options);
            this.addOptions(options);
        }
        this.fitRowsToWidth();
    }

    protected void addNonOptionsDescription(Collection<? extends OptionDescriptor> options) {
        OptionDescriptor nonOptions = this.findAndRemoveNonOptionsSpec(options);
        if (!this.shouldShowNonOptionArgumentDisplay((OptionDescriptor)nonOptions)) return;
        this.addNonOptionRow((String)this.message((String)"non.option.arguments.header", (Object[])new Object[0]));
        this.addNonOptionRow((String)this.createNonOptionArgumentsDisplay((OptionDescriptor)nonOptions));
    }

    protected boolean shouldShowNonOptionArgumentDisplay(OptionDescriptor nonOptionDescriptor) {
        if (!Strings.isNullOrEmpty((String)nonOptionDescriptor.description())) return true;
        if (!Strings.isNullOrEmpty((String)nonOptionDescriptor.argumentTypeIndicator())) return true;
        if (!Strings.isNullOrEmpty((String)nonOptionDescriptor.argumentDescription())) return true;
        return false;
    }

    protected String createNonOptionArgumentsDisplay(OptionDescriptor nonOptionDescriptor) {
        StringBuilder buffer = new StringBuilder();
        this.maybeAppendOptionInfo((StringBuilder)buffer, (OptionDescriptor)nonOptionDescriptor);
        this.maybeAppendNonOptionsDescription((StringBuilder)buffer, (OptionDescriptor)nonOptionDescriptor);
        return buffer.toString();
    }

    protected void maybeAppendNonOptionsDescription(StringBuilder buffer, OptionDescriptor nonOptions) {
        buffer.append((String)(buffer.length() > 0 && !Strings.isNullOrEmpty((String)nonOptions.description()) ? " -- " : "")).append((String)nonOptions.description());
    }

    protected OptionDescriptor findAndRemoveNonOptionsSpec(Collection<? extends OptionDescriptor> options) {
        OptionDescriptor next;
        Iterator<? extends OptionDescriptor> it = options.iterator();
        do {
            if (!it.hasNext()) throw new AssertionError((Object)"no non-options argument spec");
        } while (!(next = it.next()).representsNonOptions());
        it.remove();
        return next;
    }

    protected void addHeaders(Collection<? extends OptionDescriptor> options) {
        if (this.hasRequiredOption(options)) {
            this.addOptionRow((String)this.message((String)"option.header.with.required.indicator", (Object[])new Object[0]), (String)this.message((String)"description.header", (Object[])new Object[0]));
            this.addOptionRow((String)this.message((String)"option.divider.with.required.indicator", (Object[])new Object[0]), (String)this.message((String)"description.divider", (Object[])new Object[0]));
            return;
        }
        this.addOptionRow((String)this.message((String)"option.header", (Object[])new Object[0]), (String)this.message((String)"description.header", (Object[])new Object[0]));
        this.addOptionRow((String)this.message((String)"option.divider", (Object[])new Object[0]), (String)this.message((String)"description.divider", (Object[])new Object[0]));
    }

    protected final boolean hasRequiredOption(Collection<? extends OptionDescriptor> options) {
        OptionDescriptor each;
        Iterator<? extends OptionDescriptor> i$ = options.iterator();
        do {
            if (!i$.hasNext()) return false;
        } while (!(each = i$.next()).isRequired());
        return true;
    }

    protected void addOptions(Collection<? extends OptionDescriptor> options) {
        Iterator<? extends OptionDescriptor> i$ = options.iterator();
        while (i$.hasNext()) {
            OptionDescriptor each = i$.next();
            if (each.representsNonOptions()) continue;
            this.addOptionRow((String)this.createOptionDisplay((OptionDescriptor)each), (String)this.createDescriptionDisplay((OptionDescriptor)each));
        }
    }

    protected String createOptionDisplay(OptionDescriptor descriptor) {
        StringBuilder buffer = new StringBuilder((String)(descriptor.isRequired() ? "* " : ""));
        Iterator<String> i = descriptor.options().iterator();
        do {
            if (!i.hasNext()) {
                this.maybeAppendOptionInfo((StringBuilder)buffer, (OptionDescriptor)descriptor);
                return buffer.toString();
            }
            String option = i.next();
            buffer.append((String)this.optionLeader((String)option));
            buffer.append((String)option);
            if (!i.hasNext()) continue;
            buffer.append((String)", ");
        } while (true);
    }

    protected String optionLeader(String option) {
        if (option.length() > 1) {
            return "--";
        }
        String string = ParserRules.HYPHEN;
        return string;
    }

    protected void maybeAppendOptionInfo(StringBuilder buffer, OptionDescriptor descriptor) {
        String indicator = this.extractTypeIndicator((OptionDescriptor)descriptor);
        String description = descriptor.argumentDescription();
        if (indicator == null) {
            if (Strings.isNullOrEmpty((String)description)) return;
        }
        this.appendOptionHelp((StringBuilder)buffer, (String)indicator, (String)description, (boolean)descriptor.requiresArgument());
    }

    protected String extractTypeIndicator(OptionDescriptor descriptor) {
        String indicator = descriptor.argumentTypeIndicator();
        if (Strings.isNullOrEmpty((String)indicator)) return null;
        if (String.class.getName().equals((Object)indicator)) return null;
        return Classes.shortNameOf((String)indicator);
    }

    protected void appendOptionHelp(StringBuilder buffer, String typeIndicator, String description, boolean required) {
        if (required) {
            this.appendTypeIndicator((StringBuilder)buffer, (String)typeIndicator, (String)description, (char)'<', (char)'>');
            return;
        }
        this.appendTypeIndicator((StringBuilder)buffer, (String)typeIndicator, (String)description, (char)'[', (char)']');
    }

    protected void appendTypeIndicator(StringBuilder buffer, String typeIndicator, String description, char start, char end) {
        buffer.append((char)' ').append((char)start);
        if (typeIndicator != null) {
            buffer.append((String)typeIndicator);
        }
        if (!Strings.isNullOrEmpty((String)description)) {
            if (typeIndicator != null) {
                buffer.append((String)": ");
            }
            buffer.append((String)description);
        }
        buffer.append((char)end);
    }

    protected String createDescriptionDisplay(OptionDescriptor descriptor) {
        List<?> defaultValues = descriptor.defaultValues();
        if (defaultValues.isEmpty()) {
            return descriptor.description();
        }
        String defaultValuesDisplay = this.createDefaultValuesDisplay(defaultValues);
        return (descriptor.description() + ' ' + Strings.surround((String)(this.message((String)"default.value.header", (Object[])new Object[0]) + ' ' + defaultValuesDisplay), (char)'(', (char)')')).trim();
    }

    protected String createDefaultValuesDisplay(List<?> defaultValues) {
        String string;
        if (defaultValues.size() == 1) {
            string = defaultValues.get((int)0).toString();
            return string;
        }
        string = defaultValues.toString();
        return string;
    }

    protected String message(String keySuffix, Object ... args) {
        return Messages.message((Locale)Locale.getDefault(), (String)"joptsimple.HelpFormatterMessages", BuiltinHelpFormatter.class, (String)keySuffix, (Object[])args);
    }
}

