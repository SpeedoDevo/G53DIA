package uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.action;

import uk.ac.nott.cs.g53dia.library.RefuelAction;
import uk.ac.nott.cs.g53dia.library.Tanker;
import uk.ac.nott.cs.g53dia.solution.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Beliefs;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Position;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.FutureState;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.State;

/**
 * Moves the tanker to the pump and refuels.
 * Created by Barnabas on 20/02/2016.
 */
public class Refuel extends Action {
    private static final Position ORIGIN = new Position(0, 0);

    @Override
    public FutureState apply(State state) {
        return new FutureState(
                Tanker.MAX_FUEL,
                state.water,
                state.completedTasks,
                state.extraWaterDelivered,
                state.deliveredWater,
                ORIGIN,
                state.tasks,
                state,
                this);
    }

    @Override
    public Status getStatus(MegaTanker t, Beliefs beliefs) {
        if (beliefs.pos.equals(ORIGIN)) {
            return (t.getFuelLevel() >= Tanker.MAX_FUEL) ? Status.complete : Status.inPosition;
        } else {
            return Status.wrongPosition;
        }
    }

    @Override
    public uk.ac.nott.cs.g53dia.library.Action finish(Beliefs beliefs) {
        beliefs.fuel = Tanker.MAX_FUEL;
        return new RefuelAction();
    }

    @Override
    public uk.ac.nott.cs.g53dia.library.Action moveTowards(Beliefs beliefs) {
        return moveTowards(beliefs, ORIGIN, beliefs.pump.getPoint());
    }

    @Override
    public String toString() {
        return "Refuel";
    }
}
