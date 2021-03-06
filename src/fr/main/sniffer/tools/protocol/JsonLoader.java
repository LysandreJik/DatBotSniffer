package fr.main.sniffer.tools.protocol;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonLoader {

    public static List<Message> Messages;
    public static List<Message> Types;

    public JsonLoader(String file){
        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) parser.parse(new FileReader(file));
            Messages = parseJsonBuilderArray((JSONArray) obj.get("Messages"));
            Types = parseJsonBuilderArray((JSONArray) obj.get("Types"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }    }

    private List<Message> parseJsonBuilderArray(JSONArray array) {

        List<Message> list = new ArrayList<>();

        for (Object item : array) {
            JSONObject object = (JSONObject) item;
            Message msg = new Message();
            msg.setName((String) object.get("Name"));
            msg.setNamespace((String) object.get("Namespace"));
            msg.setParents((String) object.get("Parent"));
            msg.setProtocolId((long) object.get("ProtocolID"));
            msg.setFields(parseFieldArray((JSONArray) object.get("Fields")));
            msg.setUseHashFunc((boolean) object.get("UseHashFunc"));
            list.add(msg);
        }

        return list;
    }

    private List<Field> parseFieldArray(JSONArray array) {

        List<Field> list = new ArrayList<>();

        if (array == null) return null;

        for (Object item : array) {
            JSONObject object = (JSONObject) item;
            Field obj = new Field();
            obj.setName((String) object.get("Name"));
            obj.setType((String) object.get("Type"));

            String type = (String) object.get("Type");
            obj.setWriteMethod((String) object.get("WriteMethod"));
            obj.setIsVector((boolean) object.get("IsVector"));
            obj.setDynamicLength((boolean) object.get("IsDynamicLength"));
            obj.setLength((long) object.get("Length"));
            obj.setWriteLengthMethod((String) object.get("WriteLengthMethod"));
            obj.setUseTypeManager((boolean) object.get("UseTypeManager"));
            obj.setUseBBW((boolean) object.get("UseBBW"));
            obj.setBbwPosition((long) object.get("BBWPosition"));
            if(!((String) object.get("WriteMethod")).equals("") && (boolean) object.get("UseTypeManager")){
                System.out.println(obj);
            }
            list.add(obj);
        }

        return list;
    }

}
