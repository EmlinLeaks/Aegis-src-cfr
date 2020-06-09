/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import joptsimple.internal.Messages;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class InetAddressConverter
implements ValueConverter<InetAddress> {
    @Override
    public InetAddress convert(String value) {
        try {
            return InetAddress.getByName((String)value);
        }
        catch (UnknownHostException e) {
            throw new ValueConversionException((String)this.message((String)value));
        }
    }

    @Override
    public Class<InetAddress> valueType() {
        return InetAddress.class;
    }

    @Override
    public String valuePattern() {
        return null;
    }

    private String message(String value) {
        return Messages.message((Locale)Locale.getDefault(), (String)"joptsimple.ExceptionMessages", InetAddressConverter.class, (String)"message", (Object[])new Object[]{value});
    }
}

