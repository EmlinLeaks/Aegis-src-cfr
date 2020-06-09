/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.mysql.fabric.xmlrpc.base;

import com.mysql.fabric.xmlrpc.base.Array;
import com.mysql.fabric.xmlrpc.base.Struct;
import java.util.Arrays;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class Value {
    public static final byte TYPE_i4 = 0;
    public static final byte TYPE_int = 1;
    public static final byte TYPE_boolean = 2;
    public static final byte TYPE_string = 3;
    public static final byte TYPE_double = 4;
    public static final byte TYPE_dateTime_iso8601 = 5;
    public static final byte TYPE_base64 = 6;
    public static final byte TYPE_struct = 7;
    public static final byte TYPE_array = 8;
    protected Object objValue = "";
    protected byte objType = (byte)3;
    private DatatypeFactory dtf = null;

    public Value() {
    }

    public Value(int value) {
        this.setInt((int)value);
    }

    public Value(String value) {
        this.setString((String)value);
    }

    public Value(boolean value) {
        this.setBoolean((boolean)value);
    }

    public Value(double value) {
        this.setDouble((double)value);
    }

    public Value(GregorianCalendar value) throws DatatypeConfigurationException {
        this.setDateTime((GregorianCalendar)value);
    }

    public Value(byte[] value) {
        this.setBase64((byte[])value);
    }

    public Value(Struct value) {
        this.setStruct((Struct)value);
    }

    public Value(Array value) {
        this.setArray((Array)value);
    }

    public Object getValue() {
        return this.objValue;
    }

    public byte getType() {
        return this.objType;
    }

    public void setInt(int value) {
        this.objValue = Integer.valueOf((int)value);
        this.objType = 1;
    }

    public void setInt(String value) {
        this.objValue = Integer.valueOf((String)value);
        this.objType = 1;
    }

    public void setString(String value) {
        this.objValue = value;
        this.objType = (byte)3;
    }

    public void appendString(String value) {
        this.objValue = this.objValue != null ? this.objValue + value : value;
        this.objType = (byte)3;
    }

    public void setBoolean(boolean value) {
        this.objValue = Boolean.valueOf((boolean)value);
        this.objType = (byte)2;
    }

    public void setBoolean(String value) {
        this.objValue = value.trim().equals((Object)"1") || value.trim().equalsIgnoreCase((String)"true") ? Boolean.valueOf((boolean)true) : Boolean.valueOf((boolean)false);
        this.objType = (byte)2;
    }

    public void setDouble(double value) {
        this.objValue = Double.valueOf((double)value);
        this.objType = (byte)4;
    }

    public void setDouble(String value) {
        this.objValue = Double.valueOf((String)value);
        this.objType = (byte)4;
    }

    public void setDateTime(GregorianCalendar value) throws DatatypeConfigurationException {
        if (this.dtf == null) {
            this.dtf = DatatypeFactory.newInstance();
        }
        this.objValue = this.dtf.newXMLGregorianCalendar((GregorianCalendar)value);
        this.objType = (byte)5;
    }

    public void setDateTime(String value) throws DatatypeConfigurationException {
        if (this.dtf == null) {
            this.dtf = DatatypeFactory.newInstance();
        }
        this.objValue = this.dtf.newXMLGregorianCalendar((String)value);
        this.objType = (byte)5;
    }

    public void setBase64(byte[] value) {
        this.objValue = value;
        this.objType = (byte)6;
    }

    public void setStruct(Struct value) {
        this.objValue = value;
        this.objType = (byte)7;
    }

    public void setArray(Array value) {
        this.objValue = value;
        this.objType = (byte)8;
    }

    /*
     * Unable to fully structure code
     */
    public String toString() {
        sb = new StringBuilder((String)"<value>");
        switch (this.objType) {
            case 0: {
                sb.append((String)("<i4>" + ((Integer)this.objValue).toString() + "</i4>"));
                ** break;
            }
            case 1: {
                sb.append((String)("<int>" + ((Integer)this.objValue).toString() + "</int>"));
                ** break;
            }
            case 2: {
                sb.append((String)("<boolean>" + (((Boolean)this.objValue).booleanValue() != false ? 1 : 0) + "</boolean>"));
                ** break;
            }
            case 4: {
                sb.append((String)("<double>" + ((Double)this.objValue).toString() + "</double>"));
                ** break;
            }
            case 5: {
                sb.append((String)("<dateTime.iso8601>" + ((XMLGregorianCalendar)this.objValue).toString() + "</dateTime.iso8601>"));
                ** break;
            }
            case 6: {
                sb.append((String)("<base64>" + Arrays.toString((byte[])((byte[])this.objValue)) + "</base64>"));
                ** break;
            }
            case 7: {
                sb.append((String)((Struct)this.objValue).toString());
                ** break;
            }
            case 8: {
                sb.append((String)((Array)this.objValue).toString());
                ** break;
            }
        }
        sb.append((String)("<string>" + this.escapeXMLChars((String)this.objValue.toString()) + "</string>"));
lbl37: // 9 sources:
        sb.append((String)"</value>");
        return sb.toString();
    }

    /*
     * Unable to fully structure code
     */
    private String escapeXMLChars(String s) {
        sb = new StringBuilder((int)s.length());
        i = 0;
        while (i < s.length()) {
            c = s.charAt((int)i);
            switch (c) {
                case '&': {
                    sb.append((String)"&amp;");
                    ** break;
                }
                case '<': {
                    sb.append((String)"&lt;");
                    ** break;
                }
                case '>': {
                    sb.append((String)"&gt;");
                    ** break;
                }
            }
            sb.append((char)c);
lbl20: // 4 sources:
            ++i;
        }
        return sb.toString();
    }
}

