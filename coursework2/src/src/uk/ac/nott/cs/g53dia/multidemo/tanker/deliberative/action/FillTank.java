package uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.action;

import java.util.Map;

import uk.ac.nott.cs.g53dia.multidemo.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Position;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state.FutureState;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.state.State;
import uk.ac.nott.cs.g53dia.multidemo.util.DebugPrint;
import uk.ac.nott.cs.g53dia.multilibrary.LoadWaterAction;
import uk.ac.nott.cs.g53dia.multilibrary.Tanker;
import uk.ac.nott.cs.g53dia.multilibrary.Well;

/*
 * Moves the tanker to a well and fills the tank.
 * Created by Barnabas on 20/02/2016.
 */
public class FillTank extends Action {
    private Map.Entry<Position, Well> wellEntry;

    public FillTank(Map.Entry<Position, Well> wellEntry) {
        this.wellEntry = wellEntry;
    }

    @Override
    public FutureState apply(State state) {
        Position p = wellEntry.getKey();
        return new FutureState(
                state.fuel - state.pos.distanceTo(p),
                Tanker.MAX_WATER,
                state.completedTasks,
                state.deliveredWater,
                state.extraWaterDelivered,
                p,
                state.tasks,
                state,
                this);
    }

    @Override
    public uk.ac.nott.cs.g53dia.multilibrary.Action finish(MegaTanker tanker) {
        tanker.data.own.water = Tanker.MAX_WATER;
        DebugPrint.println("finishing " + this);
        return new LoadWaterAction();
    }

    @Override
    public uk.ac.nott.cs.g53dia.multilibrary.Action moveTowards(MegaTanker tanker) {
        return moveTowards(tanker, wellEntry.getKey(), wellEntry.getValue().getPoint());
    }

    @Override
    public Status getStatus(MegaTanker tanker) {
        if (tanker.data.own.pos.equals(wellEntry.getKey())) {
            return (tanker.getWaterLevel() >= Tanker.MAX_WATER) ? Status.complete : Status.inPosition;
        } else {
            return Status.wrongPosition;
        }

    }

    @Override
    public String toString() {
        return String.format("FillTank %s", wellEntry.getKey());
    }
}
