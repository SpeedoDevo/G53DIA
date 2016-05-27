package uk.ac.nott.cs.g53dia.multidemo.tanker;


import uk.ac.nott.cs.g53dia.multidemo.fleet.data.Server;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Data;
import uk.ac.nott.cs.g53dia.multidemo.tanker.deliberative.Planner;
import uk.ac.nott.cs.g53dia.multidemo.tanker.reactive.Reactive;
import uk.ac.nott.cs.g53dia.multilibrary.Action;
import uk.ac.nott.cs.g53dia.multilibrary.Cell;
import uk.ac.nott.cs.g53dia.multilibrary.Tanker;

/**
 * My tanker implementation. This class just glues together a Reactive and deliberative layer.
 * Created by Barnabas on 16/02/2016.
 */
public class MegaTanker extends Tanker {

    public Data data;
    public Planner planner = new Planner(this);
    private Reactive reactive;

    public MegaTanker(Server bs, int number) {
        data = new Data(this, bs);
        reactive = new Reactive(this);
        data.own.number = number;
    }

    @Override
    public Action senseAndAct(Cell[][] view, long timestep) {
        return reactive.senseAndAct(view);
    }

    public long waterDelivered() {
        return (getCompletedCount() > 0) ? (getScore() / getCompletedCount()) : 0;
    }

    public long score() {
        return getScore();
    }

    public Results results() {
        return new Results(
                data.own.reexploreTimes,
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

        Results(int reexplorations, int completed, long deliveredWater, long score) {
            this.reexplorations = reexplorations;
            this.completed = completed;
            this.deliveredWater = deliveredWater;
            this.score = score;
        }

        @Override
        public String toString() {
            return
                    reexplorations + "-" +
                            completed + "-" +
                            deliveredWater + "-" +
                            score;
        }
    }
}
