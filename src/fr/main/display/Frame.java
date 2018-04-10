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
}
