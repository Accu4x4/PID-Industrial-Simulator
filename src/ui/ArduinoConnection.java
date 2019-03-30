/*
 * PID_Industrial_Simulator Copyright Batzonis Constantinos
 */
package ui;

import arduino.*;
import arduino.PortDropdownMenu;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author dinob
 */
public class ArduinoConnection extends javax.swing.JDialog {
    
    PortDropdownMenu p = new PortDropdownMenu();
    Arduino myArduino;
    SerialPort comPort;
    boolean isConnected = false;
    JLabel sendLabel = new JLabel("");
    JLabel receiveLabel = new JLabel("");
    
    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    /**
     * Creates new form ArduinoConnection
     */
    public ArduinoConnection(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        /* Customize GridBagLayout */
        GridBagConstraints gridBagConstraints;

        mainPanel.setLayout(new GridBagLayout());

        /* Define position in the grid for textLabel */
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 12;
        gridBagConstraints.gridheight = 2;
        // Insets(int top, int left, int bottom, int right)
        gridBagConstraints.insets = new Insets(10, 103, 20, 114);
        mainPanel.add(textLabel, gridBagConstraints);
        /* add textLabel into mainPanel's gridBag */
        this.add(mainPanel, java.awt.BorderLayout.CENTER);
        
        /* setup the PortDropdownMenu JComboBox p*/
        p.refreshMenu();
        p.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    pActionPerformed(evt);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ArduinoConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }); 
        /* Define position in the grid for p */        
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 4;        
        gridBagConstraints.insets = new Insets(20, 103, 20, 114);            
        p.setSelectedIndex(-1);
        p.setVisible(true);
        mainPanel.add(p, gridBagConstraints);

        /* Create connect button and define position in the grid */         
        JButton connect = new JButton("Connect");
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 6;        
        gridBagConstraints.insets = new Insets(0, 103, 20, 114);  
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectActionPerformed(evt);
            }
        }); 
        connect.setVisible(true);
        mainPanel.add(connect, gridBagConstraints);

        /* Define position in the grid for first Label */         
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 7;        
        gridBagConstraints.insets = new Insets(30, 103, 10, 114); 
        mainPanel.add(sendLabel, gridBagConstraints);
 
        /* Define position in the grid for second Label */ 
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 8;        
        gridBagConstraints.insets = new Insets(5, 103, 15, 114); 
        mainPanel.add(receiveLabel, gridBagConstraints);
        this.pack();        

        // Close the dialog when Esc is pressed
        String cancelName = "cancel";
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                doClose(RET_CANCEL);
            }
        });
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        textLabel = new javax.swing.JLabel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        textLabel.setText("Select serial port to connect with arduino");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 416, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(textLabel)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 251, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(textLabel)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(250, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(266, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 53, Short.MAX_VALUE)))
        );

        getRootPane().setDefaultButton(okButton);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog
    
    private void doClose(int retStatus) {
        if(myArduino.openConnection() && isConnected){
          
        }
        comPort.removeDataListener();        
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ArduinoConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ArduinoConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ArduinoConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ArduinoConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ArduinoConnection dialog = new ArduinoConnection(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    private void pActionPerformed(java.awt.event.ActionEvent evt) throws InterruptedException{
        if(p.getSelectedIndex() != -1){
            String port = p.getSelectedItem().toString();
            System.out.println(port);
            myArduino = new Arduino(p.getSelectedItem().toString(), 115200);
            comPort = myArduino.getSerialPort();
            System.out.println(myArduino.openConnection());
            if(!myArduino.openConnection()){
                JOptionPane.showMessageDialog(null, "Connection failed", "Warning", JOptionPane.WARNING_MESSAGE); 
            }
            else{
                Thread.sleep(1500);
                String hi = "H";
                myArduino.serialWrite(hi);   
                sendLabel.setText("You said 'Hi' at "+port+"...");
                try {
                    while (!isConnected){
                        if(comPort.bytesAvailable() > 0){
                            Thread.sleep(10);
                            String show = myArduino.serialRead();
                            receiveLabel.setText(port+" responded with '"+show+"'..."); 
                            isConnected = true;
                            textLabel.setForeground(Color.blue);
                            textLabel.setText("Connection with arduino established");
                        }
                    }
                } catch (Exception e) { e.printStackTrace(); }
        //System.out.println("Read " + i + ": " + s + " bytes.");
            }
        }
    } 
    /* When connect button is pressed, try to communicate with arduino via selected port*/
    private void connectActionPerformed(java.awt.event.ActionEvent evt){
 
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel textLabel;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
