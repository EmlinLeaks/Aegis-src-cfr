/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.ExportControlled;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class NetworkResources {
    private final Socket mysqlConnection;
    private final InputStream mysqlInput;
    private final OutputStream mysqlOutput;

    protected NetworkResources(Socket mysqlConnection, InputStream mysqlInput, OutputStream mysqlOutput) {
        this.mysqlConnection = mysqlConnection;
        this.mysqlInput = mysqlInput;
        this.mysqlOutput = mysqlOutput;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    protected final void forceClose() {
        block21 : {
            block20 : {
                try {
                    if (ExportControlled.isSSLEstablished((Socket)this.mysqlConnection)) break block20;
                    try {
                        if (this.mysqlInput != null) {
                            this.mysqlInput.close();
                        }
                        var2_1 = null;
                        if (this.mysqlConnection != null && !this.mysqlConnection.isClosed() && !this.mysqlConnection.isInputShutdown()) {
                            try {
                                this.mysqlConnection.shutdownInput();
                            }
                            catch (UnsupportedOperationException e) {}
                        }
                    }
                    catch (Throwable var1_5) {
                        var2_2 = null;
                        if (this.mysqlConnection == null) throw var1_5;
                        if (this.mysqlConnection.isClosed() != false) throw var1_5;
                        if (this.mysqlConnection.isInputShutdown() != false) throw var1_5;
                        ** try [egrp 2[TRYBLOCK] [2 : 64->74)] { 
lbl21: // 1 sources:
                        this.mysqlConnection.shutdownInput();
                        throw var1_5;
lbl23: // 1 sources:
                        catch (UnsupportedOperationException e) {
                            // empty catch block
                        }
                        throw var1_5;
                    }
                }
                catch (IOException e) {
                    // empty catch block
                }
            }
            try {
                if (ExportControlled.isSSLEstablished((Socket)this.mysqlConnection)) break block21;
                try {
                    if (this.mysqlOutput != null) {
                        this.mysqlOutput.close();
                    }
                    var5_9 = null;
                    if (this.mysqlConnection != null && !this.mysqlConnection.isClosed() && !this.mysqlConnection.isOutputShutdown()) {
                        try {
                            this.mysqlConnection.shutdownOutput();
                        }
                        catch (UnsupportedOperationException e) {}
                    }
                }
                catch (Throwable var4_13) {
                    var5_10 = null;
                    if (this.mysqlConnection == null) throw var4_13;
                    if (this.mysqlConnection.isClosed() != false) throw var4_13;
                    if (this.mysqlConnection.isOutputShutdown() != false) throw var4_13;
                    ** try [egrp 5[TRYBLOCK] [6 : 148->158)] { 
lbl49: // 1 sources:
                    this.mysqlConnection.shutdownOutput();
                    throw var4_13;
lbl51: // 1 sources:
                    catch (UnsupportedOperationException e) {
                        // empty catch block
                    }
                    throw var4_13;
                }
            }
            catch (IOException e) {
                // empty catch block
            }
        }
        try {
            if (this.mysqlConnection == null) return;
            this.mysqlConnection.close();
            return;
        }
        catch (IOException e) {
            // empty catch block
        }
    }
}

