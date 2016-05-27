package uk.ac.nott.cs.g53dia.solution.tanker.deliberative;

import java.util.ArrayDeque;

import uk.ac.nott.cs.g53dia.library.Cell;
import uk.ac.nott.cs.g53dia.library.FuelPump;
import uk.ac.nott.cs.g53dia.library.Point;
import uk.ac.nott.cs.g53dia.library.Station;
import uk.ac.nott.cs.g53dia.library.Tanker;
import uk.ac.nott.cs.g53dia.library.Task;
import uk.ac.nott.cs.g53dia.library.Well;
import uk.ac.nott.cs.g53dia.solution.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.PlannableTask;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.Planner;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.action.Action;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state.State;

/**
 * Deliberative layer managing beliefs and desires.
 * Created by Barnabas on 20/02/2016.
 */
public class Deliberative {
    public Beliefs beliefs = new Beliefs();
    private boolean needsReplanning;
    private final MegaTanker tanker;

    public Deliberative(MegaTanker megaTanker) {
        tanker = megaTanker;
    }

    /**
     * Updates the current beliefs if a new cell or task is discovered.
     *
     * @param cellPosition the inferred position of the cell
     * @param cell         the cell instance
     */
    public void senseCell(Position cellPosition, Cell cell) {
        //discard cells outside of the Tanker's range
        if (!((Math.abs(cellPosition.x) > 50) || (Math.abs(cellPosition.y) > 50))) {
            //update the full map
            if (cellPosition.x < beliefs.minDiscovered.x) {
                beliefs.minDiscovered.x = cellPosition.x;
            }
            if (cellPosition.y < beliefs.minDiscovered.y) {
                beliefs.minDiscovered.y = cellPosition.y;
            }
            if (cellPosition.x > beliefs.maxDiscovered.x) {
                beliefs.maxDiscovered.x = cellPosition.x;
            }
            if (cellPosition.y > beliefs.maxDiscovered.y) {
                beliefs.maxDiscovered.y = cellPosition.y;
            }
            beliefs.discovered.put(cellPosition, cell);


            if (cell instanceof Station) {
                //if the cell is a station
                Station st = (Station) cell;
                //put it in the stations collection
                //this automatically puts recently seen stations at the end
                beliefs.stations.put(cellPosition, st);


                Task t = st.getTask();
                if (t != null) {
                    //if the station has a task
                    PlannableTask task = new PlannableTask(st.getTask(), cellPosition);
                    if (!beliefs.tasks.contains(task)) {
                        //and we haven't seen it already, then add it to the collection
                        beliefs.tasks.add(task);
                        //finding this should trigger a replanning
                        needsReplanning = true;
                        System.out.println("\n\nnew task");
                    }
                }


            } else if (cell instanceof Well) {
                //if the cell is a well
                if (!beliefs.wells.containsKey(cellPosition)) {
                    //and we haven't already seen it, then put it in the collection
                    beliefs.wells.put(cellPosition, (Well) cell);
                    needsReplanning = true;
                    System.out.println("well replan");
                }


            } else if (cell instanceof FuelPump) {
                //save an instance to the pump
                beliefs.pump = (FuelPump) cell;
            }
        }
    }

    /**
     * @return the best plan according to the current knowledge
     */
    public ArrayDeque<Action> newPlan() {
        needsReplanning = false;
        System.out.println("planning...");
        return Planner.planBFS(
                beliefs,
                getState(),
                (start, current) -> current.pos.equals(new Position(0, 0)) &&
                        (current.fuel == Tanker.MAX_FUEL)
        );
    }

    /**
     * @return the current state of the tanker
     */
    public State getState() {
        return new State(
                tanker.getFuelLevel(),
                tanker.getWaterLevel(),
                tanker.getCompletedCount(),
                beliefs.extraWaterDelivered,
                tanker.waterDelivered(),
                beliefs.pos,
                beliefs.tasks.toArray(new PlannableTask[beliefs.tasks.size()])
        );
    }

    public Position getPos() {
        return beliefs.pos;
    }

    public Point getPumpLocation() {
        return beliefs.pump.getPoint();
    }

    public boolean needsReplanning() {
        return needsReplanning;
    }

    public boolean hasTasks() {
        return !beliefs.tasks.isEmpty();
    }

    public void printBeliefs(long timestep) {
        beliefs.print(timestep);
    }
}
