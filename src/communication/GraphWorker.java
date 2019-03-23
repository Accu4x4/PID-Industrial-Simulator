/*
 * PID_Industrial_Simulator Copyright Batzonis Constantinos
 */
package communication;

import arduino.Arduino;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import graph.TimeGraph;
import java.nio.ByteBuffer;
import java.util.List;
import javax.swing.SwingWorker;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author dinob
 */
public class GraphWorker extends SwingWorker<String, Double>{
    
    //private final JPanel graphPanel;
    private final TimeGraph graph;
    private XYSeries series;    
    private final Arduino myArduino;
    private final SerialPort comPort;  
    private int start;
    
    public GraphWorker(Arduino arduino, TimeGraph graph){
        start = 0;
        this.myArduino = arduino;
        this.graph = graph;
        comPort = myArduino.getSerialPort();
    }
    
    @Override
    protected String doInBackground() throws InterruptedException{
        int num;
        double mA; 
        byte[] readBuffer = new byte[5];
        comPort.setFlowControl(SerialPort.FLOW_CONTROL_CTS_ENABLED);
        System.out.println("FlowControlSettings: "+comPort.getFlowControlSettings());
        series = this.graph.getSeries(); 
        try {
            while (true){
                if(comPort.bytesAvailable() > 0){
                    Thread.sleep(10);
                    num = comPort.readBytes(readBuffer, readBuffer.length);
                    String string = new String(readBuffer);
                    //System.out.println("Value: "+s+", bytes: "+num);
                    mA = Double.parseDouble(string);
                    mA = (mA*4.95)/900.9;  
                    publish(mA);  
                    for(int i = 0; i < 5; i++)
                       readBuffer[i] = 0;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        //System.out.println("Read " + i + ": " + s + " bytes.");
        return "Interrupted";
    }
    /* Μέθοδος επικοινωνίας με το Thread. Εδώ φτάνουν τα μυνήματα από τη μέθοδο publish */
    @Override
    protected void process(List<Double> chunks){
        for(int i = 0; i < chunks.size(); i++){
            series.add(start++, chunks.get(i));           
        }
    }
    
    @Override
    protected void done(){

    } 
}
