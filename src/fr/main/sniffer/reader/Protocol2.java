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

public class Protocol2 {

    DofusDataReader reader;

    public Protocol2(){
    }

    public Protocol2(DofusDataReader reader){
        this.reader = reader;
    }

    public List<String> getData(int id) throws Exception {
        Log.writeLogIdDebugMessage("PACKET : " + id + " - available : " + reader.available());
        List<String> result = new ArrayList<>();

        Message message = getMessage(id);
        if (message == null) {
            Log.writeLogDebugMessage("No existing packet with id : " + id);
            return null;
        }


        return null;

    }

    public List<Message> getParents(Message message) {
        List<Message> parents = new ArrayList<>();
        boolean hasParent = true;
        while (hasParent) {
            String parent = message.getParent();
            if (!parent.isEmpty()) {
                hasParent = true;
                message = getMessage(parent);
                parents.add(message);
            } else {
                hasParent = false;
            }
        }
        return parents;
    }

    public List<String> deserialize(Message message) throws Exception {

        Log.writeLogDebugMessage("Name message : " + message.getName());

        List<String> result = new ArrayList<>();

        List<Message> parents = getParents(message);


        for (int i = 0 ; i < parents.size(); i++) {
            int index = parents.size() - i - 1;
            List<String> resultParents = deserializeMsg(parents.get(index));
            if(resultParents != null){
                result.addAll(resultParents);
            }
        }

        List<Field> fields = message.getFields();

        if(fields == null){
            return null;
        }

        int indexBbw1 = 0;
        byte flag = 0;
        for (Field f : fields) {

            if (f.isUseBBW()) {

                Log.writeLogDebugMessage("Name : "  + f.getName());

                if (indexBbw1 == 0) {
                    flag = (byte) reader.readUnsignedByte();
                    result.add(formatField(f.getName(),BooleanByteWrapper.GetFlag(flag,(byte) 0)));
                }
                else if (indexBbw1 < 8) {
                    result.add(formatField(f.getName(),BooleanByteWrapper.GetFlag(flag,(byte) indexBbw1)));
                    }
                else {
                    indexBbw1 = 0;
                    flag = (byte) reader.readUnsignedByte();
                    result.add(formatField(f.getName(),BooleanByteWrapper.GetFlag(flag,(byte) indexBbw1)));
                }
                indexBbw1++;
            }
        }

        for (Field f : fields) {

            Log.writeLogDebugMessage("Name : "  + f.getName());


            if (!f.isDynamicLength() && !f.isVector() && !f.isUseBBW() && !f.isUseTypeManager() && !f.getWriteMethod().equals("")) {
                result.add(formatField(f.getName(),getValue(f.getWriteMethod())));
            }

            else if (!f.isDynamicLength() && !f.isVector() && !f.isUseBBW() && !f.isUseTypeManager() && f.getWriteMethod().equals("")) {
                result.addAll(deserialize(getMessage(f.getType())));
            }

            else if (!f.isDynamicLength() && !f.isVector() && !f.isUseBBW() && f.isUseTypeManager() && f.getWriteMethod().equals("")) {
                String name = ProtocolTypeManager.getInstance(reader.readShort());
                result.addAll(deserialize(getMessage(name)));
            }

            else if (f.isDynamicLength() && f.isVector() && !f.isUseBBW() && f.isUseTypeManager() && f.getWriteMethod().equals("")) {
                int loc1 = reader.readShort();
                int loc2 = 0;
                List<String> resultList = new ArrayList<>();
                while(loc2 < loc1){
                    short id = reader.readShort();
                    String name = ProtocolTypeManager.getInstance(id);
                    resultList.addAll(deserialize(getMessage(name)));
                    loc2++;
                }
                result.addAll(resultList);
            }

            else if (f.isDynamicLength() && f.isVector() && !f.isUseBBW() && !f.isUseTypeManager() && f.getWriteMethod().equals("")) {
                int loc1 = reader.readShort();
                int loc2 = 0;
                List<String> resultList = new ArrayList<>();
                while(loc2 < loc1){
                    String name = f.getType();
                    resultList.addAll(deserialize(getMessage(name)));
                    loc2++;
                }
                result.addAll(resultList);
            }

            else if (f.isDynamicLength() && f.isVector() && !f.isUseBBW() && !f.isUseTypeManager() && !f.getWriteMethod().equals("") && !f.getWriteLengthMethod().equals("")) {
                Object value = getValue(f.getWriteLengthMethod());
                if(value instanceof Short){
                    short loc1 = (short) value;
                    int loc2 = 0;
                    List<String> resultList = new ArrayList<>();
                    while(loc2 < loc1){
                        resultList.add(formatField(f.getName(),getValue(f.getWriteMethod())));
                        loc2++;
                    }
                    result.addAll(resultList);
                } else if (value instanceof Integer){
                    int loc1 = (int) value;
                    int loc2 = 0;
                    List<String> resultList = new ArrayList<>();
                    while(loc2 < loc1){
                        resultList.add(formatField(f.getName(),getValue(f.getWriteMethod())));
                        loc2++;
                    }
                    result.addAll(resultList);
                } else {
                    Log.writeLogDebugMessage("No instance of : " +value);
                }
            }

            else if (!f.isDynamicLength() && f.isVector() && !f.isUseBBW() && !f.isUseTypeManager() && f.getLength() == 5) {
                int loc1 = 0;
                List<String> resultList = new ArrayList<>();
                while(loc1 < f.getLength()){
                    resultList.add(formatField(f.getName(),getValue(f.getWriteMethod())));
                    loc1++;
                }
                result.addAll(resultList);
            }

            else if (!f.isDynamicLength() && f.isVector() && !f.isUseBBW() && !f.isUseTypeManager() && f.getLength() == 2) {
                int loc1 = 0;
                List<String> resultList = new ArrayList<>();
                while(loc1 < f.getLength()){
                    resultList.add(formatField(f.getName(),getValue(f.getWriteMethod())));
                    loc1++;
                }
                result.addAll(resultList);
            }

            else if (!f.isDynamicLength() && !f.isVector() && f.isUseBBW() && !f.isUseTypeManager() && f.getWriteMethod().equals("")) {
            }

            else {
                System.out.println(f.getName());
                System.out.println("isDynamicLength : " + f.isDynamicLength());
                System.out.println("isVector : " + f.isVector());
                System.out.println("useBBW : " + f.isUseBBW());
                System.out.println("useTypeManager : " + f.isUseTypeManager());
                System.out.println("getWriteMethod : " + f.getWriteMethod().equals(""));
                System.out.println("----------------");
            }

        }

        return result;
    }

