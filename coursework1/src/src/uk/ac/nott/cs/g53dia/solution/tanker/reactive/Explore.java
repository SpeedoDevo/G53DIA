package uk.ac.nott.cs.g53dia.solution.tanker.reactive;

import uk.ac.nott.cs.g53dia.library.Action;
import uk.ac.nott.cs.g53dia.library.Cell;
import uk.ac.nott.cs.g53dia.library.FuelPump;
import uk.ac.nott.cs.g53dia.library.MoveAction;
import uk.ac.nott.cs.g53dia.library.MoveTowardsAction;
import uk.ac.nott.cs.g53dia.library.RefuelAction;
import uk.ac.nott.cs.g53dia.library.Tanker;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Deliberative;
import uk.ac.nott.cs.g53dia.solution.tanker.deliberative.Position;

/**
 * Helper class that explores the Environment on fixed paths.
 */
public class Explore {
    private static final Position PUMP_POS = new Position(0, 0);
    private int exploreStep;
    private int moveCounter;
    private final Tanker tanker;
    private final Deliberative deliberative;

    public Explore(Tanker tanker, Deliberative deliberative) {
        this.tanker = tanker;
        this.deliberative = deliberative;
    }

    /**
     * Simple interface that makes it possible to easily use multiple types of explorations.
     */
    interface Explorer {
        int getSteps();

        Action move();
    }

    Explorer ex1 = new Explorer() {
        @Override
        public int getSteps() {
            return 8;
        }

        @Override
        public Action move() {
            int dir = 0;
            switch (exploreStep) {
                case 0:
                    dir = MoveAction.NORTHEAST;
                    break;
                case 1:
                    dir = MoveAction.NORTHWEST;
                    break;
                case 2:
                    dir = MoveAction.SOUTHWEST;
                    break;
                case 3:
                    dir = MoveAction.SOUTHEAST;
                    break;
                case 4:
                    dir = MoveAction.SOUTHEAST;
                    break;
                case 5:
                    dir = MoveAction.SOUTHWEST;
                    break;
                case 6:
                    dir = MoveAction.NORTHWEST;
                    break;
                case 7:
                    dir = MoveAction.NORTHEAST;
                    break;
            }
            if (++moveCounter > 24) {
                moveCounter = 0;
                exploreStep++;
            }
            return moveAction(dir);
        }
    };

    Explorer ex2 = new Explorer() {
        @Override
        public int getSteps() {
            return 24;
        }

        @Override
        public Action move() {
            int dir = 0;
            switch (exploreStep) {
                case 0:
                    dir = MoveAction.NORTH;
                    break;
                case 1:
                    dir = MoveAction.EAST;
                    break;
                case 2:
                    dir = MoveAction.SOUTHWEST;
                    break;
                case 3:
                    dir = MoveAction.NORTHEAST;
                    break;
                case 4:
                    dir = MoveAction.SOUTH;
                    break;
                case 5:
                    dir = MoveAction.WEST;
                    break;
                case 6:
                    dir = MoveAction.EAST;
                    break;
                case 7:
                    dir = MoveAction.SOUTH;
                    break;
                case 8:
                    dir = MoveAction.NORTHWEST;
                    break;
                case 9:
                    dir = MoveAction.SOUTHEAST;
                    break;
                case 10:
                    dir = MoveAction.WEST;
                    break;
                case 11:
                    dir = MoveAction.NORTH;
                    break;
                case 12:
                    dir = MoveAction.SOUTH;
                    break;
                case 13:
                    dir = MoveAction.WEST;
                    break;
                case 14:
                    dir = MoveAction.NORTHEAST;
                    break;
                case 15:
                    dir = MoveAction.SOUTHWEST;
                    break;
                case 16:
                    dir = MoveAction.NORTH;
                    break;
                case 17:
                    dir = MoveAction.EAST;
                    break;
                case 18:
                    dir = MoveAction.WEST;
                    break;
                case 19:
                    dir = MoveAction.NORTH;
                    break;
                case 20:
                    dir = MoveAction.SOUTHEAST;
                    break;
                case 21:
                    dir = MoveAction.NORTHWEST;
                    break;
                case 22:
                    dir = MoveAction.EAST;
                    break;
                case 23:
                    dir = MoveAction.SOUTH;
                    break;
            }
            if (++moveCounter > 32) {
                moveCounter = 0;
                exploreStep++;
            }
            return moveAction(dir);
        }
    };

