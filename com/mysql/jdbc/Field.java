/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.CharsetMapping;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ExceptionInterceptor;
import com.mysql.jdbc.Messages;
import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.MysqlDefs;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.SingleByteCharsetConverter;
import com.mysql.jdbc.StringUtils;
import java.io.UnsupportedEncodingException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.PatternSyntaxException;

public class Field {
    private static final int AUTO_INCREMENT_FLAG = 512;
    private static final int NO_CHARSET_INFO = -1;
    private byte[] buffer;
    private int collationIndex = 0;
    private String encoding = null;
    private int colDecimals;
    private short colFlag;
    private String collationName = null;
    private MySQLConnection connection = null;
    private String databaseName = null;
    private int databaseNameLength = -1;
    private int databaseNameStart = -1;
    protected int defaultValueLength = -1;
    protected int defaultValueStart = -1;
    private String fullName = null;
    private String fullOriginalName = null;
    private boolean isImplicitTempTable = false;
    private long length;
    private int mysqlType = -1;
    private String name;
    private int nameLength;
    private int nameStart;
    private String originalColumnName = null;
    private int originalColumnNameLength = -1;
    private int originalColumnNameStart = -1;
    private String originalTableName = null;
    private int originalTableNameLength = -1;
    private int originalTableNameStart = -1;
    private int precisionAdjustFactor = 0;
    private int sqlType = -1;
    private String tableName;
    private int tableNameLength;
    private int tableNameStart;
    private boolean useOldNameMetadata = false;
    private boolean isSingleBit;
    private int maxBytesPerChar;
    private final boolean valueNeedsQuoting;

    Field(MySQLConnection conn, byte[] buffer, int databaseNameStart, int databaseNameLength, int tableNameStart, int tableNameLength, int originalTableNameStart, int originalTableNameLength, int nameStart, int nameLength, int originalColumnNameStart, int originalColumnNameLength, long length, int mysqlType, short colFlag, int colDecimals, int defaultValueStart, int defaultValueLength, int charsetIndex) throws SQLException {
        boolean isFromFunction;
        this.connection = conn;
        this.buffer = buffer;
        this.nameStart = nameStart;
        this.nameLength = nameLength;
        this.tableNameStart = tableNameStart;
        this.tableNameLength = tableNameLength;
        this.length = length;
        this.colFlag = colFlag;
        this.colDecimals = colDecimals;
        this.mysqlType = mysqlType;
        this.databaseNameStart = databaseNameStart;
        this.databaseNameLength = databaseNameLength;
        this.originalTableNameStart = originalTableNameStart;
        this.originalTableNameLength = originalTableNameLength;
        this.originalColumnNameStart = originalColumnNameStart;
        this.originalColumnNameLength = originalColumnNameLength;
        this.defaultValueStart = defaultValueStart;
        this.defaultValueLength = defaultValueLength;
        this.collationIndex = charsetIndex;
        this.sqlType = MysqlDefs.mysqlToJavaType((int)this.mysqlType);
        this.checkForImplicitTemporaryTable();
        boolean bl = isFromFunction = this.originalTableNameLength == 0;
        if (this.mysqlType == 252) {
            if (this.connection.getBlobsAreStrings() || this.connection.getFunctionsNeverReturnBlobs() && isFromFunction) {
                this.sqlType = 12;
                this.mysqlType = 15;
            } else if (this.collationIndex == 63 || !this.connection.versionMeetsMinimum((int)4, (int)1, (int)0)) {
                if (this.connection.getUseBlobToStoreUTF8OutsideBMP() && this.shouldSetupForUtf8StringInBlob()) {
                    this.setupForUtf8StringInBlob();
                } else {
                    this.setBlobTypeBasedOnLength();
                    this.sqlType = MysqlDefs.mysqlToJavaType((int)this.mysqlType);
                }
            } else {
                this.mysqlType = 253;
                this.sqlType = -1;
            }
        }
        if (this.sqlType == -6 && this.length == 1L && this.connection.getTinyInt1isBit() && conn.getTinyInt1isBit()) {
            this.sqlType = conn.getTransformedBitIsBoolean() ? 16 : -7;
        }
        if (!this.isNativeNumericType() && !this.isNativeDateTimeType()) {
            this.encoding = this.connection.getEncodingForIndex((int)this.collationIndex);
            if ("UnicodeBig".equals((Object)this.encoding)) {
                this.encoding = "UTF-16";
            }
            if (this.mysqlType == 245) {
                this.encoding = "UTF-8";
            }
            boolean isBinary = this.isBinary();
            if (this.connection.versionMeetsMinimum((int)4, (int)1, (int)0) && this.mysqlType == 253 && isBinary && this.collationIndex == 63) {
                if (this.connection.getFunctionsNeverReturnBlobs() && isFromFunction) {
                    this.sqlType = 12;
                    this.mysqlType = 15;
                } else if (this.isOpaqueBinary()) {
                    this.sqlType = -3;
                }
            }
            if (this.connection.versionMeetsMinimum((int)4, (int)1, (int)0) && this.mysqlType == 254 && isBinary && this.collationIndex == 63 && this.isOpaqueBinary() && !this.connection.getBlobsAreStrings()) {
                this.sqlType = -2;
            }
            if (this.mysqlType == 16) {
                boolean bl2 = this.isSingleBit = this.length == 0L || this.length == 1L && (this.connection.versionMeetsMinimum((int)5, (int)0, (int)21) || this.connection.versionMeetsMinimum((int)5, (int)1, (int)10));
                if (!this.isSingleBit) {
                    this.colFlag = (short)(this.colFlag | 128);
                    this.colFlag = (short)(this.colFlag | 16);
                    isBinary = true;
                }
            }
            if (this.sqlType == -4 && !isBinary) {
                this.sqlType = -1;
            } else if (this.sqlType == -3 && !isBinary) {
                this.sqlType = 12;
            }
        } else {
            this.encoding = "US-ASCII";
        }
        if (!this.isUnsigned()) {
            switch (this.mysqlType) {
                case 0: 
                case 246: {
                    this.precisionAdjustFactor = -1;
                    break;
                }
                case 4: 
                case 5: {
                    this.precisionAdjustFactor = 1;
                }
            }
        } else {
            switch (this.mysqlType) {
                case 4: 
                case 5: {
                    this.precisionAdjustFactor = 1;
                }
            }
        }
        this.valueNeedsQuoting = this.determineNeedsQuoting();
    }

