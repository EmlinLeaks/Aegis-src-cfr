/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.jdbc;

import com.mysql.jdbc.Collation;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.MysqlCharset;
import com.mysql.jdbc.SQLError;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CharsetMapping {
    public static final int MAP_SIZE = 2048;
    public static final String[] COLLATION_INDEX_TO_COLLATION_NAME;
    public static final MysqlCharset[] COLLATION_INDEX_TO_CHARSET;
    public static final Map<String, MysqlCharset> CHARSET_NAME_TO_CHARSET;
    public static final Map<String, Integer> CHARSET_NAME_TO_COLLATION_INDEX;
    private static final Map<String, List<MysqlCharset>> JAVA_ENCODING_UC_TO_MYSQL_CHARSET;
    private static final Set<String> MULTIBYTE_ENCODINGS;
    private static final Map<String, String> ERROR_MESSAGE_FILE_TO_MYSQL_CHARSET;
    private static final Set<String> ESCAPE_ENCODINGS;
    public static final Set<Integer> UTF8MB4_INDEXES;
    private static final String MYSQL_CHARSET_NAME_armscii8 = "armscii8";
    private static final String MYSQL_CHARSET_NAME_ascii = "ascii";
    private static final String MYSQL_CHARSET_NAME_big5 = "big5";
    private static final String MYSQL_CHARSET_NAME_binary = "binary";
    private static final String MYSQL_CHARSET_NAME_cp1250 = "cp1250";
    private static final String MYSQL_CHARSET_NAME_cp1251 = "cp1251";
    private static final String MYSQL_CHARSET_NAME_cp1256 = "cp1256";
    private static final String MYSQL_CHARSET_NAME_cp1257 = "cp1257";
    private static final String MYSQL_CHARSET_NAME_cp850 = "cp850";
    private static final String MYSQL_CHARSET_NAME_cp852 = "cp852";
    private static final String MYSQL_CHARSET_NAME_cp866 = "cp866";
    private static final String MYSQL_CHARSET_NAME_cp932 = "cp932";
    private static final String MYSQL_CHARSET_NAME_dec8 = "dec8";
    private static final String MYSQL_CHARSET_NAME_eucjpms = "eucjpms";
    private static final String MYSQL_CHARSET_NAME_euckr = "euckr";
    private static final String MYSQL_CHARSET_NAME_gb18030 = "gb18030";
    private static final String MYSQL_CHARSET_NAME_gb2312 = "gb2312";
    private static final String MYSQL_CHARSET_NAME_gbk = "gbk";
    private static final String MYSQL_CHARSET_NAME_geostd8 = "geostd8";
    private static final String MYSQL_CHARSET_NAME_greek = "greek";
    private static final String MYSQL_CHARSET_NAME_hebrew = "hebrew";
    private static final String MYSQL_CHARSET_NAME_hp8 = "hp8";
    private static final String MYSQL_CHARSET_NAME_keybcs2 = "keybcs2";
    private static final String MYSQL_CHARSET_NAME_koi8r = "koi8r";
    private static final String MYSQL_CHARSET_NAME_koi8u = "koi8u";
    private static final String MYSQL_CHARSET_NAME_latin1 = "latin1";
    private static final String MYSQL_CHARSET_NAME_latin2 = "latin2";
    private static final String MYSQL_CHARSET_NAME_latin5 = "latin5";
    private static final String MYSQL_CHARSET_NAME_latin7 = "latin7";
    private static final String MYSQL_CHARSET_NAME_macce = "macce";
    private static final String MYSQL_CHARSET_NAME_macroman = "macroman";
    private static final String MYSQL_CHARSET_NAME_sjis = "sjis";
    private static final String MYSQL_CHARSET_NAME_swe7 = "swe7";
    private static final String MYSQL_CHARSET_NAME_tis620 = "tis620";
    private static final String MYSQL_CHARSET_NAME_ucs2 = "ucs2";
    private static final String MYSQL_CHARSET_NAME_ujis = "ujis";
    private static final String MYSQL_CHARSET_NAME_utf16 = "utf16";
    private static final String MYSQL_CHARSET_NAME_utf16le = "utf16le";
    private static final String MYSQL_CHARSET_NAME_utf32 = "utf32";
    private static final String MYSQL_CHARSET_NAME_utf8 = "utf8";
    private static final String MYSQL_CHARSET_NAME_utf8mb4 = "utf8mb4";
    private static final String MYSQL_4_0_CHARSET_NAME_cp1251cias = "cp1251cias";
    private static final String MYSQL_4_0_CHARSET_NAME_cp1251csas = "cp1251csas";
    private static final String MYSQL_4_0_CHARSET_NAME_croat = "croat";
    private static final String MYSQL_4_0_CHARSET_NAME_czech = "czech";
    private static final String MYSQL_4_0_CHARSET_NAME_danish = "danish";
    private static final String MYSQL_4_0_CHARSET_NAME_dos = "dos";
    private static final String MYSQL_4_0_CHARSET_NAME_estonia = "estonia";
    private static final String MYSQL_4_0_CHARSET_NAME_euc_kr = "euc_kr";
    private static final String MYSQL_4_0_CHARSET_NAME_german1 = "german1";
    private static final String MYSQL_4_0_CHARSET_NAME_hungarian = "hungarian";
    private static final String MYSQL_4_0_CHARSET_NAME_koi8_ru = "koi8_ru";
    private static final String MYSQL_4_0_CHARSET_NAME_koi8_ukr = "koi8_ukr";
    private static final String MYSQL_4_0_CHARSET_NAME_latin1_de = "latin1_de";
    private static final String MYSQL_4_0_CHARSET_NAME_latvian = "latvian";
    private static final String MYSQL_4_0_CHARSET_NAME_latvian1 = "latvian1";
    private static final String MYSQL_4_0_CHARSET_NAME_usa7 = "usa7";
    private static final String MYSQL_4_0_CHARSET_NAME_win1250 = "win1250";
    private static final String MYSQL_4_0_CHARSET_NAME_win1251 = "win1251";
    private static final String MYSQL_4_0_CHARSET_NAME_win1251ukr = "win1251ukr";
    public static final String NOT_USED = "latin1";
    public static final String COLLATION_NOT_DEFINED = "none";
    public static final int MYSQL_COLLATION_INDEX_utf8 = 33;
    public static final int MYSQL_COLLATION_INDEX_binary = 63;
    private static int numberOfEncodingsConfigured;

    public static final String getMysqlCharsetForJavaEncoding(String javaEncoding, Connection conn) throws SQLException {
        try {
            List<MysqlCharset> mysqlCharsets = JAVA_ENCODING_UC_TO_MYSQL_CHARSET.get((Object)javaEncoding.toUpperCase((Locale)Locale.ENGLISH));
            if (mysqlCharsets == null) return null;
            Iterator<MysqlCharset> iter = mysqlCharsets.iterator();
            MysqlCharset versionedProp = null;
            do {
                if (!iter.hasNext()) {
                    if (versionedProp == null) return null;
                    return versionedProp.charsetName;
                }
                MysqlCharset charset = iter.next();
                if (conn == null) {
                    return charset.charsetName;
                }
                if (versionedProp != null && versionedProp.major >= charset.major && versionedProp.minor >= charset.minor && versionedProp.subminor >= charset.subminor && (versionedProp.priority >= charset.priority || versionedProp.major != charset.major || versionedProp.minor != charset.minor || versionedProp.subminor != charset.subminor) || !charset.isOkayForVersion((Connection)conn)) continue;
                versionedProp = charset;
            } while (true);
        }
        catch (SQLException ex) {
            throw ex;
        }
        catch (RuntimeException ex) {
            SQLException sqlEx = SQLError.createSQLException((String)ex.toString(), (String)"S1009", null);
            sqlEx.initCause((Throwable)ex);
            throw sqlEx;
        }
    }

    public static int getCollationIndexForJavaEncoding(String javaEncoding, java.sql.Connection conn) throws SQLException {
        String charsetName = CharsetMapping.getMysqlCharsetForJavaEncoding((String)javaEncoding, (Connection)((Connection)conn));
        if (charsetName == null) return 0;
        Integer ci = CHARSET_NAME_TO_COLLATION_INDEX.get((Object)charsetName);
        if (ci == null) return 0;
        return ci.intValue();
    }

    public static String getMysqlCharsetNameForCollationIndex(Integer collationIndex) {
        if (collationIndex == null) return null;
        if (collationIndex.intValue() <= 0) return null;
        if (collationIndex.intValue() >= 2048) return null;
        return CharsetMapping.COLLATION_INDEX_TO_CHARSET[collationIndex.intValue()].charsetName;
    }

    public static String getJavaEncodingForMysqlCharset(String mysqlCharsetName, String javaEncoding) {
        String res = javaEncoding;
        MysqlCharset cs = CHARSET_NAME_TO_CHARSET.get((Object)mysqlCharsetName);
        if (cs == null) return res;
        return cs.getMatchingJavaEncoding((String)javaEncoding);
    }

    public static String getJavaEncodingForMysqlCharset(String mysqlCharsetName) {
        return CharsetMapping.getJavaEncodingForMysqlCharset((String)mysqlCharsetName, null);
    }

    public static String getJavaEncodingForCollationIndex(Integer collationIndex, String javaEncoding) {
        if (collationIndex == null) return null;
        if (collationIndex.intValue() <= 0) return null;
        if (collationIndex.intValue() >= 2048) return null;
        MysqlCharset cs = COLLATION_INDEX_TO_CHARSET[collationIndex.intValue()];
        return cs.getMatchingJavaEncoding((String)javaEncoding);
    }

    public static String getJavaEncodingForCollationIndex(Integer collationIndex) {
        return CharsetMapping.getJavaEncodingForCollationIndex((Integer)collationIndex, null);
    }

    static final int getNumberOfCharsetsConfigured() {
        return numberOfEncodingsConfigured;
    }

    static final String getCharacterEncodingForErrorMessages(ConnectionImpl conn) throws SQLException {
        int lastSlashIndex;
        if (conn.versionMeetsMinimum((int)5, (int)5, (int)0)) {
            String errorMessageCharsetName = conn.getServerVariable((String)"jdbc.local.character_set_results");
            if (errorMessageCharsetName == null) return "UTF-8";
            String javaEncoding = CharsetMapping.getJavaEncodingForMysqlCharset((String)errorMessageCharsetName);
            if (javaEncoding == null) return "UTF-8";
            return javaEncoding;
        }
        String errorMessageFile = conn.getServerVariable((String)"language");
        if (errorMessageFile == null) return "Cp1252";
        if (errorMessageFile.length() == 0) {
            return "Cp1252";
        }
        int endWithoutSlash = errorMessageFile.length();
        if (errorMessageFile.endsWith((String)"/") || errorMessageFile.endsWith((String)"\\")) {
            --endWithoutSlash;
        }
        if ((lastSlashIndex = errorMessageFile.lastIndexOf((int)47, (int)(endWithoutSlash - 1))) == -1) {
            lastSlashIndex = errorMessageFile.lastIndexOf((int)92, (int)(endWithoutSlash - 1));
        }
        if (lastSlashIndex == -1) {
            lastSlashIndex = 0;
        }
        if (lastSlashIndex == endWithoutSlash) return "Cp1252";
        if (endWithoutSlash < lastSlashIndex) {
            return "Cp1252";
        }
        String errorMessageEncodingMysql = ERROR_MESSAGE_FILE_TO_MYSQL_CHARSET.get((Object)(errorMessageFile = errorMessageFile.substring((int)(lastSlashIndex + 1), (int)endWithoutSlash)));
        if (errorMessageEncodingMysql == null) {
            return "Cp1252";
        }
        String javaEncoding = CharsetMapping.getJavaEncodingForMysqlCharset((String)errorMessageEncodingMysql);
        if (javaEncoding != null) return javaEncoding;
        return "Cp1252";
    }

    static final boolean requiresEscapeEasternUnicode(String javaEncodingName) {
        return ESCAPE_ENCODINGS.contains((Object)javaEncodingName.toUpperCase((Locale)Locale.ENGLISH));
    }

    public static final boolean isMultibyteCharset(String javaEncodingName) {
        return MULTIBYTE_ENCODINGS.contains((Object)javaEncodingName.toUpperCase((Locale)Locale.ENGLISH));
    }

    public static int getMblen(String charsetName) {
        if (charsetName == null) return 0;
        MysqlCharset cs = CHARSET_NAME_TO_CHARSET.get((Object)charsetName);
        if (cs == null) return 0;
        return cs.mblen;
    }

    static {
        numberOfEncodingsConfigured = 0;
        MysqlCharset[] charset = new MysqlCharset[]{new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_usa7, (int)1, (int)0, (String[])new String[]{"US-ASCII"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_CHARSET_NAME_ascii, (int)1, (int)0, (String[])new String[]{"US-ASCII", "ASCII"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_big5, (int)2, (int)0, (String[])new String[]{"Big5"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_gbk, (int)2, (int)0, (String[])new String[]{"GBK"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_sjis, (int)2, (int)0, (String[])new String[]{"SHIFT_JIS", "Cp943", "WINDOWS-31J"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_cp932, (int)2, (int)1, (String[])new String[]{"WINDOWS-31J"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_gb2312, (int)2, (int)0, (String[])new String[]{"GB2312"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_ujis, (int)3, (int)0, (String[])new String[]{"EUC_JP"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_eucjpms, (int)3, (int)0, (String[])new String[]{"EUC_JP_Solaris"}, (int)5, (int)0, (int)3), new MysqlCharset((String)MYSQL_CHARSET_NAME_gb18030, (int)4, (int)0, (String[])new String[]{"GB18030"}, (int)5, (int)7, (int)4), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_euc_kr, (int)2, (int)0, (String[])new String[]{"EUC_KR"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_CHARSET_NAME_euckr, (int)2, (int)0, (String[])new String[]{"EUC-KR"}), new MysqlCharset((String)"latin1", (int)1, (int)1, (String[])new String[]{"Cp1252", "ISO8859_1"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_swe7, (int)1, (int)0, (String[])new String[]{"Cp1252"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_hp8, (int)1, (int)0, (String[])new String[]{"Cp1252"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_dec8, (int)1, (int)0, (String[])new String[]{"Cp1252"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_armscii8, (int)1, (int)0, (String[])new String[]{"Cp1252"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_geostd8, (int)1, (int)0, (String[])new String[]{"Cp1252"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_latin2, (int)1, (int)0, (String[])new String[]{"ISO8859_2"}), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_czech, (int)1, (int)0, (String[])new String[]{"ISO8859_2"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_hungarian, (int)1, (int)0, (String[])new String[]{"ISO8859_2"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_croat, (int)1, (int)0, (String[])new String[]{"ISO8859_2"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_CHARSET_NAME_greek, (int)1, (int)0, (String[])new String[]{"ISO8859_7", MYSQL_CHARSET_NAME_greek}), new MysqlCharset((String)MYSQL_CHARSET_NAME_latin7, (int)1, (int)0, (String[])new String[]{"ISO-8859-13"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_hebrew, (int)1, (int)0, (String[])new String[]{"ISO8859_8"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_latin5, (int)1, (int)0, (String[])new String[]{"ISO8859_9"}), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_latvian, (int)1, (int)0, (String[])new String[]{"ISO8859_13"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_latvian1, (int)1, (int)0, (String[])new String[]{"ISO8859_13"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_estonia, (int)1, (int)1, (String[])new String[]{"ISO8859_13"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_CHARSET_NAME_cp850, (int)1, (int)0, (String[])new String[]{"Cp850", "Cp437"}), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_dos, (int)1, (int)0, (String[])new String[]{"Cp850", "Cp437"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_CHARSET_NAME_cp852, (int)1, (int)0, (String[])new String[]{"Cp852"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_keybcs2, (int)1, (int)0, (String[])new String[]{"Cp852"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_cp866, (int)1, (int)0, (String[])new String[]{"Cp866"}), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_koi8_ru, (int)1, (int)0, (String[])new String[]{"KOI8_R"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_CHARSET_NAME_koi8r, (int)1, (int)1, (String[])new String[]{"KOI8_R"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_koi8u, (int)1, (int)0, (String[])new String[]{"KOI8_R"}), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_koi8_ukr, (int)1, (int)0, (String[])new String[]{"KOI8_R"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_CHARSET_NAME_tis620, (int)1, (int)0, (String[])new String[]{"TIS620"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_cp1250, (int)1, (int)0, (String[])new String[]{"Cp1250"}), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_win1250, (int)1, (int)0, (String[])new String[]{"Cp1250"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_CHARSET_NAME_cp1251, (int)1, (int)1, (String[])new String[]{"Cp1251"}), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_win1251, (int)1, (int)0, (String[])new String[]{"Cp1251"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_cp1251cias, (int)1, (int)0, (String[])new String[]{"Cp1251"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_cp1251csas, (int)1, (int)0, (String[])new String[]{"Cp1251"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_win1251ukr, (int)1, (int)0, (String[])new String[]{"Cp1251"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_CHARSET_NAME_cp1256, (int)1, (int)0, (String[])new String[]{"Cp1256"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_cp1257, (int)1, (int)0, (String[])new String[]{"Cp1257"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_macroman, (int)1, (int)0, (String[])new String[]{"MacRoman"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_macce, (int)1, (int)0, (String[])new String[]{"MacCentralEurope"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_utf8, (int)3, (int)1, (String[])new String[]{"UTF-8"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_utf8mb4, (int)4, (int)0, (String[])new String[]{"UTF-8"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_ucs2, (int)2, (int)0, (String[])new String[]{"UnicodeBig"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_binary, (int)1, (int)1, (String[])new String[]{"ISO8859_1"}), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_latin1_de, (int)1, (int)0, (String[])new String[]{"ISO8859_1"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_german1, (int)1, (int)0, (String[])new String[]{"ISO8859_1"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_4_0_CHARSET_NAME_danish, (int)1, (int)0, (String[])new String[]{"ISO8859_1"}, (int)4, (int)0), new MysqlCharset((String)MYSQL_CHARSET_NAME_utf16, (int)4, (int)0, (String[])new String[]{"UTF-16"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_utf16le, (int)4, (int)0, (String[])new String[]{"UTF-16LE"}), new MysqlCharset((String)MYSQL_CHARSET_NAME_utf32, (int)4, (int)0, (String[])new String[]{"UTF-32"})};
        HashMap<String, MysqlCharset> charsetNameToMysqlCharsetMap = new HashMap<String, MysqlCharset>();
        HashMap<String, ArrayList<MysqlCharset>> javaUcToMysqlCharsetMap = new HashMap<String, ArrayList<MysqlCharset>>();
        HashSet<String> tempMultibyteEncodings = new HashSet<String>();
        HashSet<String> tempEscapeEncodings = new HashSet<String>();
        for (int i = 0; i < charset.length; ++i) {
            String charsetName = charset[i].charsetName;
            charsetNameToMysqlCharsetMap.put(charsetName, charset[i]);
            numberOfEncodingsConfigured += charset[i].javaEncodingsUc.size();
            for (String encUC : charset[i].javaEncodingsUc) {
                ArrayList<MysqlCharset> charsets = (ArrayList<MysqlCharset>)javaUcToMysqlCharsetMap.get((Object)encUC);
                if (charsets == null) {
                    charsets = new ArrayList<MysqlCharset>();
                    javaUcToMysqlCharsetMap.put(encUC, charsets);
                }
                charsets.add(charset[i]);
                if (charset[i].mblen <= 1) continue;
                tempMultibyteEncodings.add(encUC);
            }
            if (!charsetName.equals((Object)MYSQL_CHARSET_NAME_big5) && !charsetName.equals((Object)MYSQL_CHARSET_NAME_gbk) && !charsetName.equals((Object)MYSQL_CHARSET_NAME_sjis)) continue;
            tempEscapeEncodings.addAll(charset[i].javaEncodingsUc);
        }
        CHARSET_NAME_TO_CHARSET = Collections.unmodifiableMap(charsetNameToMysqlCharsetMap);
        JAVA_ENCODING_UC_TO_MYSQL_CHARSET = Collections.unmodifiableMap(javaUcToMysqlCharsetMap);
        MULTIBYTE_ENCODINGS = Collections.unmodifiableSet(tempMultibyteEncodings);
        ESCAPE_ENCODINGS = Collections.unmodifiableSet(tempEscapeEncodings);
        Collation[] collation = new Collation[2048];
        collation[1] = new Collation((int)1, (String)"big5_chinese_ci", (int)1, (String)MYSQL_CHARSET_NAME_big5);
        collation[2] = new Collation((int)2, (String)"latin2_czech_cs", (int)0, (String)MYSQL_CHARSET_NAME_latin2);
        collation[3] = new Collation((int)3, (String)"dec8_swedish_ci", (int)0, (String)MYSQL_CHARSET_NAME_dec8);
        collation[4] = new Collation((int)4, (String)"cp850_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_cp850);
        collation[5] = new Collation((int)5, (String)"latin1_german1_ci", (int)0, (String)"latin1");
        collation[6] = new Collation((int)6, (String)"hp8_english_ci", (int)0, (String)MYSQL_CHARSET_NAME_hp8);
        collation[7] = new Collation((int)7, (String)"koi8r_general_ci", (int)0, (String)MYSQL_CHARSET_NAME_koi8r);
        collation[8] = new Collation((int)8, (String)"latin1_swedish_ci", (int)1, (String)"latin1");
        collation[9] = new Collation((int)9, (String)"latin2_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_latin2);
        collation[10] = new Collation((int)10, (String)"swe7_swedish_ci", (int)0, (String)MYSQL_CHARSET_NAME_swe7);
        collation[11] = new Collation((int)11, (String)"ascii_general_ci", (int)0, (String)MYSQL_CHARSET_NAME_ascii);
        collation[12] = new Collation((int)12, (String)"ujis_japanese_ci", (int)0, (String)MYSQL_CHARSET_NAME_ujis);
        collation[13] = new Collation((int)13, (String)"sjis_japanese_ci", (int)0, (String)MYSQL_CHARSET_NAME_sjis);
        collation[14] = new Collation((int)14, (String)"cp1251_bulgarian_ci", (int)0, (String)MYSQL_CHARSET_NAME_cp1251);
        collation[15] = new Collation((int)15, (String)"latin1_danish_ci", (int)0, (String)"latin1");
        collation[16] = new Collation((int)16, (String)"hebrew_general_ci", (int)0, (String)MYSQL_CHARSET_NAME_hebrew);
        collation[18] = new Collation((int)18, (String)"tis620_thai_ci", (int)0, (String)MYSQL_CHARSET_NAME_tis620);
        collation[19] = new Collation((int)19, (String)"euckr_korean_ci", (int)0, (String)MYSQL_CHARSET_NAME_euckr);
        collation[20] = new Collation((int)20, (String)"latin7_estonian_cs", (int)0, (String)MYSQL_CHARSET_NAME_latin7);
        collation[21] = new Collation((int)21, (String)"latin2_hungarian_ci", (int)0, (String)MYSQL_CHARSET_NAME_latin2);
        collation[22] = new Collation((int)22, (String)"koi8u_general_ci", (int)0, (String)MYSQL_CHARSET_NAME_koi8u);
        collation[23] = new Collation((int)23, (String)"cp1251_ukrainian_ci", (int)0, (String)MYSQL_CHARSET_NAME_cp1251);
        collation[24] = new Collation((int)24, (String)"gb2312_chinese_ci", (int)0, (String)MYSQL_CHARSET_NAME_gb2312);
        collation[25] = new Collation((int)25, (String)"greek_general_ci", (int)0, (String)MYSQL_CHARSET_NAME_greek);
        collation[26] = new Collation((int)26, (String)"cp1250_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_cp1250);
        collation[27] = new Collation((int)27, (String)"latin2_croatian_ci", (int)0, (String)MYSQL_CHARSET_NAME_latin2);
        collation[28] = new Collation((int)28, (String)"gbk_chinese_ci", (int)1, (String)MYSQL_CHARSET_NAME_gbk);
        collation[29] = new Collation((int)29, (String)"cp1257_lithuanian_ci", (int)0, (String)MYSQL_CHARSET_NAME_cp1257);
        collation[30] = new Collation((int)30, (String)"latin5_turkish_ci", (int)1, (String)MYSQL_CHARSET_NAME_latin5);
        collation[31] = new Collation((int)31, (String)"latin1_german2_ci", (int)0, (String)"latin1");
        collation[32] = new Collation((int)32, (String)"armscii8_general_ci", (int)0, (String)MYSQL_CHARSET_NAME_armscii8);
        collation[33] = new Collation((int)33, (String)"utf8_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_utf8);
        collation[34] = new Collation((int)34, (String)"cp1250_czech_cs", (int)0, (String)MYSQL_CHARSET_NAME_cp1250);
        collation[35] = new Collation((int)35, (String)"ucs2_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[36] = new Collation((int)36, (String)"cp866_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_cp866);
        collation[37] = new Collation((int)37, (String)"keybcs2_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_keybcs2);
        collation[38] = new Collation((int)38, (String)"macce_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_macce);
        collation[39] = new Collation((int)39, (String)"macroman_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_macroman);
        collation[40] = new Collation((int)40, (String)"cp852_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_cp852);
        collation[41] = new Collation((int)41, (String)"latin7_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_latin7);
        collation[42] = new Collation((int)42, (String)"latin7_general_cs", (int)0, (String)MYSQL_CHARSET_NAME_latin7);
        collation[43] = new Collation((int)43, (String)"macce_bin", (int)0, (String)MYSQL_CHARSET_NAME_macce);
        collation[44] = new Collation((int)44, (String)"cp1250_croatian_ci", (int)0, (String)MYSQL_CHARSET_NAME_cp1250);
        collation[45] = new Collation((int)45, (String)"utf8mb4_general_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[46] = new Collation((int)46, (String)"utf8mb4_bin", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[47] = new Collation((int)47, (String)"latin1_bin", (int)0, (String)"latin1");
        collation[48] = new Collation((int)48, (String)"latin1_general_ci", (int)0, (String)"latin1");
        collation[49] = new Collation((int)49, (String)"latin1_general_cs", (int)0, (String)"latin1");
        collation[50] = new Collation((int)50, (String)"cp1251_bin", (int)0, (String)MYSQL_CHARSET_NAME_cp1251);
        collation[51] = new Collation((int)51, (String)"cp1251_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_cp1251);
        collation[52] = new Collation((int)52, (String)"cp1251_general_cs", (int)0, (String)MYSQL_CHARSET_NAME_cp1251);
        collation[53] = new Collation((int)53, (String)"macroman_bin", (int)0, (String)MYSQL_CHARSET_NAME_macroman);
        collation[54] = new Collation((int)54, (String)"utf16_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_utf16);
        collation[55] = new Collation((int)55, (String)"utf16_bin", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[56] = new Collation((int)56, (String)"utf16le_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_utf16le);
        collation[57] = new Collation((int)57, (String)"cp1256_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_cp1256);
        collation[58] = new Collation((int)58, (String)"cp1257_bin", (int)0, (String)MYSQL_CHARSET_NAME_cp1257);
        collation[59] = new Collation((int)59, (String)"cp1257_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_cp1257);
        collation[60] = new Collation((int)60, (String)"utf32_general_ci", (int)1, (String)MYSQL_CHARSET_NAME_utf32);
        collation[61] = new Collation((int)61, (String)"utf32_bin", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[62] = new Collation((int)62, (String)"utf16le_bin", (int)0, (String)MYSQL_CHARSET_NAME_utf16le);
        collation[63] = new Collation((int)63, (String)MYSQL_CHARSET_NAME_binary, (int)1, (String)MYSQL_CHARSET_NAME_binary);
        collation[64] = new Collation((int)64, (String)"armscii8_bin", (int)0, (String)MYSQL_CHARSET_NAME_armscii8);
        collation[65] = new Collation((int)65, (String)"ascii_bin", (int)0, (String)MYSQL_CHARSET_NAME_ascii);
        collation[66] = new Collation((int)66, (String)"cp1250_bin", (int)0, (String)MYSQL_CHARSET_NAME_cp1250);
        collation[67] = new Collation((int)67, (String)"cp1256_bin", (int)0, (String)MYSQL_CHARSET_NAME_cp1256);
        collation[68] = new Collation((int)68, (String)"cp866_bin", (int)0, (String)MYSQL_CHARSET_NAME_cp866);
        collation[69] = new Collation((int)69, (String)"dec8_bin", (int)0, (String)MYSQL_CHARSET_NAME_dec8);
        collation[70] = new Collation((int)70, (String)"greek_bin", (int)0, (String)MYSQL_CHARSET_NAME_greek);
        collation[71] = new Collation((int)71, (String)"hebrew_bin", (int)0, (String)MYSQL_CHARSET_NAME_hebrew);
        collation[72] = new Collation((int)72, (String)"hp8_bin", (int)0, (String)MYSQL_CHARSET_NAME_hp8);
        collation[73] = new Collation((int)73, (String)"keybcs2_bin", (int)0, (String)MYSQL_CHARSET_NAME_keybcs2);
        collation[74] = new Collation((int)74, (String)"koi8r_bin", (int)0, (String)MYSQL_CHARSET_NAME_koi8r);
        collation[75] = new Collation((int)75, (String)"koi8u_bin", (int)0, (String)MYSQL_CHARSET_NAME_koi8u);
        collation[76] = new Collation((int)76, (String)"utf8_tolower_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[77] = new Collation((int)77, (String)"latin2_bin", (int)0, (String)MYSQL_CHARSET_NAME_latin2);
        collation[78] = new Collation((int)78, (String)"latin5_bin", (int)0, (String)MYSQL_CHARSET_NAME_latin5);
        collation[79] = new Collation((int)79, (String)"latin7_bin", (int)0, (String)MYSQL_CHARSET_NAME_latin7);
        collation[80] = new Collation((int)80, (String)"cp850_bin", (int)0, (String)MYSQL_CHARSET_NAME_cp850);
        collation[81] = new Collation((int)81, (String)"cp852_bin", (int)0, (String)MYSQL_CHARSET_NAME_cp852);
        collation[82] = new Collation((int)82, (String)"swe7_bin", (int)0, (String)MYSQL_CHARSET_NAME_swe7);
        collation[83] = new Collation((int)83, (String)"utf8_bin", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[84] = new Collation((int)84, (String)"big5_bin", (int)0, (String)MYSQL_CHARSET_NAME_big5);
        collation[85] = new Collation((int)85, (String)"euckr_bin", (int)0, (String)MYSQL_CHARSET_NAME_euckr);
        collation[86] = new Collation((int)86, (String)"gb2312_bin", (int)0, (String)MYSQL_CHARSET_NAME_gb2312);
        collation[87] = new Collation((int)87, (String)"gbk_bin", (int)0, (String)MYSQL_CHARSET_NAME_gbk);
        collation[88] = new Collation((int)88, (String)"sjis_bin", (int)0, (String)MYSQL_CHARSET_NAME_sjis);
        collation[89] = new Collation((int)89, (String)"tis620_bin", (int)0, (String)MYSQL_CHARSET_NAME_tis620);
        collation[90] = new Collation((int)90, (String)"ucs2_bin", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[91] = new Collation((int)91, (String)"ujis_bin", (int)0, (String)MYSQL_CHARSET_NAME_ujis);
        collation[92] = new Collation((int)92, (String)"geostd8_general_ci", (int)0, (String)MYSQL_CHARSET_NAME_geostd8);
        collation[93] = new Collation((int)93, (String)"geostd8_bin", (int)0, (String)MYSQL_CHARSET_NAME_geostd8);
        collation[94] = new Collation((int)94, (String)"latin1_spanish_ci", (int)0, (String)"latin1");
        collation[95] = new Collation((int)95, (String)"cp932_japanese_ci", (int)1, (String)MYSQL_CHARSET_NAME_cp932);
        collation[96] = new Collation((int)96, (String)"cp932_bin", (int)0, (String)MYSQL_CHARSET_NAME_cp932);
        collation[97] = new Collation((int)97, (String)"eucjpms_japanese_ci", (int)1, (String)MYSQL_CHARSET_NAME_eucjpms);
        collation[98] = new Collation((int)98, (String)"eucjpms_bin", (int)0, (String)MYSQL_CHARSET_NAME_eucjpms);
        collation[99] = new Collation((int)99, (String)"cp1250_polish_ci", (int)0, (String)MYSQL_CHARSET_NAME_cp1250);
        collation[101] = new Collation((int)101, (String)"utf16_unicode_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[102] = new Collation((int)102, (String)"utf16_icelandic_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[103] = new Collation((int)103, (String)"utf16_latvian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[104] = new Collation((int)104, (String)"utf16_romanian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[105] = new Collation((int)105, (String)"utf16_slovenian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[106] = new Collation((int)106, (String)"utf16_polish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[107] = new Collation((int)107, (String)"utf16_estonian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[108] = new Collation((int)108, (String)"utf16_spanish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[109] = new Collation((int)109, (String)"utf16_swedish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[110] = new Collation((int)110, (String)"utf16_turkish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[111] = new Collation((int)111, (String)"utf16_czech_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[112] = new Collation((int)112, (String)"utf16_danish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[113] = new Collation((int)113, (String)"utf16_lithuanian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[114] = new Collation((int)114, (String)"utf16_slovak_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[115] = new Collation((int)115, (String)"utf16_spanish2_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[116] = new Collation((int)116, (String)"utf16_roman_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[117] = new Collation((int)117, (String)"utf16_persian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[118] = new Collation((int)118, (String)"utf16_esperanto_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[119] = new Collation((int)119, (String)"utf16_hungarian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[120] = new Collation((int)120, (String)"utf16_sinhala_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[121] = new Collation((int)121, (String)"utf16_german2_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[122] = new Collation((int)122, (String)"utf16_croatian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[123] = new Collation((int)123, (String)"utf16_unicode_520_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[124] = new Collation((int)124, (String)"utf16_vietnamese_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[128] = new Collation((int)128, (String)"ucs2_unicode_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[129] = new Collation((int)129, (String)"ucs2_icelandic_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[130] = new Collation((int)130, (String)"ucs2_latvian_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[131] = new Collation((int)131, (String)"ucs2_romanian_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[132] = new Collation((int)132, (String)"ucs2_slovenian_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[133] = new Collation((int)133, (String)"ucs2_polish_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[134] = new Collation((int)134, (String)"ucs2_estonian_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[135] = new Collation((int)135, (String)"ucs2_spanish_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[136] = new Collation((int)136, (String)"ucs2_swedish_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[137] = new Collation((int)137, (String)"ucs2_turkish_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[138] = new Collation((int)138, (String)"ucs2_czech_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[139] = new Collation((int)139, (String)"ucs2_danish_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[140] = new Collation((int)140, (String)"ucs2_lithuanian_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[141] = new Collation((int)141, (String)"ucs2_slovak_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[142] = new Collation((int)142, (String)"ucs2_spanish2_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[143] = new Collation((int)143, (String)"ucs2_roman_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[144] = new Collation((int)144, (String)"ucs2_persian_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[145] = new Collation((int)145, (String)"ucs2_esperanto_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[146] = new Collation((int)146, (String)"ucs2_hungarian_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[147] = new Collation((int)147, (String)"ucs2_sinhala_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[148] = new Collation((int)148, (String)"ucs2_german2_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[149] = new Collation((int)149, (String)"ucs2_croatian_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[150] = new Collation((int)150, (String)"ucs2_unicode_520_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[151] = new Collation((int)151, (String)"ucs2_vietnamese_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[159] = new Collation((int)159, (String)"ucs2_general_mysql500_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[160] = new Collation((int)160, (String)"utf32_unicode_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[161] = new Collation((int)161, (String)"utf32_icelandic_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[162] = new Collation((int)162, (String)"utf32_latvian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[163] = new Collation((int)163, (String)"utf32_romanian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[164] = new Collation((int)164, (String)"utf32_slovenian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[165] = new Collation((int)165, (String)"utf32_polish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[166] = new Collation((int)166, (String)"utf32_estonian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[167] = new Collation((int)167, (String)"utf32_spanish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[168] = new Collation((int)168, (String)"utf32_swedish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[169] = new Collation((int)169, (String)"utf32_turkish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[170] = new Collation((int)170, (String)"utf32_czech_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[171] = new Collation((int)171, (String)"utf32_danish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[172] = new Collation((int)172, (String)"utf32_lithuanian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[173] = new Collation((int)173, (String)"utf32_slovak_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[174] = new Collation((int)174, (String)"utf32_spanish2_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[175] = new Collation((int)175, (String)"utf32_roman_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[176] = new Collation((int)176, (String)"utf32_persian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[177] = new Collation((int)177, (String)"utf32_esperanto_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[178] = new Collation((int)178, (String)"utf32_hungarian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[179] = new Collation((int)179, (String)"utf32_sinhala_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[180] = new Collation((int)180, (String)"utf32_german2_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[181] = new Collation((int)181, (String)"utf32_croatian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[182] = new Collation((int)182, (String)"utf32_unicode_520_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[183] = new Collation((int)183, (String)"utf32_vietnamese_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[192] = new Collation((int)192, (String)"utf8_unicode_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[193] = new Collation((int)193, (String)"utf8_icelandic_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[194] = new Collation((int)194, (String)"utf8_latvian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[195] = new Collation((int)195, (String)"utf8_romanian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[196] = new Collation((int)196, (String)"utf8_slovenian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[197] = new Collation((int)197, (String)"utf8_polish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[198] = new Collation((int)198, (String)"utf8_estonian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[199] = new Collation((int)199, (String)"utf8_spanish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[200] = new Collation((int)200, (String)"utf8_swedish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[201] = new Collation((int)201, (String)"utf8_turkish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[202] = new Collation((int)202, (String)"utf8_czech_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[203] = new Collation((int)203, (String)"utf8_danish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[204] = new Collation((int)204, (String)"utf8_lithuanian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[205] = new Collation((int)205, (String)"utf8_slovak_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[206] = new Collation((int)206, (String)"utf8_spanish2_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[207] = new Collation((int)207, (String)"utf8_roman_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[208] = new Collation((int)208, (String)"utf8_persian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[209] = new Collation((int)209, (String)"utf8_esperanto_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[210] = new Collation((int)210, (String)"utf8_hungarian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[211] = new Collation((int)211, (String)"utf8_sinhala_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[212] = new Collation((int)212, (String)"utf8_german2_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[213] = new Collation((int)213, (String)"utf8_croatian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[214] = new Collation((int)214, (String)"utf8_unicode_520_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[215] = new Collation((int)215, (String)"utf8_vietnamese_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[223] = new Collation((int)223, (String)"utf8_general_mysql500_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[224] = new Collation((int)224, (String)"utf8mb4_unicode_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[225] = new Collation((int)225, (String)"utf8mb4_icelandic_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[226] = new Collation((int)226, (String)"utf8mb4_latvian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[227] = new Collation((int)227, (String)"utf8mb4_romanian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[228] = new Collation((int)228, (String)"utf8mb4_slovenian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[229] = new Collation((int)229, (String)"utf8mb4_polish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[230] = new Collation((int)230, (String)"utf8mb4_estonian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[231] = new Collation((int)231, (String)"utf8mb4_spanish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[232] = new Collation((int)232, (String)"utf8mb4_swedish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[233] = new Collation((int)233, (String)"utf8mb4_turkish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[234] = new Collation((int)234, (String)"utf8mb4_czech_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[235] = new Collation((int)235, (String)"utf8mb4_danish_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[236] = new Collation((int)236, (String)"utf8mb4_lithuanian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[237] = new Collation((int)237, (String)"utf8mb4_slovak_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[238] = new Collation((int)238, (String)"utf8mb4_spanish2_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[239] = new Collation((int)239, (String)"utf8mb4_roman_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[240] = new Collation((int)240, (String)"utf8mb4_persian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[241] = new Collation((int)241, (String)"utf8mb4_esperanto_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[242] = new Collation((int)242, (String)"utf8mb4_hungarian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[243] = new Collation((int)243, (String)"utf8mb4_sinhala_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[244] = new Collation((int)244, (String)"utf8mb4_german2_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[245] = new Collation((int)245, (String)"utf8mb4_croatian_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[246] = new Collation((int)246, (String)"utf8mb4_unicode_520_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[247] = new Collation((int)247, (String)"utf8mb4_vietnamese_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[248] = new Collation((int)248, (String)"gb18030_chinese_ci", (int)1, (String)MYSQL_CHARSET_NAME_gb18030);
        collation[249] = new Collation((int)249, (String)"gb18030_bin", (int)0, (String)MYSQL_CHARSET_NAME_gb18030);
        collation[250] = new Collation((int)250, (String)"gb18030_unicode_520_ci", (int)0, (String)MYSQL_CHARSET_NAME_gb18030);
        collation[255] = new Collation((int)255, (String)"utf8mb4_0900_ai_ci", (int)1, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[256] = new Collation((int)256, (String)"utf8mb4_de_pb_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[257] = new Collation((int)257, (String)"utf8mb4_is_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[258] = new Collation((int)258, (String)"utf8mb4_lv_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[259] = new Collation((int)259, (String)"utf8mb4_ro_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[260] = new Collation((int)260, (String)"utf8mb4_sl_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[261] = new Collation((int)261, (String)"utf8mb4_pl_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[262] = new Collation((int)262, (String)"utf8mb4_et_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[263] = new Collation((int)263, (String)"utf8mb4_es_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[264] = new Collation((int)264, (String)"utf8mb4_sv_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[265] = new Collation((int)265, (String)"utf8mb4_tr_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[266] = new Collation((int)266, (String)"utf8mb4_cs_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[267] = new Collation((int)267, (String)"utf8mb4_da_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[268] = new Collation((int)268, (String)"utf8mb4_lt_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[269] = new Collation((int)269, (String)"utf8mb4_sk_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[270] = new Collation((int)270, (String)"utf8mb4_es_trad_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[271] = new Collation((int)271, (String)"utf8mb4_la_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[273] = new Collation((int)273, (String)"utf8mb4_eo_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[274] = new Collation((int)274, (String)"utf8mb4_hu_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[275] = new Collation((int)275, (String)"utf8mb4_hr_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[277] = new Collation((int)277, (String)"utf8mb4_vi_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[278] = new Collation((int)278, (String)"utf8mb4_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[279] = new Collation((int)279, (String)"utf8mb4_de_pb_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[280] = new Collation((int)280, (String)"utf8mb4_is_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[281] = new Collation((int)281, (String)"utf8mb4_lv_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[282] = new Collation((int)282, (String)"utf8mb4_ro_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[283] = new Collation((int)283, (String)"utf8mb4_sl_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[284] = new Collation((int)284, (String)"utf8mb4_pl_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[285] = new Collation((int)285, (String)"utf8mb4_et_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[286] = new Collation((int)286, (String)"utf8mb4_es_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[287] = new Collation((int)287, (String)"utf8mb4_sv_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[288] = new Collation((int)288, (String)"utf8mb4_tr_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[289] = new Collation((int)289, (String)"utf8mb4_cs_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[290] = new Collation((int)290, (String)"utf8mb4_da_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[291] = new Collation((int)291, (String)"utf8mb4_lt_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[292] = new Collation((int)292, (String)"utf8mb4_sk_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[293] = new Collation((int)293, (String)"utf8mb4_es_trad_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[294] = new Collation((int)294, (String)"utf8mb4_la_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[296] = new Collation((int)296, (String)"utf8mb4_eo_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[297] = new Collation((int)297, (String)"utf8mb4_hu_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[298] = new Collation((int)298, (String)"utf8mb4_hr_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[300] = new Collation((int)300, (String)"utf8mb4_vi_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[303] = new Collation((int)303, (String)"utf8mb4_ja_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[304] = new Collation((int)304, (String)"utf8mb4_ja_0900_as_cs_ks", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[305] = new Collation((int)305, (String)"utf8mb4_0900_as_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[306] = new Collation((int)306, (String)"utf8mb4_ru_0900_ai_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[307] = new Collation((int)307, (String)"utf8mb4_ru_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[308] = new Collation((int)308, (String)"utf8mb4_zh_0900_as_cs", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[309] = new Collation((int)309, (String)"utf8mb4_0900_bin", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[326] = new Collation((int)326, (String)"utf8mb4_test_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[327] = new Collation((int)327, (String)"utf16_test_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf16);
        collation[328] = new Collation((int)328, (String)"utf8mb4_test_400_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8mb4);
        collation[336] = new Collation((int)336, (String)"utf8_bengali_standard_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[337] = new Collation((int)337, (String)"utf8_bengali_traditional_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[352] = new Collation((int)352, (String)"utf8_phone_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[353] = new Collation((int)353, (String)"utf8_test_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[354] = new Collation((int)354, (String)"utf8_5624_1", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[355] = new Collation((int)355, (String)"utf8_5624_2", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[356] = new Collation((int)356, (String)"utf8_5624_3", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[357] = new Collation((int)357, (String)"utf8_5624_4", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[358] = new Collation((int)358, (String)"ucs2_test_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[359] = new Collation((int)359, (String)"ucs2_vn_ci", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[360] = new Collation((int)360, (String)"ucs2_5624_1", (int)0, (String)MYSQL_CHARSET_NAME_ucs2);
        collation[368] = new Collation((int)368, (String)"utf8_5624_5", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        collation[391] = new Collation((int)391, (String)"utf32_test_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf32);
        collation[2047] = new Collation((int)2047, (String)"utf8_maxuserid_ci", (int)0, (String)MYSQL_CHARSET_NAME_utf8);
        COLLATION_INDEX_TO_COLLATION_NAME = new String[2048];
        COLLATION_INDEX_TO_CHARSET = new MysqlCharset[2048];
        TreeMap<String, Integer> charsetNameToCollationIndexMap = new TreeMap<String, Integer>();
        TreeMap<String, Integer> charsetNameToCollationPriorityMap = new TreeMap<String, Integer>();
        HashSet<Integer> tempUTF8MB4Indexes = new HashSet<Integer>();
        Collation notUsedCollation = new Collation((int)0, (String)COLLATION_NOT_DEFINED, (int)0, (String)"latin1");
        int i = 1;
        do {
            if (i >= 2048) {
                CHARSET_NAME_TO_COLLATION_INDEX = Collections.unmodifiableMap(charsetNameToCollationIndexMap);
                UTF8MB4_INDEXES = Collections.unmodifiableSet(tempUTF8MB4Indexes);
                HashMap<String, String> tempMap = new HashMap<String, String>();
                tempMap.put(MYSQL_4_0_CHARSET_NAME_czech, MYSQL_CHARSET_NAME_latin2);
                tempMap.put(MYSQL_4_0_CHARSET_NAME_danish, "latin1");
                tempMap.put("dutch", "latin1");
                tempMap.put("english", "latin1");
                tempMap.put("estonian", MYSQL_CHARSET_NAME_latin7);
                tempMap.put("french", "latin1");
                tempMap.put("german", "latin1");
                tempMap.put(MYSQL_CHARSET_NAME_greek, MYSQL_CHARSET_NAME_greek);
                tempMap.put(MYSQL_4_0_CHARSET_NAME_hungarian, MYSQL_CHARSET_NAME_latin2);
                tempMap.put("italian", "latin1");
                tempMap.put("japanese", MYSQL_CHARSET_NAME_ujis);
                tempMap.put("japanese-sjis", MYSQL_CHARSET_NAME_sjis);
                tempMap.put("korean", MYSQL_CHARSET_NAME_euckr);
                tempMap.put("norwegian", "latin1");
                tempMap.put("norwegian-ny", "latin1");
                tempMap.put("polish", MYSQL_CHARSET_NAME_latin2);
                tempMap.put("portuguese", "latin1");
                tempMap.put("romanian", MYSQL_CHARSET_NAME_latin2);
                tempMap.put("russian", MYSQL_CHARSET_NAME_koi8r);
                tempMap.put("serbian", MYSQL_CHARSET_NAME_cp1250);
                tempMap.put("slovak", MYSQL_CHARSET_NAME_latin2);
                tempMap.put("spanish", "latin1");
                tempMap.put("swedish", "latin1");
                tempMap.put("ukrainian", MYSQL_CHARSET_NAME_koi8u);
                ERROR_MESSAGE_FILE_TO_MYSQL_CHARSET = Collections.unmodifiableMap(tempMap);
                return;
            }
            Collation coll = collation[i] != null ? collation[i] : notUsedCollation;
            CharsetMapping.COLLATION_INDEX_TO_COLLATION_NAME[i] = coll.collationName;
            CharsetMapping.COLLATION_INDEX_TO_CHARSET[i] = coll.mysqlCharset;
            String charsetName = coll.mysqlCharset.charsetName;
            if (!charsetNameToCollationIndexMap.containsKey((Object)charsetName) || ((Integer)charsetNameToCollationPriorityMap.get((Object)charsetName)).intValue() < coll.priority) {
                charsetNameToCollationIndexMap.put(charsetName, Integer.valueOf((int)i));
                charsetNameToCollationPriorityMap.put(charsetName, Integer.valueOf((int)coll.priority));
            }
            if (charsetName.equals((Object)MYSQL_CHARSET_NAME_utf8mb4)) {
                tempUTF8MB4Indexes.add(Integer.valueOf((int)i));
            }
            ++i;
        } while (true);
    }
}

