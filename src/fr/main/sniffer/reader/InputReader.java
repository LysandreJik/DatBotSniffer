package fr.main.sniffer.reader;

import fr.main.display.Frame;
import fr.main.display.FrameComponent;
import fr.main.sniffer.reader.utils.DofusDataReader;
import fr.main.sniffer.tools.Log;
import fr.main.sniffer.tools.protocol.JsonLoader;

import java.io.ByteArrayInputStream;
import javax.xml.bind.DatatypeConverter;


public class InputReader {

    private byte[] bigPacketData;
    private int bigPacketId;
    // Big packet split
    private int bigPacketLengthToFull;// Length needed to finish the packet
    private Message message;

    private Frame frame;

    public static boolean addPacket = true;

    public InputReader(Frame frame){
        this.frame = frame;
    }

    public void buildMessage(DofusDataReader reader, boolean isFromClient) throws Exception {
        if (reader.available() <= 0) { return; }

        // Packet split
        if (bigPacketLengthToFull != 0) {
            if (reader.available() <= bigPacketLengthToFull) {
                bigPacketLengthToFull -= reader.available();
                byte[] destination = new byte[bigPacketData.length + reader.available()];
                System.arraycopy(bigPacketData, 0, destination, 0, bigPacketData.length);
                System.arraycopy(reader.readBytes(reader.available()), 0, destination, bigPacketData.length, reader.available());
                this.bigPacketData = destination;
            }
            else if (reader.available() > bigPacketLengthToFull) {
                byte[] destination = new byte[bigPacketData.length + bigPacketLengthToFull];
                System.arraycopy(bigPacketData, 0, destination, 0, bigPacketData.length);
                System.arraycopy(reader.readBytes(bigPacketLengthToFull), 0, destination, bigPacketData.length, bigPacketLengthToFull);
                bigPacketLengthToFull = 0;
                this.bigPacketData = destination;
            }
            if (bigPacketLengthToFull == 0) {
                treatPacket(bigPacketId, bigPacketData, isFromClient);
                bigPacketData = null;
                bigPacketId = 0;
            }
        }
        else {
            if (this.message == null) {
                this.message = new Message(isFromClient);
            }
            message.build(reader);
            if (message.getId() != 0 && message.bigPacketLength == 0) {
                treatPacket(message.getId(), message.getData(), isFromClient);
            }
            else if (message.getId() != 0 && message.bigPacketLength != 0) {
                bigPacketLengthToFull = message.bigPacketLength;
                bigPacketId = message.getId();
                bigPacketData = message.getData();
            }
        }
        this.message = null;
        buildMessage(reader,isFromClient);
    }

    private void treatPacket(int id, byte[] data, boolean isFromClient){
        String namePacket = "";
        if(isFromClient && !FrameComponent.rowC.contains(id)){
            FrameComponent.rowC.add(id);
        }
        for(fr.main.sniffer.tools.protocol.Message msg : JsonLoader.Messages){
            if(msg.getProtocolId() == id){
                namePacket = msg.getName();
            }
        }
        Protocol protocol = new Protocol();
        DofusDataReader reader = new DofusDataReader(new ByteArrayInputStream(data));
        try {
            if(id == 6253){
                if(addPacket)
                    this.frame.addPacket(id,namePacket,String.valueOf(data.length),"");
            } else {
                if(addPacket)
                    this.frame.addPacket(id,namePacket,String.valueOf(data.length),protocol.getData(id,reader));
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(addPacket)
                this.frame.addPacket(id,namePacket,"ERROR","ERROR");
            Log.writeLogDebugMessage("Impossible to parse packet " +id);
        }
    }


    public static String toHexString(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }

    public static byte[] toByteArray(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }


}
