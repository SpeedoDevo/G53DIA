package uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.action;


import uk.ac.nott.cs.g53dia.multidemo.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Position;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state.FutureState;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state.State;
import uk.ac.nott.cs.g53dia.multilibrary.MoveTowardsAction;
import uk.ac.nott.cs.g53dia.multilibrary.Point;

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
     * @return the status of this action
     * @see Status
     */
    public abstract Status getStatus(MegaTanker t);

    /**
     * @param tanker the tanker
     * @return the low-level action that finishes this high-level action
     */
    public abstract uk.ac.nott.cs.g53dia.multilibrary.Action finish(MegaTanker tanker);

    /**
     * @param tanker the tanker
     * @return the low-level action that moves the tanker towards the goal of this high-level action
     */
    public abstract uk.ac.nott.cs.g53dia.multilibrary.Action moveTowards(MegaTanker tanker);

    /**
     * produces an action that moves the tanker towards the goal and
     * updates the beliefs to reflect the new position
     *
     * @param tanker the tanker
     * @param target the target to go to
     * @param point  the target to go to using the library representation
     * @return a {@link MoveTowardsAction}
     */
    static uk.ac.nott.cs.g53dia.multilibrary.Action moveTowards
    (MegaTanker tanker, Position target, Point point) {
        Position pos = tanker.data.own.pos;

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

        tanker.data.own.fuel--;
        return new MoveTowardsAction(point);
    }
}
