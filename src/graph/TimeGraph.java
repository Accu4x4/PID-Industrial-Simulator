/*
 * PID_Industrial_Simulator Copyright Batzonis Constantinos
 */
package graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author dinob
 *  A class that creates a graph for visualization of board mA inputs
 *  It uses 2 channels (channel A and channel B), so we need 2 XYSeries
 *  It is based on JFreeChart library
 */
public class TimeGraph {
    
    private final String graphTitle;            // Title for the graph
    private final ChartPanel graphPanel;        // Panel to add the graph
    private final XYSeries seriesA, seriesB;    // Series of data collection from arduino

    public TimeGraph(String title){
        this.graphTitle = title;
        this.seriesA = new XYSeries("input A");
        this.seriesB = new XYSeries("input B");
        XYSeriesCollection dataset = new XYSeriesCollection();  // Shows both series
        dataset.addSeries(seriesA);
        dataset.addSeries(seriesB);
        // Create the graph
        JFreeChart chart = ChartFactory.createXYLineChart(graphTitle, "Time (seconds)", "mA", dataset);
        this.graphPanel = new ChartPanel(chart);
        //graphPanel.setMaximumDrawHeight(40);
        //graphPanel.setMinimumDrawWidth(60);                
    }
    
    public ChartPanel getGraphPanel(){
        return this.graphPanel;
    }
    
    public XYSeries getSeriesA(){
        return this.seriesA;
    }
    
    public XYSeries getSeriesB(){
        return this.seriesB;
    }
}
