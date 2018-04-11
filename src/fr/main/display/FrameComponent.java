package fr.main.display;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrameComponent {
    private JButton button1;
    private JTable table1;
    JPanel panelMain;

    public FrameComponent(){
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); //Windows Look and feel
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    public void addPacket(int id, String name, String value){
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        Object[] row = { id, name, value };
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.addRow(row);
                updateRowHeights();
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

        table1.getColumnModel().getColumn(0).setPreferredWidth(80);

        TableColumn tm = table1.getColumnModel().getColumn(0);
        tm.setCellRenderer(new ColorColumnRenderer(Color.white, Color.BLACK));

        table1.getColumnModel().getColumn(1).setPreferredWidth(300);

        tm = table1.getColumnModel().getColumn(1);
        tm.setCellRenderer(new ColorColumnRenderer(Color.white, Color.black));

        table1.getColumnModel().getColumn(2).setPreferredWidth(500);

        tm = table1.getColumnModel().getColumn(2);
        tm.setCellRenderer(new ColorColumnRenderer(Color.white, Color.darkGray));
    }

    private void updateRowHeights() {
        for (int row = 0; row < table1.getRowCount(); row++)
        {
            int rowHeight = table1.getRowHeight();

            for (int column = 0; column < table1.getColumnCount(); column++)
            {
                Component comp = table1.prepareRenderer(table1.getCellRenderer(row, column), row, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }

            table1.setRowHeight(row, rowHeight);
        }
    }

    class ColorColumnRenderer extends DefaultTableCellRenderer {
        Color bkgndColor, fgndColor;

        public ColorColumnRenderer(Color bkgnd, Color foregnd) {
            super();
            bkgndColor = bkgnd;
            fgndColor = foregnd;
        }

        public Component getTableCellRendererComponent
                (JTable table, Object value, boolean isSelected,
                 boolean hasFocus, int row, int column)
        {
            Component cell = super.getTableCellRendererComponent
                    (table, value, isSelected, hasFocus, row, column);

            cell.setBackground( bkgndColor );
            cell.setForeground( fgndColor );

            return cell;
        }
    }
}