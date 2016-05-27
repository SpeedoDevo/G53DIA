package uk.ac.nott.cs.g53dia.multilibrary;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 * A simple user interface for watching an individual Tanker.
 * 
 * @author Neil Madden.
 */
/*
 * Copyright (c) 2003 Stuart Reeves Copyright (c) 2003-2005 Neil Madden
 * (nem@cs.nott.ac.uk). Copyright (c) 2011 Julian Zappala (jxz@cs.nott.ac.uk).
 * See the file "license.terms" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
public class BigTankerViewer extends JFrame implements ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = -2810783821678793885L;
	TankerViewerIconFactory iconfactory;
	JLabel[][] cells;
	JLabel[][] tankers;
	JLabel tstep, fuel, pos, water, completed, delivered, score;
	Tanker tank;
	Fleet fleet;
	final static int SIZE = Tanker.MAX_FUEL+1;
	JLayeredPane lp;
	JComboBox tankerList;
	JPanel infop;

	public BigTankerViewer(Tanker Tanker) {
		this(Tanker, new DefaultTankerViewerIconFactory());
	}

	public BigTankerViewer(Tanker Tanker, TankerViewerIconFactory fac) {
		this.tank = Tanker;
		this.iconfactory = fac;
		Container c = getContentPane();
		c.setLayout(new BorderLayout());

		// Create the cell viewer
		cells = new JLabel[SIZE][SIZE];
		tankers = new JLabel[SIZE][SIZE];
		lp = new JLayeredPane();
		JPanel pCells = new JPanel(new GridLayout(SIZE, SIZE));
		JPanel pTankers = new JPanel(new GridLayout(SIZE, SIZE));
		pCells.setBackground(Color.GREEN);
		pTankers.setOpaque(false);

		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				cells[x][y] = new JLabel();
				tankers[x][y] = new JLabel();
				tankers[x][y].setIcon(iconfactory.getIconForTanker(Tanker));
				tankers[x][y].setVisible(false);
				pCells.add(cells[x][y]);
				pTankers.add(tankers[x][y]);
			}
		}

		lp.add(pTankers, new Integer(1));


		lp.add(pCells, new Integer(0));

		pCells.setBounds(0, 0, 900, 900);
		pTankers.setBounds(0, 0, 900, 900);

		c.add(lp, BorderLayout.CENTER);

		// Create some labels to show info about the Tanker and environment
		infop = new JPanel(new GridLayout(4, 4));
		infop.add(new JLabel("Timestep:"));
		tstep = new JLabel("0");
		infop.add(tstep);
		infop.add(new JLabel("Fuel:"));
		fuel = new JLabel("200");
		infop.add(fuel);
		infop.add(new JLabel("Position:"));
		pos = new JLabel("(0,0)");
		infop.add(pos);
		infop.add(new JLabel("Water:"));
		water = new JLabel("0");
		infop.add(water);

		infop.add(new JLabel("Completed:"));
		completed = new JLabel("0");
		infop.add(completed);

		infop.add(new JLabel("Delivered:"));
		delivered = new JLabel("0");
		infop.add(delivered);

		infop.add(new JLabel("Overall Score:"));
		score = new JLabel("0");
		infop.add(score);

		c.add(infop, BorderLayout.SOUTH);
		// infop.setPreferredSize(new Dimension(200,100));

		setSize(900, 1025);
		setTitle("Tanker Viewer");
		setVisible(true);

	}

	public BigTankerViewer(Fleet fleet) {
		this(fleet.get(0));
		this.fleet = fleet;

		String[] tankerNames = new String[fleet.size()];

		for (int i = 0; i < fleet.size(); i++) {
			tankerNames[i] = "Tanker " + i;
		}

		//A drop down list to select which tanker to view
		tankerList = new JComboBox(tankerNames);
		infop.add(tankerList);

		//Event handler for drop down list
		tankerList.addActionListener(this);

	}

	public void setTanker(Tanker t) {
		this.tank = t;
	}

	public void tick(Environment env) {
//		Cell[][] view = env.getView(tank.getPosition(), Tanker.VIEW_RANGE);
		Cell[][] view = env.getView(new Point(0,0), Tanker.MAX_FUEL/2);
		pos.setText(tank.getPosition().toString());
		tstep.setText("" + env.getTimestep());
		water.setText("" + tank.waterLevel);
		fuel.setText("" + tank.getFuelLevel());
		completed.setText("" + tank.getCompletedCount());
		delivered.setText("" + tank.waterDelivered);
		score.setText("" + convert(fleet.getScore(),3));
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				Icon cur = iconfactory.getIconForCell(view[x][y]);
				cells[x][y].setIcon(cur);
				tankers[x][y].setVisible(false);

				// Now Draw Tankers
				for (Tanker t : fleet) {
					if (view[x][y].getPoint().equals(t.getPosition())) {
						tankers[x][y].setVisible(true);
					}

				}
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		tank = fleet.get(tankerList.getSelectedIndex());
	}

    private final static int PREFIX_OFFSET = 5;
    private final static String[] PREFIX_ARRAY = {"f", "p", "n", "µ", "m", "", "k", "M", "G", "T"};

    public static String convert(double val, int dp)
    {
        // If the value is zero, then simply return 0 with the correct number of dp
        if (val == 0) return String.format("%." + dp + "f", 0.0);

        // If the value is negative, make it positive so the log10 works
        double posVal = (val<0) ? -val : val;
        double log10 = Math.log10(posVal);

        // Determine how many orders of 3 magnitudes the value is
        int count = (int) Math.floor(log10/3);

        // Calculate the index of the prefix symbol
        int index = count + PREFIX_OFFSET;

        // Scale the value into the range 1<=val<1000
        val /= Math.pow(10, count * 3);

        if (index >= 0 && index < PREFIX_ARRAY.length)
        {
            // If a prefix exists use it to create the correct string
            return String.format("%." + dp + "f%s", val, PREFIX_ARRAY[index]);
        }
        else
        {
            // If no prefix exists just make a string of the form 000e000
            return String.format("%." + dp + "fe%d", val, count * 3);
        }
    }
}
