package uk.ac.nott.cs.g53dia.solution;

import java.awt.event.WindowEvent;

import javax.swing.WindowConstants;

import uk.ac.nott.cs.g53dia.demo.DemoTanker;
import uk.ac.nott.cs.g53dia.library.Action;
import uk.ac.nott.cs.g53dia.library.ActionFailedException;
import uk.ac.nott.cs.g53dia.library.BigTankerViewer;
import uk.ac.nott.cs.g53dia.library.Cell;
import uk.ac.nott.cs.g53dia.library.Environment;
import uk.ac.nott.cs.g53dia.library.Fleet;
import uk.ac.nott.cs.g53dia.library.OutOfFuelException;
import uk.ac.nott.cs.g53dia.library.Tanker;
import uk.ac.nott.cs.g53dia.library.TankerViewer;
import uk.ac.nott.cs.g53dia.solution.tanker.MegaTanker;

/**
 * An example of how to simulate execution of a tanker agent in the sample (task) environment.
 * <p>
 * Creates a default {@link Environment}, a {@link DemoTanker} and a GUI window
 * (a {@link TankerViewer}) and executes the Tanker for DURATION days in the environment.
 *
 * @author Julian Zappala
 */

public class Main {

    /**
     * Time for which execution pauses so that GUI can update.
     * Reducing this value causes the simulation to run faster.
     */
    private static final int DELAY = 10;

    /**
     * Number of timesteps to execute
     */
    private static final int DURATION = 10 * 10000;

    @SuppressWarnings("BooleanVariableAlwaysNegated") private static final boolean HEADLESS = false;

    private static final int EXPERIMENTS = 50;

    private Main() {
    }

    public static void main(String[] args) {
        test();
        //        MegaTanker.Results[] res = new MegaTanker.Results[EXPERIMENTS];
        //        for (int i = 0; EXPERIMENTS > i; i++) {
        //            System.out.println("experiment " + (i + 1));
        //            res[i] = experiment();
        //        }
        //        System.out.println("\n\n\n\n\n\n\n" + Arrays.toString(res));
    }

    private static MegaTanker.Results experiment() {
        // Create an environment
        Environment env = new Environment((Tanker.MAX_FUEL / 2) - 5);
        // Create our tanker
        MegaTanker t = new MegaTanker();
        // Create a GUI window to show our tanker

        // Start executing the Tanker
        while (env.getTimestep() < (DURATION)) {
            if ((env.getTimestep() % 1000) == 0) {
                System.out.println("env.getTimestep() = " + env.getTimestep());
            }
            // Advance the environment timestep
            env.tick();
            // Get the current view of the tanker
            Cell[][] view = env.getView(t.getPosition(), Tanker.VIEW_RANGE);
            // Let the tanker choose an action
            Action a = t.senseAndAct(view, env.getTimestep());
            // Try to execute the action
            try {
                a.execute(env, t);
            } catch (OutOfFuelException dte) {
                System.out.println("Tanker out of fuel!");
                break;
            } catch (ActionFailedException afe) {
                System.err.println("Failed: " + afe.getMessage());
            }
        }
        return t.results();
    }

    @SuppressWarnings("PointlessBooleanExpression")
    public static void test() {
        // Create an environment
        Environment env = new Environment((Tanker.MAX_FUEL / 2) - 5);
        // Create our tanker
        Tanker t = new MegaTanker();
        Fleet f = new Fleet();
        f.add(t);
        // Create a GUI window to show our tanker

        BigTankerViewer tv;
        if (!HEADLESS) {
            tv = new BigTankerViewer(f);
            tv.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }
        // Start executing the Tanker
        while (env.getTimestep() < (DURATION)) {
            // Advance the environment timestep
            env.tick();
            // Update the GUI
            if (!HEADLESS) tv.tick(env);
            // Get the current view of the tanker
            Cell[][] view = env.getView(t.getPosition(), Tanker.VIEW_RANGE);
            // Let the tanker choose an action
            Action a = t.senseAndAct(view, env.getTimestep());
            // Try to execute the action
            try {
                a.execute(env, t);
            } catch (OutOfFuelException dte) {
                System.out.println("Tanker out of fuel!");
                break;
            } catch (ActionFailedException afe) {
                System.err.println("Failed: " + afe.getMessage());
            }

            try {
                Thread.sleep(DELAY);
            } catch (Exception ignored) {
            }
        }
        if (!HEADLESS) tv.dispatchEvent(new WindowEvent(tv, WindowEvent.WINDOW_CLOSING));
    }


}
