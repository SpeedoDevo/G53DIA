package uk.ac.nott.cs.g53dia.multidemo.fleet.data;

import java.util.ArrayList;

import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Position;

import static uk.ac.nott.cs.g53dia.multidemo.tanker.data.exchange.DownstreamDataExchange.Listener;

/**
 * An event bus that notifies tankers when new data is available.
 * Created by Barnabas on 18/03/2016.
 */
public class EventHandler {
    private ArrayList<Listener> listeners = new ArrayList<>();

    public void task(Position pos) {
        for (Listener listener : listeners) {
            listener.onTask(pos);
        }
    }

    public void well(Position pos) {
        for (Listener listener : listeners) {
            listener.onWell(pos);
        }
    }

    public void listener(Listener listener) {
        listeners.add(listener);
    }
}
