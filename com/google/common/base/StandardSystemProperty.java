/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import javax.annotation.Nullable;

@GwtIncompatible
public enum StandardSystemProperty {
    JAVA_VERSION((String)"java.version"),
    JAVA_VENDOR((String)"java.vendor"),
    JAVA_VENDOR_URL((String)"java.vendor.url"),
    JAVA_HOME((String)"java.home"),
    JAVA_VM_SPECIFICATION_VERSION((String)"java.vm.specification.version"),
    JAVA_VM_SPECIFICATION_VENDOR((String)"java.vm.specification.vendor"),
    JAVA_VM_SPECIFICATION_NAME((String)"java.vm.specification.name"),
    JAVA_VM_VERSION((String)"java.vm.version"),
    JAVA_VM_VENDOR((String)"java.vm.vendor"),
    JAVA_VM_NAME((String)"java.vm.name"),
    JAVA_SPECIFICATION_VERSION((String)"java.specification.version"),
    JAVA_SPECIFICATION_VENDOR((String)"java.specification.vendor"),
    JAVA_SPECIFICATION_NAME((String)"java.specification.name"),
    JAVA_CLASS_VERSION((String)"java.class.version"),
    JAVA_CLASS_PATH((String)"java.class.path"),
    JAVA_LIBRARY_PATH((String)"java.library.path"),
    JAVA_IO_TMPDIR((String)"java.io.tmpdir"),
    JAVA_COMPILER((String)"java.compiler"),
    JAVA_EXT_DIRS((String)"java.ext.dirs"),
    OS_NAME((String)"os.name"),
    OS_ARCH((String)"os.arch"),
    OS_VERSION((String)"os.version"),
    FILE_SEPARATOR((String)"file.separator"),
    PATH_SEPARATOR((String)"path.separator"),
    LINE_SEPARATOR((String)"line.separator"),
    USER_NAME((String)"user.name"),
    USER_HOME((String)"user.home"),
    USER_DIR((String)"user.dir");
    
    private final String key;

    private StandardSystemProperty(String key) {
        this.key = key;
    }

    public String key() {
        return this.key;
    }

    @Nullable
    public String value() {
        return System.getProperty((String)this.key);
    }

    public String toString() {
        return this.key() + "=" + this.value();
    }
}

