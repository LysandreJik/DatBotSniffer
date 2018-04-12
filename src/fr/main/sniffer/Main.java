package fr.main.sniffer;

import fr.main.display.Frame;
import fr.main.sniffer.reader.InitListener;

/**
 * Main class for the datbot packet sniffer
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Initializing the packet sniffer");

        // Example usage of the Frame class
        // Init of the Frame object
        Frame frame = new Frame();

        // Displaying the frame object
        frame.display();

        // Disposing of the window
        // frame.dispose();

        InitListener i = new InitListener(frame);
        new Thread(i).start();
    }

}