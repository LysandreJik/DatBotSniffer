package fr.main.sniffer;

import fr.main.display.Frame;
import fr.main.sniffer.reader.InitListener;
import fr.main.sniffer.tools.Log;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.util.ArrayList;
import java.util.List;


/**
 * Main class for the datbot packet sniffer
 */
public class Main {

    static List<InitListener> listeners;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Initializing the packet sniffer");

        // Example usage of the Frame class
        // Init of the Frame object
        Frame frame = new Frame();

        // Displaying the frame object
        frame.display();

        // Disposing of the window
        // frame.dispose();

        List<PcapIf> devices = getAvailableDevices();
        listeners = new ArrayList<>();

        for(PcapIf device : devices){
            listeners.add(new InitListener(frame, device, listeners.size()));
            new Thread(listeners.get(listeners.size()-1)).start();
        }


    }

    private static List<PcapIf> getAvailableDevices(){
        List<PcapIf> alldevs = new ArrayList<>();
        Log.writeLogDebugMessage("Device available");
        StringBuilder errbuf = new StringBuilder();
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
            Log.writeLogDebugMessage("Device not found");
        }

        return alldevs;
    }

    public static void cleanDevices(int kept){
        if(listeners.size() > 1){
            for(int i = 0; i < listeners.size(); i++){
                if(i != kept){
                    listeners.get(i).kill();
                    System.out.println("killed listener "+i);
                }
            }
        }else{
            System.out.println("Only one listener available.");
        }
    }


}