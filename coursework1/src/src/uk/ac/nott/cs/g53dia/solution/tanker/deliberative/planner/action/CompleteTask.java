package uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.action;

import java.util.Arrays;

import uk.ac.nott.cs.g53dia.library.DeliverWaterAction;
import uk.ac.nott.cs.g53dia.solution.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Beliefs;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.PlannableTask;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.FutureState;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.State;

/**
 * Moves the tanker to a station and completes a task.
 * Created by Barnabas on 20/02/2016.
 */
public class CompleteTask extends Action {
    PlannableTask task;

    public CompleteTask(PlannableTask task) {
        this.task = task;
    }

    @Override
    public FutureState apply(State state) {
        PlannableTask[] futureTasks = removeFromCopy(state.tasks, task);
        return new FutureState(
                state.fuel - state.pos.distanceTo(task.pos),
                state.water - task.required(),
                state.completedTasks + 1,
                state.extraWaterDelivered,
                state.deliveredWater + task.required(),
                task.pos,
                futureTasks,
                state,
                this);
    }

    @Override
    public Status getStatus(MegaTanker t, Beliefs beliefs) {
        if (beliefs.pos.equals(task.pos)) {
            return !beliefs.tasks.contains(task) ? Status.complete : Status.inPosition;
        } else {
            return Status.wrongPosition;
        }

    }

    @Override
    public uk.ac.nott.cs.g53dia.library.Action finish(Beliefs beliefs) {
        beliefs.water -= task.required();
        beliefs.tasks.stream() // from the original task list
                .filter(t -> t.pos.equals(task.pos)) // get this task (this instance might be a copy)
                .forEach(t -> { // only applies to one element
                    if (t.supplied != 0) { //this task is partially complete
                        beliefs.extraWaterDelivered -= t.supplied; //update the belief about partial tasks
            }
                    t.supplied += t.required(); //update beliefs about completion
            if (task != t) {
                task.supplied += t.required(); // and update this instance as well
            }
        });
        beliefs.tasks.remove(task); //but still remove it from beliefs
        //updates done to keep other references updated
        return new DeliverWaterAction(task.t); //return the low level action
    }

    @Override
    public uk.ac.nott.cs.g53dia.library.Action moveTowards(Beliefs beliefs) {
        return moveTowards(beliefs, task.pos, task.t.getStationPosition());
    }

    @Override
    public String toString() {
        return String.format("CompleteTask %s %d", task.pos, task.required());
    }

    public static PlannableTask[] removeFromCopy
            (PlannableTask[] from, PlannableTask minus) {
        return Arrays.stream(from)
                .filter(e -> !e.equals(minus))
                .map(PlannableTask::copy)
                .toArray(PlannableTask[]::new);
    }
}
