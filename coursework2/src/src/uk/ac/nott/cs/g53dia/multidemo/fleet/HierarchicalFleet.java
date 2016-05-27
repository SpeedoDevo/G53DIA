package uk.ac.nott.cs.g53dia.multidemo.fleet;

import java.util.Arrays;
import java.util.stream.Collectors;

import uk.ac.nott.cs.g53dia.multidemo.fleet.data.Server;
import uk.ac.nott.cs.g53dia.multidemo.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.multilibrary.Action;
import uk.ac.nott.cs.g53dia.multilibrary.ActionFailedException;
import uk.ac.nott.cs.g53dia.multilibrary.Cell;
import uk.ac.nott.cs.g53dia.multilibrary.Environment;
import uk.ac.nott.cs.g53dia.multilibrary.Fleet;
import uk.ac.nott.cs.g53dia.multilibrary.Tanker;

/**
 * My fleet of MegaTankers.
 * Created by Barnabas on 18/03/2016.
 */
public class HierarchicalFleet extends Fleet {
    private static final int TANKERS = 2;
    private final Server server = new Server();

    public HierarchicalFleet() {
        for (int i = 0; i < TANKERS; i++) {
            add(new MegaTanker(server, i));
        }
    }

    /**
     * SenseAndAct for a fleet of tankers.
     *
     * @param env the environment
     * @throws ActionFailedException
     */
    public void tick(Environment env) throws ActionFailedException {
        ActionFailedException exception = null;
        for (Tanker tank : this) {
            // Get the current view of the tanker
            Cell[][] view = env.getView(tank.getPosition(), Tanker.VIEW_RANGE);
            // Let the tanker choose an action
            Action a = tank.senseAndAct(view, env.getTimestep());
            // Try to execute the action

            try {
                a.execute(env, tank);
            } catch (ActionFailedException afe) {
                exception = afe;
            }
        }

        if (exception != null) {
            throw exception;
        }
    }

    public Results results() {
        return new Results(
                stream().map(t -> ((MegaTanker) t).results()).toArray(MegaTanker.Results[]::new),
                server.stations.size(), server.wells.size()
        );
    }

    public class Results {
        MegaTanker.Results[] tankers;
        int stationCnt;
        int wellCnt;

        Results(MegaTanker.Results[] tankers, int stationCnt, int wellCnt) {
            this.tankers = tankers;
            this.stationCnt = stationCnt;
            this.wellCnt = wellCnt;
        }

        @Override
        public String toString() {
            return Arrays.toString(tankers) + ", " + stationCnt + ", " + wellCnt + ", "
                    + getScore() + ", " + stream().collect(Collectors.summingLong(t -> ((MegaTanker) t).score()));
        }
    }
}
