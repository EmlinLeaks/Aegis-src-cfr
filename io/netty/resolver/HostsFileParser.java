/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.resolver;

import io.netty.resolver.HostsFileEntries;
import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public final class HostsFileParser {
    private static final String WINDOWS_DEFAULT_SYSTEM_ROOT = "C:\\Windows";
    private static final String WINDOWS_HOSTS_FILE_RELATIVE_PATH = "\\system32\\drivers\\etc\\hosts";
    private static final String X_PLATFORMS_HOSTS_FILE_PATH = "/etc/hosts";
    private static final Pattern WHITESPACES = Pattern.compile((String)"[ \t]+");
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(HostsFileParser.class);

    private static File locateHostsFile() {
        if (!PlatformDependent.isWindows()) return new File((String)X_PLATFORMS_HOSTS_FILE_PATH);
        File hostsFile = new File((String)(System.getenv((String)"SystemRoot") + WINDOWS_HOSTS_FILE_RELATIVE_PATH));
        if (hostsFile.exists()) return hostsFile;
        return new File((String)"C:\\Windows\\system32\\drivers\\etc\\hosts");
    }

    public static HostsFileEntries parseSilently() {
        return HostsFileParser.parseSilently((Charset[])new Charset[]{Charset.defaultCharset()});
    }

    public static HostsFileEntries parseSilently(Charset ... charsets) {
        File hostsFile = HostsFileParser.locateHostsFile();
        try {
            return HostsFileParser.parse((File)hostsFile, (Charset[])charsets);
        }
        catch (IOException e) {
            if (!logger.isWarnEnabled()) return HostsFileEntries.EMPTY;
            logger.warn((String)("Failed to load and parse hosts file at " + hostsFile.getPath()), (Throwable)e);
            return HostsFileEntries.EMPTY;
        }
    }

    public static HostsFileEntries parse() throws IOException {
        return HostsFileParser.parse((File)HostsFileParser.locateHostsFile());
    }

    public static HostsFileEntries parse(File file) throws IOException {
        return HostsFileParser.parse((File)file, (Charset[])new Charset[]{Charset.defaultCharset()});
    }

    public static HostsFileEntries parse(File file, Charset ... charsets) throws IOException {
        ObjectUtil.checkNotNull(file, (String)"file");
        ObjectUtil.checkNotNull(charsets, (String)"charsets");
        if (!file.exists()) return HostsFileEntries.EMPTY;
        if (!file.isFile()) return HostsFileEntries.EMPTY;
        Charset[] arrcharset = charsets;
        int n = arrcharset.length;
        int n2 = 0;
        while (n2 < n) {
            Charset charset = arrcharset[n2];
            HostsFileEntries entries = HostsFileParser.parse((Reader)new BufferedReader((Reader)new InputStreamReader((InputStream)new FileInputStream((File)file), (Charset)charset)));
            if (entries != HostsFileEntries.EMPTY) {
                return entries;
            }
            ++n2;
        }
        return HostsFileEntries.EMPTY;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    public static HostsFileEntries parse(Reader reader) throws IOException {
        block15 : {
            ObjectUtil.checkNotNull(reader, (String)"reader");
            buff = new BufferedReader((Reader)reader);
            ipv4Entries = new HashMap<String, Inet4Address>();
            ipv6Entries = new HashMap<String, Inet6Address>();
            do lbl-1000: // 3 sources:
            {
                block16 : {
                    if ((line = buff.readLine()) == null) break block16;
                    commentPosition = line.indexOf((int)35);
                    if (commentPosition != -1) {
                        line = line.substring((int)0, (int)commentPosition);
                    }
                    if ((line = line.trim()).isEmpty()) ** GOTO lbl-1000
                    lineParts = new ArrayList<String>();
                    for (String s : HostsFileParser.WHITESPACES.split((CharSequence)line)) {
                        if (s.isEmpty()) continue;
                        lineParts.add(s);
                    }
                    if (lineParts.size() < 2 || (ipBytes = NetUtil.createByteArrayFromIpAddressString((String)((String)lineParts.get((int)0)))) == null) ** GOTO lbl-1000
                    break block15;
                }
                commentPosition = ipv4Entries.isEmpty() != false && ipv6Entries.isEmpty() != false ? HostsFileEntries.EMPTY : new HostsFileEntries(ipv4Entries, ipv6Entries);
                return commentPosition;
                break;
            } while (true);
            finally {
                try {
                    buff.close();
                }
                catch (IOException e) {
                    HostsFileParser.logger.warn((String)"Failed to close a reader", (Throwable)e);
                }
            }
        }
        i = 1;
        do {
            if (i >= lineParts.size()) ** continue;
            hostname = (String)lineParts.get((int)i);
            hostnameLower = hostname.toLowerCase((Locale)Locale.ENGLISH);
            address = InetAddress.getByAddress((String)hostname, (byte[])ipBytes);
            if (address instanceof Inet4Address) {
                previous = ipv4Entries.put((String)hostnameLower, (Inet4Address)((Inet4Address)address));
                if (previous != null) {
                    ipv4Entries.put((String)hostnameLower, (Inet4Address)previous);
                }
            } else {
                previous = ipv6Entries.put((String)hostnameLower, (Inet6Address)((Inet6Address)address));
                if (previous != null) {
                    ipv6Entries.put((String)hostnameLower, (Inet6Address)previous);
                }
            }
            ++i;
        } while (true);
    }

    private HostsFileParser() {
    }
}

