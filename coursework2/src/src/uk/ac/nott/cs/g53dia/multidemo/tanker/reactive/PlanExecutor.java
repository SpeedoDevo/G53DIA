package uk.ac.nott.cs.g53dia.multidemo.tanker.reactive;

import java.util.ArrayDeque;

import uk.ac.nott.cs.g53dia.multidemo.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Position;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.Planner;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.action.Action;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.action.CompleteTask;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.action.EmptyTank;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state.FutureState;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state.State;
import uk.ac.nott.cs.g53dia.multidemo.util.DebugPrint;

/**
 * Executes a plan created by the {@link Planner}
 * Created by Barnabas on 18/03/2016.
 */
class PlanExecutor {
    private final MegaTanker tanker;

    private Action currentAction;
    private ArrayDeque<Action> currentPlan;

    PlanExecutor(MegaTanker tanker) {
        this.tanker = tanker;
    }

    /**
     * Applies a list of actions to a start state, returning the resulting {@link FutureState}.
     *
     * @param plan  the plan
     * @param start the start state
     * @return the future state after applying all actions in the {@code plan}
     */
    private static FutureState applyPlan(Iterable<Action> plan, State start) {
        State ret = start;
        for (Action aPlan : plan) {
            ret = aPlan.apply(ret);
        }
        return (FutureState) ret;
    }

    private static int furthestPoint(FutureState state) {
        State current = state;
        int max = 0;
        Position pump = new Position(0, 0);
        while (current instanceof FutureState) {
            int distance = current.pos.distanceTo(pump);
            if (distance > max) {
                max = distance;
            }
            current = ((FutureState) current).parent;
        }
        return max;
    }

    /**
     * @return the next move to accomplish a plan returned by {Planner}
     */
    public uk.ac.nott.cs.g53dia.multilibrary.Action move() {
        if ((currentPlan == null) || (currentAction == null) ||
                ((currentPlan.size() <= 0) && isActionComplete(currentAction))) {
            //if there is no plan or the current plan is complete
            acceptPlan(tanker.planner.newPlan()); //generate a new plan

            DebugPrint.println("currentPlan = " + currentAction +
                    " " + currentAction.getStatus(tanker) + " " +
                    " + " + currentPlan);


        } else if (tanker.data.own.needsReplanning) { //if new knowledge triggered a replan
            DebugPrint.println("replan currentPlan = " + currentAction +
                    " " + currentAction.getStatus(tanker) + " " +
                    " + " + currentPlan);

            ArrayDeque<Action> old = currentPlan.clone();
            ArrayDeque<Action> fresh = tanker.planner.newPlan(); //generate a new plan

            if (old.peekFirst() != currentAction && !isActionComplete(currentAction)) {
                old.addFirst(currentAction);
            }
            DebugPrint.println("fresh = " + fresh);
            if (comparePlans(old, fresh)) { //if the new plan is better
                releasePlan(old);
                acceptPlan(fresh); //then replace it
            } else {
                DebugPrint.println("keeping old plan"); //otherwise keep the current plan
                if (isActionComplete(currentAction)) {
                    currentAction = currentPlan.pollFirst();
                }
            }

            DebugPrint.println("currentPlan = " + currentAction +
                    " " + currentAction.getStatus(tanker) + " " +
                    " + " + currentPlan);


        } else if (isActionComplete(currentAction)) { //otherwise if the current action is complete
            //get the next action
            currentAction = currentPlan.pollFirst();
        }


        //depending on the status of the current action either
        switch (currentAction.getStatus(tanker)) {
            case wrongPosition:
                //move towards the goal
                return currentAction.moveTowards(tanker);
            case inPosition:
                //or finish this action
                return currentAction.finish(tanker);
            case complete: //fall through
            default:
                System.err.println("PlanExecutor: Invalid state");
                return null;
        }
    }

    private void releasePlan(ArrayDeque<Action> plan) {
        DebugPrint.println("releasing = " + plan);
        for (Action action : plan) {
            if (action instanceof CompleteTask) {
                ((CompleteTask) action).task.unlock();
                tanker.data.publish().unlockTask(((CompleteTask) action).task);
            } else if (action instanceof EmptyTank) {
                ((EmptyTank) action).task.unlock();
                tanker.data.publish().unlockTask(((EmptyTank) action).task);
            }
        }
    }

    private void acceptPlan(ArrayDeque<Action> plan) {
        DebugPrint.println("locking = " + plan);
        currentPlan = plan;
        for (Action action : plan) {
            if (action instanceof CompleteTask) {
                ((CompleteTask) action).task.lockFor(tanker.data.own.number);
                tanker.data.publish().lockTask(((CompleteTask) action).task);
            } else if (action instanceof EmptyTank) {
                ((EmptyTank) action).task.lockFor(tanker.data.own.number);
                tanker.data.publish().lockTask(((EmptyTank) action).task);
            }
        }
        currentAction = currentPlan.pollFirst(); //get the first action to execute
    }

    private boolean isActionComplete(Action action) {
        return action.getStatus(tanker) == Action.Status.complete;
    }

    boolean finished() {
        return (currentPlan != null) &&
                (currentPlan.size() <= 0) &&
                isActionComplete(currentAction);
    }

    /**
     * Compares two plans and returns true if the new one is better.
     * The better plan is one that improves the score a lot or the one that goes further.
     *
     * @param old   the old plan
     * @param fresh the new plan
     * @return true if the new one is better
     */
    private boolean comparePlans(Iterable<Action> old, Iterable<Action> fresh) {
        FutureState oldEndState = applyPlan(old, State.build(tanker));
        FutureState freshEndState = applyPlan(fresh, State.build(tanker));

        long deliveredWater = freshEndState.deliveredWater == 0 ? -1 : freshEndState.deliveredWater;
        return (((freshEndState.score() - oldEndState.score())
                / deliveredWater) > 1.5) || // arbitrary measure of a better score
                ((furthestPoint(freshEndState) - furthestPoint(oldEndState)) > 0);
    }
}
