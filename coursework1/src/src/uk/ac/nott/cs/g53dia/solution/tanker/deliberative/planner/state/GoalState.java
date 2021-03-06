package uk.ac.nott.cs.g53dia.solution.tanker.deliberative.planner.state;

/**
 * A simple interface to check if future states complete some arbitrary goal.
 */
public interface GoalState {
    /**
     * Should return true if the current state fulfills some goal.
     *
     * @param start passed to check difference
     */
    boolean matches(State start, State current);
}
