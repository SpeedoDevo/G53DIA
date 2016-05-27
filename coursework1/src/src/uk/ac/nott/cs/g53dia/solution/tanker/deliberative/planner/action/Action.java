package uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.action;

import uk.ac.nott.cs.g53dia.library.MoveTowardsAction;
import uk.ac.nott.cs.g53dia.library.Point;
import uk.ac.nott.cs.g53dia.solution.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Beliefs;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Position;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.FutureState;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.State;

/**
 * Moves a State to a FutureState
 */
public abstract class Action {
    public enum Status {
        wrongPosition,
        inPosition,
        complete
    }

    /**
     * @param state the state to transform
     * @return a FutureState that represents the world after this action was taken
     */
    public abstract FutureState apply(State state);

    /**
     * Gets the status of this action. Can be one of {@link Status#inPosition},
     * {@link Status#wrongPosition} or {@link Status#complete}
     *
     * @param t the tanker
     * @param beliefs beliefs
     * @return the status of this action
     * @see Status
     */
    public abstract Status getStatus(MegaTanker t, Beliefs beliefs);

    /**
     * @param beliefs beliefs
     * @return the low-level action that finishes this high-level action
     */
    public abstract uk.ac.nott.cs.g53dia.library.Action finish(Beliefs beliefs);

    /**
     * @param beliefs beliefs
     * @return the low-level action that moves the tanker towards the goal of this high-level action
     */
    public abstract uk.ac.nott.cs.g53dia.library.Action moveTowards(Beliefs beliefs);

    /**
     * produces an action that moves the tanker towards the goal and
     * updates the beliefs to reflect the new position
     *
     * @param beliefs      beliefs
     * @param target the target to go to
     * @param point  the target to go to using the library representation
     * @return a {@link MoveTowardsAction}
     */
    protected static uk.ac.nott.cs.g53dia.library.Action moveTowards
    (Beliefs beliefs, Position target, Point point) {
        Position pos = beliefs.pos;

        int dx = target.x - pos.x;
        int dy = target.y - pos.y;
        if (dx < 0) {
            pos.x--;
        } else if (dx > 0) {
            pos.x++;
        }
        if (dy < 0) {
            pos.y--;
        } else if (dy > 0) {
            pos.y++;
        }

        beliefs.fuel--;
        return new MoveTowardsAction(point);
    }


}
