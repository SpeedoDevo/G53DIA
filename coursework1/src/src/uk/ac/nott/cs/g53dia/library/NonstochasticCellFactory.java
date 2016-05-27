package uk.ac.nott.cs.g53dia.library;

import java.util.Random;

/**
 * nonstochastic cell factury used for debugging.
 * Created by Barnabas on 18/02/2016.
 */
public class NonstochasticCellFactory implements CellFactory {
    Random generate = new Random(42);

    public void generateCell(Environment env, Point pos) {
        if (pos.x==0 & pos.y==0) {
            env.putCell(new FuelPump(pos));
        }
        else if (generate.nextDouble() < DefaultCellFactory.DEFAULT_WELL_DENSITY) {
            env.putCell(new Well(pos));
        }


        else if (generate.nextDouble() < DefaultCellFactory.DEFAULT_STATION_DENSITY) {
            env.putCell(new NonstochasticStation(pos));
            env.stations.add((Station)env.getCell(pos));
        }
        else {
            env.putCell(new EmptyCell(pos));
        }
    }
}
