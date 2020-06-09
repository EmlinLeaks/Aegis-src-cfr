/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.flowpowered.nbt.exception;

import com.flowpowered.nbt.Tag;
import java.io.PrintStream;

public class InvalidTagException
extends Exception {
    public InvalidTagException(Tag t) {
        System.out.println((String)("Invalid tag: " + t.toString() + " encountered!"));
    }
}

