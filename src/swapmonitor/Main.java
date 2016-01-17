/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package swapmonitor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import defaultclass.*;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;
import java.awt.Toolkit;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author kamol
 */
public class Main extends Observable implements ActionListener {

    JPanel JP = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    //JPanel JP2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    JPanel JP2 = new JPanel(new BorderLayout());
    JPanel JPW = new JPanel(new BorderLayout(5, 5));
    JPanel JP1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
    SQL5484 sql5484 = new SQL5484();
    ResultSet resultset = null;
    Vector SiteName, faultySiteName, clearSiteName, deactivateSiteName, cellDownSite, downSite;
    int noOfRow, noOfColumns;
    Object[][] data;
    JTextField JL[];
    Timer t = new Timer(5000, this);
    private JTable resultTable;
    private MyTableModel tableModel;
    private ResultSetMetaData metadata;
    //Color Option
    Color colRed = new Color(255, 102, 102); //Red Color
    Color colYellow = new Color(255, 255, 102); //Yellow Color
    Color colGreen = new Color(153, 255, 153);
    Color colGray = new Color(153, 153, 153);
    Color colLightRed = new Color(255, 204, 153);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    //Info Label
    JTextField timeLabel = new JTextField("Database Refresh Time : ");
    long DateA, DateB;
    Date lastDateModified, actualLastDateModified;
    int noTotal, noUp, noDown, noFaulty, noDeactivated, noCellDwon;
    southPanel sP;
    infoPanel iP;

    Main() {
        bodyFrame MA = new bodyFrame();
        MA.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MA.setSize(screenSize.width, screenSize.height - (screenSize.height / 13));
        MA.setVisible(true);
    }

    public class bodyFrame extends JFrame {

