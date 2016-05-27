package uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.action;

import uk.ac.nott.cs.g53dia.library.DeliverWaterAction;
import uk.ac.nott.cs.g53dia.solution.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Beliefs;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Position;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.PlannableTask;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.FutureState;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.State;
import uk.ac.nott.cs.g53dia.solution.util.Util;

/**
 * Moves the tanker to a station and empties the tank.
 * Created by Barnabas on 20/02/2016.
 */
public class EmptyTank extends Action {
    PlannableTask task;

    public EmptyTank(PlannableTask task) {
        this.task = task;
    }

    @Override
    public FutureState apply(State state) {
        PlannableTask[] futureTasks = deepCopy(state.tasks); //create a copy of the parent's tasks
        int index = Util.indexOf(task, state.tasks);
        // so that this instance can be modified without interfering
        futureTasks[index].supplied += state.water;

        Position p = task.pos;
        return new FutureState(
                state.fuel - state.pos.distanceTo(p),
                0,
                state.completedTasks,
                state.extraWaterDelivered + state.water,
                state.deliveredWater,
                p,
                futureTasks,
                state,
                this);
    }

    @Override
    public uk.ac.nott.cs.g53dia.library.Action finish(Beliefs beliefs) {
        beliefs.extraWaterDelivered += beliefs.water; //update beliefs
        beliefs.tasks.stream()
                .filter(t -> t.pos.equals(task.pos)) //get the belief task that equals to this instance
                .forEach(t -> { //only applies to one element
                    //update both instances
                    t.supplied += beliefs.water;
            if (task != t) {
                task.supplied += beliefs.water;
            }
        });
        return new DeliverWaterAction(task.t);
    }

    @Override
    public uk.ac.nott.cs.g53dia.library.Action moveTowards(Beliefs beliefs) {
        return moveTowards(beliefs, task.pos, task.t.getStationPosition());
    }

    @Override
    public Status getStatus(MegaTanker t, Beliefs beliefs) {
        if (beliefs.pos.equals(task.pos)) {
            return (t.getWaterLevel() <= 0) ? Status.complete : Status.inPosition;
        } else {
            return Status.wrongPosition;
        }
    }

    @Override
    public String toString() {
        return String.format("EmptyTank %s", task.pos);
    }

    public static PlannableTask[] deepCopy(PlannableTask[] list) {
        PlannableTask[] ret = list.clone();
        for (int i = 0; i < list.length; i++) {
            ret[i] = list[i].copy();
        }
        return ret;
    }
}
