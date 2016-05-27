package uk.ac.nott.cs.g53dia.multidemo.util;

/**
 * Control printing to stdout.
 * Created by Barnabas on 23/03/2016.
 */
public class DebugPrint {
    private static final boolean DEBUG = false;

    public static void println(String s) {
        if (DEBUG) System.out.println(s);
    }

    public static void print(String s) {
        if (DEBUG) System.out.print(s);
    }


    public static void println() {
        if (DEBUG) println("");
    }

    public static void println(Object o) {
        if (DEBUG) println(String.valueOf(o));
    }
}
