/*
 * Decompiled with CFR <Could not determine version>.
 */
package net.md_5.bungee.log;

import com.google.common.base.Charsets;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingOutputStream
extends ByteArrayOutputStream {
    private static final String separator = System.getProperty((String)"line.separator");
    private final Logger logger;
    private final Level level;

    @Override
    public void flush() throws IOException {
        String contents = this.toString((String)Charsets.UTF_8.name());
        super.reset();
        if (contents.isEmpty()) return;
        if (contents.equals((Object)separator)) return;
        this.logger.logp((Level)this.level, (String)"", (String)"", (String)contents);
    }

    public LoggingOutputStream(Logger logger, Level level) {
        this.logger = logger;
        this.level = level;
    }
}

