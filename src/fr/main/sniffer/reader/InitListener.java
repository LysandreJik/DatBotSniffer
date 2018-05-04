package fr.main.sniffer.reader;

import fr.main.display.Frame;
import fr.main.display.FrameComponent;
import fr.main.sniffer.Main;
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

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class InitListener implements Runnable{

    private int port;
    private  Pcap pcap;
    private InputReader input;
    private int index;

    @Override
    public void run() {
        pcap.loop(Pcap.LOOP_INFINITE, jpacketHandler, "");
    }

    public void kill(){
        pcap.close();
    }

    public void breakLoop(){
        InputReader.addPacket = false;
    }

    public void startLoop(){
        InputReader.addPacket = true;
    }

    public InitListener(Frame frame, PcapIf device, int index){
        this.input = new InputReader(frame);
        this.index = index;

        Properties prop;
        try {
            prop = PropertyLoader.load("src/fr/main/sniffer/config/init.properties");
            this.port = Integer.parseInt(prop.getProperty("serv.auth.port", "5555"));
            this.initDevice(device);
            this.generateProtocol();
        } catch (IOException e) {
            Log.writeLogDebugMessage("Can't read init.properties");
        } catch (InterruptedException e) {
            Log.writeLogDebugMessage("Can't generate protocol");
        }


    }


    private void initDevice(PcapIf device){
        StringBuilder errbuf = new StringBuilder();
        Log.writeLogDebugMessage(String.format("Choosing : %s",(device.getDescription() != null) ? device.getDescription(): device.getName()));
        int snaplen = 64 * 1024;
        int flags = Pcap.MODE_PROMISCUOUS;
        int timeout = 1;
        pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
        if (pcap == null) {
            Log.writeLogDebugMessage("Error while openning the device : " + errbuf.toString());
            return;
        }
        this.applyFilter();
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
        Log.writeLogDebugMessage("Sniffer listenning to  "+this.port);
        Log.writeLogDebugMessage("Waiting for client");
    }

    private String getPathDatBotSniffer() {
        String s = Paths.get("").toAbsolutePath().toString();
        int i = s.indexOf("DatBotSniffer");
        if(i == -1){
            s = Paths.get("").toAbsolutePath().toString()+"\\DatBotSniffer";
        }else{
            s = s.substring(0, i + 13);
        }
        return s;
    }

    private void generateProtocol() throws IOException, InterruptedException {
        String pathConfig = getPathDatBotSniffer() + "\\src\\fr\\main\\sniffer\\config";
        File output = new File(pathConfig + "\\d2jsonOutput.json");
        Process p = new ProcessBuilder(pathConfig + "\\d2json.exe", pathConfig + "\\Invoker.swf").redirectOutput(output).start();
        p.waitFor();
        new JsonLoader(pathConfig + "\\d2jsonOutput.json");
        Log.writeLogDebugMessage("Protocol generated");
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
                        Main.cleanDevices(index);
                    } else {
                        input.buildMessage(reader, false);
                        Main.cleanDevices(index);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

}
