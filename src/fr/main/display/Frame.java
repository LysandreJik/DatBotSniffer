package fr.main.display;

import javax.swing.*;

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

    public void addPacket(int id, String name, String value){
        try{
            bf.addPacket(id, name, value);
        }catch(NullPointerException e){
            throw new NullPointerException("The frame has not been initialized yet! Call new Frame().display() first.");
        }
    }

    public void addPacket(int id, String name, String[] values){
        try{
            StringBuilder valuesString = new StringBuilder("<html>");
            for(String value : values){
                valuesString.append(value).append("<br/>");
            }
            valuesString = new StringBuilder(valuesString.substring(0, valuesString.length() - 5) + "</html>");
            bf.addPacket(id, name, valuesString.toString());
        }catch(NullPointerException e){
            throw new NullPointerException("The frame has not been initialized yet! Call new Frame().display() first.");
        }
    }

    public static void main(String[] args){
        Frame frame = new Frame();
        frame.display();
        frame.addPacket(150, "OIOUOIFJSGFDBJDKFDSGJFDDFLKQLDSGJKKL", "<html>SIGJFIDSegsthjlgkfdqsgdjlthg<br/>fkdmdqrmjslgkdfjqmrsjthlgfnklfgqmgrljshkgmdfkgqmjshgklbmfkqjgln\nshmjkgdbflqgmjgshdlgbfjqdùglmdfjkshklqmdj</html>");
        frame.addPacket(150, "OIOUOIFJSGFDBJDKFDSGJFDDFLKQLDSGJKKL", "<html>SIGJFIDSegsthjlgkfdqsgdjlthg<br/>fkdmdqrmjslgkdfjqmrsjthlgfnkl<br/>fgqmgrljshkgmdfkgqmjshgklbmfkqjgl<br/>n\nshmjkgdb<br/>flq<br/>gmjgshdlgbfjqdùglmdfjkshklqmdj</html>");
        frame.addPacket(500, "Name", new String[]{"Nom du packet", "ID : 50", "x : 20", "y : 50"});
    }
}
