package uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import uk.ac.nott.cs.g53dia.library.Tanker;
import uk.ac.nott.cs.g53dia.library.Well;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Beliefs;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Position;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.PlannableTask;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.action.CompleteTask;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.action.EmptyTank;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.action.FillTank;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.action.Refuel;

/**
 * Represents the current state of the tanker.
 */
public class State implements Comparable<State> {
    public int fuel;
    public int water;
    public int completedTasks;
    public int extraWaterDelivered;
    public long deliveredWater;

    public Position pos;
    public PlannableTask[] tasks;

    public State(int fuel, int water, int completedTasks, int extraWaterDelivered,
                 long deliveredWater, Position pos, PlannableTask[] tasks) {
        this.fuel = fuel;
        this.water = water;
        this.completedTasks = completedTasks;
        this.extraWaterDelivered = extraWaterDelivered;
        this.deliveredWater = deliveredWater;
        this.pos = pos;
        this.tasks = tasks;
    }


    /**
     * @return the score that the simulator will display
     */
    private double objectiveScore() {
        return completedTasks * deliveredWater;
    }

    /**
     * @return a score that rewards actions that don't have an immediate score increase.
     * This is a fairly small score, so it only causes small changes to the main task,
     * which is completing tasks.
     */
    private double heuristicScore() {
        return (0.3f * extraWaterDelivered) +
                ((100 * water) / Tanker.MAX_WATER) + ((10 * fuel) / Tanker.MAX_FUEL);
    }

    /**
     * @return the utility of this state
     */
    public double score() {
        return objectiveScore() + heuristicScore();
    }

    @Override
    public int compareTo(State other) {
        return Double.compare(other.score(), this.score());
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object other) {
        if ((other == null) || !(other instanceof State)) return false;
        return pos.equals(((State) other).pos);
    }

    /**
     * Expands this state to all possible and viable future states until the next refueling.
     *
     * @param b the current knowledge (or belief) base
     * @return a list of FutureStates
     */
    public ArrayList<FutureState> expand(Beliefs b) {
        //collect FutureStates in this list
        ArrayList<FutureState> children = new ArrayList<>();
        //we only look as far as the first refueling goes
        if ((this instanceof FutureState) &&
                (((FutureState) this).resultOf instanceof Refuel)) return children;

        //expand with completable and partially completable tasks
        expandWithTasks(children);

        //if there is less than MAX_WATER in the tanker consider collecting some
        expandWithWells(b.wells, children);
        //if there is less than MAX_FUEL consider refueling
        if (fuel < Tanker.MAX_FUEL) {
            children.add(new Refuel().apply(this));
        }
        return children;
    }

    /**
     * Expands this state with completable and partially completable tasks.
     * Doesn't do anything if there is no water in the tank.
     *
     * @param children results collected here
     */
    private void expandWithTasks(Collection<FutureState> children) {
        if (water <= 0) return;

        //for each task
        for (PlannableTask task : tasks) {
            Position location = task.pos;
            //check if it's possible to move there
            if (this.pos.distanceTo(location) < fuel) {
                //whether it's possible to complete it
                if (task.required() <= water) {
                    FutureState newState = new CompleteTask(task).apply(this);
                    if (newState.isViable()) children.add(newState);
                } else {
                    FutureState newState = new EmptyTank(task).apply(this);
                    if (newState.isViable()) children.add(newState);
                }
            }
        }
    }

    /**
     * Expand this state with all reachable wells if there is less than MAX_WATER
     *
     * @param wells    collection of wells to check
     * @param children results collected here
     */
    private void expandWithWells(Map<Position, Well> wells, Collection<FutureState> children) {
        if (Tanker.MAX_WATER <= water) {
            return;
        }
        for (Map.Entry<Position, Well> entry : wells.entrySet()) {
            Position location = entry.getKey();
            if (this.pos.distanceTo(location) < fuel) {
                FutureState newState = new FillTank(entry).apply(this);
                if (newState.isViable()) children.add(newState);
            }
        }
    }

    @Override
    public String toString() {
        return "pos = " + pos
                + " score = " + score();

    }
}
