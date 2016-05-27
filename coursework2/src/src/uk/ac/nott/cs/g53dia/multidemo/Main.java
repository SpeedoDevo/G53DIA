package uk.ac.nott.cs.g53dia.multidemo;

import java.util.ArrayList;

import javax.swing.WindowConstants;

import uk.ac.nott.cs.g53dia.multidemo.fleet.HierarchicalFleet;
import uk.ac.nott.cs.g53dia.multidemo.util.DebugPrint;
import uk.ac.nott.cs.g53dia.multilibrary.ActionFailedException;
import uk.ac.nott.cs.g53dia.multilibrary.Environment;
import uk.ac.nott.cs.g53dia.multilibrary.OutOfFuelException;
import uk.ac.nott.cs.g53dia.multilibrary.Tanker;
import uk.ac.nott.cs.g53dia.multilibrary.TankerViewer;

/**
 * Simulation entry point.
 */
public class Main {

    private static final boolean DISPLAY = false;
    private static final boolean PRINT_PROGRESS_RESULT = false;
    /**
     * Time for which execution pauses so that GUI can update.
     * Reducing this value causes the simulation to run faster.
     */
    private static final int DELAY = 0;
    /**
     * Number of timesteps to execute
     */
    private static final int DURATION = 10 * 10000;

    public static void main(String[] args) {
        experiment();
    }

    private static HierarchicalFleet.Results run() {
        // Create an environment
        Environment env = new Environment((Tanker.MAX_FUEL / 2) - 5);

        //Create a fleet
        HierarchicalFleet fleet = new HierarchicalFleet();

        // Create a GUI window to show our tanker
        TankerViewer tv;
        if (DISPLAY) {
            tv = new TankerViewer(fleet);
            tv.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }
        // Start executing the Tanker
        while (env.getTimestep() < DURATION) {
            if (env.getTimestep() % 10000 == 0
                    && PRINT_PROGRESS_RESULT) System.out.println("tick " + env.getTimestep());
            // Advance the environment timestep
            env.tick();
            // Update the GUI
            if (DISPLAY) tv.tick(env);


            try {
                fleet.tick(env);
            } catch (OutOfFuelException dte) {
                DebugPrint.println("Tanker out of fuel!");
                break;
            } catch (ActionFailedException afe) {
                System.err.println("Failed: " + afe.getMessage());
            }

            if (DISPLAY) {
                try {
                    Thread.sleep(DELAY);
                } catch (Exception ignored) {
                }
            }
        }

        return fleet.results();
    }

    private static void experiment() {
        ArrayList<HierarchicalFleet.Results> res = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            if (PRINT_PROGRESS_RESULT) System.out.println("run " + (1 + i));
            res.add(run());
        }

        if (PRINT_PROGRESS_RESULT) res.stream().forEach(System.out::println);
    }


}