        bodyFrame() {
            super("SWAP MONITOR");
            setLayout(new BorderLayout());

            String sqlquery = "select * from rtinfo.dbo.MonitorSiteAlarm_RefreshTime";
            try {
                resultset = sql5484.SQL5484(sqlquery);
                while (resultset.next()) {
                    lastDateModified = (Date) resultset.getObject("RefreshTime");
                    DateA = lastDateModified.getTime();
                    //System.out.println("DateA: "+DateA);
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }



            JMenu fileMenu = new JMenu("File");
            fileMenu.setMnemonic('F');

            JMenuItem addSite = new JMenuItem("Add Site");
            JMenuItem channelAnalyzer = new JMenuItem("Channel Analyzer");
            addSite.setMnemonic('A');
            channelAnalyzer.setMnemonic('C');
            fileMenu.add(addSite);
            fileMenu.add(channelAnalyzer);


            addSite.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent event) {
                            AddSite AS = new AddSite();
                            AS.setResizable(false);
                            AS.setVisible(true);
                        }
                    });
            channelAnalyzer.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent event) {
                            ChannelAnalyzer CA = new ChannelAnalyzer();
                            CA.setVisible(true);
                        }
                    });
            JMenuBar bar = new JMenuBar();
            setJMenuBar(bar);
            bar.add(fileMenu);

            try {
                sqlquery = "select * from swapmonitorsite order by SiteName";
                resultset = sql5484.SQL5484(sqlquery);
                SiteName = new Vector();
                while (resultset.next()) {
                    SiteName.addElement((String) resultset.getString("SiteName").trim());

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            int siteW = 0;
            int siteH = 0;
            int textSiteW = 0;
            int textSiteH = 0;
            int fintSize = 0;
            if (SiteName.size() > 100) {
                siteW = 80;
                siteH = 30;
                textSiteW = 75;
                textSiteH = 25;
                fintSize = 12;
            } else if (SiteName.size() > 50 && SiteName.size() <= 100) {
                siteW = 90;
                siteH = 35;
                textSiteW = 85;
                textSiteH = 30;
                fintSize = 14;
            } else if (SiteName.size() <= 50) {
                siteW = 120;
                siteH = 45;
                textSiteW = 115;
                textSiteH = 40;
                fintSize = 18;
            }
            JL = new JTextField[SiteName.size()];

            for (int i = 0; i < SiteName.size(); i++) {
                //System.out.println(SiteName.elementAt(i));
                String siteName = (String) SiteName.elementAt(i);

                JL[i] = new JTextField();
                JL[i].setPreferredSize(new Dimension(75, 20));
                JL[i].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                JL[i].setFont(new java.awt.Font("Tahoma", 1, fintSize));
                JL[i].setText(siteName);
                JL[i].setPreferredSize(new Dimension(textSiteW, textSiteH));
                JL[i].setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
                JL[i].setBackground(colRed);
                JPanel siteHolder = new JPanel();
                siteHolder.setPreferredSize(new Dimension(siteW, siteH));
                siteHolder.add(JL[i]);
                JP.add(siteHolder);
            }


            JLabel showLabel = new JLabel();
            showLabel.setFont(new java.awt.Font("Tahoma", 1, 36));
            //showLabel.setForeground(new java.awt.Color(51, 0, 255));
            showLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            showLabel.setText("S W A P     M O N I T O R");
            //showLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

            /*
            try {
            tableModel = new MyTableModel();

            resultTable = new JTable(tableModel);

            } catch (Exception e1) {
            e1.printStackTrace();
            }
             */
            JLabel radarLabel = new JLabel();
            ImageIcon icon = new ImageIcon("Other\\radar.gif");
            radarLabel.setIcon(icon);

            JButton refreshButton = new JButton("Refresh");

            refreshButton.addActionListener((new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    performedTask();

                }
            }));



            JP1.add(showLabel);

            JPanel JPB = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            JPB.add(refreshButton);

            JPW.setPreferredSize(new Dimension(250, 300));
            JPW.add(radarLabel, BorderLayout.NORTH);
            JPW.add(JPB, BorderLayout.SOUTH);

            sP = new southPanel();
            iP = new infoPanel();

            JP2.add(sP, BorderLayout.NORTH);
            JP2.add(iP, BorderLayout.SOUTH);


            add(JP1, BorderLayout.NORTH);
            add(JP, BorderLayout.CENTER);
            add(JP2, BorderLayout.SOUTH);
            add(JPW, BorderLayout.WEST);

            performedTask();
            t.start();
        }
    }

    public void actionPerformed(ActionEvent e) {
        String sqlquery = "select RefreshTime,convert(float,(getdate()-RefreshTime))*24 as Gap from MonitorSiteAlarm_RefreshTime";
        
        try {
            resultset = sql5484.SQL5484(sqlquery);
            while (resultset.next()) {
                actualLastDateModified = (Date) resultset.getObject("RefreshTime");
                DateB = actualLastDateModified.getTime();
                String refreshTime = (String) resultset.getString("RefreshTime").substring(11, 19);
                float Gap = Float.valueOf(resultset.getString("Gap")).floatValue();
                iP.changeText(refreshTime, Gap);
            }

            if (DateA != DateB) {
                DateA = DateB;
                performedTask();
                setChanged();
                notifyObservers();
            }



        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }

    void performedTask() {
        faultySiteName = new Vector();
        clearSiteName = new Vector();
        deactivateSiteName = new Vector();
        cellDownSite = new Vector();
        downSite = new Vector();
        if (!faultySiteName.isEmpty()) {
            faultySiteName.removeAllElements();
            clearSiteName.removeAllElements();
            deactivateSiteName.removeAllElements();
            cellDownSite.removeAllElements();
            downSite.removeAllElements();
        }
        try {
            String sqlquery = "select * from SwapSiteSummary";
            resultset = sql5484.SQL5484(sqlquery);
            resultset.first();
            noTotal = Integer.parseInt(resultset.getString("TotalSite"));
            noUp = Integer.parseInt(resultset.getString("UpSite"));
            noDown = Integer.parseInt(resultset.getString("DownSite"));
            noFaulty = Integer.parseInt(resultset.getString("FaultySite"));
            noDeactivated = Integer.parseInt(resultset.getString("DeactivateSite"));
            noCellDwon = Integer.parseInt(resultset.getString("CellDownSite"));
            noUp = noUp - noDeactivated;
            sP.changeText(noTotal, noUp, noDown, noFaulty, noDeactivated, noCellDwon);





            /*

            sqlquery = "select Distinct SiteName from rtinfo.dbo.monitorsitealarm_real where SiteName in (Select SiteName from Rtinfo.dbo.SwapMonitorSite)";
            ResultSet resultset = sql5484.SQL5484(sqlquery);
            while (resultset.next()) {
            faultySiteName.addElement((String) resultset.getString("SiteName").trim());
            }

            for (int i = 0; i < faultySiteName.size(); i++) {
            String currentSiteName = (String) faultySiteName.elementAt(i);
            int indexSite = SiteName.indexOf(currentSiteName);
            //System.out.println(currentSiteName+"    : "+indexSite);
            sqlquery = "select * from rtinfo.dbo.monitorsitealarm_real where SiteName ='" + currentSiteName + "' and AlarmName like '%OML%'";
            resultset = sql5484.SQL5484(sqlquery);
            resultset.last();
            int noRow = resultset.getRow();

            if (noRow > 0) {
            JL[indexSite].setBackground(colRed);
            } else {
            sqlquery = "select * from rtinfo.dbo.monitorsitealarm_real where SiteName ='" + currentSiteName + "' and AlarmName not like '%OML%'";
            resultset = sql5484.SQL5484(sqlquery);
            resultset.last();
            noRow = resultset.getRow();
            if (noRow > 0) {
            JL[indexSite].setBackground(colYellow);
            } else {
            JL[indexSite].setBackground(colGreen);
            }
            }
            }
             * 
             */

            // Coloring for Site Down
            sqlquery = "select *  from rtinfo.dbo.monitorsitealarm_real where AlarmName like '%OML%' and SiteName in (Select Distinct SiteName from rtinfo.dbo.SwapMonitorSite)";
            resultset = sql5484.SQL5484(sqlquery);
            while (resultset.next()) {
                downSite.addElement((String) resultset.getString("SiteName").trim());
            }
            for (int i = 0; i < downSite.size(); i++) {
                String currentSiteName = (String) downSite.elementAt(i);
                int indexSite = SiteName.indexOf(currentSiteName);
                //System.out.println(currentSiteName+"    : "+indexSite);
                JL[indexSite].setBackground(colRed);
            }


            // Coloring for Cell Down
            sqlquery = "select * from rtinfo.dbo.MonitorCellDownSite";
            resultset = sql5484.SQL5484(sqlquery);
            while (resultset.next()) {
                cellDownSite.addElement((String) resultset.getString("SiteName").trim());
            }
            for (int i = 0; i < cellDownSite.size(); i++) {
                String currentSiteName = (String) cellDownSite.elementAt(i);
                int indexSite = SiteName.indexOf(currentSiteName);
                //System.out.println(currentSiteName+"    : "+indexSite);
                JL[indexSite].setBackground(colLightRed);
            }

            // Coloring for faulty Site
            sqlquery = "select * from MonitorFaultySite";
            resultset = sql5484.SQL5484(sqlquery);
            while (resultset.next()) {
                faultySiteName.addElement((String) resultset.getString("SiteName").trim());
            }
            for (int i = 0; i < faultySiteName.size(); i++) {
                String currentSiteName = (String) faultySiteName.elementAt(i);
                int indexSite = SiteName.indexOf(currentSiteName);
                //System.out.println(currentSiteName+"    : "+indexSite);
                JL[indexSite].setBackground(colYellow);
            }




            // Coloring for Site UP
            sqlquery = "Select SiteName from rtinfo.dbo.SwapMonitorSite where SiteName not in (select Distinct SiteName from rtinfo.dbo.monitorsitealarm_real ) order by SiteName";
            resultset = sql5484.SQL5484(sqlquery);
            while (resultset.next()) {
                clearSiteName.addElement((String) resultset.getString("SiteName").trim());
            }
            for (int i = 0; i < clearSiteName.size(); i++) {
                String currentSiteName = (String) clearSiteName.elementAt(i);
                int indexSite = SiteName.indexOf(currentSiteName);
                //System.out.println(currentSiteName+"    : "+indexSite);
                JL[indexSite].setBackground(colGreen);
            }

            // Coloring for Deactivated Site
            sqlquery = "select * from rtinfo.dbo.monitorsitestatus where SiteName in (Select SiteName from rtinfo.dbo.SwapMonitorSite) and ActiveStatus = 'DEACTIVATED'";
            resultset = sql5484.SQL5484(sqlquery);
            while (resultset.next()) {
                deactivateSiteName.addElement((String) resultset.getString("SiteName").trim());
            }
            for (int i = 0; i < deactivateSiteName.size(); i++) {

                String currentSiteName = (String) deactivateSiteName.elementAt(i);
                int indexSite = SiteName.indexOf(currentSiteName);
                System.out.println(currentSiteName + "    : " + indexSite);
                JL[indexSite].setBackground(colGray);
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    class MyTableModel extends AbstractTableModel {

        String[] columnNames;
        String sqlquery = "select SiteName,AlarmName,EventTime from rtinfo.dbo.monitorsitealarm_real "
                + "where SiteName in (Select SiteName from rtinfo.dbo.SwapMonitorSite) order by EventTime desc";

        MyTableModel() {
            setQuery();
        }

        void setQuery() {
            try {
                resultset = sql5484.SQL5484(sqlquery);
                metadata = resultset.getMetaData();
                noOfColumns = metadata.getColumnCount();
                resultset.last();
                noOfRow = resultset.getRow();
                columnNames = new String[noOfColumns];
                data = new Object[noOfRow][noOfColumns];

                for (int i = 0; i < noOfColumns; i++) {
                    columnNames[i] = (String) metadata.getColumnName(i + 1);
                }

                resultset.first();
                for (int j = 0; j < noOfRow; j++) {
                    for (int i = 0; i < noOfColumns; i++) {
                        data[j][i] = resultset.getString(i + 1);
                    }
                    resultset.next();
                    System.out.println();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public int getRowCount() {
            return noOfRow;
        }

        public int getColumnCount() {
            return noOfColumns;
        }

        public Object getValueAt(int r, int c) {
            return data[r][c];
        }

        public String getColumnName(int c) {
            return columnNames[c];
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new Main();
    }
}
