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
 * SwingWorker class for real-time data collection via serial interface
 * It works "forever" and gathers data from arduino adding them on the
 * specific XYSeries for graphical visualization.
 */
public class GraphWorker extends SwingWorker<String, String>{
    
    private LocalSettings lc = new LocalSettings();
    private final TimeGraph graph;
    private XYSeries seriesA, seriesB;    
    private final Arduino myArduino;
    private final SerialPort comPort;  
    private final JLabel mAValueA, mAValueB;
    private double time;
    private int counter = 0;
    private final double dT = 0.01;     // deltaT sample rate (defined by arduino)
    private String channel;
    
    public GraphWorker(Arduino arduino, TimeGraph graph, JLabel jLabelA, JLabel jLabelB){
        time = 0;
        this.myArduino = arduino;
        this.graph = graph;
        this.mAValueA = jLabelA;
        this.mAValueB = jLabelB;
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
        seriesA = this.graph.getSeriesA();
        seriesB = this.graph.getSeriesB();
        try {
            while (true){
                if(comPort.bytesAvailable() > 0){
                    //Thread.sleep(10);
                    num = comPort.readBytes(readSerial, 1);
                    string = new String(readSerial);
                    if(string.equals("A") || string.equals("B")){
                        channel = string;
                        result = new String(byteBuffer);
                        mA = Double.parseDouble(result);
                        time += dT; 
                        if(channel.equals("A")){ 
                           mA = (mA*lc.getArduino_Vin_5V())/(lc.getConverter_max_Value()*(lc.getChannel_A_Rsense())*0.001);       // ((dataIn*VOLTarduino)/4095)/Rsense*0.001  
                           seriesA.add(time, mA);
                           publish(Double.toString(mA)+"A");
                        }
                        else{
                           mA = (mA*lc.getArduino_Vin_5V())/(lc.getConverter_max_Value()*(lc.getChannel_B_Rsense())*0.001);       // ((dataIn*VOLTarduino)/4095)/Rsense*0.001  
                           seriesB.add(time, mA); 
                           publish(Double.toString(mA)+"B");                           
                        }
                        for(int i = 0; i <= arrayPointer; i++)
                            byteBuffer[i] = 0;                        
                        arrayPointer = 0;
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
            for(int i = 0; i < chunks.size()-1; i++){
                if(chunks.get(i).charAt(chunks.get(i).length()-1) == 'A'){
                    String str = chunks.get(i).substring(0, chunks.get(i).length()-2);
                    mAValueA.setText(String.format("%.2f", Double.parseDouble(str)));             
                }
                if(chunks.get(i).charAt(chunks.get(i).length()-1) == 'B'){
                    String str = chunks.get(i).substring(0, chunks.get(i).length()-2);
                    mAValueB.setText(String.format("%.2f", Double.parseDouble(str)));              
                }                
            }             
        }       
    }
    
    @Override
    protected void done(){

    } 
}
