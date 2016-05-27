package uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.action;


import uk.ac.nott.cs.g53dia.multidemo.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.PlannableTask;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Position;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state.FutureState;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state.State;
import uk.ac.nott.cs.g53dia.multidemo.util.DebugPrint;
import uk.ac.nott.cs.g53dia.multidemo.util.Util;
import uk.ac.nott.cs.g53dia.multilibrary.DeliverWaterAction;

/**
 * Moves the tanker to a station and empties the tank.
 * Created by Barnabas on 20/02/2016.
 */
public class EmptyTank extends Action {
    public PlannableTask task;

    public EmptyTank(PlannableTask task) {
        this.task = task;
    }

    private static PlannableTask[] deepCopy(PlannableTask[] list) {
        PlannableTask[] ret = list.clone();
        for (int i = 0; i < list.length; i++) {
            ret[i] = list[i].copy();
        }
        return ret;
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
                state.deliveredWater,
                state.extraWaterDelivered + state.water,
                p,
                futureTasks,
                state,
                this);
    }

    @Override
    public uk.ac.nott.cs.g53dia.multilibrary.Action finish(MegaTanker tanker) {
        tanker.data.own.extraWaterDelivered += tanker.data.own.water; //update beliefs

        tanker.data.publish().suppliedTask(tanker.data.own.water, task);
        tanker.data.publish().unlockTask(task);
        task.unlock();

        task.supplied += tanker.data.own.water;
        tanker.data.own.water = 0;

        DebugPrint.println("finishing " + this);
        return new DeliverWaterAction(task.t);
    }

    @Override
    public uk.ac.nott.cs.g53dia.multilibrary.Action moveTowards(MegaTanker tanker) {
        return moveTowards(tanker, task.pos, task.t.getStationPosition());
    }

    @Override
    public Status getStatus(MegaTanker tanker) {
        if (tanker.data.own.pos.equals(task.pos)) {
            return (tanker.data.own.water <= 0) ? Status.complete : Status.inPosition;
        } else {
            return Status.wrongPosition;
        }
    }

    @Override
    public String toString() {
        return String.format("EmptyTank %s", task.pos);
    }
}
