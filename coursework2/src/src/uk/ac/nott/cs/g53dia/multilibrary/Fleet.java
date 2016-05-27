package uk.ac.nott.cs.g53dia.multilibrary;

import java.util.*;

public class Fleet extends ArrayList<Tanker>{

    private static final long serialVersionUID = 8031611383212571139L;

    /**
     * The average score achieved by tankers in the fleet
     */
    public long getScore() {
	int delivered = 0;
	int completed = 0;
	
	for (Tanker t:this) {
	    delivered += t.waterDelivered;
	    completed += t.completedCount;
	}
		
	// Return the average score for each tanker 
	return ((long)delivered * (long)completed) / (long)this.size();
    }
}