    Explorer ex3 = new Explorer() {
        @Override
        public int getSteps() {
            return 16;
        }

        @Override
        public Action move() {
            int dir = 0;
            switch (exploreStep) {
                case 0:
                    dir = MoveAction.NORTHWEST;
                    break;
                case 1:
                    dir = MoveAction.EAST;
                    break;
                case 2:
                    dir = MoveAction.EAST;
                    break;
                case 3:
                    dir = MoveAction.SOUTHWEST;
                    break;
                case 4:
                    dir = MoveAction.NORTHEAST;
                    break;
                case 5:
                    dir = MoveAction.SOUTH;
                    break;
                case 6:
                    dir = MoveAction.SOUTH;
                    break;
                case 7:
                    dir = MoveAction.NORTHWEST;
                    break;
                case 8:
                    dir = MoveAction.SOUTHEAST;
                    break;
                case 9:
                    dir = MoveAction.WEST;
                    break;
                case 10:
                    dir = MoveAction.WEST;
                    break;
                case 11:
                    dir = MoveAction.NORTHEAST;
                    break;
                case 12:
                    dir = MoveAction.SOUTHWEST;
                    break;
                case 13:
                    dir = MoveAction.NORTH;
                    break;
                case 14:
                    dir = MoveAction.NORTH;
                    break;
                case 15:
                    dir = MoveAction.SOUTHEAST;
                    break;
            }
            if (++moveCounter > 24) {
                moveCounter = 0;
                exploreStep++;
            }
            return moveAction(dir);
        }
    };

    Explorer ex4 = new Explorer() {
        @Override
        public int getSteps() {
            return 12;
        }

        @Override
        public Action move() {
            int dir = 0;
            switch (exploreStep) {
                case 0:
                    dir = MoveAction.NORTH;
                    break;
                case 1:
                    dir = MoveAction.EAST;
                    break;
                case 2:
                    dir = MoveAction.SOUTHWEST;
                    break;
                case 3:
                    dir = MoveAction.EAST;
                    break;
                case 4:
                    dir = MoveAction.SOUTH;
                    break;
                case 5:
                    dir = MoveAction.NORTHWEST;
                    break;
                case 6:
                    dir = MoveAction.SOUTH;
                    break;
                case 7:
                    dir = MoveAction.WEST;
                    break;
                case 8:
                    dir = MoveAction.NORTHEAST;
                    break;
                case 9:
                    dir = MoveAction.WEST;
                    break;
                case 10:
                    dir = MoveAction.NORTH;
                    break;
                case 11:
                    dir = MoveAction.SOUTHEAST;
                    break;
            }
            if (++moveCounter > 32) {
                moveCounter = 0;
                exploreStep++;
            }
            return moveAction(dir);
        }
    };

    Explorer ex5 = new Explorer() {
        @Override
        public int getSteps() {
            return 11;
        }

        @Override
        public Action move() {
            int dir = 0;
            switch (exploreStep) {
                case 0:
                    dir = MoveAction.NORTHWEST;
                    break;
                case 1:
                    dir = MoveAction.EAST;
                    break;
                case 3:
                    dir = MoveAction.NORTHEAST;
                    break;
                case 4:
                    dir = MoveAction.SOUTH;
                    break;
                case 6:
                    dir = MoveAction.SOUTHEAST;
                    break;
                case 7:
                    dir = MoveAction.WEST;
                    break;
                case 9:
                    dir = MoveAction.SOUTHWEST;
                    break;
                case 10:
                    dir = MoveAction.NORTH;
                    break;
            }
            switch (exploreStep % 3) {
                case 0:
                    if (++moveCounter > 37) {
                        moveCounter = 0;
                        exploreStep++;
                    }
                    return moveAction(dir);
                case 1:
                    if (++moveCounter > 23) {
                        moveCounter = 0;
                        exploreStep++;
                    }
                    return moveAction(dir);
                case 2:
                    if (++moveCounter > 37) {
                        moveCounter = 0;
                        exploreStep++;
                    }
                    return moveTowardsPump();
            }
            return moveAction(dir);
        }
    };

