package uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state;

import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Position;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.PlannableTask;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.action.Action;

/**
 * Represents the world after some sequence of actions were taken.
 */
public class FutureState extends State {
    static Position pump = new Position(0, 0);

    public State parent;
    public Action resultOf;

    public FutureState(int fuel, int water, int completedTasks, int extraWaterDelivered,
                       long deliveredWater, Position pos, PlannableTask[] tasks,
                       State parent, Action resultOf) {
        super(fuel, water, completedTasks, extraWaterDelivered, deliveredWater, pos, tasks);
        this.parent = parent;
        this.resultOf = resultOf;
    }

    /**
     * Checks if this state is not too far from the pump.
     *
     * @return true if the tanker can still return to the pump.
     */
    public boolean isViable() {
        return pos.distanceTo(pump) <= fuel;
    }
}
