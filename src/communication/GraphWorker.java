/*
 * PID_Industrial_Simulator Copyright Batzonis Constantinos
 */
package communication;

import arduino.Arduino;
import com.fazecast.jSerialComm.SerialPort;
import graph.TimeGraph;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author dinob
 */
public class GraphWorker extends SwingWorker<String, Double>{
    
    private final TimeGraph graph;
    private XYSeries series;    
    private final Arduino myArduino;
    private final SerialPort comPort;  
    private final JLabel mAValue;
    private double time;
    private int counter = 0;
    private final double dT = 0.01;     // deltaT sample rate (defined by arduino)
    
    public GraphWorker(Arduino arduino, TimeGraph graph, JLabel jLabel){
        time = 0;
        this.myArduino = arduino;
        this.graph = graph;
        this.mAValue = jLabel;
        comPort = myArduino.getSerialPort();
    }
    
    @Override
    protected String doInBackground() throws InterruptedException{
        int num;
        double mA; 
        int arrayPointer = 0;
        byte[] byteBuffer = new byte[10];
        byte[] readSerial = {0};
        String string, result;
        //comPort.setFlowControl(SerialPort.FLOW_CONTROL_CTS_ENABLED);
        System.out.println("FlowControlSettings: "+comPort.getFlowControlSettings());
        series = this.graph.getSeries(); 
        try {
            while (true){
                if(comPort.bytesAvailable() > 0){
                    //Thread.sleep(10);
                    num = comPort.readBytes(readSerial, 1);
                    string = new String(readSerial);
                    if(string.equals("k")){
                        result = new String(byteBuffer);
                        mA = Double.parseDouble(result);
                        mA = (mA*4.95)/900.9;  
                        publish(mA);
                        for(int i = 0; i <= arrayPointer; i++)
                            byteBuffer[i] = 0;                        
                        arrayPointer = 0;
                    }
                    else{
                        byteBuffer[arrayPointer] = readSerial[0];
                        arrayPointer++;
                    }
                    //num = comPort.readBytes(readBuffer, readBuffer.length);
                    //String string = new String(readBuffer);
                    //if(string.contains("ok")){
                        //System.err.println(string);
                        //string = string.replaceAll("ok", "");
                        //System.out.println("Value: "+s+", bytes: "+num);                       
                        //mA = Double.parseDouble(string);
                        //mA = (mA*4.95)/900.9;  
                        //publish(mA);
                        //for(int i = 0; i < 10; i++)
                            //readBuffer[i] = 0;
                    //}
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
            time += dT;
            counter++;
            if(counter == 50){
                mAValue.setText(String.format("%.2f", chunks.get(i))); 
                counter = 0;
            }               
            series.add(time, chunks.get(i));
        }
    }
    
    @Override
    protected void done(){

    } 
}
