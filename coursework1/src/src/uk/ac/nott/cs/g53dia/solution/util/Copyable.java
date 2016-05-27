package uk.ac.nott.cs.g53dia.solution.util;

/**
 * An alternative for Cloneable, that actually infers that a class can be copied.
 * Created by Barnabas on 20/02/2016.
 */
public interface Copyable {
    Copyable copy();
}
