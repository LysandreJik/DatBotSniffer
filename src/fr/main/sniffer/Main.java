package fr.main.sniffer;

import fr.main.display.Frame;
import fr.main.sniffer.reader.InitListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class for the datbot packet sniffer
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Initializing the packet sniffer");

        // Example usage of the Frame class
        // Init of the Frame object
        Frame frame = new Frame();

        // Displaying the frame object
        frame.display();

        ArrayList<String> str = new ArrayList<>();
        str.add("OKOKOKOKO");
        str.add("OKOKOKOKO");
        str.add("OKOKOKOKO");
        str.add("OKOKOKOKO");
        str.add("OKOKOKOKO");
        str.add("OKOKOKOKO");
        frame.addPacket(150, "sqdqqd", "tooltip", str);
        Thread.sleep(1000);
        frame.addPacket(150, "sqdqqd", "tooltip", str);
        Thread.sleep(1000);
        frame.addPacket(150, "sqdqqd", "tooltip", str);
        Thread.sleep(1000);
        frame.addPacket(150, "sqdqqd", "tooltip", str);
        Thread.sleep(1000);
        frame.addPacket(150, "sqdqqd", "tooltip", str);
        Thread.sleep(1000);
        frame.addPacket(150, "sqdqqd", "tooltip", str);
        Thread.sleep(1000);
        frame.addPacket(150, "sqdqqd", "tooltip", str);
        Thread.sleep(1000);
        frame.addPacket(150, "sqdqqd", "tooltip", str);
        Thread.sleep(1000);


        // Disposing of the window
        // frame.dispose();
//
//        InitListener i = new InitListener(frame);
//        new Thread(i).start();
    }

}