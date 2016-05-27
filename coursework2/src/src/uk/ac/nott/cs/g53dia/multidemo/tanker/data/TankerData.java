package uk.ac.nott.cs.g53dia.multidemo.tanker.data;

import static uk.ac.nott.cs.g53dia.multidemo.tanker.data.TankerData.S.initialExploration;

/**
 * This class contains all state information of the tanker.
 * Created by Barnabas on 18/03/2016.
 */
public class TankerData {
    public int reexploreTimes;
    public S state = initialExploration;
    public Position pos = new Position();
    public boolean needsReplanning;
    public int extraWaterDelivered;
    public int fuel;
    public int water;
    public int number;

    public enum S {
        initialExploration,
        executePlan,
        reexploration
    }
}
