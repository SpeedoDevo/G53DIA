package uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.action;

import java.util.Map;

import uk.ac.nott.cs.g53dia.library.LoadWaterAction;
import uk.ac.nott.cs.g53dia.library.Tanker;
import uk.ac.nott.cs.g53dia.library.Well;
import uk.ac.nott.cs.g53dia.solution.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Beliefs;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Position;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.FutureState;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.State;

/*
 * Moves the tanker to a well and fills the tank.
 * Created by Barnabas on 20/02/2016.
 */
public class FillTank extends Action {
    Map.Entry<Position, Well> wellEntry;

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
                state.extraWaterDelivered,
                state.deliveredWater,
                p,
                state.tasks,
                state,
                this);
    }

    @Override
    public uk.ac.nott.cs.g53dia.library.Action finish(Beliefs beliefs) {
        beliefs.water = Tanker.MAX_WATER;
        return new LoadWaterAction();
    }

    @Override
    public uk.ac.nott.cs.g53dia.library.Action moveTowards(Beliefs beliefs) {
        return moveTowards(beliefs, wellEntry.getKey(), wellEntry.getValue().getPoint());
    }

    @Override
    public Status getStatus(MegaTanker t, Beliefs beliefs) {
        if (beliefs.pos.equals(wellEntry.getKey())) {
            return (t.getWaterLevel() >= Tanker.MAX_WATER) ? Status.complete : Status.inPosition;
        } else {
            return Status.wrongPosition;
        }

    }

    @Override
    public String toString() {
        return String.format("FillTank %s", wellEntry.getKey());
    }
}
