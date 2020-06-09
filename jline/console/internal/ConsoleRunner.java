/*
 * Decompiled with CFR <Could not determine version>.
 */
package jline.console.internal;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.history.FileHistory;
import jline.console.history.History;
import jline.console.internal.ConsoleReaderInputStream;
import jline.internal.Configuration;

public class ConsoleRunner {
    public static final String property = "jline.history";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) throws Exception {
        ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
        if (argList.size() == 0) {
            ConsoleRunner.usage();
            return;
        }
        String historyFileName = System.getProperty((String)property, null);
        String mainClass = (String)argList.remove((int)0);
        ConsoleReader reader = new ConsoleReader();
        if (historyFileName != null) {
            reader.setHistory((History)new FileHistory((File)new File((File)Configuration.getUserHome(), (String)String.format((String)".jline-%s.%s.history", (Object[])new Object[]{mainClass, historyFileName}))));
        } else {
            reader.setHistory((History)new FileHistory((File)new File((File)Configuration.getUserHome(), (String)String.format((String)".jline-%s.history", (Object[])new Object[]{mainClass}))));
        }
        String completors = System.getProperty((String)(ConsoleRunner.class.getName() + ".completers"), (String)"");
        ArrayList<Completer> completorList = new ArrayList<Completer>();
        StringTokenizer tok = new StringTokenizer((String)completors, (String)",");
        while (tok.hasMoreTokens()) {
            ? obj = Class.forName((String)tok.nextToken()).newInstance();
            completorList.add((Completer)((Completer)obj));
        }
        if (completorList.size() > 0) {
            reader.addCompleter((Completer)new ArgumentCompleter(completorList));
        }
        ConsoleReaderInputStream.setIn((ConsoleReader)reader);
        try {
            Class<?> type = Class.forName((String)mainClass);
            Method method = type.getMethod((String)"main", String[].class);
            method.invoke(null, (Object[])new Object[0]);
            return;
        }
        finally {
            ConsoleReaderInputStream.restoreIn();
        }
    }

    private static void usage() {
        System.out.println((String)("Usage: \n   java [-Djline.history='name'] " + ConsoleRunner.class.getName() + " <target class name> [args]" + "\n\nThe -Djline.history option will avoid history" + "\nmangling when running ConsoleRunner on the same application." + "\n\nargs will be passed directly to the target class name."));
    }
}

