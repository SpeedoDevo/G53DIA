package uk.ac.nott.cs.g53dia.multidemo.fleet.data;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import uk.ac.nott.cs.g53dia.multidemo.tanker.data.PlannableTask;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Position;
import uk.ac.nott.cs.g53dia.multidemo.util.DebugPrint;
import uk.ac.nott.cs.g53dia.multilibrary.FuelPump;
import uk.ac.nott.cs.g53dia.multilibrary.Well;

/**
 * Handles data requests.
 * Created by Barnabas on 18/03/2016.
 */
public class Transmitter {
    private Server server;

    Transmitter(Server server) {
        this.server = server;
    }

    /**
     * @param pos the position
     * @return the path index that is LIKELY to explore the {@code pos}
     */
    private static int getQuadrant(Position pos) {
        if (pos.y > 0) {
            if (pos.x < 0) {
                if (Math.abs(pos.x) <= Math.abs(pos.y)) {
                    return 0;
                } else {
                    return 4;
                }
            } else {
                if (Math.abs(pos.x) > Math.abs(pos.y)) {
                    return 1;
                } else {
                    return 5;
                }
            }
        } else {
            if (pos.x >= 0) {
                if (Math.abs(pos.x) <= Math.abs(pos.y)) {
                    return 2;
                } else {
                    return 6;
                }
            } else {
                if (Math.abs(pos.x) > Math.abs(pos.y)) {
                    return 3;
                } else {
                    return 7;
                }
            }
        }
    }

    /**
     * Gives a copy of tasks, if {@code lockedToo} is true, it shows locked tasks as well.
     *
     * @param forTanker whether show locked tasks
     * @return copy of current tasks
     */
    public PlannableTask[] tasks(int forTanker) {
        return server.tasks.stream()
                .filter(task -> !task.locked(forTanker))
                .map(PlannableTask::copy)
                .toArray(PlannableTask[]::new);
    }

    /**
     * @param task the task
     * @return true if the task is still in the list - i.e. incomplete
     */
    public boolean hasTask(PlannableTask task) {
        return server.tasks.contains(task);
    }

    /**
     * @return a reference to the pump
     */
    public FuelPump pump() {
        return server.pump;
    }

    /**
     * @return a map of wells
     */
    public Map<Position, Well> wells() {
        return server.wells;
    }

    /**
     * If the initial exploration is incomplete then it returns paths in order, otherwise it returns
     * the path where the most least-recently-visited stations are.
     *
     * @return the index of the next exploration path
     */
    public int nextExplorationPath() {
        DebugPrint.println(server.stations.keySet());
        if (!initialFinished()) {
            //still initial, return next path
            return server.nextExplorationPath++;
        } else {
            //take at most 5 stations into account
            int until = Math.min(server.stations.size(), 5);
            //but at least one
            if (until == 0) {
                System.err.println("no stations found");
                return -1;
            }

            DebugPrint.println(server.lockedExplorationPaths);
            return server.stations.keySet().stream() //convert positions
                    .map(Transmitter::getQuadrant) //into quadrants
                    //filter out the ones that are locked
                    .filter(q -> !server.lockedExplorationPaths.contains(q))
                    .limit(until) //get at most 5
                    //count how many least-recently-visited stations are there in each quadrant
                    .collect(Collectors.groupingBy(
                            Function.identity(),
                            Collectors.reducing(0, e -> 1, Integer::sum)
                    )).entrySet().stream() //make new stream out of entries
                    //reverse natural sort them based on value
                    .sorted(Map.Entry.comparingByValue((o1, o2) -> o2.compareTo(o1)))
                    .peek(DebugPrint::println)
                    //get the first (biggest value) (or default which should never happen)
                    .findFirst().orElse(new AbstractMap.SimpleEntry<>(-1, -1))
                    .getKey(); //return the biggest
        }
    }

    public boolean initialFinished() {
        return server.nextExplorationPath > 3;
    }

    /**
     * @return true if there are unlocked tasks
     * @param lock which tanker
     */
    public boolean hasTasks(int lock) {
        return tasks(lock).length > 0;
    }
}
