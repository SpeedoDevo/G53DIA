package uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative;

import java.util.ArrayDeque;
import java.util.Queue;

import uk.ac.nott.cs.g53dia.multidemo.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Position;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.action.Action;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state.FutureState;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state.GoalState;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state.State;
import uk.ac.nott.cs.g53dia.multidemo.util.DebugPrint;
import uk.ac.nott.cs.g53dia.multilibrary.Tanker;

/**
 * Wrapper for a BFS planning algorithm.
 * Created by Barnabas on 18/03/2016.
 */
public class Planner {
    private MegaTanker tanker;

    public Planner(MegaTanker tanker) {
        this.tanker = tanker;
    }

    /**
     * BFS based planning.
     *
     * @param tanker the tanker
     * @param start  start state
     * @param goal   goal state
     * @return list of most optimal high level actions
     */
    private static ArrayDeque<Action> plan(MegaTanker tanker, State start, GoalState goal) {
        Queue<State> open = new ArrayDeque<>();
        open.add(start); //add the start state to open nodes

        State best = start;

        while (!open.isEmpty()) { //until open is empty
            State current = open.poll();//get the first open node

            if (goal.matches(start, current) && (best.score() < current.score())) {
                //if it matches the goal, then see if it is better than the previous best solution
                best = current;
            }
            open.addAll(current.expand(tanker.data.request().wells()));//expand the node and add child states to the queue

        }
        return makeActionList(new ArrayDeque<>(), best); //return the best plan
    }

    /**
     * Recursively builds a high level action sequence.
     *
     * @param ret     list to collect results in
     * @param current the current state
     * @return the action sequence
     */
    private static ArrayDeque<Action> makeActionList(ArrayDeque<Action> ret, State current) {
        if (current instanceof FutureState) {
            makeActionList(ret, ((FutureState) current).parent);
            ret.add(((FutureState) current).resultOf);
        }
        return ret;
    }

    /**
     * @return the best plan according to the current knowledge
     */
    public ArrayDeque<Action> newPlan() {
        tanker.data.own.needsReplanning = false;
        DebugPrint.println("planning...");
        return Planner.plan(
                tanker,
                State.build(tanker),
                (start, current) -> current.pos.equals(new Position(0, 0)) &&
                        (current.fuel == Tanker.MAX_FUEL)
        );
    }

}