    private boolean shouldSetupForUtf8StringInBlob() throws SQLException {
        String includePattern = this.connection.getUtf8OutsideBmpIncludedColumnNamePattern();
        String excludePattern = this.connection.getUtf8OutsideBmpExcludedColumnNamePattern();
        if (excludePattern == null) return true;
        if (StringUtils.isEmptyOrWhitespaceOnly((String)excludePattern)) return true;
        try {
            if (!this.getOriginalName().matches((String)excludePattern)) return true;
            if (includePattern == null) return false;
            if (StringUtils.isEmptyOrWhitespaceOnly((String)includePattern)) return false;
            try {
                if (!this.getOriginalName().matches((String)includePattern)) return false;
                return true;
            }
            catch (PatternSyntaxException pse) {
                SQLException sqlEx = SQLError.createSQLException((String)"Illegal regex specified for \"utf8OutsideBmpIncludedColumnNamePattern\"", (String)"S1009", (ExceptionInterceptor)this.connection.getExceptionInterceptor());
                if (this.connection.getParanoid()) throw sqlEx;
                sqlEx.initCause((Throwable)pse);
                throw sqlEx;
            }
        }
        catch (PatternSyntaxException pse) {
            SQLException sqlEx = SQLError.createSQLException((String)"Illegal regex specified for \"utf8OutsideBmpExcludedColumnNamePattern\"", (String)"S1009", (ExceptionInterceptor)this.connection.getExceptionInterceptor());
            if (this.connection.getParanoid()) throw sqlEx;
            sqlEx.initCause((Throwable)pse);
            throw sqlEx;
        }
    }

    private void setupForUtf8StringInBlob() {
        if (this.length == 255L || this.length == 65535L) {
            this.mysqlType = 15;
            this.sqlType = 12;
        } else {
            this.mysqlType = 253;
            this.sqlType = -1;
        }
        this.collationIndex = 33;
    }

