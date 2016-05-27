package uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner;

import uk.ac.nott.cs.g53dia.library.Task;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Position;
import uk.ac.nott.cs.g53dia.solution.util.Copyable;

/**
 * A mutable Task container.
 * Created by Barnabas on 19/02/2016.
 */
public class PlannableTask implements Copyable, Comparable<PlannableTask> {
    public Task t;
    public Position pos;
    int demand;
    public int supplied;

    public PlannableTask(Task t, Position pos) {
        this.t = t;
        this.pos = pos;
        demand = t.getWaterDemand();
        supplied = demand - t.getRequired();
    }

    @Override
    public String toString() {
        return String.format("task pos=%s s/d=%d/%d\n", t.getStationPosition(), supplied, demand);
    }

    /**
     * How much water is required to complete the task?
     */
    public int required() {
        return demand - supplied;
    }

    @Override
    public boolean equals(Object other) {
        if ((other == null) || !(other instanceof PlannableTask)) return false;
        return t.equals(((PlannableTask) other).t);
    }

    @Override
    public PlannableTask copy() {
        PlannableTask ret = new PlannableTask(t, pos);
        ret.supplied = this.supplied;
        return ret;
    }

    @Override
    public int compareTo(PlannableTask other) {
        return Integer.compare(this.required(), other.required());
    }
}
