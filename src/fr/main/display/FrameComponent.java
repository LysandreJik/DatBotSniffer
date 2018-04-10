package fr.main.display;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class FrameComponent {
    private JButton button1;
    private JTable table1;
    JPanel panelMain;
    private JFrame frame;

    public void addPacket(int id, String name, String value){
        DefaultTableModel model = (DefaultTableModel) table1.getModel();

        Object[] row = { id, name, value };
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.addRow(row);
            }
        });


    }

    public String[] getColumnNames(){
        String[] columnNames = {"Packet ID", "Packet name", "Packet value"};
        return columnNames;
    }

    public Object[][] getData(){
        Object[][] data = {
                {150, "RAND_NAME", "RANDOM VALUES"}
        };
        return data;
    }

    private void createUIComponents() {
        TableModel tableModel = new DefaultTableModel(getData(), getColumnNames());
        table1 = new JTable(tableModel);
        table1.setAutoCreateColumnsFromModel(true);
    }

    public void dispose(){
        frame.dispose();
    }
}
