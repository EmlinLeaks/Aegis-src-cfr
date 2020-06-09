/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.CertificateVerifier
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSsl;
import io.netty.internal.tcnative.CertificateVerifier;
import java.security.cert.CertificateException;

public final class OpenSslCertificateException
extends CertificateException {
    private static final long serialVersionUID = 5542675253797129798L;
    private final int errorCode;

    public OpenSslCertificateException(int errorCode) {
        this((String)((String)null), (int)errorCode);
    }

    public OpenSslCertificateException(String msg, int errorCode) {
        super((String)msg);
        this.errorCode = OpenSslCertificateException.checkErrorCode((int)errorCode);
    }

    public OpenSslCertificateException(String message, Throwable cause, int errorCode) {
        super((String)message, (Throwable)cause);
        this.errorCode = OpenSslCertificateException.checkErrorCode((int)errorCode);
    }

    public OpenSslCertificateException(Throwable cause, int errorCode) {
        this(null, (Throwable)cause, (int)errorCode);
    }

    public int errorCode() {
        return this.errorCode;
    }

    private static int checkErrorCode(int errorCode) {
        if (!OpenSsl.isAvailable()) return errorCode;
        if (CertificateVerifier.isValid((int)errorCode)) return errorCode;
        throw new IllegalArgumentException((String)("errorCode '" + errorCode + "' invalid, see https://www.openssl.org/docs/man1.0.2/apps/verify.html."));
    }
}

