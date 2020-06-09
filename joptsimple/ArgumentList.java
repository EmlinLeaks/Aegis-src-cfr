/*
 * Decompiled with CFR <Could not determine version>.
 */
package joptsimple;

class ArgumentList {
    private final String[] arguments;
    private int currentIndex;

    ArgumentList(String ... arguments) {
        this.arguments = (String[])arguments.clone();
    }

    boolean hasMore() {
        if (this.currentIndex >= this.arguments.length) return false;
        return true;
    }

    String next() {
        return this.arguments[this.currentIndex++];
    }

    String peek() {
        return this.arguments[this.currentIndex];
    }

    void treatNextAsLongOption() {
        if ('-' == this.arguments[this.currentIndex].charAt((int)0)) return;
        this.arguments[this.currentIndex] = "--" + this.arguments[this.currentIndex];
    }
}

