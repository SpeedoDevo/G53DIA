package uk.ac.nott.cs.g53dia.solution.tanker.deliberative;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;

import uk.ac.nott.cs.g53dia.library.Cell;
import uk.ac.nott.cs.g53dia.library.EmptyCell;
import uk.ac.nott.cs.g53dia.library.FuelPump;
import uk.ac.nott.cs.g53dia.library.Station;
import uk.ac.nott.cs.g53dia.library.Well;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.PlannableTask;

/**
 * Simple class that contains all the information of the tanker.
 */
public class Beliefs {
    /**
     * Collection of all cells, for debugging purposes.
     */
    Map<Position, Cell> discovered = new HashMap<>();
    Position minDiscovered = new Position(0, 0);
    Position maxDiscovered = new Position(0, 0);


    public Map<Position, Well> wells = new HashMap<>();
    /**
     * Collection of all stations. Can be iterated in last seen order.
     */
    public LinkedHashMap<Position, Station> stations = new LinkedHashMap<>(16, 0.75f, true);
    /**
     * Collection of tasks ordered based on required water.
     */
    public PriorityQueue<PlannableTask> tasks = new PriorityQueue<>();


    public FuelPump pump;

    public int fuel = 0;
    public int water = 0;
    public int extraWaterDelivered = 0;
    public Position pos = new Position(0, 0);

    /**
     * Prints the current beliefs, for debugging purposes.
     *
     * @param timestep the timestep for calculating discovery efficiency.
     */
    public void print(long timestep) {
        System.out.println(minDiscovered + " " + maxDiscovered);
        System.out.printf("efficiency: %d/%d=%.2f\n",
                discovered.size(), timestep, (float) discovered.size() / timestep);
        System.out.printf("discovered: %.2f%%\n", (float) discovered.size() / 100);
        //print the map
        for (int i = maxDiscovered.y; i > minDiscovered.y; i--) {
            for (int j = minDiscovered.x; j < maxDiscovered.x; j++) {
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
                System.out.print(ch + ',');
            }
            System.out.println();
        }

        //list of wells
        System.out.println("wells " + wells.size());
        for (Well well : wells.values()) {
            System.out.println("well " + well.getPoint());
        }

        //list of stations
        System.out.println("stations " + stations.size());
        for (Station station : stations.values()) {
            System.out.println("station " + station.getPoint());
        }

        //list of tasks
        System.out.println("tasks " + tasks.size());
        PriorityQueue<PlannableTask> ts = new PriorityQueue<>(tasks);
        while (ts.size() > 0) {
            PlannableTask t = ts.poll();
            System.out.printf("task %d, %s\n", t.required(), t.pos);
        }
    }
}
