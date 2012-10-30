/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishdict;

import java.awt.EventQueue;
import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 *
 * @author rose
 */
public class DicGenerator {
    private DAO dao = new DAO();
    private PreparedStatement ps;
    public static void main(String[] args) throws Exception {
        EventQueue.invokeLater(new Runnable() {
            private DAO dao;
            private ResultSet rs, mrs0, mrs1,vrs0;
            Boolean nextmrs0 = true, nextmrs1 = true, nextvrs0 = true;
            Boolean endmrs0 = false, endmrs1 = false, endvrs0 = false;
                    
            @Override
            public void run() {
                    int n0 = 0, m0 = 0, m1 = 0; 
                    int lastisn = 0;
                    int currieo = 0;
                    int currisn = 0;
                    String lastword = "", lastentry = "", lastsn = "", ltext = "";
                    String currword = "", currentry = "";

                try {
//                    File file = new File("d:\\temp\\test.txt");
                    FileInputStream    fis = new FileInputStream("config.properties");
                    Properties prop = new Properties();
                    prop.load(fis);
                    System.out.println(prop.getProperty("maxltext"));
                    int maxltext = Integer.parseInt(prop.getProperty("maxltext"));

//                    Writer out = new BufferedWriter(new OutputStreamWriter(
//			new FileOutputStream(file), "UTF8"));
                    dao = DAO.getInstance();
//                    dao.query("select count(word) as numberofwords from voc");
//                    rs = dao.executeQuery();
//                    rs.first();
 //                   n0 = rs.getInt(1);
                    
                    dao.query("select word, entry, ieo, voice, odate from meaning0 order by word asc, ieo asc");
                    mrs0 = dao.executeQuery();
                    dao.query("select word, entry, ieo, isn, sn, meaning from meaning1 order by word asc, ieo asc, isn asc");
                    mrs1 = dao.executeQuery();
//                    dao.query("select word from voc where word in (?, ?, ?, ?) order by word asc ");
//                    dao.setString(1, "him");
//                    dao.setString(2, "whom");
//                    dao.setString(3, "helium");
//                    dao.setString(4, "'em");
//                    dao.query("select distinct word  from meaning1 where word not in (select word from dict350)");
//                    dao.query("select word from dict350 where right(ltext, 1) = ?"); 
//                    dao.setString(1, "|");
                    dao.query("select word from voc order by word asc");
                    vrs0 = dao.executeQuery();
                    mrs0.first(); mrs1.first(); vrs0.first();
/*                   for (int i = 0; i < 100; i++){
                        System.out.println(vrs0.getString(1) + "--" + mrs0.getString(1) + "--" + mrs1.getString(1));
                        vrs0.next();
                        mrs0.next();
                        mrs1.next();
                    }*/
int k0 = 0, k1 = 0;    
                    if ( vrs0.first() ) {
                    do { //loop vrs0
//                        out = out.append(vrs0.getString(1) + " ");
                        ltext = "";
                        lastentry = ""; currentry = "";
                        k0 = k0 + 1;
                        currword = vrs0.getString(1);
//                        System.out.println(currword + "," + lastword);
                        if (currword.compareTo(mrs0.getString(1))> 0) {
//                            System.err.println(currword + " ahead " + mrs0.getString(1));
                            endvrs0 = !(mrs0.next()); //Not a mistake! justify the loop should be continued at the level of vrs0 by mrs0 resultset.
                            continue; //in case more code added after this if block
                        } else if (currword.compareTo(mrs0.getString(1)) < 0) {
                            lastword = vrs0.getString(1); //word
//                            System.err.println(currword + " behind " + mrs0.getString(1));
                            endvrs0 = !(vrs0.next());
                            continue; //in case more code added after this if block
//                            if (nextvrs0) { continue; } else {break;}
                        } else {
                            do { //loop mrs0
//                            System.err.println(mrs0.getString(1) + ","+ mrs0.getString(2));
                                currieo = mrs0.getInt(3);
                                if ((currword.compareTo(mrs1.getString(1)) > 0) 
                                  || (currword.equals(mrs1.getString(1)) && (currieo > mrs1.getInt(3))))
                                {
//                            System.out.println("ahead " + currword + "," +currentry + "," + mrs1.getString(1) + ","+ mrs1.getString(2));
                                    endmrs0 = !mrs1.next();
                                    if (endmrs0) { endvrs0 = endvrs0 || true ; }
                                } else if ( (currword.compareTo(mrs1.getString(1)) < 0)
                                   || (currword.equals(mrs1.getString(1)) && (currieo < mrs1.getInt(3)))) {
//                            System.out.println("behind " + currword + "," +currentry + "," + mrs0.getString(1) + ","+ mrs0.getString(2));
                                    endmrs0 = !mrs0.next();
                                    if (endmrs0) { endvrs0 = endvrs0 || true;}
                                    if (currword.compareTo(mrs0.getString(1)) < 0) {
                                        endmrs0 = true;
                                    }
                                } else {
//                            System.out.println("====" + currword + "," +currentry + "," + mrs0.getString(1) + ","+ mrs0.getString(2));
                                    if (ltext.length() <= maxltext) {
                                        ltext = ltext + "<entry>" + mrs0.getString(2) + "</entry>"; //Entry
                                        if (!mrs0.getString(4).isEmpty()) {ltext = ltext + "<wpr>" + mrs0.getString(4) + "</wpr>";} //voice
                                        if (!mrs0.getString(5).isEmpty()) {ltext = ltext + "<date>" + mrs0.getString(5) + "</date>";} //date
//                                        ltext = ltext + "|";//Entry and voice(wpr mark) and date of word
                                    }
                                    do { //loop mrs1
//                                      System.out.println("->" + currword + "," + currentry + " of "+ Integer.valueOf(currieo).toString() +"<>" + 
//                                              mrs1.getString(1) + "," + mrs1.getString(2) + "," +mrs1.getString(5));
                                       if (currword.equals(mrs1.getString(1)) && currieo == mrs1.getInt(3))
//                                      && !(lastword.equals(mrs1.getString(1))) && !(lastentry.equals(mrs1.getString(2)))) 
                                       {
                                          if (!mrs1.getString(6).isEmpty() && ltext.length() <= maxltext) {
                                              if (!mrs1.getString(5).isEmpty()){ltext = ltext + "(" + mrs1.getString(5) + ")"; }//sn
                                              ltext = ltext + mrs1.getString(6); //meaning
                                          }
                                        } 
                                        endmrs1 = !mrs1.next();
                                        if (endmrs1) {endvrs0 = endvrs0 || true; continue;} 
                                        if (!(currword.equals(mrs1.getString(1)) && currieo == mrs1.getInt(3))){
                                            endmrs1 = true;
                                        }
                                     } while ( !endmrs1 && !endvrs0) ;
                                     endmrs0 = !mrs0.next();
                                     if (endmrs0) {endvrs0 = endvrs0 || true;}
                                     if ((!endmrs0) && (!currword.equals(mrs0.getString(1)))) {
                                         endmrs0 = true;
                                     }
                                 }
                            } while (!endmrs0 && !endvrs0);
                            if (ltext.length() > 0) {
                                k1 = k1 + 1;
                                dao.update("replace into dict350 (word, ltext) values( ?, ? )");
                                dao.setString(1, currword);
                                dao.setString(2, ltext);
                                dao.executeUpdate();
//                                System.out.println(currword + "^^^" + ltext);
                            }
                            lastword = vrs0.getString(1); //word
                            endvrs0 = !vrs0.next();
                        }
                    } while (!endvrs0);
                    }
                    System.out.println(Integer.valueOf(k0).toString() + "," + Integer.valueOf(k1).toString());
                } catch (Exception e) {
                    System.out.println(currword);
                    e.printStackTrace();
                } finally {
                    dao.deleteDAO();
                    System.out.println("Finished!");
                }
        }});
    }
}
/* 
 
public class test {
	public static void main(String[] args){
 
	  try {
		File fileDir = new File("c:\\temp\\test.txt");
 
		Writer out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(fileDir), "UTF8"));
 
		out.append("Website UTF-8").append("\r\n");
		out.append("?? UTF-8").append("\r\n");
		out.append("??????? UTF-8").append("\r\n");
 
		out.flush();
		out.close();
 
	    } 
	   catch (UnsupportedEncodingException e) 
	   {
		System.out.println(e.getMessage());
	   } 
	   catch (IOException e) 
	   {
		System.out.println(e.getMessage());
	    }
	   catch (Exception e)
	   {
		System.out.println(e.getMessage());
	   } 
	}	
}
 */