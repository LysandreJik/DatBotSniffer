package fr.main.display;

import fr.main.sniffer.Main;
import fr.main.sniffer.reader.InitListener;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class FrameComponent {
    private JButton button1;
    private JTable table1;
    JPanel panelMain;
    private JScrollPane scrollPane1;
    private JButton stopButton;
    private JButton toggleSnifferButton;
    private JButton removeAllButton;
    JScrollBar scrollBar;
    List<Data> columnData;
    boolean runningScroll = true;
    boolean runningSniff = true;

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
        scrollBar = scrollPane1.getVerticalScrollBar();

        Action extend = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                int modelRow = Integer.valueOf( e.getActionCommand() );
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {

                            boolean tryExtend = true;
                            while(tryExtend){
                                try{
                                    if(columnData.get(modelRow).isShowingTooltip()){
                                        model.setValueAt(columnData.get(modelRow).getExtendedData(), modelRow, 2);
                                        columnData.get(modelRow).setShowingTooltip(false);
                                    }else{
                                        model.setValueAt(columnData.get(modelRow).getReducedData(), modelRow, 2);
                                        columnData.get(modelRow).setShowingTooltip(true);
                                    }
                                    tryExtend = false;
                                }catch(Exception e){}
                            }
                        updateRowHeights();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                scrollBar.setValue(scrollBar.getMaximum());
                            }
                        });
                    }
                });
            }
        };

        ButtonColumn buttonColumnExtend = new ButtonColumn(table1, extend, 3);
        buttonColumnExtend.setMnemonic(KeyEvent.VK_D);

        Action delete = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                JTable table = (JTable)e.getSource();
                int modelRow = Integer.valueOf( e.getActionCommand() );
                ((DefaultTableModel)table.getModel()).removeRow(modelRow);
            }
        };

        ButtonColumn buttonColumnDelete = new ButtonColumn(table1, delete, 4);
        buttonColumnDelete.setMnemonic(KeyEvent.VK_D);

        columnData = new ArrayList<>();
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(runningScroll)
                    runningScroll = false;
                else
                    runningScroll = true;
            }
        });
        removeAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                int nbRow = model.getRowCount();
                for(int i = 0; i < nbRow; i++){
                    model.removeRow(0);
                }

                columnData.clear();
            }
        });
        toggleSnifferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(runningSniff) {
                    Main.listeners.get(0).breakLoop();
                    runningSniff = false;
                }else{
                    Main.listeners.get(0).startLoop();
                    runningSniff = true;
                }
            }
        });
    }

    public void addPacket(int id, String name, String tooltip, String value){

        Data data = new Data(id, name, tooltip, value);
        columnData.add(data);

        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        Object[] row = data.getReducedDataObject();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.addRow(row);
                updateRowHeights();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if(runningScroll){
                            scrollBar.setValue(scrollBar.getMaximum());
                        }
                    }
                });
            }
        });
    }


    public String[] getColumnNames(){
        String[] columnNames = {"Packet ID", "Packet name", "Packet value", "extend", "remove"};
        return columnNames;
    }

    public Object[][] getData(){
        Object[][] data = new Object[0][];
        return data;
    }

    private void createUIComponents() {
        TableModel tableModel = new DefaultTableModel(getData(), getColumnNames());
        table1 = new JTable(tableModel);
        table1.setAutoCreateColumnsFromModel(true);

        table1.getColumnModel().getColumn(0).setMaxWidth(100);

        TableColumn tm = table1.getColumnModel().getColumn(0);
        tm.setCellRenderer(new ColorColumnRenderer(Color.white, Color.BLACK));

        table1.getColumnModel().getColumn(1).setPreferredWidth(300);

        tm = table1.getColumnModel().getColumn(1);
        tm.setCellRenderer(new ColorColumnRenderer(Color.white, Color.black));

        table1.getColumnModel().getColumn(2).setPreferredWidth(500);

        tm = table1.getColumnModel().getColumn(2);
        tm.setCellRenderer(new ColorColumnRenderer(Color.white, Color.darkGray));

        table1.getColumnModel().getColumn(3).setMaxWidth(50);
        table1.getColumnModel().getColumn(4).setMaxWidth(200);

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
