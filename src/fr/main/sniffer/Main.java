package fr.main.sniffer;

import fr.main.display.Frame;

/**
 * Main class for the datbot packet sniffer
 */
public class Main {
    public static void main(String[] args){
        System.out.println("Initializing the packet sniffer");

        // Example usage of the Frame class
        // Init of the Frame object
        Frame frame = new Frame();

        // Displaying the frame object
        frame.display();

        //Adding packet to the window
        frame.addPacket(500, "Name", new String[]{"Nom du packet", "ID : 50", "x : 20", "y : 50"});// Multi line information
        frame.addPacket(150, "PacketName", "Single line value");                             // Single Line information

        // Disposing of the window
        // frame.dispose();
    }


}
