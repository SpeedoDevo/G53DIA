package uk.ac.nott.cs.g53dia.library;

import java.util.Random;

/**
 * Created by Barnabas on 18/02/2016.
 */
public class NonstochasticStation extends Station {
    final static double NEW_TASK_PROBABILITY = 0.001;
    private Task task;
    Random generate = new Random(1000000);

    NonstochasticStation(Point pos) {
        super(pos);
    }

    protected void generateTask() {
        if (this.task==null) {
            if (generate.nextDouble()<NEW_TASK_PROBABILITY) {
                this.task=new Task(this);
            }
        }
    }

    public Task getTask() {
        return this.task;
    }

    protected void removeTask() {
        this.task = null;
    }

    protected Station clone() {
        NonstochasticStation s = new NonstochasticStation(this.getPoint());
        s.task = this.task;
        return s;
    }

    public boolean equals(Object o) {
        Station s = (Station)o;
        return this.getPoint().equals(s.getPoint());
    }
}
