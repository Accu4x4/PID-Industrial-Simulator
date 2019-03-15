
import ui.MainMenu;

/*
 * Batzonis Constantinos
 */

/**
 *
 * @author dinob
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() 
        {
            public void run() 
            {
                MainMenu startProgram = new MainMenu();
                startProgram.setLocationRelativeTo(null);
                startProgram.setVisible(true);
            }
        });
    }
    
}
