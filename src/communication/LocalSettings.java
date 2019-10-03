/*
 * PID_Industrial_Simulator Copyright Batzonis Constantinos
 */
package communication;

import java.io.*;

/**
 *
 * @author dinob
 */
public class LocalSettings {
    
    private float channel_A_max_mA_out;
    private float channel_B_max_mA_out;
    private float channel_A_Rsense;
    private float channel_B_Rsense;
    private float arduino_Vin_5V;
    private float converter_max_Value;
    private final String filePath = "settings.txt";
    
    public LocalSettings(){
        
        File file = new File(filePath);
        
        // Check weather settings.txt file exists or not
        if(file.exists())
            System.out.println("exists");
        else{
            // Write into settings.txt the default values
            try (Writer writer = new BufferedWriter(new FileWriter(file))) {
                String contents = "1.channel A max mA out:" + 20.40 + 
                    System.getProperty("line.separator") + "2.channel B max mA out:" + 20.40 +
                    System.getProperty("line.separator") + "3.channel A Rsense:" + 220 +
                    System.getProperty("line.separator") + "4.channel B Rsense:" + 220 + 
                    System.getProperty("line.separator") + "5.arduino Vin 5V:" + 5.08 + 
                    System.getProperty("line.separator") + "6.converter max value:" + 4095;

                writer.write(contents);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Reads stored values from file settings.txt
    public void getSettings() throws FileNotFoundException, IOException{
        File file = new File(filePath);
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        String test;
        int i = 0;
        float[] values = new float[6]; // keeps the values
        while((test = br.readLine()) != null){  // read all settings
            String[] part = test.split(":");    // split text from actual value
            values[i] = Float.parseFloat(part[1]);
            i++;
        }
        this.setChannel_A_max_mA_out(values[0]);
        this.setChannel_B_max_mA_out(values[1]);
        this.setChannel_A_Rsense(values[2]);
        this.setChannel_B_Rsense(values[3]);
        this.setArduino_Vin_5V(values[4]);
        this.setConverter_max_Value(values[5]);
        br.close();
    }
    
    public void saveSettings (float chAmA, float chBmA, float chAR, float chBR, float arduino5v, float converterValue) throws FileNotFoundException, IOException{
        File file = new File(filePath);
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            String contents = "1.channel A max mA out:" + chAmA + 
                System.getProperty("line.separator") + "2.channel B max mA out:" + chBmA +
                System.getProperty("line.separator") + "3.channel A Rsense:" + chAR +
                System.getProperty("line.separator") + "4.channel B Rsense:" + chBR + 
                System.getProperty("line.separator") + "5.arduino Vin 5V:" + arduino5v + 
                System.getProperty("line.separator") + "6.converter max value:" + converterValue;

                writer.write(contents);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    // Getters and Setters
    
    /**
     * @return the channel_A_max_mA_out
     */
    public float getChannel_A_max_mA_out() {
        return channel_A_max_mA_out;
    }

    /**
     * @param channel_A_max_mA_out the channel_A_max_mA_out to set
     */
    public void setChannel_A_max_mA_out(float channel_A_max_mA_out) {
        this.channel_A_max_mA_out = channel_A_max_mA_out;
    }

    /**
     * @return the channel_B_max_mA_out
     */
    public float getChannel_B_max_mA_out() {
        return channel_B_max_mA_out;
    }

    /**
     * @param channel_B_max_mA_out the channel_B_max_mA_out to set
     */
    public void setChannel_B_max_mA_out(float channel_B_max_mA_out) {
        this.channel_B_max_mA_out = channel_B_max_mA_out;
    }

    /**
     * @return the channel_A_Rsense
     */
    public float getChannel_A_Rsense() {
        return channel_A_Rsense;
    }

    /**
     * @param channel_A_Rsense the channel_A_Rsense to set
     */
    public void setChannel_A_Rsense(float channel_A_Rsense) {
        this.channel_A_Rsense = channel_A_Rsense;
    }

    /**
     * @return the channel_B_Rsense
     */
    public float getChannel_B_Rsense() {
        return channel_B_Rsense;
    }

    /**
     * @param channel_B_Rsense the channel_B_Rsense to set
     */
    public void setChannel_B_Rsense(float channel_B_Rsense) {
        this.channel_B_Rsense = channel_B_Rsense;
    }

    /**
     * @return the arduino_Vin_5V
     */
    public float getArduino_Vin_5V() {
        return arduino_Vin_5V;
    }

    /**
     * @param arduino_Vin_5V the arduino_Vin_5V to set
     */
    public void setArduino_Vin_5V(float arduino_Vin_5V) {
        this.arduino_Vin_5V = arduino_Vin_5V;
    }

    /**
     * @return the converter_max_Value
     */
    public float getConverter_max_Value() {
        return converter_max_Value;
    }

    /**
     * @param converter_max_Value the converter_max_Value to set
     */
    public void setConverter_max_Value(float converter_max_Value) {
        this.converter_max_Value = converter_max_Value;
    }
}
