package uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state;

import uk.ac.nott.cs.g53dia.multidemo.tanker.data.PlannableTask;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Position;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.action.Action;

/**
 * Represents the world after some sequence of actions were taken.
 */
public class FutureState extends State {
    private static Position pump = new Position();

    public State parent;
    public Action resultOf;

    public FutureState(int fuel, int water, int completedTasks, long deliveredWater,
                       int extraWaterDelivered, Position pos, PlannableTask[] tasks,
                       State parent, Action resultOf) {
        super(fuel, water, completedTasks, deliveredWater, extraWaterDelivered, pos, tasks);
        this.parent = parent;
        this.resultOf = resultOf;
    }

    /**
     * Checks if this state is not too far from the pump.
     *
     * @return true if the tanker can still return to the pump.
     */
    boolean isViable() {
        return pos.distanceTo(pump) <= fuel;
    }
}
