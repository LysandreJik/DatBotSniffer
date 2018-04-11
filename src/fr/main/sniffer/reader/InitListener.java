package fr.main.sniffer.reader;

import fr.main.display.Frame;
import fr.main.sniffer.reader.utils.DofusDataReader;
import fr.main.sniffer.tools.Log;
import fr.main.sniffer.tools.PropertyLoader;
import fr.main.sniffer.tools.protocol.JsonLoader;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.Payload;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.tcpip.Tcp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class InitListener implements Runnable{

    private int port;
    private Pcap pcap;
    private InputReader input;

    @Override
    public void run() {
        startListener();
    }

    public InitListener(Frame frame){
        this.input = new InputReader(frame);
        Properties prop;
        try {
            prop = PropertyLoader.load("src/fr/main/sniffer/config/init.properties");
            this.port = Integer.parseInt(prop.getProperty("serv.auth.port", "5555"));
            this.initDevice();

        } catch (IOException e) {
            Log.writeLogDebugMessage("Impossible de lire le fichier init.properties");
        }
    }

    private void initDevice(){
        List<PcapIf> alldevs = new ArrayList<>();
        Log.writeLogDebugMessage("Device available");
        StringBuilder errbuf = new StringBuilder();
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
            Log.writeLogDebugMessage("Impossible de trouver un communicateur");
            return;
        }
        for(PcapIf dev : alldevs){
            Log.writeLogDebugMessage((dev.getDescription() != null) ? dev.getDescription(): dev.getName());
        }
        PcapIf device = alldevs.get(1);
        Log.writeLogDebugMessage(String.format("Choix : %s",(device.getDescription() != null) ? device.getDescription(): device.getName()));
        int snaplen = 64 * 1024;
        int flags = Pcap.MODE_PROMISCUOUS;
        int timeout = 1;
        pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
        if (pcap == null) {
            Log.writeLogDebugMessage("Erreur lors de l'ouverture du communicateur : " + errbuf.toString());
            return;
        }
        this.applyFilter();
    }

    public void startListener(){
        Log.writeLogDebugMessage("Starting listening...");
        this.pcap.loop(Pcap.LOOP_INFINITE, jpacketHandler, "");
    }

    public void closeListener(){
        Log.writeLogDebugMessage("Closing listening...");
        this.pcap.breakloop();
    }

    private void applyFilter(){
        // Filter for port 5555 or 443
        PcapBpfProgram program = new PcapBpfProgram();
        String expression = "tcp port " + port;
        int optimize = 0;         // 0 = false
        int netmask = 0xFFFFFF00; // 255.255.255.0
        if (pcap.compile(program, expression, optimize, netmask) != Pcap.OK) {
            Log.writeLogDebugMessage(pcap.getErr());
            return;
        }
        if (pcap.setFilter(program) != Pcap.OK) {
            Log.writeLogDebugMessage(pcap.getErr());
            return;
        }
        Log.writeLogDebugMessage("Le sniffer écoute le port "+this.port);
        Log.writeLogDebugMessage("En attente du client");
    }

    private PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {
        public void nextPacket(PcapPacket packet, String user) {
            Tcp tcp = new Tcp();
            int port = 0;
            if(packet.hasHeader(tcp)){
                port = tcp.destination();
            }
            // Capturing data
            JBuffer buffer = packet.getHeader(new Payload());
            if(buffer != null){
                int size = buffer.size();
                try {
                    DofusDataReader reader = new DofusDataReader(new ByteArrayInputStream(buffer.getByteArray(0, size)));
                    if(port == 5555){
                        input.buildMessage(reader, true);
                    } else {
                        input.buildMessage(reader, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

}
