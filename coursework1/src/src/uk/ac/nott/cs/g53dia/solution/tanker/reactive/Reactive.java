package uk.ac.nott.cs.g53dia.solution.tanker.reactive;

import uk.ac.nott.cs.g53dia.library.Action;
import uk.ac.nott.cs.g53dia.library.Cell;
import uk.ac.nott.cs.g53dia.library.FuelPump;
import uk.ac.nott.cs.g53dia.library.RefuelAction;
import uk.ac.nott.cs.g53dia.library.Tanker;
import uk.ac.nott.cs.g53dia.solution.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Deliberative;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Position;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.Planner;

import static uk.ac.nott.cs.g53dia.solution.tanker.reactive.Explore.Explorer;
import static uk.ac.nott.cs.g53dia.solution.tanker.reactive.Reactive.S.executePlan;
import static uk.ac.nott.cs.g53dia.solution.tanker.reactive.Reactive.S.initialExploration;
import static uk.ac.nott.cs.g53dia.solution.tanker.reactive.Reactive.S.reexploration;

/**
 * Layer managing sensing and low level actions.
 * Created by Barnabas on 20/02/2016.
 */
public class Reactive {
    enum S {
        initialExploration,
        executePlan,
        reexploration
    }

    S state = initialExploration;

    private final Explore explore;
    private final MegaTanker tanker;
    private final PlanExecutor executor;
    private final Deliberative deliberative;

    public int reexploreTimes;

    public Reactive(MegaTanker megaTanker, Deliberative deliberative) {
        this.tanker = megaTanker;
        this.deliberative = deliberative;
        explore = new Explore(tanker, deliberative);
        executor = new PlanExecutor(tanker, deliberative);
    }

    public Action senseAndAct(Cell[][] view, long timestep) {
        sense(view);
        return act(view, timestep);
    }

    /**
     * Infers the position of sensed cells and triggers an update in the {@link Deliberative} layer.
     *
     * @param view the view of the tanker
     */
    private void sense(Cell[][] view) {
        for (int i = 0; i < view.length; i++) {
            for (int j = 0; j < view[0].length; j++) {
                Cell cell = view[i][j];
                Position pos = deliberative.getPos();
                int x = (pos.x - Tanker.VIEW_RANGE) + i;
                int y = (pos.y + Tanker.VIEW_RANGE) - j;

                Position cellPosition = new Position(x, y);

                deliberative.senseCell(cellPosition, cell);
            }
        }
    }

    /**
     * Returns the next action either produced by an {@link Explorer} or executing a plan made by
     * {@link Planner}
     *
     * @param view     the view
     * @param timestep the timestep
     * @return the next action of the tanker
     */
    @SuppressWarnings("TailRecursion")
    private Action act(Cell[][] view, long timestep) {
        Cell cell = tanker.getCurrentCell(view);

        //depending on the current state
        switch (state) {
            case reexploration:
                if (deliberative.beliefs.tasks.size() > 10) {
                    //if the reexploration has found enough tasks then stop,
                    //and switch to planning mode
                    state = executePlan;
                    return act(view, timestep); //recurse with new state
                }
                //fall through, reexploration is executed by an Explorer


            case initialExploration:
                //unless exploring has finished
                if (!explore.finished()) {
                    //make the next move on the static path
                    return explore.move(cell);
                } else {
                    //otherwise switch to planning mode
                    state = executePlan;

                    //                    deliberative.printBeliefs(timestep);

                    if (cell instanceof FuelPump) {
                        //but before switching refuel first
                        return new RefuelAction();
                    } else {
                        //shouldn't happen
                        System.err.println("exploration didn't finish on FuelPump");
                        return act(view, timestep); //recurse with new state
                    }
                }
                // break;


            case executePlan:
                //until there are plans and the PlanExecutor has something to do
                if (!executor.finished() || deliberative.hasTasks()) {
                    //use the PlanExecutor to generate an Action
                    return executor.move();
                } else {
                    //otherwise switch to reexploration
                    state = reexploration;
                    explore.resetToReexplore();
                    reexploreTimes++;

                    System.out.println("ran out of tasks");
                    return act(view, timestep); //recurse with new state
                }
                // break;
        }

        //shouldn't happen
        System.err.println("no Action was generated");
        return null;
    }

}