    Field(MySQLConnection conn, byte[] buffer, int nameStart, int nameLength, int tableNameStart, int tableNameLength, int length, int mysqlType, short colFlag, int colDecimals) throws SQLException {
        this((MySQLConnection)conn, (byte[])buffer, (int)-1, (int)-1, (int)tableNameStart, (int)tableNameLength, (int)-1, (int)-1, (int)nameStart, (int)nameLength, (int)-1, (int)-1, (long)((long)length), (int)mysqlType, (short)colFlag, (int)colDecimals, (int)-1, (int)-1, (int)-1);
    }

    Field(String tableName, String columnName, int jdbcType, int length) {
        this.tableName = tableName;
        this.name = columnName;
        this.length = (long)length;
        this.sqlType = jdbcType;
        this.colFlag = 0;
        this.colDecimals = 0;
        this.valueNeedsQuoting = this.determineNeedsQuoting();
    }

    Field(String tableName, String columnName, int charsetIndex, int jdbcType, int length) {
        this.tableName = tableName;
        this.name = columnName;
        this.length = (long)length;
        this.sqlType = jdbcType;
        this.colFlag = 0;
        this.colDecimals = 0;
        this.collationIndex = charsetIndex;
        this.valueNeedsQuoting = this.determineNeedsQuoting();
        switch (this.sqlType) {
            case -3: 
            case -2: {
                this.colFlag = (short)(this.colFlag | 128);
                this.colFlag = (short)(this.colFlag | 16);
            }
        }
    }

    private void checkForImplicitTemporaryTable() {
        this.isImplicitTempTable = this.tableNameLength > 5 && this.buffer[this.tableNameStart] == 35 && this.buffer[this.tableNameStart + 1] == 115 && this.buffer[this.tableNameStart + 2] == 113 && this.buffer[this.tableNameStart + 3] == 108 && this.buffer[this.tableNameStart + 4] == 95;
    }

    public String getEncoding() throws SQLException {
        return this.encoding;
    }