    Explorer reexplorer = new Explorer() {
        @Override
        public int getSteps() {
            return 11;
        }

        @Override
        public Action move() {
            int dir = 0;
            switch (exploreStep) {
                case 0:
                    dir = MoveAction.SOUTHEAST;
                    break;
                case 1:
                    dir = MoveAction.NORTH;
                    break;
                case 3:
                    dir = MoveAction.SOUTHWEST;
                    break;
                case 4:
                    dir = MoveAction.EAST;
                    break;
                case 6:
                    dir = MoveAction.NORTHWEST;
                    break;
                case 7:
                    dir = MoveAction.SOUTH;
                    break;
                case 9:
                    dir = MoveAction.NORTHEAST;
                    break;
                case 10:
                    dir = MoveAction.WEST;
                    break;
            }
            switch (exploreStep % 3) {
                case 0:
                    if (++moveCounter > 37) {
                        moveCounter = 0;
                        exploreStep++;
                    }
                    return moveAction(dir);
                case 1:
                    if (++moveCounter > 23) {
                        moveCounter = 0;
                        exploreStep++;
                    }
                    return moveAction(dir);
                case 2:
                    if (++moveCounter > 37) {
                        moveCounter = 0;
                        exploreStep++;
                    }
                    return moveTowardsPump();
            }
            return moveAction(dir);
        }
    };

    Explorer explorer = ex5;

    public boolean finished() {
        return exploreStep > explorer.getSteps();
    }

    /**
     * @param cell the current cell
     * @return the next action on the exploration path
     */
    public Action move(Cell cell) {
        if (cell instanceof FuelPump) {
            if (tanker.getFuelLevel() < Tanker.MAX_FUEL) {
                return new RefuelAction();
            }
        }
        return explorer.move();
    }

    boolean reexploreType;

    /**
     * Resets counters, and switches the exploration path.
     */
    public void resetToReexplore() {
        exploreStep = 0;
        moveCounter = 0;
        explorer = reexploreType ? ex5 : reexplorer;
        reexploreType = !reexploreType;
    }

    /**
     * @param dir the direction
     * @return the {@link MoveAction} going in {@code dir}, also updates the believed position
     */
    private Action moveAction(int dir) {
        Position pos = deliberative.getPos();

        switch (dir) {
            case MoveAction.NORTH:
                pos.y++;
                break;
            case MoveAction.SOUTH:
                pos.y--;
                break;
            case MoveAction.EAST:
                pos.x++;
                break;
            case MoveAction.WEST:
                pos.x--;
                break;
            case MoveAction.NORTHEAST:
                pos.x++;
                pos.y++;
                break;
            case MoveAction.NORTHWEST:
                pos.x--;
                pos.y++;
                break;
            case MoveAction.SOUTHEAST:
                pos.x++;
                pos.y--;
                break;
            case MoveAction.SOUTHWEST:
                pos.x--;
                pos.y--;
                break;
        }
        return new MoveAction(dir);
    }

    /**
     * @return the {@link MoveTowardsAction} that moves the tanker towards the pump,
     * also updates the believed position
     */
    private Action moveTowardsPump() {
        Position pos = deliberative.getPos();

        int dx = PUMP_POS.x - pos.x;
        int dy = PUMP_POS.y - pos.y;
        if (dx < 0) {
            pos.x--;
        } else if (dx > 0) {
            pos.x++;
        }
        if (dy < 0) {
            pos.y--;
        } else if (dy > 0) {
            pos.y++;
        }

        return new MoveTowardsAction(deliberative.getPumpLocation());
    }
}