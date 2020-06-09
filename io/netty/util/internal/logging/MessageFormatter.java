/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.FormattingTuple;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

final class MessageFormatter {
    private static final String DELIM_STR = "{}";
    private static final char ESCAPE_CHAR = '\\';

    static FormattingTuple format(String messagePattern, Object arg) {
        return MessageFormatter.arrayFormat((String)messagePattern, (Object[])new Object[]{arg});
    }

    static FormattingTuple format(String messagePattern, Object argA, Object argB) {
        return MessageFormatter.arrayFormat((String)messagePattern, (Object[])new Object[]{argA, argB});
    }

    static FormattingTuple arrayFormat(String messagePattern, Object[] argArray) {
        Throwable throwable;
        Throwable throwable2;
        if (argArray == null) return new FormattingTuple((String)messagePattern, null);
        if (argArray.length == 0) {
            return new FormattingTuple((String)messagePattern, null);
        }
        int lastArrIdx = argArray.length - 1;
        Object lastEntry = argArray[lastArrIdx];
        Throwable throwable3 = throwable2 = lastEntry instanceof Throwable ? (Throwable)lastEntry : null;
        if (messagePattern == null) {
            return new FormattingTuple(null, (Throwable)throwable2);
        }
        int j = messagePattern.indexOf((String)DELIM_STR);
        if (j == -1) {
            return new FormattingTuple((String)messagePattern, (Throwable)throwable2);
        }
        StringBuilder sbuf = new StringBuilder((int)(messagePattern.length() + 50));
        int i = 0;
        int L = 0;
        do {
            boolean notEscaped;
            boolean bl = notEscaped = j == 0 || messagePattern.charAt((int)(j - 1)) != '\\';
            if (notEscaped) {
                sbuf.append((CharSequence)messagePattern, (int)i, (int)j);
            } else {
                sbuf.append((CharSequence)messagePattern, (int)i, (int)(j - 1));
                notEscaped = j >= 2 && messagePattern.charAt((int)(j - 2)) == '\\';
            }
            i = j + 2;
            if (notEscaped) {
                MessageFormatter.deeplyAppendParameter((StringBuilder)sbuf, (Object)argArray[L], null);
                if (++L <= lastArrIdx) continue;
                break;
            }
            sbuf.append((String)DELIM_STR);
        } while ((j = messagePattern.indexOf((String)DELIM_STR, (int)i)) != -1);
        sbuf.append((CharSequence)messagePattern, (int)i, (int)messagePattern.length());
        if (L <= lastArrIdx) {
            throwable = throwable2;
            return new FormattingTuple((String)sbuf.toString(), (Throwable)throwable);
        }
        throwable = null;
        return new FormattingTuple((String)sbuf.toString(), (Throwable)throwable);
    }

    private static void deeplyAppendParameter(StringBuilder sbuf, Object o, Set<Object[]> seenSet) {
        if (o == null) {
            sbuf.append((String)"null");
            return;
        }
        Class<?> objClass = o.getClass();
        if (!objClass.isArray()) {
            if (!Number.class.isAssignableFrom(objClass)) {
                MessageFormatter.safeObjectAppend((StringBuilder)sbuf, (Object)o);
                return;
            }
            if (objClass == Long.class) {
                sbuf.append((long)((Long)o).longValue());
                return;
            }
            if (objClass == Integer.class || objClass == Short.class || objClass == Byte.class) {
                sbuf.append((int)((Number)o).intValue());
                return;
            }
            if (objClass == Double.class) {
                sbuf.append((double)((Double)o).doubleValue());
                return;
            }
            if (objClass == Float.class) {
                sbuf.append((float)((Float)o).floatValue());
                return;
            }
            MessageFormatter.safeObjectAppend((StringBuilder)sbuf, (Object)o);
            return;
        }
        sbuf.append((char)'[');
        if (objClass == boolean[].class) {
            MessageFormatter.booleanArrayAppend((StringBuilder)sbuf, (boolean[])((boolean[])o));
        } else if (objClass == byte[].class) {
            MessageFormatter.byteArrayAppend((StringBuilder)sbuf, (byte[])((byte[])o));
        } else if (objClass == char[].class) {
            MessageFormatter.charArrayAppend((StringBuilder)sbuf, (char[])((char[])o));
        } else if (objClass == short[].class) {
            MessageFormatter.shortArrayAppend((StringBuilder)sbuf, (short[])((short[])o));
        } else if (objClass == int[].class) {
            MessageFormatter.intArrayAppend((StringBuilder)sbuf, (int[])((int[])o));
        } else if (objClass == long[].class) {
            MessageFormatter.longArrayAppend((StringBuilder)sbuf, (long[])((long[])o));
        } else if (objClass == float[].class) {
            MessageFormatter.floatArrayAppend((StringBuilder)sbuf, (float[])((float[])o));
        } else if (objClass == double[].class) {
            MessageFormatter.doubleArrayAppend((StringBuilder)sbuf, (double[])((double[])o));
        } else {
            MessageFormatter.objectArrayAppend((StringBuilder)sbuf, (Object[])((Object[])o), seenSet);
        }
        sbuf.append((char)']');
    }

