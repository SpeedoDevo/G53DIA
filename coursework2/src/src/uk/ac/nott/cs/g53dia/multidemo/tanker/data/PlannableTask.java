package uk.ac.nott.cs.g53dia.multidemo.tanker.data;


import uk.ac.nott.cs.g53dia.multidemo.util.Copyable;
import uk.ac.nott.cs.g53dia.multilibrary.Task;

/**
 * A mutable Task container.
 * Created by Barnabas on 19/02/2016.
 */
public class PlannableTask implements Copyable, Comparable<PlannableTask> {
    public Task t;
    public Position pos;
    public int supplied;
    private int lockedFor = -1;
    private int lockCnt = 0;
    private int demand;

    public PlannableTask(Task t, Position pos) {
        this.t = t;
        this.pos = pos;
        demand = t.getWaterDemand();
        supplied = demand - t.getRequired();
    }

    /**
     * @param forTanker the tanker
     * @return false if this task is available to the tanker
     */
    public boolean locked(int forTanker) {
        return lockCnt != 0 && lockedFor != forTanker;
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

    /**
     * Locks the semaphore. (Or unlocks it is lock is -1)
     *
     * @param lock the lock
     */
    public void lockFor(int lock) {
        if (lock == -1) {
            unlock();
        } else {
            lockedFor = lock;
            lockCnt++;
        }
    }

    public void unlock() {
        if (--lockCnt == 0) {
            lockedFor = -1;
        }
    }

}
