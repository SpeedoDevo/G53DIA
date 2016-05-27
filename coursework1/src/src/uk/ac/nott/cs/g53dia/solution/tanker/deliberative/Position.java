package uk.ac.nott.cs.g53dia.solution.tanker.deliberative;

import uk.ac.nott.cs.g53dia.library.Point;

/**
 * A simple class containing a coordinate on the grid.
 * Created by Barnabas on 18/02/2016.
 */
public class Position implements Cloneable, Comparable<Position> {
    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @param other the other Position
     * @return the Chebyshev distance to the other Position
     */
    public int distanceTo(Position other) {
        return Math.max(Math.abs(this.x - other.x), Math.abs(this.y - other.y));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof Position) {
            Position pos = (Position) o;
            return (x == pos.x) && (y == pos.y);
        } else if (o instanceof Point) {
            Point p = (Point) o;
            return p.toString().equals(toString());
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public Position clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException ignored) {
        }
        return new Position(x, y);
    }

    /**
     * Override hashCode to make sure identical points produce identical
     * hashes.
     */
    public int hashCode() {
        return (((x & 0xff) << 16) + (y & 0xff));
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public int compareTo(Position other) {
        int compare = Integer.compare(this.x, other.x);
        return compare != 0 ? compare : Integer.compare(this.y, other.y);
    }
}
