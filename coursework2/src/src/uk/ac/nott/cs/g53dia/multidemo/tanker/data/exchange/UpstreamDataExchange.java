package uk.ac.nott.cs.g53dia.multidemo.tanker.data.exchange;

import uk.ac.nott.cs.g53dia.multidemo.tanker.data.PlannableTask;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Position;
import uk.ac.nott.cs.g53dia.multilibrary.Cell;

/**
 * The implementation of this interface is handling all outgoing data updates.
 * Created by Barnabas on 22/03/2016.
 */
public interface UpstreamDataExchange {
    /**
     * A cell is seen.
     *
     * @param cellPosition the cell position
     * @param cell         the cell
     */
    void cell(Position cellPosition, Cell cell);

    /**
     * A task is seen.
     *
     * @param t the task
     */
    void task(PlannableTask t);

    /**
     * A task is fulfilled.
     *
     * @param task the task
     */
    void finishedTask(PlannableTask task);

    /**
     * A task is supplied with some water.
     *
     * @param water the water
     * @param task  the task
     */
    void suppliedTask(int water, PlannableTask task);

    /**
     * A task has to be hidden from other agents.
     *
     * @param task the task
     */
    void lockTask(PlannableTask task);

    /**
     * A task can be seen again by other agents.
     *
     * @param task the task
     */
    void unlockTask(PlannableTask task);

    /**
     * An exploration has been started, another agent may not start this path until this has finished.
     *
     * @param path the path
     */
    void startedExploring(int path);

    /**
     * This exploration path is available again.
     *
     * @param path the path
     */
    void stoppedExploring(int path);
}
