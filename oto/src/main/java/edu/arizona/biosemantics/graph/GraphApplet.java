package edu.arizona.biosemantics.graph;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;


public class GraphApplet extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5837142395110535813L;

	//Called when this applet is loaded into the browser.
    public void init() {
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                	CoOccurrenceGraph.viewGraph("E:\\FNA\\FNA-v19\\FNA-v19-excerpt\\target\\co-occurrence\\Group_2.xml", "Group_2");
                }
            });
        } catch (Exception e) { 
            System.err.println("createGUI didn't complete successfully");
        }
    }      
}
