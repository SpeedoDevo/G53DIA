package uk.ac.nott.cs.g53dia.multidemo.tanker.data;

import java.util.Map;

import uk.ac.nott.cs.g53dia.multidemo.fleet.data.Server;
import uk.ac.nott.cs.g53dia.multidemo.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.exchange.DataExchange;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.exchange.DownstreamDataExchange;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.exchange.UpstreamDataExchange;
import uk.ac.nott.cs.g53dia.multidemo.util.DebugPrint;
import uk.ac.nott.cs.g53dia.multilibrary.Cell;
import uk.ac.nott.cs.g53dia.multilibrary.FuelPump;
import uk.ac.nott.cs.g53dia.multilibrary.Station;
import uk.ac.nott.cs.g53dia.multilibrary.Task;
import uk.ac.nott.cs.g53dia.multilibrary.Well;

/**
 * Local data storage.
 * Created by Barnabas on 18/03/2016.
 */
public class Data implements DataExchange {
    public TankerData own = new TankerData();
    private UpstreamDataExchange publish;
    private DownstreamDataExchange request;

    public Data(MegaTanker tanker, Server bs) {
        publish = new Upstream(bs);
        request = new Downstream(bs);
        DownstreamDataExchange.Listener listener = new Listener(tanker);
        bs.announce.listener(listener);
    }

    @Override
    public UpstreamDataExchange publish() {
        return publish;
    }

    @Override
    public DownstreamDataExchange request() {
        return request;
    }

    private class Upstream implements UpstreamDataExchange {
        private final Server bs;

        Upstream(Server bs) {
            this.bs = bs;
        }

        @Override
        public void cell(Position cellPosition, Cell cell) {
            if (!cellPosition.isInBounds()) return;
            bs.post.cell(cellPosition, cell);


            if (cell instanceof Station) {
                //if the cell is a station
                Station st = (Station) cell;
                //put it in the stations collection
                //this automatically puts recently seen stations at the end
                bs.post.station(cellPosition, st);


                Task t = st.getTask();
                if (t != null) {
                    //if the station has a task
                    task(new PlannableTask(st.getTask(), cellPosition));
                }


            } else if (cell instanceof Well) {
                //if the cell is a well
                bs.post.well(cellPosition, (Well) cell);


            } else if (cell instanceof FuelPump) {
                //save an instance to the pump
                bs.post.pump((FuelPump) cell);
            }
        }

        @Override
        public void task(PlannableTask t) {
            bs.post.task(t);
        }

        @Override
        public void finishedTask(PlannableTask task) {
            bs.post.finishedTask(task);
        }

        @Override
        public void suppliedTask(int water, PlannableTask task) {
            bs.post.suppliedTask(water, task);
        }

        @Override
        public void lockTask(PlannableTask task) {
            bs.post.setTaskLock(task, own.number);
        }

        @Override
        public void unlockTask(PlannableTask task) {
            bs.post.setTaskLock(task, -1);
        }

        @Override
        public void startedExploring(int path) {
            bs.post.startedExploring(path);
        }

        @Override
        public void stoppedExploring(int path) {
            bs.post.stoppedExploring(path);
        }
    }

    private class Downstream implements DownstreamDataExchange {
        private final Server bs;

        Downstream(Server bs) {
            this.bs = bs;
        }

        @Override
        public PlannableTask[] tasks() {
            return bs.get.tasks(own.number);
        }

        @Override
        public boolean hasTask(PlannableTask task) {
            return bs.get.hasTask(task);
        }

        @Override
        public FuelPump pump() {
            return bs.get.pump();
        }

        @Override
        public Map<Position, Well> wells() {
            return bs.get.wells();
        }

        @Override
        public boolean hasTasks() {
            return bs.get.hasTasks(own.number);
        }

        @Override
        public int nextExplorationPath() {
            return bs.get.nextExplorationPath();
        }

        @Override
        public boolean initialFinished() {
            return bs.get.initialFinished();
        }
    }

    private class Listener implements DownstreamDataExchange.Listener {
        private final MegaTanker tanker;
        private Position pump;

        Listener(MegaTanker tanker) {
            this.tanker = tanker;
            pump = new Position();
        }

        @Override
        public void onTask(Position pos) {
            DebugPrint.print("task at " + pos);
            onEvent(pos);
        }

        @Override
        public void onWell(Position pos) {
            DebugPrint.print("well at " + pos);
            onEvent(pos);
        }

        private void onEvent(Position pos) {
            int distFromTanker = pos.distanceTo(tanker.data.own.pos);
            int distFromPump = pos.distanceTo(pump);
            if (distFromPump + distFromTanker < tanker.getFuelLevel()) {
                //if the current plan is discarded and go to the event location can we still refuel
                own.needsReplanning = true;
                DebugPrint.println(" replanned");
            } else {
                DebugPrint.println(" discarded");
            }
        }
    }
}
