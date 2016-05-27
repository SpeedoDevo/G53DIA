package uk.ac.nott.cs.g53dia.solution.tanker.reactive;

import java.util.ArrayDeque;

import uk.ac.nott.cs.g53dia.solution.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Deliberative;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Position;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.Planner;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.action.Action;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.FutureState;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.State;

/**
 * Translates high level action sequences to low level actions that the Tanker understands.
 * Created by Barnabas on 20/02/2016.
 */
public class PlanExecutor {
    private final MegaTanker tanker;
    private final Deliberative deliberative;

    private Action currentAction;
    private ArrayDeque<Action> currentPlan;

    public PlanExecutor(MegaTanker tanker, Deliberative deliberative) {
        this.deliberative = deliberative;
        this.tanker = tanker;
    }

    /**
     * @return the next move to accomplish a plan returned by {@link Planner}
     */
    public uk.ac.nott.cs.g53dia.library.Action move() {
        if ((currentPlan == null) || (currentAction == null) ||
                ((currentPlan.size() <= 0) && isActionComplete(currentAction))) {
            //if there is no plan or the current plan is complete
            currentPlan = deliberative.newPlan(); //generate new plan
            currentAction = currentPlan.pollFirst(); //get the first action to execute#

            System.out.println("currentPlan = " + currentAction +
                    " " + currentAction.getStatus(tanker, deliberative.beliefs) + " " +
                    " + " + currentPlan);


        } else if (deliberative.needsReplanning()) { //if new knowledge triggered a replan
            System.out.println("replan currentPlan = " + currentAction +
                    " " + currentAction.getStatus(tanker, deliberative.beliefs) + " " +
                    " + " + currentPlan);

            ArrayDeque<Action> old = currentPlan.clone();
            old.addFirst(currentAction);
            ArrayDeque<Action> fresh = deliberative.newPlan(); //generate a new plan
            System.out.println("fresh = " + fresh);
            if (comparePlans(old, fresh)) { //if the new plan is better
                currentPlan = fresh; //then replace it
                currentAction = currentPlan.pollFirst();
            } else {
                System.out.println("keeping old plan"); //otherwise keep the current plan
                if (isActionComplete(currentAction)) {
                    currentAction = currentPlan.pollFirst();
                }
            }

            System.out.println("currentPlan = " + currentAction +
                    " " + currentAction.getStatus(tanker, deliberative.beliefs) + " " +
                    " + " + currentPlan);


        } else if (isActionComplete(currentAction)) { //otherwise if the current action is complete
            //get the next action
            currentAction = currentPlan.pollFirst();
        }


        //depending on the status of the current action either
        switch (currentAction.getStatus(tanker, deliberative.beliefs)) {
            case wrongPosition:
                //move towards the goal
                return currentAction.moveTowards(deliberative.beliefs);
            case inPosition:
                //or finish this action
                return currentAction.finish(deliberative.beliefs);
            case complete: //fall through
            default:
                System.out.printf("shouldn't happen");
                return null;
        }
    }

    public boolean isActionComplete(Action action) {
        return action.getStatus(tanker, deliberative.beliefs) == Action.Status.complete;
    }

    public boolean finished() {
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
    boolean comparePlans(Iterable<Action> old, Iterable<Action> fresh) {
        FutureState oldEndState = applyPlan(old, deliberative.getState());
        FutureState freshEndState = applyPlan(fresh, deliberative.getState());

        //        System.out.printf("old %.2f new %.2f diff %.2f stg %.2f\n",
        //                oldEndState.score(), freshEndState.score(),
        //                freshEndState.score() - oldEndState.score(),
        //                (freshEndState.score() - oldEndState.score()) / freshEndState.deliveredWater);
        //        System.out.printf("old %d new %d diff %+d\n",
        //                oldEndState.parent.fuel, freshEndState.parent.fuel,
        //                freshEndState.parent.fuel - oldEndState.parent.fuel);
        //        System.out.printf("old %d new %d diff %+d, %b\n",
        //                furthestPoint(oldEndState), furthestPoint(freshEndState),
        //                furthestPoint(freshEndState) - furthestPoint(oldEndState),
        //                ((furthestPoint(freshEndState) - furthestPoint(oldEndState)) > -10));

        return (((freshEndState.score() - oldEndState.score())
                / freshEndState.deliveredWater) > 2) || // arbitrary measure of a better score
                ((furthestPoint(freshEndState) - furthestPoint(oldEndState)) > -10);
        //        return oldEndState.score() < freshEndState.score();
        //        return oldEndState.parent.fuel > freshEndState.parent.fuel + 10;
    }

    /**
     * Applies a list of actions to a start state, returning the resulting {@link FutureState}.
     *
     * @param plan  the plan
     * @param start the start state
     * @return the future state after applying all actions in the {@code plan}
     */
    static FutureState applyPlan(Iterable<Action> plan, State start) {
        State ret = start;
        for (Action aPlan : plan) {
            ret = aPlan.apply(ret);
        }
        return (FutureState) ret;
    }

    static int furthestPoint(FutureState state) {
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
}
