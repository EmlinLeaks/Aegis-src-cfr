/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;

public class HttpResponseStatus
implements Comparable<HttpResponseStatus> {
    public static final HttpResponseStatus CONTINUE = HttpResponseStatus.newStatus((int)100, (String)"Continue");
    public static final HttpResponseStatus SWITCHING_PROTOCOLS = HttpResponseStatus.newStatus((int)101, (String)"Switching Protocols");
    public static final HttpResponseStatus PROCESSING = HttpResponseStatus.newStatus((int)102, (String)"Processing");
    public static final HttpResponseStatus OK = HttpResponseStatus.newStatus((int)200, (String)"OK");
    public static final HttpResponseStatus CREATED = HttpResponseStatus.newStatus((int)201, (String)"Created");
    public static final HttpResponseStatus ACCEPTED = HttpResponseStatus.newStatus((int)202, (String)"Accepted");
    public static final HttpResponseStatus NON_AUTHORITATIVE_INFORMATION = HttpResponseStatus.newStatus((int)203, (String)"Non-Authoritative Information");
    public static final HttpResponseStatus NO_CONTENT = HttpResponseStatus.newStatus((int)204, (String)"No Content");
    public static final HttpResponseStatus RESET_CONTENT = HttpResponseStatus.newStatus((int)205, (String)"Reset Content");
    public static final HttpResponseStatus PARTIAL_CONTENT = HttpResponseStatus.newStatus((int)206, (String)"Partial Content");
    public static final HttpResponseStatus MULTI_STATUS = HttpResponseStatus.newStatus((int)207, (String)"Multi-Status");
    public static final HttpResponseStatus MULTIPLE_CHOICES = HttpResponseStatus.newStatus((int)300, (String)"Multiple Choices");
    public static final HttpResponseStatus MOVED_PERMANENTLY = HttpResponseStatus.newStatus((int)301, (String)"Moved Permanently");
    public static final HttpResponseStatus FOUND = HttpResponseStatus.newStatus((int)302, (String)"Found");
    public static final HttpResponseStatus SEE_OTHER = HttpResponseStatus.newStatus((int)303, (String)"See Other");
    public static final HttpResponseStatus NOT_MODIFIED = HttpResponseStatus.newStatus((int)304, (String)"Not Modified");
    public static final HttpResponseStatus USE_PROXY = HttpResponseStatus.newStatus((int)305, (String)"Use Proxy");
    public static final HttpResponseStatus TEMPORARY_REDIRECT = HttpResponseStatus.newStatus((int)307, (String)"Temporary Redirect");
    public static final HttpResponseStatus PERMANENT_REDIRECT = HttpResponseStatus.newStatus((int)308, (String)"Permanent Redirect");
    public static final HttpResponseStatus BAD_REQUEST = HttpResponseStatus.newStatus((int)400, (String)"Bad Request");
    public static final HttpResponseStatus UNAUTHORIZED = HttpResponseStatus.newStatus((int)401, (String)"Unauthorized");
    public static final HttpResponseStatus PAYMENT_REQUIRED = HttpResponseStatus.newStatus((int)402, (String)"Payment Required");
    public static final HttpResponseStatus FORBIDDEN = HttpResponseStatus.newStatus((int)403, (String)"Forbidden");
    public static final HttpResponseStatus NOT_FOUND = HttpResponseStatus.newStatus((int)404, (String)"Not Found");
    public static final HttpResponseStatus METHOD_NOT_ALLOWED = HttpResponseStatus.newStatus((int)405, (String)"Method Not Allowed");
    public static final HttpResponseStatus NOT_ACCEPTABLE = HttpResponseStatus.newStatus((int)406, (String)"Not Acceptable");
    public static final HttpResponseStatus PROXY_AUTHENTICATION_REQUIRED = HttpResponseStatus.newStatus((int)407, (String)"Proxy Authentication Required");
    public static final HttpResponseStatus REQUEST_TIMEOUT = HttpResponseStatus.newStatus((int)408, (String)"Request Timeout");
    public static final HttpResponseStatus CONFLICT = HttpResponseStatus.newStatus((int)409, (String)"Conflict");
    public static final HttpResponseStatus GONE = HttpResponseStatus.newStatus((int)410, (String)"Gone");
    public static final HttpResponseStatus LENGTH_REQUIRED = HttpResponseStatus.newStatus((int)411, (String)"Length Required");
    public static final HttpResponseStatus PRECONDITION_FAILED = HttpResponseStatus.newStatus((int)412, (String)"Precondition Failed");
    public static final HttpResponseStatus REQUEST_ENTITY_TOO_LARGE = HttpResponseStatus.newStatus((int)413, (String)"Request Entity Too Large");
    public static final HttpResponseStatus REQUEST_URI_TOO_LONG = HttpResponseStatus.newStatus((int)414, (String)"Request-URI Too Long");
    public static final HttpResponseStatus UNSUPPORTED_MEDIA_TYPE = HttpResponseStatus.newStatus((int)415, (String)"Unsupported Media Type");
    public static final HttpResponseStatus REQUESTED_RANGE_NOT_SATISFIABLE = HttpResponseStatus.newStatus((int)416, (String)"Requested Range Not Satisfiable");
    public static final HttpResponseStatus EXPECTATION_FAILED = HttpResponseStatus.newStatus((int)417, (String)"Expectation Failed");
    public static final HttpResponseStatus MISDIRECTED_REQUEST = HttpResponseStatus.newStatus((int)421, (String)"Misdirected Request");
    public static final HttpResponseStatus UNPROCESSABLE_ENTITY = HttpResponseStatus.newStatus((int)422, (String)"Unprocessable Entity");
    public static final HttpResponseStatus LOCKED = HttpResponseStatus.newStatus((int)423, (String)"Locked");
    public static final HttpResponseStatus FAILED_DEPENDENCY = HttpResponseStatus.newStatus((int)424, (String)"Failed Dependency");
    public static final HttpResponseStatus UNORDERED_COLLECTION = HttpResponseStatus.newStatus((int)425, (String)"Unordered Collection");
    public static final HttpResponseStatus UPGRADE_REQUIRED = HttpResponseStatus.newStatus((int)426, (String)"Upgrade Required");
    public static final HttpResponseStatus PRECONDITION_REQUIRED = HttpResponseStatus.newStatus((int)428, (String)"Precondition Required");
    public static final HttpResponseStatus TOO_MANY_REQUESTS = HttpResponseStatus.newStatus((int)429, (String)"Too Many Requests");
    public static final HttpResponseStatus REQUEST_HEADER_FIELDS_TOO_LARGE = HttpResponseStatus.newStatus((int)431, (String)"Request Header Fields Too Large");
    public static final HttpResponseStatus INTERNAL_SERVER_ERROR = HttpResponseStatus.newStatus((int)500, (String)"Internal Server Error");
    public static final HttpResponseStatus NOT_IMPLEMENTED = HttpResponseStatus.newStatus((int)501, (String)"Not Implemented");
    public static final HttpResponseStatus BAD_GATEWAY = HttpResponseStatus.newStatus((int)502, (String)"Bad Gateway");
    public static final HttpResponseStatus SERVICE_UNAVAILABLE = HttpResponseStatus.newStatus((int)503, (String)"Service Unavailable");
    public static final HttpResponseStatus GATEWAY_TIMEOUT = HttpResponseStatus.newStatus((int)504, (String)"Gateway Timeout");
    public static final HttpResponseStatus HTTP_VERSION_NOT_SUPPORTED = HttpResponseStatus.newStatus((int)505, (String)"HTTP Version Not Supported");
    public static final HttpResponseStatus VARIANT_ALSO_NEGOTIATES = HttpResponseStatus.newStatus((int)506, (String)"Variant Also Negotiates");
    public static final HttpResponseStatus INSUFFICIENT_STORAGE = HttpResponseStatus.newStatus((int)507, (String)"Insufficient Storage");
    public static final HttpResponseStatus NOT_EXTENDED = HttpResponseStatus.newStatus((int)510, (String)"Not Extended");
    public static final HttpResponseStatus NETWORK_AUTHENTICATION_REQUIRED = HttpResponseStatus.newStatus((int)511, (String)"Network Authentication Required");
    private final int code;
    private final AsciiString codeAsText;
    private HttpStatusClass codeClass;
    private final String reasonPhrase;
    private final byte[] bytes;

    private static HttpResponseStatus newStatus(int statusCode, String reasonPhrase) {
        return new HttpResponseStatus((int)statusCode, (String)reasonPhrase, (boolean)true);
    }

    public static HttpResponseStatus valueOf(int code) {
        HttpResponseStatus httpResponseStatus;
        HttpResponseStatus status = HttpResponseStatus.valueOf0((int)code);
        if (status != null) {
            httpResponseStatus = status;
            return httpResponseStatus;
        }
        httpResponseStatus = new HttpResponseStatus((int)code);
        return httpResponseStatus;
    }

    private static HttpResponseStatus valueOf0(int code) {
        switch (code) {
            case 100: {
                return CONTINUE;
            }
            case 101: {
                return SWITCHING_PROTOCOLS;
            }
            case 102: {
                return PROCESSING;
            }
            case 200: {
                return OK;
            }
            case 201: {
                return CREATED;
            }
            case 202: {
                return ACCEPTED;
            }
            case 203: {
                return NON_AUTHORITATIVE_INFORMATION;
            }
            case 204: {
                return NO_CONTENT;
            }
            case 205: {
                return RESET_CONTENT;
            }
            case 206: {
                return PARTIAL_CONTENT;
            }
            case 207: {
                return MULTI_STATUS;
            }
            case 300: {
                return MULTIPLE_CHOICES;
            }
            case 301: {
                return MOVED_PERMANENTLY;
            }
            case 302: {
                return FOUND;
            }
            case 303: {
                return SEE_OTHER;
            }
            case 304: {
                return NOT_MODIFIED;
            }
            case 305: {
                return USE_PROXY;
            }
            case 307: {
                return TEMPORARY_REDIRECT;
            }
            case 308: {
                return PERMANENT_REDIRECT;
            }
            case 400: {
                return BAD_REQUEST;
            }
            case 401: {
                return UNAUTHORIZED;
            }
            case 402: {
                return PAYMENT_REQUIRED;
            }
            case 403: {
                return FORBIDDEN;
            }
            case 404: {
                return NOT_FOUND;
            }
            case 405: {
                return METHOD_NOT_ALLOWED;
            }
            case 406: {
                return NOT_ACCEPTABLE;
            }
            case 407: {
                return PROXY_AUTHENTICATION_REQUIRED;
            }
            case 408: {
                return REQUEST_TIMEOUT;
            }
            case 409: {
                return CONFLICT;
            }
            case 410: {
                return GONE;
            }
            case 411: {
                return LENGTH_REQUIRED;
            }
            case 412: {
                return PRECONDITION_FAILED;
            }
            case 413: {
                return REQUEST_ENTITY_TOO_LARGE;
            }
            case 414: {
                return REQUEST_URI_TOO_LONG;
            }
            case 415: {
                return UNSUPPORTED_MEDIA_TYPE;
            }
            case 416: {
                return REQUESTED_RANGE_NOT_SATISFIABLE;
            }
            case 417: {
                return EXPECTATION_FAILED;
            }
            case 421: {
                return MISDIRECTED_REQUEST;
            }
            case 422: {
                return UNPROCESSABLE_ENTITY;
            }
            case 423: {
                return LOCKED;
            }
            case 424: {
                return FAILED_DEPENDENCY;
            }
            case 425: {
                return UNORDERED_COLLECTION;
            }
            case 426: {
                return UPGRADE_REQUIRED;
            }
            case 428: {
                return PRECONDITION_REQUIRED;
            }
            case 429: {
                return TOO_MANY_REQUESTS;
            }
            case 431: {
                return REQUEST_HEADER_FIELDS_TOO_LARGE;
            }
            case 500: {
                return INTERNAL_SERVER_ERROR;
            }
            case 501: {
                return NOT_IMPLEMENTED;
            }
            case 502: {
                return BAD_GATEWAY;
            }
            case 503: {
                return SERVICE_UNAVAILABLE;
            }
            case 504: {
                return GATEWAY_TIMEOUT;
            }
            case 505: {
                return HTTP_VERSION_NOT_SUPPORTED;
            }
            case 506: {
                return VARIANT_ALSO_NEGOTIATES;
            }
            case 507: {
                return INSUFFICIENT_STORAGE;
            }
            case 510: {
                return NOT_EXTENDED;
            }
            case 511: {
                return NETWORK_AUTHENTICATION_REQUIRED;
            }
        }
        return null;
    }

    public static HttpResponseStatus valueOf(int code, String reasonPhrase) {
        HttpResponseStatus httpResponseStatus;
        HttpResponseStatus responseStatus = HttpResponseStatus.valueOf0((int)code);
        if (responseStatus != null && responseStatus.reasonPhrase().contentEquals((CharSequence)reasonPhrase)) {
            httpResponseStatus = responseStatus;
            return httpResponseStatus;
        }
        httpResponseStatus = new HttpResponseStatus((int)code, (String)reasonPhrase);
        return httpResponseStatus;
    }

    public static HttpResponseStatus parseLine(CharSequence line) {
        HttpResponseStatus httpResponseStatus;
        if (line instanceof AsciiString) {
            httpResponseStatus = HttpResponseStatus.parseLine((AsciiString)((AsciiString)line));
            return httpResponseStatus;
        }
        httpResponseStatus = HttpResponseStatus.parseLine((String)line.toString());
        return httpResponseStatus;
    }

    public static HttpResponseStatus parseLine(String line) {
        try {
            HttpResponseStatus httpResponseStatus;
            int space = line.indexOf((int)32);
            if (space == -1) {
                httpResponseStatus = HttpResponseStatus.valueOf((int)Integer.parseInt((String)line));
                return httpResponseStatus;
            }
            httpResponseStatus = HttpResponseStatus.valueOf((int)Integer.parseInt((String)line.substring((int)0, (int)space)), (String)line.substring((int)(space + 1)));
            return httpResponseStatus;
        }
        catch (Exception e) {
            throw new IllegalArgumentException((String)("malformed status line: " + line), (Throwable)e);
        }
    }

    public static HttpResponseStatus parseLine(AsciiString line) {
        try {
            HttpResponseStatus httpResponseStatus;
            int space = line.forEachByte((ByteProcessor)ByteProcessor.FIND_ASCII_SPACE);
            if (space == -1) {
                httpResponseStatus = HttpResponseStatus.valueOf((int)line.parseInt());
                return httpResponseStatus;
            }
            httpResponseStatus = HttpResponseStatus.valueOf((int)line.parseInt((int)0, (int)space), (String)line.toString((int)(space + 1)));
            return httpResponseStatus;
        }
        catch (Exception e) {
            throw new IllegalArgumentException((String)("malformed status line: " + line), (Throwable)e);
        }
    }

    private HttpResponseStatus(int code) {
        this((int)code, (String)(HttpStatusClass.valueOf((int)code).defaultReasonPhrase() + " (" + code + ')'), (boolean)false);
    }

    public HttpResponseStatus(int code, String reasonPhrase) {
        this((int)code, (String)reasonPhrase, (boolean)false);
    }

    /*
     * Exception decompiling
     */
    private HttpResponseStatus(int code, String reasonPhrase, boolean bytes) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Extractable last case doesn't follow previous
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.examineSwitchContiguity(SwitchReplacer.java:478)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.SwitchReplacer.rebuildSwitches(SwitchReplacer.java:328)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:466)
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

    public int code() {
        return this.code;
    }

    public AsciiString codeAsText() {
        return this.codeAsText;
    }

    public String reasonPhrase() {
        return this.reasonPhrase;
    }

    public HttpStatusClass codeClass() {
        HttpStatusClass type = this.codeClass;
        if (type != null) return type;
        this.codeClass = type = HttpStatusClass.valueOf((int)this.code);
        return type;
    }

    public int hashCode() {
        return this.code();
    }

    public boolean equals(Object o) {
        if (!(o instanceof HttpResponseStatus)) {
            return false;
        }
        if (this.code() != ((HttpResponseStatus)o).code()) return false;
        return true;
    }

    @Override
    public int compareTo(HttpResponseStatus o) {
        return this.code() - o.code();
    }

    public String toString() {
        return new StringBuilder((int)(this.reasonPhrase.length() + 4)).append((CharSequence)this.codeAsText).append((char)' ').append((String)this.reasonPhrase).toString();
    }

    void encode(ByteBuf buf) {
        if (this.bytes == null) {
            ByteBufUtil.copy((AsciiString)this.codeAsText, (ByteBuf)buf);
            buf.writeByte((int)32);
            buf.writeCharSequence((CharSequence)this.reasonPhrase, (Charset)CharsetUtil.US_ASCII);
            return;
        }
        buf.writeBytes((byte[])this.bytes);
    }
}

