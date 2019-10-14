/*
 * PID_Industrial_Simulator Copyright Batzonis Constantinos
 */
package communication;

import arduino.Arduino;
import com.fazecast.jSerialComm.SerialPort;
import graph.TimeGraph;
import java.io.IOException;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author dinob
 */
public class GraphWorker2 extends SwingWorker<String, String>{
   private LocalSettings lc = new LocalSettings();
    private final TimeGraph graph;
    private XYSeries seriesA, seriesB;    
    private final Arduino myArduino;
    private final SerialPort comPort;  
    private final JLabel PVLabel;
    private double time;
    private int counter = 0;
    private final double dT = 0.01;     // deltaT sample rate (defined by arduino)
    
    public GraphWorker2(Arduino arduino, TimeGraph graph, JLabel label) throws IOException{
        time = 0;
        this.myArduino = arduino;
        this.graph = graph;
        this.PVLabel = label;
        comPort = myArduino.getSerialPort();
        lc.getSettings();
    }
    
    @Override
    protected String doInBackground() throws InterruptedException{
        int num;
        double pv; 
        int arrayPointer = 0;
        byte[] byteBuffer = new byte[20];
        byte[] readSerial = {0};
        String string, result;    
        //comPort.setFlowControl(SerialPort.FLOW_CONTROL_CTS_ENABLED);
        seriesA = this.graph.getSeriesA();
        //seriesB = this.graph.getSeriesB();
        try {
            while (!isCancelled()){
                if(comPort.bytesAvailable() > 0){
                    //Thread.sleep(10);
                    num = comPort.readBytes(readSerial, 1);
                    string = new String(readSerial);
                    if(string.equals("D")){
                        result = new String(byteBuffer);
                        pv = Double.parseDouble(result);
                        time += dT; 
                        pv = (pv*lc.getArduino_Vin_5V())/1023;       // ((dataIn*VOLTarduino)/4095)/Rsense*0.001 
                        seriesA.add(time, pv);
                        publish(Double.toString(pv));
                        for(int i = 0; i <= arrayPointer; i++)      // clear byte buffer
                            byteBuffer[i] = 0;                        
                        arrayPointer = 0;                           // reset pointer
                    }
                    else{
                        byteBuffer[arrayPointer] = readSerial[0];
                        arrayPointer++;
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        //System.out.println("Read " + i + ": " + s + " bytes.");
        return "Interrupted";
    }
    /* Μέθοδος επικοινωνίας με το Thread. Εδώ φτάνουν τα μυνήματα από τη μέθοδο publish */
    @Override
    protected void process(List<String> chunks){
        // This is a delay routine so that mA text is clear
        counter++;
        if(counter >= 21)
            counter = 0;
        if(counter >= 20){
            PVLabel.setText(String.format("%.2f", Double.parseDouble(chunks.get(0))));                                         
        }       
    }
    
    @Override
    protected void done(){

    }    
}
