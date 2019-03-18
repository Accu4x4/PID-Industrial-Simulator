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
 */
public class TimeGraph {
    
    private final String graphTitle;
    private final ChartPanel graphPanel;
    private final XYSeries series;

    public TimeGraph(String title){
        this.graphTitle = title;
        this.series = new XYSeries(graphTitle);
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(graphTitle, "Time (seconds)", "mA", dataset);
        this.graphPanel = new ChartPanel(chart);
        //graphPanel.setMaximumDrawHeight(40);
        //graphPanel.setMinimumDrawWidth(60);                
    }
    
    public ChartPanel getGraphPanel(){
        return this.graphPanel;
    }
    
    public XYSeries getSeries(){
        return this.series;
    }
    
}
