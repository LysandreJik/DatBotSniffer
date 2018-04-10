package fr.main.display;

import javax.swing.*;

public class Frame {
    private JPanel panel;
    private JTextArea textArea1;

    public Frame(){
        textArea1 = new JTextArea();
    }

    public void addPacket(String name, String value){
        textArea1.append(name);
        textArea1.append(value);
    }
}
