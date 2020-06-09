/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.multipart.AbstractDiskHttpData;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.FileUploadUtil;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCounted;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class DiskFileUpload
extends AbstractDiskHttpData
implements FileUpload {
    public static String baseDirectory;
    public static boolean deleteOnExitTemporaryFile;
    public static final String prefix = "FUp_";
    public static final String postfix = ".tmp";
    private String filename;
    private String contentType;
    private String contentTransferEncoding;

    public DiskFileUpload(String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size) {
        super((String)name, (Charset)charset, (long)size);
        this.setFilename((String)filename);
        this.setContentType((String)contentType);
        this.setContentTransferEncoding((String)contentTransferEncoding);
    }

    @Override
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return InterfaceHttpData.HttpDataType.FileUpload;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public void setFilename(String filename) {
        if (filename == null) {
            throw new NullPointerException((String)"filename");
        }
        this.filename = filename;
    }

    public int hashCode() {
        return FileUploadUtil.hashCode((FileUpload)this);
    }

    public boolean equals(Object o) {
        if (!(o instanceof FileUpload)) return false;
        if (!FileUploadUtil.equals((FileUpload)this, (FileUpload)((FileUpload)o))) return false;
        return true;
    }

    @Override
    public int compareTo(InterfaceHttpData o) {
        if (o instanceof FileUpload) return this.compareTo((FileUpload)((FileUpload)o));
        throw new ClassCastException((String)("Cannot compare " + (Object)((Object)this.getHttpDataType()) + " with " + (Object)((Object)o.getHttpDataType())));
    }

    @Override
    public int compareTo(FileUpload o) {
        return FileUploadUtil.compareTo((FileUpload)this, (FileUpload)o);
    }

    @Override
    public void setContentType(String contentType) {
        if (contentType == null) {
            throw new NullPointerException((String)"contentType");
        }
        this.contentType = contentType;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public String getContentTransferEncoding() {
        return this.contentTransferEncoding;
    }

    @Override
    public void setContentTransferEncoding(String contentTransferEncoding) {
        this.contentTransferEncoding = contentTransferEncoding;
    }

    public String toString() {
        String string;
        File file = null;
        try {
            file = this.getFile();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        String string2 = this.getCharset() != null ? "; " + HttpHeaderValues.CHARSET + '=' + this.getCharset().name() + "\r\n" : "\r\n";
        if (file != null) {
            string = file.getAbsolutePath();
            return HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.FORM_DATA + "; " + HttpHeaderValues.NAME + "=\"" + this.getName() + "\"; " + HttpHeaderValues.FILENAME + "=\"" + this.filename + "\"\r\n" + HttpHeaderNames.CONTENT_TYPE + ": " + this.contentType + string2 + HttpHeaderNames.CONTENT_LENGTH + ": " + this.length() + "\r\nCompleted: " + this.isCompleted() + "\r\nIsInMemory: " + this.isInMemory() + "\r\nRealFile: " + string + " DefaultDeleteAfter: " + deleteOnExitTemporaryFile;
        }
        string = "null";
        return HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.FORM_DATA + "; " + HttpHeaderValues.NAME + "=\"" + this.getName() + "\"; " + HttpHeaderValues.FILENAME + "=\"" + this.filename + "\"\r\n" + HttpHeaderNames.CONTENT_TYPE + ": " + this.contentType + string2 + HttpHeaderNames.CONTENT_LENGTH + ": " + this.length() + "\r\nCompleted: " + this.isCompleted() + "\r\nIsInMemory: " + this.isInMemory() + "\r\nRealFile: " + string + " DefaultDeleteAfter: " + deleteOnExitTemporaryFile;
    }

    @Override
    protected boolean deleteOnExit() {
        return deleteOnExitTemporaryFile;
    }

    @Override
    protected String getBaseDirectory() {
        return baseDirectory;
    }

    @Override
    protected String getDiskFilename() {
        return "upload";
    }

    @Override
    protected String getPostfix() {
        return postfix;
    }

    @Override
    protected String getPrefix() {
        return prefix;
    }

    @Override
    public FileUpload copy() {
        ByteBuf byteBuf;
        ByteBuf content = this.content();
        if (content != null) {
            byteBuf = content.copy();
            return this.replace((ByteBuf)byteBuf);
        }
        byteBuf = null;
        return this.replace((ByteBuf)byteBuf);
    }

    @Override
    public FileUpload duplicate() {
        ByteBuf byteBuf;
        ByteBuf content = this.content();
        if (content != null) {
            byteBuf = content.duplicate();
            return this.replace((ByteBuf)byteBuf);
        }
        byteBuf = null;
        return this.replace((ByteBuf)byteBuf);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FileUpload retainedDuplicate() {
        ByteBuf content = this.content();
        if (content == null) return this.replace(null);
        content = content.retainedDuplicate();
        boolean success = false;
        try {
            FileUpload duplicate = this.replace((ByteBuf)content);
            success = true;
            FileUpload fileUpload = duplicate;
            return fileUpload;
        }
        finally {
            if (!success) {
                content.release();
            }
        }
    }

    @Override
    public FileUpload replace(ByteBuf content) {
        DiskFileUpload upload = new DiskFileUpload((String)this.getName(), (String)this.getFilename(), (String)this.getContentType(), (String)this.getContentTransferEncoding(), (Charset)this.getCharset(), (long)this.size);
        if (content == null) return upload;
        try {
            upload.setContent((ByteBuf)content);
            return upload;
        }
        catch (IOException e) {
            throw new ChannelException((Throwable)e);
        }
    }

    @Override
    public FileUpload retain(int increment) {
        super.retain((int)increment);
        return this;
    }

    @Override
    public FileUpload retain() {
        super.retain();
        return this;
    }

    @Override
    public FileUpload touch() {
        super.touch();
        return this;
    }

    @Override
    public FileUpload touch(Object hint) {
        super.touch((Object)hint);
        return this;
    }

    static {
        deleteOnExitTemporaryFile = true;
    }
}

