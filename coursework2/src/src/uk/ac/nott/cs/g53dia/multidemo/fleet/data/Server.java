package uk.ac.nott.cs.g53dia.multidemo.fleet.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import uk.ac.nott.cs.g53dia.multidemo.tanker.data.PlannableTask;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Position;
import uk.ac.nott.cs.g53dia.multidemo.util.DebugPrint;
import uk.ac.nott.cs.g53dia.multilibrary.Cell;
import uk.ac.nott.cs.g53dia.multilibrary.EmptyCell;
import uk.ac.nott.cs.g53dia.multilibrary.FuelPump;
import uk.ac.nott.cs.g53dia.multilibrary.MoveTowardsAction;
import uk.ac.nott.cs.g53dia.multilibrary.Station;
import uk.ac.nott.cs.g53dia.multilibrary.Well;

/**
 * Shared model of the world.
 * Created by Barnabas on 18/03/2016.
 */
public class Server {

    public Receiver post = new Receiver(this);
    public Transmitter get = new Transmitter(this);
    public EventHandler announce = new EventHandler();


    public Map<Position, Well> wells = new HashMap<>();
    /**
     * Collection of all stations. Can be iterated in last seen order.
     */
    public LinkedHashMap<Position, Station> stations = new LinkedHashMap<>(16, 0.75f, true);

    /**
     * Collection of tasks ordered based on required water.
     */
    public PriorityQueue<PlannableTask> tasks = new PriorityQueue<>();

    /**
     * Reference to the pump, for generating {@link MoveTowardsAction}{@code s}
     */
    public FuelPump pump;

    /**
     * Initial exploration counter.
     */
    int nextExplorationPath = 0;

    /**
     * Reexploration paths that cannot be started until the last agent starting it finishes.
     */
    Set<Integer> lockedExplorationPaths = new HashSet<>();

    /**
     * Copy of the map, for debugging purposes.
     */
    Map<Position, Cell> discovered = new HashMap<>();
    Position minDiscovered = new Position();
    Position maxDiscovered = new Position();

    void printDiscovered() {
        for (int i = maxDiscovered.y; i >= minDiscovered.y; i--) {
            for (int j = minDiscovered.x; j <= maxDiscovered.x; j++) {
                String ch = " ";
                Position p = new Position(j, i);
                if (discovered.containsKey(p)) {
                    Cell c = discovered.get(p);
                    if (c instanceof EmptyCell) {
                        ch = "_";
                    } else if (c instanceof Station) {
                        if (((Station) c).getTask() != null) {
                            ch = "T";
                        } else {
                            ch = "S";
                        }
                    } else if (c instanceof Well) {
                        ch = "W";
                    } else {
                        ch = "F";
                    }
                }
                DebugPrint.print(ch);
            }
            DebugPrint.println();
        }
    }

}
