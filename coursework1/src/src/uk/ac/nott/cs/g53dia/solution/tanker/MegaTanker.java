package uk.ac.nott.cs.g53dia.solution.tanker;

import uk.ac.nott.cs.g53dia.library.Action;
import uk.ac.nott.cs.g53dia.library.Cell;
import uk.ac.nott.cs.g53dia.library.Tanker;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Deliberative;
import uk.ac.nott.cs.g53dia.solution.tanker.reactive.Reactive;

/**
 * My tanker implementation. This class just glues together a Reactive and Deliberative layer.
 * Created by Barnabas on 16/02/2016.
 */
public class MegaTanker extends Tanker {

    Deliberative deliberative = new Deliberative(this);
    Reactive reactive = new Reactive(this, deliberative);

    @Override
    public Action senseAndAct(Cell[][] view, long timestep) {
        return reactive.senseAndAct(view, timestep);
    }

    public long waterDelivered() {
        return (getCompletedCount() > 0) ? (getScore() / getCompletedCount()) : 0;
    }

    public long score() {
        return getScore();
    }

    public Results results() {
        return new Results(
                reactive.reexploreTimes,
                getCompletedCount(),
                waterDelivered(),
                score()
        );
    }

    public static class Results {
        int reexplorations;
        int completed;
        long deliveredWater;
        long score;

        public Results(int reexplorations, int completed, long deliveredWater, long score) {
            this.reexplorations = reexplorations;
            this.completed = completed;
            this.deliveredWater = deliveredWater;
            this.score = score;
        }

        @Override
        public String toString() {
            return
                    reexplorations + "," +
                            completed + "," +
                            deliveredWater + "," +
                            score + ",\n";
        }
    }
}
