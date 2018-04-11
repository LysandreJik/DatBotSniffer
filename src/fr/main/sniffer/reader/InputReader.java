package fr.main.sniffer.reader;

import fr.main.display.Frame;
import fr.main.sniffer.reader.utils.DofusDataReader;

public class InputReader {

    private byte[] bigPacketData;
    private int bigPacketId;
    // Big packet split
    private int bigPacketLengthToFull;// Length needed to finish the packet
    private Message message;

    private Frame frame;

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
                treatPacket(bigPacketId, bigPacketData);
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
                treatPacket(message.getId(), message.getData());
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

    private void treatPacket(int id, byte[] data){

        this.frame.addPacket(id,"test",bytesToString(data));
    }


    private String bytesToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%X", b));
        }
        return sb.toString();
    }


}
