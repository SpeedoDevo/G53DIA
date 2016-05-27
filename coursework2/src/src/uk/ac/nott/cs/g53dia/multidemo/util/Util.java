package uk.ac.nott.cs.g53dia.multidemo.util;

/**
 * Utility methods.
 * Created by Barnabas on 18/02/2016.
 */
public class Util {
    private Util() {
    }

    public static <T> int indexOf(T needle, T[] haystack) {
        for (int i = 0; i < haystack.length; i++) {
            if (((haystack[i] != null) && haystack[i].equals(needle))
                    || ((needle == null) && (haystack[i] == null))) return i;
        }

        return -1;
    }
}
