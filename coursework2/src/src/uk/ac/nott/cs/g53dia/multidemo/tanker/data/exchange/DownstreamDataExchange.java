package uk.ac.nott.cs.g53dia.multidemo.tanker.data.exchange;

import java.util.Map;

import uk.ac.nott.cs.g53dia.multidemo.tanker.data.PlannableTask;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Position;
import uk.ac.nott.cs.g53dia.multilibrary.FuelPump;
import uk.ac.nott.cs.g53dia.multilibrary.Well;

/**
 * The implementation of this interface is handling all incoming data updates.
 * <p>
 * Created by Barnabas on 22/03/2016.
 */
public interface DownstreamDataExchange {
    /**
     * Return all available tasks, locked ones too if {@code lockedToo}
     *
     * @return copy of available tasks
     */
    PlannableTask[] tasks();

    /**
     * @param task the task
     * @return true if the task is incomplete - i.e. in the list
     */
    boolean hasTask(PlannableTask task);

    /**
     * @return the fuel pump
     */
    FuelPump pump();

    /**
     * @return a map of wells
     */
    Map<Position, Well> wells();

    /**
     * @return true if there are available tasks
     */
    boolean hasTasks();

    /**
     * @return the index of the next exploration path
     */
    int nextExplorationPath();

    /**
     * @return true if the initial exploration has finished
     */
    boolean initialFinished();

    /**
     * Updates should be sent to this Listener.
     */
    interface Listener {
        /**
         * Should be called when a new task is available at {@code pos}
         *
         * @param pos the position
         */
        void onTask(Position pos);

        /**
         * Should be called when a new well is available at {@code pos}
         *
         * @param pos the position
         */
        void onWell(Position pos);
    }
}