    public List<String> deserializeMsg(Message message) throws Exception {

        Log.writeLogDebugMessage("Name message : " + message.getName());

        List<String> result = new ArrayList<>();

        List<Field> fields = message.getFields();

        if(fields == null){
            return null;
        }

        int indexBbw1 = 0;
        byte flag = 0;
        for (Field f : fields) {

            if (f.isUseBBW()) {

                Log.writeLogDebugMessage("Name : "  + f.getName());

                if (indexBbw1 == 0) {
                    flag = (byte) reader.readUnsignedByte();
                    result.add(formatField(f.getName(),BooleanByteWrapper.GetFlag(flag,(byte) 0)));
                }
                else if (indexBbw1 < 8) {
                    result.add(formatField(f.getName(),BooleanByteWrapper.GetFlag(flag,(byte) indexBbw1)));
                }
                else {
                    indexBbw1 = 0;
                    flag = (byte) reader.readUnsignedByte();
                    result.add(formatField(f.getName(),BooleanByteWrapper.GetFlag(flag,(byte) indexBbw1)));
                }
                indexBbw1++;
            }
        }

        for (Field f : fields) {

            Log.writeLogDebugMessage("Name : "  + f.getName());


            if (!f.isDynamicLength() && !f.isVector() && !f.isUseBBW() && !f.isUseTypeManager() && !f.getWriteMethod().equals("")) {
                result.add(formatField(f.getName(),getValue(f.getWriteMethod())));
            }

            else if (!f.isDynamicLength() && !f.isVector() && !f.isUseBBW() && !f.isUseTypeManager() && f.getWriteMethod().equals("")) {
                result.addAll(deserialize(getMessage(f.getType())));
            }

            else if (!f.isDynamicLength() && !f.isVector() && !f.isUseBBW() && f.isUseTypeManager() && f.getWriteMethod().equals("")) {
                String name = ProtocolTypeManager.getInstance(reader.readShort());
                result.addAll(deserialize(getMessage(name)));
            }

            else if (f.isDynamicLength() && f.isVector() && !f.isUseBBW() && f.isUseTypeManager() && f.getWriteMethod().equals("")) {
                int loc1 = reader.readShort();
                int loc2 = 0;
                List<String> resultList = new ArrayList<>();
                while(loc2 < loc1){
                    short id = reader.readShort();
                    String name = ProtocolTypeManager.getInstance(id);
                    resultList.addAll(deserialize(getMessage(name)));
                    loc2++;
                }
                result.addAll(resultList);
            }

            else if (f.isDynamicLength() && f.isVector() && !f.isUseBBW() && !f.isUseTypeManager() && f.getWriteMethod().equals("")) {
                int loc1 = reader.readShort();
                int loc2 = 0;
                List<String> resultList = new ArrayList<>();
                while(loc2 < loc1){
                    String name = f.getType();
                    resultList.addAll(deserialize(getMessage(name)));
                    loc2++;
                }
                result.addAll(resultList);
            }

            else if (f.isDynamicLength() && f.isVector() && !f.isUseBBW() && !f.isUseTypeManager() && !f.getWriteMethod().equals("") && !f.getWriteLengthMethod().equals("")) {
                Object value = getValue(f.getWriteLengthMethod());
                if(value instanceof Short){
                    short loc1 = (short) value;
                    int loc2 = 0;
                    List<String> resultList = new ArrayList<>();
                    while(loc2 < loc1){
                        resultList.add(formatField(f.getName(),getValue(f.getWriteMethod())));
                        loc2++;
                    }
                    result.addAll(resultList);
                } else if (value instanceof Integer){
                    int loc1 = (int) value;

                    int loc2 = 0;
                    List<String> resultList = new ArrayList<>();
                    while(loc2 < loc1){
                        resultList.add(formatField(f.getName(),getValue(f.getWriteMethod())));
                        loc2++;
                    }
                    result.addAll(resultList);
                } else {
                    Log.writeLogDebugMessage("No instance of : " +value);
                }
            }

            else if (!f.isDynamicLength() && f.isVector() && !f.isUseBBW() && !f.isUseTypeManager() && f.getLength() == 5) {
                int loc1 = 0;
                List<String> resultList = new ArrayList<>();
                while(loc1 < f.getLength()){
                    resultList.add(formatField(f.getName(),getValue(f.getWriteMethod())));
                    loc1++;
                }
                result.addAll(resultList);
            }

            else if (!f.isDynamicLength() && f.isVector() && !f.isUseBBW() && !f.isUseTypeManager() && f.getLength() == 2) {
                int loc1 = 0;
                List<String> resultList = new ArrayList<>();
                while(loc1 < f.getLength()){
                    resultList.add(formatField(f.getName(),getValue(f.getWriteMethod())));
                    loc1++;
                }
                result.addAll(resultList);
            }

            else if (!f.isDynamicLength() && !f.isVector() && f.isUseBBW() && !f.isUseTypeManager() && f.getWriteMethod().equals("")) {
            }

            else {
                System.out.println(f.getName());
                System.out.println("isDynamicLength : " + f.isDynamicLength());
                System.out.println("isVector : " + f.isVector());
                System.out.println("useBBW : " + f.isUseBBW());
                System.out.println("useTypeManager : " + f.isUseTypeManager());
                System.out.println("getWriteMethod : " + f.getWriteMethod().equals(""));
                System.out.println("----------------");
            }

        }

        return result;
    }

