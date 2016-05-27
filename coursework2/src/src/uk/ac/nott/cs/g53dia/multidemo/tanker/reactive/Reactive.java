package uk.ac.nott.cs.g53dia.multidemo.tanker.reactive;

import uk.ac.nott.cs.g53dia.multidemo.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Position;
import uk.ac.nott.cs.g53dia.multidemo.util.DebugPrint;
import uk.ac.nott.cs.g53dia.multilibrary.Action;
import uk.ac.nott.cs.g53dia.multilibrary.Cell;
import uk.ac.nott.cs.g53dia.multilibrary.Tanker;

import static uk.ac.nott.cs.g53dia.multidemo.tanker.data.TankerData.S.executePlan;
import static uk.ac.nott.cs.g53dia.multidemo.tanker.data.TankerData.S.reexploration;

/**
 * Low level control logic. Decides whether to explore, or to complete tasks.
 *
 * Created by Barnabas on 18/03/2016.
 */
public class Reactive {
    private final MegaTanker tanker;
    private final PlanExecutor executor;
    private Explorer explorer;

    public Reactive(MegaTanker tanker) {
        this.tanker = tanker;
        explorer = new Explorer(tanker);
        executor = new PlanExecutor(tanker);
    }

    public Action senseAndAct(Cell[][] view) {
        DebugPrint.println(">>>tanker " + tanker.data.own.number + " S&A start");
        sense(view);
        Action act = act();
        DebugPrint.println("<<<tanker " + tanker.data.own.number + " S&A end");
        return act;
    }

    /**
     * Infers the position of sensed cells and uploads them to the shared database.
     *
     * @param view the view of the tanker
     */
    private void sense(Cell[][] view) {
        for (int i = 0; i < view.length; i++) {
            for (int j = 0; j < view[0].length; j++) {
                Cell cell = view[i][j];
                Position pos = tanker.data.own.pos;
                int x = (pos.x - Tanker.VIEW_RANGE) + i;
                int y = (pos.y + Tanker.VIEW_RANGE) - j;

                Position cellPosition = new Position(x, y);

                tanker.data.publish().cell(cellPosition, cell);
            }
        }
    }

    /**
     * Returns the next action either produced by the {Explorer} or executing a plan made by the {Planner}
     *
     * @return the next action of the tanker
     */
    @SuppressWarnings("TailRecursion")
    private Action act() {

        //depending on the current state
        switch (tanker.data.own.state) {
            case reexploration:
                if (tanker.data.request().tasks().length > 10) {
                    //if the reexploration has found enough tasks then stop,
                    //and switch to planning mode
                    tanker.data.own.state = executePlan;
                    explorer.stop();
                    return act(); //recurse with new state
                }
                //fall through, reexploration is executed by an Explorer


            case initialExploration:
                //unless exploring has finished
                if (!explorer.finished()) {
                    //make the next move on the static path
                    return explorer.move();
                } else {
                    if (!tanker.data.request().initialFinished()) {
                        explorer.setPath(tanker.data.request().nextExplorationPath());
                        return act();
                    }
                    //otherwise switch to planning mode
                    tanker.data.own.state = executePlan;
                    return act(); //recurse with new state
                }
                // break;


            case executePlan:
                //until there are plans and the PlanExecutor has something to do
                if (!executor.finished() || tanker.data.request().hasTasks()) {
                    //use the PlanExecutor to generate an Action
                    return executor.move();
                } else {
                    //otherwise switch to reexploration
                    tanker.data.own.state = reexploration;
                    int path = tanker.data.request().nextExplorationPath();
                    explorer.setPath(path);
                    tanker.data.own.reexploreTimes++;

                    DebugPrint.println("ran out of tasks");
                    return act(); //recurse with new state
                }
                // break;
        }

        //shouldn't happen
        System.err.println("no Action was generated");
        return null;
    }

}
