package fr.main.display;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Frame {

    JFrame frame;
    FrameComponent bf;


    public void display(){
        frame = new JFrame();
        bf = new FrameComponent();
        frame.setContentPane(bf.panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    public void dispose(){
        try{
            frame.dispose();
        }catch(NullPointerException e){
            throw new NullPointerException("The frame has not been initialized yet! Call new Frame().display() first.");
        }
    }

    public void addPacket(int id, String name, String tooltip, String value){
        try{
            bf.addPacket(id, name, tooltip, value);
        }catch(NullPointerException e){
            throw new NullPointerException("The frame has not been initialized yet! Call new Frame().display() first.");
        }
    }

    public void addPacket(int id, String name, String tooltip, List<String> values){


        try{
            StringBuilder valuesString = new StringBuilder("<html>");
            for(String value : values){
                valuesString.append(value).append("<br/>");
            }
            valuesString = new StringBuilder(valuesString.substring(0, valuesString.length() - 5) + "</html>");
            bf.addPacket(id, name, tooltip, valuesString.toString());
        }catch(NullPointerException e){
            throw new NullPointerException("The frame has not been initialized yet! Call new Frame().display() first.");
        }
    }
}