    private List<String> getValues(Field f) throws Exception {
        List<String> result = new ArrayList<>();
        Object value = getValue(f.getWriteMethod());
        if (value != null) {
            result.add(formatField(f.getName(), value));
        } else {
            if (f.isUseTypeManager()) {
                short id = reader.readShort();
                String name = ProtocolTypeManager.getInstance(id);
                result.addAll(deserialize(getMessage(name)));
            } else {
                String name = f.getType();
                result.addAll(deserialize(getMessage(name)));
            }
        }
        return result;
    }


    private Object getValue(String method) throws IOException {
        Log.writeLogDebugMessage("Available : " + reader.available() + " - method : " + method);
        Object value = new Object();
        switch (method) {
            case "writeUTF":
                value = reader.readUTF(); break;
            case "writeByte":
                value = reader.readByte(); break;
            case "writeVarShort":
                value = reader.readVarShort(); break;
            case "writeDouble":
                value = reader.readDouble(); break;
            case "writeShort":
                value = reader.readShort(); break;
            case "writeInt":
                value = reader.readInt(); break;
            case "writeVarInt":
                value = reader.readVarInt(); break;
            case "writeVarLong":
                value = reader.readVarLong(); break;
            case "writeBoolean":
                value = reader.readBoolean(); break;
            case "writeFloat":
                value = reader.readFloat(); break;
            case "writeUnsignedInt":
                value = reader.readUnsignedByte(); break;
            case "":
                value = null;
        }
        Log.writeLogDebugMessage("Value : " +value);
        return value;
    }

    public Message getMessage(int id) {
        for (Message msg : JsonLoader.Messages) {
            if (msg.getProtocolId() == id) {
                return msg;
            }
        }
        return null;
    }

    public Message getMessage(String name) {
        for (Message msg : JsonLoader.Messages) {
            if (msg.getName().equals(name)) {
                return msg;
            }
        }
        for (Message msg : JsonLoader.Types) {
            if (msg.getName().equals(name)) {
                return msg;
            }
        }
        return null;
    }

    private String formatField(String name, Object value) {
        return String.format("%s: %s", name, value);
    }

}
