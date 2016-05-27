package uk.ac.nott.cs.g53dia.multidemo.tanker.reactive;

import uk.ac.nott.cs.g53dia.multidemo.tanker.MegaTanker;
import uk.ac.nott.cs.g53dia.multidemo.tanker.data.Position;
import uk.ac.nott.cs.g53dia.multilibrary.Action;
import uk.ac.nott.cs.g53dia.multilibrary.MoveAction;
import uk.ac.nott.cs.g53dia.multilibrary.MoveTowardsAction;
import uk.ac.nott.cs.g53dia.multilibrary.RefuelAction;

/**
 * Explores the map in a windmill like patter, one blade at a time.
 * Created by Barnabas on 21/03/2016.
 */
class Explorer {
    private static final Position PUMP_POS = new Position();
    private Path[] paths = new Path[]{
            new Path() {
                @Override
                public int step1() {
                    return MoveAction.NORTHWEST;
                }

                @Override
                public int step2() {
                    return MoveAction.EAST;
                }
            },
            new Path() {
                @Override
                public int step1() {
                    return MoveAction.NORTHEAST;
                }

                @Override
                public int step2() {
                    return MoveAction.SOUTH;
                }
            },
            new Path() {
                @Override
                public int step1() {
                    return MoveAction.SOUTHEAST;
                }

                @Override
                public int step2() {
                    return MoveAction.WEST;
                }
            },
            new Path() {
                @Override
                public int step1() {
                    return MoveAction.SOUTHWEST;
                }

                @Override
                public int step2() {
                    return MoveAction.NORTH;
                }
            },
            new Path() {
                @Override
                public int step1() {
                    return MoveAction.NORTHWEST;
                }

                @Override
                public int step2() {
                    return MoveAction.SOUTH;
                }
            },
            new Path() {
                @Override
                public int step1() {
                    return MoveAction.NORTHEAST;
                }

                @Override
                public int step2() {
                    return MoveAction.WEST;
                }
            },
            new Path() {
                @Override
                public int step1() {
                    return MoveAction.SOUTHEAST;
                }

                @Override
                public int step2() {
                    return MoveAction.NORTH;
                }
            },
            new Path() {
                @Override
                public int step1() {
                    return MoveAction.SOUTHWEST;
                }

                @Override
                public int step2() {
                    return MoveAction.EAST;
                }
            },
    };
    private MegaTanker tanker;
    private int currentPath;
    private int exploreStep;
    private int moveCounter;

    Explorer(MegaTanker tanker) {
        this.tanker = tanker;
        setPath(tanker.data.request().nextExplorationPath());
    }

    public Action move() {
        switch (exploreStep) {
            case 0:
                if (++moveCounter > 37) {
                    moveCounter = 0;
                    exploreStep++;
                }
                return moveAction(paths[currentPath].step1());
            case 1:
                if (++moveCounter > 23) {
                    moveCounter = 0;
                    exploreStep++;
                }
                return moveAction(paths[currentPath].step2());
            case 2:
                if (++moveCounter > 37) {
                    moveCounter = 0;
                    exploreStep++;
                }
                return moveTowardsPump();
            case 3:
                exploreStep++;
                stop();
                return new RefuelAction();
        }
        return null;
    }

    boolean finished() {
        return exploreStep > 3;

    }

    void setPath(int path) {
        tanker.data.publish().startedExploring(path);
        currentPath = path;
        moveCounter = 0;
        exploreStep = 0;
    }

    /**
     * @param dir the direction
     * @return the {@link MoveAction} going in {@code dir}, also updates the believed position
     */
    private Action moveAction(int dir) {
        Position pos = tanker.data.own.pos;

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
        Position pos = tanker.data.own.pos;

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

        return new MoveTowardsAction(tanker.data.request().pump().getPoint());
    }

    void stop() {
        tanker.data.publish().stoppedExploring(currentPath);
    }

    private interface Path {
        int step1();

        int step2();
    }

}