    public void setEncoding(String javaEncodingName, Connection conn) throws SQLException {
        this.encoding = javaEncodingName;
        try {
            this.collationIndex = CharsetMapping.getCollationIndexForJavaEncoding((String)javaEncodingName, (java.sql.Connection)conn);
            return;
        }
        catch (RuntimeException ex) {
            SQLException sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", null);
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    public int getCollationIndex() throws SQLException {
        return this.collationIndex;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized String getCollation() throws SQLException {
        if (this.collationName != null) return this.collationName;
        if (this.connection == null) return this.collationName;
        if (!this.connection.versionMeetsMinimum((int)4, (int)1, (int)0)) return this.collationName;
        if (this.connection.getUseDynamicCharsetInfo()) {
            Statement collationStmt;
            block8 : {
                DatabaseMetaData dbmd = this.connection.getMetaData();
                String quotedIdStr = dbmd.getIdentifierQuoteString();
                if (" ".equals((Object)quotedIdStr)) {
                    quotedIdStr = "";
                }
                String csCatalogName = this.getDatabaseName();
                String csTableName = this.getOriginalTableName();
                String csColumnName = this.getOriginalName();
                if (csCatalogName == null) return this.collationName;
                if (csCatalogName.length() == 0) return this.collationName;
                if (csTableName == null) return this.collationName;
                if (csTableName.length() == 0) return this.collationName;
                if (csColumnName == null) return this.collationName;
                if (csColumnName.length() == 0) return this.collationName;
                StringBuilder queryBuf = new StringBuilder((int)(csCatalogName.length() + csTableName.length() + 28));
                queryBuf.append((String)"SHOW FULL COLUMNS FROM ");
                queryBuf.append((String)quotedIdStr);
                queryBuf.append((String)csCatalogName);
                queryBuf.append((String)quotedIdStr);
                queryBuf.append((String)".");
                queryBuf.append((String)quotedIdStr);
                queryBuf.append((String)csTableName);
                queryBuf.append((String)quotedIdStr);
                collationStmt = null;
                ResultSet collationRs = null;
                try {
                    collationStmt = this.connection.createStatement();
                    collationRs = collationStmt.executeQuery((String)queryBuf.toString());
                    while (collationRs.next()) {
                        if (!csColumnName.equals((Object)collationRs.getString((String)"Field"))) continue;
                        this.collationName = collationRs.getString((String)"Collation");
                        break;
                    }
                    Object var10_11 = null;
                    if (collationRs == null) break block8;
                }
                catch (Throwable throwable) {
                    Object var10_12 = null;
                    if (collationRs != null) {
                        collationRs.close();
                        collationRs = null;
                    }
                    if (collationStmt == null) throw throwable;
                    collationStmt.close();
                    collationStmt = null;
                    throw throwable;
                }
                collationRs.close();
                collationRs = null;
            }
            if (collationStmt == null) return this.collationName;
            collationStmt.close();
            return this.collationName;
        }
        try {
            this.collationName = CharsetMapping.COLLATION_INDEX_TO_COLLATION_NAME[this.collationIndex];
            return this.collationName;
        }
        catch (RuntimeException ex) {
            SQLException sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", null);
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    public String getColumnLabel() throws SQLException {
        return this.getName();
    }

    public String getDatabaseName() throws SQLException {
        if (this.databaseName != null) return this.databaseName;
        if (this.databaseNameStart == -1) return this.databaseName;
        if (this.databaseNameLength == -1) return this.databaseName;
        this.databaseName = this.getStringFromBytes((int)this.databaseNameStart, (int)this.databaseNameLength);
        return this.databaseName;
    }

    int getDecimals() {
        return this.colDecimals;
    }

    public String getFullName() throws SQLException {
        if (this.fullName != null) return this.fullName;
        StringBuilder fullNameBuf = new StringBuilder((int)(this.getTableName().length() + 1 + this.getName().length()));
        fullNameBuf.append((String)this.tableName);
        fullNameBuf.append((char)'.');
        fullNameBuf.append((String)this.name);
        this.fullName = fullNameBuf.toString();
        return this.fullName;
    }

    public String getFullOriginalName() throws SQLException {
        this.getOriginalName();
        if (this.originalColumnName == null) {
            return null;
        }
        if (this.fullName != null) return this.fullOriginalName;
        StringBuilder fullOriginalNameBuf = new StringBuilder((int)(this.getOriginalTableName().length() + 1 + this.getOriginalName().length()));
        fullOriginalNameBuf.append((String)this.originalTableName);
        fullOriginalNameBuf.append((char)'.');
        fullOriginalNameBuf.append((String)this.originalColumnName);
        this.fullOriginalName = fullOriginalNameBuf.toString();
        return this.fullOriginalName;
    }

    public long getLength() {
        return this.length;
    }

    public synchronized int getMaxBytesPerCharacter() throws SQLException {
        if (this.maxBytesPerChar != 0) return this.maxBytesPerChar;
        this.maxBytesPerChar = this.connection.getMaxBytesPerChar((Integer)Integer.valueOf((int)this.collationIndex), (String)this.getEncoding());
        return this.maxBytesPerChar;
    }

    public int getMysqlType() {
        return this.mysqlType;
    }

    public String getName() throws SQLException {
        if (this.name != null) return this.name;
        this.name = this.getStringFromBytes((int)this.nameStart, (int)this.nameLength);
        return this.name;
    }

    public String getNameNoAliases() throws SQLException {
        if (this.useOldNameMetadata) {
            return this.getName();
        }
        if (this.connection == null) return this.getName();
        if (!this.connection.versionMeetsMinimum((int)4, (int)1, (int)0)) return this.getName();
        return this.getOriginalName();
    }

    public String getOriginalName() throws SQLException {
        if (this.originalColumnName != null) return this.originalColumnName;
        if (this.originalColumnNameStart == -1) return this.originalColumnName;
        if (this.originalColumnNameLength == -1) return this.originalColumnName;
        this.originalColumnName = this.getStringFromBytes((int)this.originalColumnNameStart, (int)this.originalColumnNameLength);
        return this.originalColumnName;
    }

    public String getOriginalTableName() throws SQLException {
        if (this.originalTableName != null) return this.originalTableName;
        if (this.originalTableNameStart == -1) return this.originalTableName;
        if (this.originalTableNameLength == -1) return this.originalTableName;
        this.originalTableName = this.getStringFromBytes((int)this.originalTableNameStart, (int)this.originalTableNameLength);
        return this.originalTableName;
    }

    public int getPrecisionAdjustFactor() {
        return this.precisionAdjustFactor;
    }

    public int getSQLType() {
        return this.sqlType;
    }

    private String getStringFromBytes(int stringStart, int stringLength) throws SQLException {
        if (stringStart == -1) return null;
        if (stringLength == -1) {
            return null;
        }
        if (stringLength == 0) {
            return "";
        }
        String stringVal = null;
        if (this.connection == null) {
            return StringUtils.toAsciiString((byte[])this.buffer, (int)stringStart, (int)stringLength);
        }
        if (!this.connection.getUseUnicode()) {
            return StringUtils.toAsciiString((byte[])this.buffer, (int)stringStart, (int)stringLength);
        }
        String javaEncoding = this.connection.getCharacterSetMetadata();
        if (javaEncoding == null) {
            javaEncoding = this.connection.getEncoding();
        }
        if (javaEncoding == null) {
            return StringUtils.toAsciiString((byte[])this.buffer, (int)stringStart, (int)stringLength);
        }
        SingleByteCharsetConverter converter = null;
        if (this.connection != null) {
            converter = this.connection.getCharsetConverter((String)javaEncoding);
        }
        if (converter != null) {
            return converter.toString((byte[])this.buffer, (int)stringStart, (int)stringLength);
        }
        try {
            return StringUtils.toString((byte[])this.buffer, (int)stringStart, (int)stringLength, (String)javaEncoding);
        }
        catch (UnsupportedEncodingException ue) {
            throw new RuntimeException((String)(Messages.getString((String)"Field.12") + javaEncoding + Messages.getString((String)"Field.13")));
        }
    }

    public String getTable() throws SQLException {
        return this.getTableName();
    }

    public String getTableName() throws SQLException {
        if (this.tableName != null) return this.tableName;
        this.tableName = this.getStringFromBytes((int)this.tableNameStart, (int)this.tableNameLength);
        return this.tableName;
    }

    public String getTableNameNoAliases() throws SQLException {
        if (!this.connection.versionMeetsMinimum((int)4, (int)1, (int)0)) return this.getTableName();
        return this.getOriginalTableName();
    }

    public boolean isAutoIncrement() {
        if ((this.colFlag & 512) <= 0) return false;
        return true;
    }

    public boolean isBinary() {
        if ((this.colFlag & 128) <= 0) return false;
        return true;
    }

    public boolean isBlob() {
        if ((this.colFlag & 16) <= 0) return false;
        return true;
    }

    private boolean isImplicitTemporaryTable() {
        return this.isImplicitTempTable;
    }

    public boolean isMultipleKey() {
        if ((this.colFlag & 8) <= 0) return false;
        return true;
    }

    boolean isNotNull() {
        if ((this.colFlag & 1) <= 0) return false;
        return true;
    }

    boolean isOpaqueBinary() throws SQLException {
        if (this.collationIndex == 63 && this.isBinary() && (this.getMysqlType() == 254 || this.getMysqlType() == 253)) {
            if (this.originalTableNameLength == 0 && this.connection != null && !this.connection.versionMeetsMinimum((int)5, (int)0, (int)25)) {
                return false;
            }
            if (this.isImplicitTemporaryTable()) return false;
            return true;
        }
        if (!this.connection.versionMeetsMinimum((int)4, (int)1, (int)0)) return false;
        if (!"binary".equalsIgnoreCase((String)this.getEncoding())) return false;
        return true;
    }

    public boolean isPrimaryKey() {
        if ((this.colFlag & 2) <= 0) return false;
        return true;
    }

    boolean isReadOnly() throws SQLException {
        if (!this.connection.versionMeetsMinimum((int)4, (int)1, (int)0)) return false;
        String orgColumnName = this.getOriginalName();
        String orgTableName = this.getOriginalTableName();
        if (orgColumnName == null) return true;
        if (orgColumnName.length() <= 0) return true;
        if (orgTableName == null) return true;
        if (orgTableName.length() <= 0) return true;
        return false;
    }

    public boolean isUniqueKey() {
        if ((this.colFlag & 4) <= 0) return false;
        return true;
    }

    public boolean isUnsigned() {
        if ((this.colFlag & 32) <= 0) return false;
        return true;
    }

    public void setUnsigned() {
        this.colFlag = (short)(this.colFlag | 32);
    }

    public boolean isZeroFill() {
        if ((this.colFlag & 64) <= 0) return false;
        return true;
    }

    private void setBlobTypeBasedOnLength() {
        if (this.length == 255L) {
            this.mysqlType = 249;
            return;
        }
        if (this.length == 65535L) {
            this.mysqlType = 252;
            return;
        }
        if (this.length == 0xFFFFFFL) {
            this.mysqlType = 250;
            return;
        }
        if (this.length != 0xFFFFFFFFL) return;
        this.mysqlType = 251;
    }

    private boolean isNativeNumericType() {
        if (this.mysqlType >= 1) {
            if (this.mysqlType <= 5) return true;
        }
        if (this.mysqlType == 8) return true;
        if (this.mysqlType == 13) return true;
        return false;
    }

    private boolean isNativeDateTimeType() {
        if (this.mysqlType == 10) return true;
        if (this.mysqlType == 14) return true;
        if (this.mysqlType == 12) return true;
        if (this.mysqlType == 11) return true;
        if (this.mysqlType == 7) return true;
        return false;
    }

    public boolean isCharsetApplicableType() {
        if (this.mysqlType == 247) return true;
        if (this.mysqlType == 245) return true;
        if (this.mysqlType == 248) return true;
        if (this.mysqlType == 254) return true;
        if (this.mysqlType == 253) return true;
        if (this.mysqlType == 15) return true;
        return false;
    }

    public void setConnection(MySQLConnection conn) {
        this.connection = conn;
        if (this.encoding != null) {
            if (this.collationIndex != 0) return;
        }
        this.encoding = this.connection.getEncoding();
    }

    void setMysqlType(int type) {
        this.mysqlType = type;
        this.sqlType = MysqlDefs.mysqlToJavaType((int)this.mysqlType);
    }

    protected void setUseOldNameMetadata(boolean useOldNameMetadata) {
        this.useOldNameMetadata = useOldNameMetadata;
    }

    public String toString() {
        try {
            StringBuilder asString = new StringBuilder();
            asString.append((String)super.toString());
            asString.append((String)"[");
            asString.append((String)"catalog=");
            asString.append((String)this.getDatabaseName());
            asString.append((String)",tableName=");
            asString.append((String)this.getTableName());
            asString.append((String)",originalTableName=");
            asString.append((String)this.getOriginalTableName());
            asString.append((String)",columnName=");
            asString.append((String)this.getName());
            asString.append((String)",originalColumnName=");
            asString.append((String)this.getOriginalName());
            asString.append((String)",mysqlType=");
            asString.append((int)this.getMysqlType());
            asString.append((String)"(");
            asString.append((String)MysqlDefs.typeToName((int)this.getMysqlType()));
            asString.append((String)")");
            asString.append((String)",flags=");
            if (this.isAutoIncrement()) {
                asString.append((String)" AUTO_INCREMENT");
            }
            if (this.isPrimaryKey()) {
                asString.append((String)" PRIMARY_KEY");
            }
            if (this.isUniqueKey()) {
                asString.append((String)" UNIQUE_KEY");
            }
            if (this.isBinary()) {
                asString.append((String)" BINARY");
            }
            if (this.isBlob()) {
                asString.append((String)" BLOB");
            }
            if (this.isMultipleKey()) {
                asString.append((String)" MULTI_KEY");
            }
            if (this.isUnsigned()) {
                asString.append((String)" UNSIGNED");
            }
            if (this.isZeroFill()) {
                asString.append((String)" ZEROFILL");
            }
            asString.append((String)", charsetIndex=");
            asString.append((int)this.collationIndex);
            asString.append((String)", charsetName=");
            asString.append((String)this.encoding);
            asString.append((String)"]");
            return asString.toString();
        }
        catch (Throwable t) {
            return super.toString();
        }
    }

    protected boolean isSingleBit() {
        return this.isSingleBit;
    }

    protected boolean getvalueNeedsQuoting() {
        return this.valueNeedsQuoting;
    }

    private boolean determineNeedsQuoting() {
        boolean retVal = false;
        switch (this.sqlType) {
            case -7: 
            case -6: 
            case -5: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                return false;
            }
        }
        return true;
    }
}

