/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.KeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PemReader {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PemReader.class);
    private static final Pattern CERT_PATTERN = Pattern.compile((String)"-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*CERTIFICATE[^-]*-+", (int)2);
    private static final Pattern KEY_PATTERN = Pattern.compile((String)"-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*PRIVATE\\s+KEY[^-]*-+", (int)2);

    /*
     * Exception decompiling
     */
    static ByteBuf[] readCertificates(File file) throws CertificateException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 3[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    static ByteBuf[] readCertificates(InputStream in) throws CertificateException {
        String content;
        try {
            content = PemReader.readContent((InputStream)in);
        }
        catch (IOException e) {
            throw new CertificateException((String)"failed to read certificate input stream", (Throwable)e);
        }
        ArrayList<ByteBuf> certs = new ArrayList<ByteBuf>();
        Matcher m = CERT_PATTERN.matcher((CharSequence)content);
        int start = 0;
        do {
            if (!m.find((int)start)) {
                if (!certs.isEmpty()) return certs.toArray(new ByteBuf[0]);
                throw new CertificateException((String)"found no certificates in input stream");
            }
            ByteBuf base64 = Unpooled.copiedBuffer((CharSequence)m.group((int)1), (Charset)CharsetUtil.US_ASCII);
            ByteBuf der = Base64.decode((ByteBuf)base64);
            base64.release();
            certs.add(der);
            start = m.end();
        } while (true);
    }

    /*
     * Exception decompiling
     */
    static ByteBuf readPrivateKey(File file) throws KeyException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 3[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    static ByteBuf readPrivateKey(InputStream in) throws KeyException {
        String content;
        try {
            content = PemReader.readContent((InputStream)in);
        }
        catch (IOException e) {
            throw new KeyException((String)"failed to read key input stream", (Throwable)e);
        }
        Matcher m = KEY_PATTERN.matcher((CharSequence)content);
        if (!m.find()) {
            throw new KeyException((String)"could not find a PKCS #8 private key in input stream (see https://netty.io/wiki/sslcontextbuilder-and-private-key.html for more information)");
        }
        ByteBuf base64 = Unpooled.copiedBuffer((CharSequence)m.group((int)1), (Charset)CharsetUtil.US_ASCII);
        ByteBuf der = Base64.decode((ByteBuf)base64);
        base64.release();
        return der;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String readContent(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[8192];
            do {
                int ret2;
                if ((ret2 = in.read((byte[])buf)) < 0) {
                    String ret2 = out.toString((String)CharsetUtil.US_ASCII.name());
                    return ret2;
                }
                out.write((byte[])buf, (int)0, (int)ret2);
            } while (true);
        }
        finally {
            PemReader.safeClose((OutputStream)out);
        }
    }

    private static void safeClose(InputStream in) {
        try {
            in.close();
            return;
        }
        catch (IOException e) {
            logger.warn((String)"Failed to close a stream.", (Throwable)e);
        }
    }

    private static void safeClose(OutputStream out) {
        try {
            out.close();
            return;
        }
        catch (IOException e) {
            logger.warn((String)"Failed to close a stream.", (Throwable)e);
        }
    }

    private PemReader() {
    }
}

