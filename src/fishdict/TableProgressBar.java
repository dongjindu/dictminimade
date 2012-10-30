package fishdict;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
        
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;

public class TableProgressBar {
    private String[] columnNames = {"Task groups", "Status"};
    private Object[][] data = {{"dummy", 100}};
    private DAO dao;
    private static final Integer pslock = 1;
    
    private DefaultTableModel model = new DefaultTableModel(data, columnNames) {
        private static final long serialVersionUID = 1L;
        @Override
        public Class<?> getColumnClass(int column) {
            return getValueAt(0, column).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private JTable table = new JTable(model);
    private     ResultSet[] mrs = new ResultSet[5]; 
    private     Integer m0[] = new Integer[5];
    
    public JComponent makeUI() {
        TableColumn column = table.getColumnModel().getColumn(1);
        column.setCellRenderer(new ProgressRenderer());
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ((DefaultTableModel)table.getModel()).removeRow(0);
                dao = DAO.getInstance();
                ResultSet rs;
                Integer numberofwords = 0;
                synchronized (pslock) {
                    dao.query("select count(word) as nunmberofwords from voc where dragged = ? and suggested = ? and docnull = ?");
//                    dao.query("select count(word) as nunmberofwords from voc where (dragged = ? or suggested = ? or docnull = ?) and word in (?, ?, ?)");
                    dao.setBoolean(1, false);
                    dao.setBoolean(2, false);
                    dao.setBoolean(3, false);
//                    dao.setString(4, "him");
//                    dao.setString(5, "whom");
//                    dao.setString(6, "'em");
                    rs = dao.executeQuery();
                }
                try{
                    rs.first();
                    numberofwords = rs.getInt(1);
                    m0[0] = numberofwords / 5;
                    m0[1] = m0[0];
                    m0[2] = m0[0];
                    m0[3] = m0[0];
                    m0[4] = numberofwords - m0[0] - m0[1] - m0[2] - m0 [3];
                    Integer accumulate = 0;
                    for (int i = 0; i < m0.length; i++){
                        accumulate = accumulate + m0[i];
                        String sql = "select word from voc where dragged = ? and suggested = ? and docnull = ? limit ?, ?";
                        synchronized (pslock) {
                            dao.query(sql);
//                    dao.query("select word from voc where (dragged = ? or suggested = ? or docnull = ?) and word in (?, ?, ?) limit ?, ?");
                            dao.setBoolean(1, false);
                            dao.setBoolean(2, false);
                            dao.setBoolean(3, false);
//                    dao.setString(4, "him");
//                    dao.setString(5, "whom");
//                    dao.setString(6, "'em");
                            dao.setInt(4, accumulate - m0[i]);
                            dao.setInt(5, m0[i]);
                            mrs[i] = dao.executeQuery();
                        }
                    }
                    accumulate = 0;
                    for (int i = 0; i < m0.length; i++) {
                        accumulate = accumulate + m0[i];
                        startTask("Task " + Integer.valueOf(i).toString() + ":" + Integer.valueOf(accumulate - m0[i]).toString() + "==>" + Integer.valueOf(accumulate - 1).toString());
                    }
                }catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table));
        return p;
    }
    private void startTask(final String str) {
        final int key = model.getRowCount();
        
        SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer>() {
           HttpClient httpclient = new HttpClient();
           private Integer lengthOfTask = m0[key];
            
            @Override
            protected Integer doInBackground() {
                int current = 0;
                try{
                    mrs[key].first();
//                    while (mrs[key].next()) {
//                        System.out.println(Integer.valueOf(key).toString() + ":" + mrs[key].getString("word"));
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                System.out.println("Length of task for " + Integer.valueOf(key).toString() + ":" + Integer.valueOf(lengthOfTask).toString());
                while (current < lengthOfTask) {
                    if (!table.isDisplayable()) {
                       break;
                    }
                    current = current + 1;
                    publish(current);
                    try {
                       String targetword = mrs[key].getString("word");
                          mrs[key].next();
//                         System.out.println("now looping on current value of group " + Integer.valueOf(key).toString() + "->" + Integer.valueOf(current).toString() + ": " + targetword);
                       String url = URIUtil.encodeQuery("http://www.dictionaryapi.com/api/v1/references/collegiate/xml/" + targetword + "?key=" + 
                               "******put your developer key here!");
                       GetMethod method = new GetMethod(url);
                       method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
                           		new DefaultHttpMethodRetryHandler(6, false));
                       try {
                           int statuscode = httpclient.executeMethod(method);
                           if (statuscode != HttpStatus.SC_OK){
                               System.err.println("Method failed: " + targetword);
                           }
                           byte[] responsebody = method.getResponseBody();
                           Document doc = Resources.loadXMLFromString(new String(responsebody));
                          NodeList nlist = doc.getElementsByTagName("entry");
                          if ( nlist.getLength() == 0 ) { 
                               NodeList nlistkk = doc.getElementsByTagName("suggestion");
                               if (nlistkk.getLength() == 0) {
                                       synchronized (pslock) {
                                            dao.update("update voc set docnull =? where word =?");
                                            dao.setBoolean(1, true);
                                            dao.setString(2, targetword);
                                            dao.executeUpdate();
                                       }
                                  continue; //Neither found nor suggested records have false for both dragged and suggested.
                               } else {
                                    for (int i = 0; i < nlistkk.getLength(); i++) {
                                       nlistkk.item(i).getTextContent();
                                       synchronized (pslock) {
                                            dao.update("replace notfound set word = ?, suggestion =?");
                                            dao.setString(1, targetword);
                                            dao.setString(2, nlistkk.item(i).getTextContent());
                                            dao.executeUpdate();
                                       }
                                    }
                                    synchronized (pslock) {
                                            dao.update("update voc set suggested = ?, mw = ?, over10000 = ? where word  =?");
                                            dao.setBoolean(1, true);
                               if (new String(responsebody).length() > Resources.MAX_LENGTH_XML) {
                                   dao.setBoolean(3, true);
                  System.err.println("Too long xml of suggestion" + Integer.valueOf(new String(responsebody).substring(1, Resources.MAX_LENGTH_XML).length()).toString());
                                   dao.setString(2, new String(responsebody).substring(1,Resources.MAX_LENGTH_XML).trim());
                               }else {
                                   dao.setBoolean(3, false);
                                   dao.setString(2, new String(responsebody));
                               }
                                            dao.setString(4, targetword);
                                            dao.executeUpdate();
                                    }
                                   continue;
                               }
                          }
// Of course now are at least one entry found.                          
                          synchronized (pslock) {
                               dao.update("update voc set dragged = ? , mw =?, over10000 = ? where word = ?");
                               dao.setBoolean(1, true);
                               if (new String(responsebody).length() > Resources.MAX_LENGTH_XML) {
                  System.err.println("Too long XML of Entries for " + targetword + ":" +Integer.valueOf(new String(responsebody).substring(1, Resources.MAX_LENGTH_XML).length()).toString());
                                   dao.setString(2, new String(responsebody)); //.substring(1,Resources.MAX_LENGTH_XML).trim());
                                   dao.setBoolean(3, true);
                               }else {
                                   dao.setString(2, new String(responsebody));
                                   dao.setBoolean(3, false);
                               }
                               dao.setString(4, targetword);
                               dao.executeUpdate();
                          }
                          for (int i = 0; i < nlist.getLength(); i++) { //one entry per loop
                              String entryid = nlist.item(i).getAttributes().getNamedItem("id").getNodeValue();
                              NodeList childnodesofentry = nlist.item(i).getChildNodes(); //Node node = nlist.item(i);
                              int ipr = 0, ifl = 0, iodate = 0;
                              String pr = "", fl = "", odate = "";
                              int isn = 0;
                              for (int i1 = 0; i1 < childnodesofentry.getLength(); i1++){
                                  //<sound>:<wpr> could be more popular than <pr>, and sometimes <pr> is under <vr>. so finally decide to go first <wpr> under first <sound>. But <sound> is sometimes under <vr> too.
                                   if (childnodesofentry.item(i1).getNodeName().equals("sound")) {
                                       NodeList childnodesofsound = childnodesofentry.item(i1).getChildNodes();
                                       for (int i3=0; i3 < childnodesofsound.getLength(); i3++){
                                           if (childnodesofsound.item(i3).getNodeName().equals("wpr")) {
                                               ipr = ipr + 1;
                                               if (ipr == 1 ) {pr = childnodesofsound.item(i3).getTextContent();}
////                                               if (ipr > 1 ) {System.err.println("2 or more pronounciation found, wpr=" + Integer.valueOf(ipr));}
                                           }
                                       }
                                   }
                                   if (childnodesofentry.item(i1).getNodeName().equals("vr")) {
                                       NodeList childnodesofvr = childnodesofentry.item(i1).getChildNodes();
                                       for (int i4=0; i4 < childnodesofvr.getLength(); i4++){
                                           if (childnodesofvr.item(i4).getNodeName().equals("sound")) {
                                               NodeList childnodesofsound1 = childnodesofvr.item(i4).getChildNodes();
                                               for (int i5=0; i5 < childnodesofsound1.getLength(); i5++) {
                                                   if (childnodesofsound1.item(i5).getNodeName().equals("wpr")) {
                                                       ipr = ipr + 1;
                                                       if (ipr == 1 ) {pr = childnodesofsound1.item(i5).getTextContent();}
////                                                   if (ipr > 1 ) {System.err.println("2 or more pronounciation found, wpr=" + Integer.valueOf(ipr));}
                                                   }
                                               }
                                           }
                                       }
                                   }
                                   
                                   if (childnodesofentry.item(i1).getNodeName().equals("f1")) {
                                       ifl = ifl + 1;
                                       if (ifl == 1) {fl = childnodesofentry.item(i1).getTextContent();}
                                       if (ifl > 1) { System.err.println("2 or more word type found, ifl=" + Integer.valueOf(ifl));}
                                   }
                                   if (childnodesofentry.item(i1).getNodeName().equals("def")) {
                                       NodeList childnodesofdef = childnodesofentry.item(i1).getChildNodes();
                                       String sn = "";
                                       for (int i2 = 0; i2 < childnodesofdef.getLength(); i2++) {
                                           String meaning = "";
                                           if (childnodesofdef.item(i2).getNodeName().equals("date")) {
                                               iodate = iodate + 1;
                                               if (iodate == 1) {odate = childnodesofdef.item(i2).getTextContent();}
                                               if (iodate > 1) {System.err.println("2 or more date origination found, iodate=" + Integer.valueOf(iodate));}
                                           }
                                           if (childnodesofdef.item(i2).getNodeName().equals("sn")) {
                                               isn = isn + 1;
                                               sn = childnodesofdef.item(i2).getTextContent();
                                           }
                                           if (childnodesofdef.item(i2).getNodeName().equals("dt")) {
                                               meaning = childnodesofdef.item(i2).getTextContent();
//                                               System.err.println(targetword + " length is:" + Integer.valueOf(meaning.length()));
                                               synchronized (pslock) {
                                                   dao.update("replace meaning1 set word = ? , entry = ?, isn = ?,  sn = ?, meaning = ?, ieo = ?");
                                                   dao.setString(1, targetword);
                                                   dao.setString(2, entryid);
                                                   dao.setInt(3, isn);
                                                   dao.setString(4, sn);
                                                   dao.setString(5, meaning);
                                                   dao.setInt(6, i);
                                                   dao.executeUpdate();
                                               }
                                           }
                                       }
                                   }
                               }
                               synchronized (pslock) {
//                                   System.out.println(targetword + Integer.valueOf(i).toString());
                                        dao.update("replace into meaning0 (word, entry, wordtype, voice, odate, ieo) values (?, ?, ?, ?, ?, ?)");
                                        dao.setString(1, targetword);
                                        dao.setString(2, entryid);
                                        dao.setString(3, fl);
                                        dao.setString(4, pr);
                                        dao.setString(5, odate);
                                        dao.setInt(6, i);
                                        dao.executeUpdate();
                               }
//                                   System.out.println(nlist.item(i).getTextContent().toString());
                          }
//                           System.out.println(Resources.loadXMLFromString(new String(responsebody)).toString());
//                           System.out.println("Output the meaning of " + targetword + "==>");
//                           System.out.println(new String(responsebody));
                       } catch (Exception e) {
                           synchronized (pslock) {
                               e.printStackTrace();
                               System.out.println("Suppose httpclient exception " + targetword + ": " + url);
                           }
                       } finally {
                           method.releaseConnection();
                       }
                    } catch (Exception e) {
                       System.out.println("-----------++++++++++");
                       e.printStackTrace();
                       //break;
                    };
                }
                return 300;
            }

            @Override
            protected void process(java.util.List<Integer> c) {
                model.setValueAt(100 * c.get(c.size() - 1) / m0[key], key, 1);
                model.setValueAt(str + ":" + Integer.valueOf(c.get(c.size() - 1)), key, 0);
//                model.setValueAt(c.get(c.size() - 1), key, 0);
            }

            @Override
            protected void done() {
                String text;
                int i = -1;
                if (isCancelled()) {
                    text = "Cancelled";
                } else {
                    try {
                        i = get();
                        text = (i >= 0) ? "Done" : "Disposed";
                    } catch (Exception ignore) {
                        ignore.printStackTrace();
                        text = ignore.getMessage();
                    }
                }
                System.out.println(key + ":" + text + "(" + i + "ms)");
            }
        };
        model.addRow(new Object[]{str, 0});
        worker.execute();
    }

}

class ProgressRenderer extends DefaultTableCellRenderer {

    private final JProgressBar b = new JProgressBar(0, 100);

    public ProgressRenderer() {
        super();
        setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Integer i = (Integer) value;
        String text = "Completed";
        if (i < 0) {
            text = "Error";
        } else if (i < 100) {
            b.setValue(i);
            return b;
        }
        super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
        return this;
    }
}