package uk.ac.nott.cs.g53dia.multidemo.fleet.data;

import java.util.function.Consumer;

import uk.ac.nott.cs.g53dia.multidemo.tanker.data.PlannableTask;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Position;
import uk.ac.nott.cs.g53dia.multilibrary.Cell;
import uk.ac.nott.cs.g53dia.multilibrary.FuelPump;
import uk.ac.nott.cs.g53dia.multilibrary.Station;
import uk.ac.nott.cs.g53dia.multilibrary.Well;

/**
 * Handles incoming data updates.
 * Created by Barnabas on 18/03/2016.
 */
public class Receiver {
    private Server server;

    Receiver(Server server) {
        this.server = server;
    }

    /**
     * Stores a cell in the map copy.
     *
     * @param cellPosition the cell position
     * @param cell         the cell
     */
    public void cell(Position cellPosition, Cell cell) {
        //update the full map
        if (cellPosition.x < server.minDiscovered.x) {
            server.minDiscovered.x = cellPosition.x;
        }
        if (cellPosition.y < server.minDiscovered.y) {
            server.minDiscovered.y = cellPosition.y;
        }
        if (cellPosition.x > server.maxDiscovered.x) {
            server.maxDiscovered.x = cellPosition.x;
        }
        if (cellPosition.y > server.maxDiscovered.y) {
            server.maxDiscovered.y = cellPosition.y;
        }

        server.discovered.put(cellPosition, cell);
    }

    /**
     * Stores a station.
     *
     * @param cellPosition the cell position
     * @param st           the st
     */
    public void station(Position cellPosition, Station st) {
        //put the station even if it is already in the map as this will keep the
        //least recently visited ordering
        server.stations.put(cellPosition, st);
    }

    /**
     * Stores a task.
     *
     * @param task the task
     */
    public void task(PlannableTask task) {
        if (!server.tasks.contains(task)) {
            //if we haven't seen it already, then add it to the collection
            server.tasks.add(task);
            //finding this should trigger a replanning
            server.announce.task(task.pos);
        }
    }

    public void well(Position cellPosition, Well well) {
        if (!server.wells.containsKey(cellPosition)) {
            //if we haven't already seen it, then put it in the collection
            server.wells.put(cellPosition, well);
            //and trigger a replan in concerned tankers
            server.announce.well(cellPosition);
        }
    }

    public void pump(FuelPump pump) {
        server.pump = pump;
    }

    /**
     * Finds a task and applies a consumer on it.
     *
     * @param task the task
     * @param fn   the fn
     */
    private void findTaskAndApply(PlannableTask task, Consumer<PlannableTask> fn) {
        //find the first task that is in the same location - i.e. same task different instance
        server.tasks.stream()
                .filter(t -> t.pos.equals(task.pos))
                .findFirst()
                .ifPresent(fn); //apply consumer on it
    }

    /**
     * Delete a task from the collection, as it has been fulfilled.
     *
     * @param task the task
     */
    public void finishedTask(PlannableTask task) {
        //find and update task
        findTaskAndApply(task, t -> t.supplied += t.required());
        server.tasks.remove(task); //but still remove it from the database
    }

    /**
     * Increase the supplied amount of a task.
     *
     * @param water the amount
     * @param task  the task
     */
    public void suppliedTask(int water, PlannableTask task) {
        findTaskAndApply(task, t -> t.supplied += water);
    }

    /**
     * Show/hide a task from other agents.
     * @param task the task
     * @param lock true to hide, false to show
     */
    public void setTaskLock(PlannableTask task, int lock) {
        findTaskAndApply(task, t -> t.lockFor(lock));
        if (lock == -1 && task.locked(-1)) {
            //if task is free then reannounce it
            server.announce.task(task.pos);
        }
    }

    /**
     * Mark a path as not explorable to other agents.
     *
     * @param path the path
     */
    public void startedExploring(int path) {
        int path2 = path <= 4 ? path + 4 : path - 4;
        server.lockedExplorationPaths.add(path);
        server.lockedExplorationPaths.add(path2);
    }

    /**
     * Mark a path as explorable again.
     *
     * @param path the path
     */
    public void stoppedExploring(int path) {
        int path2 = path <= 4 ? path + 4 : path - 4;
        server.lockedExplorationPaths.remove(path);
        server.lockedExplorationPaths.remove(path2);
    }
}
