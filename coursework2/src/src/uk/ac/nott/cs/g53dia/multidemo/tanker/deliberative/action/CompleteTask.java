package uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.action;


import java.util.Arrays;

import uk.ac.nott.cs.g53dia.multidemo.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.PlannableTask;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state.FutureState;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state.State;
import uk.ac.nott.cs.g53dia.multidemo.util.DebugPrint;
import uk.ac.nott.cs.g53dia.multilibrary.DeliverWaterAction;

/**
 * Moves the tanker to a station and completes a task.
 * Created by Barnabas on 20/02/2016.
 */
public class CompleteTask extends Action {
    public PlannableTask task;

    public CompleteTask(PlannableTask task) {
        this.task = task;
    }

    private static PlannableTask[] removeFromCopy
            (PlannableTask[] from, PlannableTask minus) {
        return Arrays.stream(from)
                .filter(e -> !e.equals(minus))
                .map(PlannableTask::copy)
                .toArray(PlannableTask[]::new);
    }

    @Override
    public FutureState apply(State state) {
        PlannableTask[] futureTasks = removeFromCopy(state.tasks, task);
        return new FutureState(
                state.fuel - state.pos.distanceTo(task.pos),
                state.water - task.required(),
                state.completedTasks + 1,
                state.deliveredWater + task.required(),
                state.extraWaterDelivered,
                task.pos,
                futureTasks,
                state,
                this);
    }

    @Override
    public Status getStatus(MegaTanker tanker) {
        if (tanker.data.own.pos.equals(task.pos)) {
            return !tanker.data.request().hasTask(task) ? Status.complete : Status.inPosition;
        } else {
            return Status.wrongPosition;
        }

    }

    @Override
    public uk.ac.nott.cs.g53dia.multilibrary.Action finish(MegaTanker tanker) {
        tanker.data.own.water -= task.required();

        if (task.supplied != 0) { //this task is partially complete
            tanker.data.own.extraWaterDelivered -= task.supplied; //update the belief about partial tasks
        }
        task.supplied += task.required();


        tanker.data.publish().finishedTask(task);
        DebugPrint.println("finishing " + this);
        return new DeliverWaterAction(task.t); //return the low level action
    }

    @Override
    public uk.ac.nott.cs.g53dia.multilibrary.Action moveTowards(MegaTanker tanker) {
        return moveTowards(tanker, task.pos, task.t.getStationPosition());
    }

    @Override
    public String toString() {
        return String.format("CompleteTask %s %d", task.pos, task.required());
    }
}
