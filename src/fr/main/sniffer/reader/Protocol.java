package fr.main.sniffer.reader;

import fr.main.sniffer.reader.utils.DofusDataReader;
import fr.main.sniffer.reader.utils.types.BooleanByteWrapper;
import fr.main.sniffer.tools.Log;
import fr.main.sniffer.tools.protocol.Field;
import fr.main.sniffer.tools.protocol.JsonLoader;
import fr.main.sniffer.tools.protocol.Message;
import fr.main.sniffer.tools.protocol.ProtocolTypeManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Protocol {

    DofusDataReader reader;

    public List<String> getData(int id, DofusDataReader reader) throws Exception {

        Log.writeLogIdDebugMessage("PACKET : " +id + " - available : " + reader.available());

        this.reader = reader;
        List<String> result = new ArrayList<>();
        Message message = getMessage(id);
        if(message == null){
            Log.writeLogDebugMessage("No existing packet with id : " +id);
            return null;
        }
        String parent = message.getParent();
        if(!parent.isEmpty()){
            List<String> resultFields = getData(parent);
            if(resultFields != null){
                result.addAll(resultFields);
            }
        }
        result.add(message.getName());
        List<Field> fields = message.getFields();
        if(fields != null){
            result.addAll(getValueData(fields));
        }
        return result;
    }

    private List<String> getData(String name) throws Exception {
        List<String> result = new ArrayList<>();
        Message message;
        if(name.contains("Message")){
            message = getMessage(name);
        } else {
            message = getType(name);
        }

        if(message == null){
            Log.writeLogDebugMessage("No existing packet with name : " +name);
            return null;
        }

        Log.writeLogDebugMessage("Class : " +name);

        String parent = message.getParent();
        if(!parent.isEmpty()){
            List<String> resultFields = getData(parent);
            if(resultFields != null){
                result.addAll(resultFields);
            }
        }
        result.add(name);
        List<Field> fields = message.getFields();
        if(fields != null){
            result.addAll(getValueData(fields));
        }
        return result;
    }

    private List<String> getValueData(List<Field> fields) throws Exception {
        List<String> result = new ArrayList<>();
        List<Field> fieldBBW = new ArrayList<>();
        List<Field> fieldNormal = new ArrayList<>();


        for(Field f : fields){
            if(f.isUseBBW()){
                fieldBBW.add(f);
            } else {
                fieldNormal.add(f);
            }
        }

        if(!fieldBBW.isEmpty()){ //Take care of boolean
            byte flag = (byte) reader.readUnsignedByte();
            for (Field f : fieldBBW){
                result.add(formatField(f.getName(),BooleanByteWrapper.GetFlag(flag,(byte) f.getBbwPosition())));
                if(f.getBbwPosition() == 7){
                    flag = (byte) reader.readUnsignedByte();
                }
            }
        }

        for (Field f : fieldNormal){
            if(f.isVector()){
                if(f.isDynamicLength()){
                    Object value = getValue(f.getWriteLengthMethod());
                    if(value instanceof Short){
                        for(int i=0 ; i< (short) value ; i++){
                            result.addAll(getValues(f));
                        }
                    } else if (value instanceof Integer){
                        for(int i=0 ; i< (int) value ; i++){
                            result.addAll(getValues(f));
                        }
                    } else {
                        Log.writeLogDebugMessage("No instance of : " +value);
                    }

                } else {
                    for(int i=0 ; i<f.getLength() ; i++){
                        result.addAll(getValues(f));
                    }
                }
            } else {
                result.addAll(getValues(f));
            }
        }
        return result;
    }

    private List<String> getValues(Field f) throws Exception {
        List<String> result = new ArrayList<>();
        Object value = getValue(f.getWriteMethod());
        if(value != null){
            result.add(formatField(f.getName(),value));
        } else {
            if(f.isUseTypeManager()){
                short id = reader.readShort();
                String name = ProtocolTypeManager.getInstance(id);
                result.addAll(getData(name));
            } else {
                String name = f.getType();
                result.addAll(getData(name));
            }
        }
        return result;
    }

    private Object getValue(String method) throws IOException {
        switch (method) {
            case "writeUTF":
                return reader.readUTF();
            case "writeByte":
                return reader.readByte();
            case "writeVarShort":
                return reader.readVarShort();
            case "writeDouble":
                return reader.readDouble();
            case "writeShort":
                return reader.readShort();
            case "writeInt":
                return reader.readInt();
            case "writeVarInt":
                return reader.readVarInt();
            case "writeVarLong":
                return reader.readVarLong();
            case "writeBoolean":
                return reader.readBoolean();
            case "writeFloat":
                return reader.readFloat();
            case "writeUnsignedInt":
                return reader.readUnsignedByte();
            case "":
                return null;
        }
        return new Object();
    }

    private Message getMessage(int id){
        for(Message msg : JsonLoader.Messages){
            if(msg.getProtocolId() == id){
                return msg;
            }
        }
        return null;
    }

    private Message getType(int id){
        for(Message msg : JsonLoader.Types){
            if(msg.getProtocolId() == id){
                return msg;
            }
        }
        return null;
    }

    private Message getMessage(String name){
        for(Message msg : JsonLoader.Messages){
            if(msg.getName().equals(name)){
                return msg;
            }
        }
        return null;
    }

    private Message getType(String name){
        for(Message msg : JsonLoader.Types){
            if(msg.getName().equals(name)){
                return msg;
            }
        }
        return null;
    }

    private String formatField(String name, Object value){
        name = "\t" + name;
        return String.format("%s: %s",name,value);
    }

}
