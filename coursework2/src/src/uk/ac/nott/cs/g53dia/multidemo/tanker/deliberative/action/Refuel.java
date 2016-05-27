package uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.action;

import uk.ac.nott.cs.g53dia.multidemo.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Position;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state.FutureState;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state.State;
import uk.ac.nott.cs.g53dia.multidemo.util.DebugPrint;
import uk.ac.nott.cs.g53dia.multilibrary.RefuelAction;
import uk.ac.nott.cs.g53dia.multilibrary.Tanker;

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
                state.deliveredWater,
                state.extraWaterDelivered,
                ORIGIN,
                state.tasks,
                state,
                this);
    }

    @Override
    public Status getStatus(MegaTanker tanker) {
        if (tanker.data.own.pos.equals(ORIGIN)) {
            return (tanker.getFuelLevel() >= Tanker.MAX_FUEL) ? Status.complete : Status.inPosition;
        } else {
            return Status.wrongPosition;
        }
    }

    @Override
    public uk.ac.nott.cs.g53dia.multilibrary.Action finish(MegaTanker tanker) {
        tanker.data.own.fuel = Tanker.MAX_FUEL;
        DebugPrint.println("finishing " + this);
        return new RefuelAction();
    }

    @Override
    public uk.ac.nott.cs.g53dia.multilibrary.Action moveTowards(MegaTanker tanker) {
        return moveTowards(tanker, ORIGIN, tanker.data.request().pump().getPoint());
    }

    @Override
    public String toString() {
        return "Refuel";
    }
}
