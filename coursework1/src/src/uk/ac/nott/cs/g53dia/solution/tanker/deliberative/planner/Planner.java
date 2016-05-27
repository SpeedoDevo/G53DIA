package uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Beliefs;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.action.Action;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.FutureState;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.GoalState;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.State;

/**
 * Planning with a*
 * Created by Barnabas on 17/02/2016.
 */
public class Planner {

    private Planner() {
    }

    /**
     * Initial implementation of A*, not working.
     *
     * @param beliefs beliefs
     * @param start   start state
     * @param goal    goal state
     * @return list of high level actions
     */
    static ArrayDeque<Action> planAStar(Beliefs beliefs, State start, GoalState goal) {
        PriorityQueue<State> open = new PriorityQueue<>();
        open.add(start);
        Set<State> closed = new TreeSet<>((Comparator<State>) (l, r) -> {
            int c = l.pos.compareTo(r.pos);
            if (c == 0) {
                return Double.compare(l.score(), r.score());
            } else {
                return c;
            }
        });

        while (!open.isEmpty()) {
            State current = open.poll();
            closed.add(current);

            if (goal.matches(start, current)) {
                return makeActionList(new ArrayDeque<>(), current);
            } else {
                ArrayList<FutureState> children = current.expand(beliefs);
                for (State child : children) {
                    if (closed.contains(child)) {
                        continue;
                    }

                    if (open.contains(child)) {
                        State oldChild = null;
                        for (State s : open) {
                            if (s.equals(child)) {
                                oldChild = s;
                            }
                        }
                        if (oldChild != null && oldChild.score() < child.score()) {
                            open.remove(oldChild);
                            open.add(child);
                        }
                    } else {
                        open.add(child);
                    }
                }
            }
        }
        return null;
    }

    /**
     * BFS based planning.
     *
     * @param beliefs     beliefs
     * @param start start state
     * @param goal  goal state
     * @return list of most optimal high level actions
     */
    public static ArrayDeque<Action> planBFS(Beliefs beliefs, State start, GoalState goal) {
        Queue<State> open = new ArrayDeque<>();
        open.add(start); //add the start state to open nodes

        State best = start;

        while (!open.isEmpty()) { //until open is empty
            State current = open.poll();//get the first open node

            if (goal.matches(start, current) && (best.score() < current.score())) {
                //if it matches the goal, then see if it is better than the previous best solution
                best = current;
            }
            open.addAll(current.expand(beliefs));//expand the node and add child states to the queue

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


}
