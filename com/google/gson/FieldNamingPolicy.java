/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.google.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;

public enum FieldNamingPolicy implements FieldNamingStrategy
{
    IDENTITY{

        public String translateName(java.lang.reflect.Field f) {
            return f.getName();
        }
    }
    ,
    UPPER_CAMEL_CASE{

        public String translateName(java.lang.reflect.Field f) {
            return 2.upperCaseFirstLetter((String)f.getName());
        }
    }
    ,
    UPPER_CAMEL_CASE_WITH_SPACES{

        public String translateName(java.lang.reflect.Field f) {
            return 3.upperCaseFirstLetter((String)3.separateCamelCase((String)f.getName(), (String)" "));
        }
    }
    ,
    LOWER_CASE_WITH_UNDERSCORES{

        public String translateName(java.lang.reflect.Field f) {
            return 4.separateCamelCase((String)f.getName(), (String)"_").toLowerCase((java.util.Locale)java.util.Locale.ENGLISH);
        }
    }
    ,
    LOWER_CASE_WITH_DASHES{

        public String translateName(java.lang.reflect.Field f) {
            return 5.separateCamelCase((String)f.getName(), (String)"-").toLowerCase((java.util.Locale)java.util.Locale.ENGLISH);
        }
    };
    

    static String separateCamelCase(String name, String separator) {
        StringBuilder translation = new StringBuilder();
        int i = 0;
        while (i < name.length()) {
            char character = name.charAt((int)i);
            if (Character.isUpperCase((char)character) && translation.length() != 0) {
                translation.append((String)separator);
            }
            translation.append((char)character);
            ++i;
        }
        return translation.toString();
    }

    static String upperCaseFirstLetter(String name) {
        StringBuilder fieldNameBuilder = new StringBuilder();
        int index = 0;
        char firstCharacter = name.charAt((int)index);
        while (index < name.length() - 1 && !Character.isLetter((char)firstCharacter)) {
            fieldNameBuilder.append((char)firstCharacter);
            firstCharacter = name.charAt((int)(++index));
        }
        if (index == name.length()) {
            return fieldNameBuilder.toString();
        }
        if (Character.isUpperCase((char)firstCharacter)) return name;
        String modifiedTarget = FieldNamingPolicy.modifyString((char)Character.toUpperCase((char)firstCharacter), (String)name, (int)(++index));
        return fieldNameBuilder.append((String)modifiedTarget).toString();
    }

    private static String modifyString(char firstCharacter, String srcString, int indexOfSubstring) {
        String string;
        if (indexOfSubstring < srcString.length()) {
            string = firstCharacter + srcString.substring((int)indexOfSubstring);
            return string;
        }
        string = String.valueOf((char)firstCharacter);
        return string;
    }
}