    private static void safeObjectAppend(StringBuilder sbuf, Object o) {
        try {
            String oAsString = o.toString();
            sbuf.append((String)oAsString);
            return;
        }
        catch (Throwable t) {
            System.err.println((String)("SLF4J: Failed toString() invocation on an object of type [" + o.getClass().getName() + ']'));
            t.printStackTrace();
            sbuf.append((String)"[FAILED toString()]");
        }
    }

    private static void objectArrayAppend(StringBuilder sbuf, Object[] a, Set<Object[]> seenSet) {
        if (a.length == 0) {
            return;
        }
        if (seenSet == null) {
            seenSet = new HashSet<Object[]>((int)a.length);
        }
        if (!seenSet.add((Object[])a)) {
            sbuf.append((String)"...");
            return;
        }
        MessageFormatter.deeplyAppendParameter((StringBuilder)sbuf, (Object)a[0], seenSet);
        int i = 1;
        do {
            if (i >= a.length) {
                seenSet.remove((Object)a);
                return;
            }
            sbuf.append((String)", ");
            MessageFormatter.deeplyAppendParameter((StringBuilder)sbuf, (Object)a[i], seenSet);
            ++i;
        } while (true);
    }

    private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append((boolean)a[0]);
        int i = 1;
        while (i < a.length) {
            sbuf.append((String)", ");
            sbuf.append((boolean)a[i]);
            ++i;
        }
    }

    private static void byteArrayAppend(StringBuilder sbuf, byte[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append((int)a[0]);
        int i = 1;
        while (i < a.length) {
            sbuf.append((String)", ");
            sbuf.append((int)a[i]);
            ++i;
        }
    }

    private static void charArrayAppend(StringBuilder sbuf, char[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append((char)a[0]);
        int i = 1;
        while (i < a.length) {
            sbuf.append((String)", ");
            sbuf.append((char)a[i]);
            ++i;
        }
    }

    private static void shortArrayAppend(StringBuilder sbuf, short[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append((int)a[0]);
        int i = 1;
        while (i < a.length) {
            sbuf.append((String)", ");
            sbuf.append((int)a[i]);
            ++i;
        }
    }

    private static void intArrayAppend(StringBuilder sbuf, int[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append((int)a[0]);
        int i = 1;
        while (i < a.length) {
            sbuf.append((String)", ");
            sbuf.append((int)a[i]);
            ++i;
        }
    }

    private static void longArrayAppend(StringBuilder sbuf, long[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append((long)a[0]);
        int i = 1;
        while (i < a.length) {
            sbuf.append((String)", ");
            sbuf.append((long)a[i]);
            ++i;
        }
    }

    private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append((float)a[0]);
        int i = 1;
        while (i < a.length) {
            sbuf.append((String)", ");
            sbuf.append((float)a[i]);
            ++i;
        }
    }

    private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append((double)a[0]);
        int i = 1;
        while (i < a.length) {
            sbuf.append((String)", ");
            sbuf.append((double)a[i]);
            ++i;
        }
    }

    private MessageFormatter() {
    }
}

