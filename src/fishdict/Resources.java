/*
 * Resources.java
 *
 * Created on 2006年9月4日, 下午4:02
 *
 * RealCix2.0
 * Resources of RealCix2.0
 */

package fishdict;

import java.io.StringReader;
import java.sql.*;
import org.xml.sax.*;
import javax.xml.stream.*;
import javax.xml.parsers.*;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.*;
/**
 *
 * @author JerryChen
 */
public class Resources {
    
	public static String PROPERTYFILE="config.properties";
//jdbc:mysql://localhost:3306/dictionary?useUnicode=yes&characterEncoding=UTF-8
    //DataBase Connection Properties
//    public final static String DRIVER_NAME = "org.hsqldb.jdbcDriver";
//    public final static String DRIVER_NAME = "com.mysql.jdbc.Driver";
    public final static String DRIVER_NAME = "com.mysql.jdbc.Driver";
    public static String IPADDRESS = "localhost";
    public static String PORT = "3306";
    public static String DBNAME = "Dictionary";
	
//    public static String DATABASE_ADDRESS = "jdbc:hsqldb:hsql://" + IPADDRESS + ":" + PORT + "/hsql1";    
    public static String DATABASE_ADDRESS = "jdbc:mysql://localhost:3306/dictionary?useUnicode=yes&characterEncoding=UTF-8";    
    //public final static String DATABASE_ADDRESS = "jdbc:hsqldb:hsql://localhost:9002/hsql1";    
    
    public static String LANGUAGE = "ZH";
    public static String PASSWORDE_TXT = "******";
    public static String DEFAULT_DATE_FORMAT = "yyyyMM";
    public static String DEFAULT_DATE_FORMAT1 = "yyyy-M-dd";
    public static int MAX_LENGTH_XML = 16384;
    
    //SQL used by realcix20.classes
    public final static String SELECT_CLS_SQL = "SELECT * FROM CLS WHERE CLSID=?";
    public final static String SELECT_TABLENAME_FROM_CLS_TABLES_SQL_ = "SELECT * FROM CLSTABLES WHERE CLSID=? AND PARENTTABLE IS NOT NULL";
    public final static String SELECT_CLS_TABLES_FIELDS_SQL = "SELECT * FROM CLSTABLESFIELDS WHERE CLSID=? ORDER BY GROUPORDINAL";
    public final static String SELECT_CLS_TABLES_FIELDS_A_SQL = "SELECT * FROM CLSTABLESFIELDS WHERE CLSID=? AND TABLENAME=? AND COLUMNNAME=?";
    public final static String SELECT_CLS_FIELDS_TXT_SQL = "SELECT * FROM CLSFIELDSTXT WHERE CLSID=? AND TABLENAME=? AND COLUMNNAME=? AND LANG=?";
    public final static String SELECT_CLS_TABLES_SQL = "SELECT * FROM CLSTABLES WHERE CLSID=? ORDER BY UPDATEPROFILE";
    public final static String SELECT_CLS_TABLES_DESC_SQL = "SELECT * FROM CLSTABLES WHERE CLSID=? ORDER BY UPDATEPROFILE DESC";
    
    //SQL used by other
    public final static String INSERT_TXT_SQL = "INSERT INTO TXT (TXTID,LANG,LONG) VALUES(?,?,?)";
    public final static String UPDATE_TXT_SQL = "UPDATE TXT SET LONG=? WHERE TXTID=? AND LANG=?";
    public final static String SELECT_ACTIVE_LANGUAGE_SQL = "SELECT * FROM LANG WHERE ACTIVE=true";
    public final static String UPDATE_GLOBALVAR_TABLE_SQL = "UPDATE GV SET VVALUE=? WHERE VID=?";
    public final static String SELECT_TXT_TABLE_SQL = "SELECT * FROM TXT WHERE TXTID=? AND LANG=?";
    public final static String SELECT_ALL_CLS_SQL = "SELECT * FROM CLS";
    public final static String SELECT_PARENT_TABLE_FROM_CLS_TABLES = "SELECT * FROM CLSTABLES WHERE CLSID=? AND PARENTTABLE IS NULL";
    public final static String SELECT_CHILD_TABLE_FROM_CLS_TABLES = "SELECT * FROM CLSTABLES WHERE CLSID=? AND PARENTTABLE IS NOT NULL";
    public final static String SELECT_CLS_ALL_LAYOUTS_SQL = "SELECT * FROM CL WHERE CLSID=? ORDER BY LAYOUT";
    public final static String SELECT_CL_SQL = "SELECT * FROM CL WHERE CLSID=? AND LAYOUT=?";
    public final static String DELETE_LAYOUT_BY_CLSID_AND_LAYOUT_1 = "DELETE FROM CL WHERE CLSID=? AND LAYOUT=?";
    public final static String DELETE_LAYOUT_BY_CLSID_AND_LAYOUT_2 = "DELETE FROM CLFIELDS WHERE CLSID=? AND LAYOUT=?";
    public final static String SELECT_CLFIELDS_BY_CLSID_AND_LAYOUT_FOR_DISPLAY = "SELECT * FROM CLFIELDS WHERE CLSID=? AND LAYOUT=? AND DISORDINAL != -1 ORDER BY DISORDINAL";
    public final static String SELECT_CLFIELDS_BY_CLSID_AND_LAYOUT_ORDERBY_DISORDINAL = "SELECT * FROM CLFIELDS WHERE CLSID=? AND LAYOUT=? ORDER BY DISORDINAL";
    public final static String SELECT_CLFIELDS_BY_CLSID_AND_LAYOUT_ORDERBY_ORDERBYORDINAL_FOR_ORDERBY = "SELECT * FROM CLFIELDS WHERE CLSID=? AND LAYOUT=? AND ((ISSUMMARY = FALSE OR ISSUMMARY IS NULL) AND (ISCOUNT = FALSE OR ISCOUNT IS NULL)) AND (ORDERBYORDINAL IS NOT NULL) ORDER BY ORDERBYORDINAL";
    public final static String SELECT_CLFIELDS_BY_CLSID_AND_LAYOUT_ORDERBY_ORDERBYORDINAL = "SELECT * FROM CLFIELDS WHERE CLSID=? AND LAYOUT=? AND ((ISSUMMARY = FALSE OR ISSUMMARY IS NULL) AND (ISCOUNT = FALSE OR ISCOUNT IS NULL)) AND (ORDERBYORDINAL IS NULL)  ORDER BY ORDERBYORDINAL";
    public final static String SELECT_CLFIELDS_BY_CLSID_AND_LAYOUT_AND_TABLENAME_AND_COLUMNNAME = "SELECT * FROM CLFIELDS WHERE CLSID=? AND LAYOUT=? AND TABLENAME=? AND COLUMNNAME=?";
    public final static String UPDATE_CL_SQL = "UPDATE CL SET SQL=? WHERE CLSID=? AND LAYOUT=?";
    public final static String UPDATE_CL_ISFAVORITE_SQL = "UPDATE CL SET ISFAVORITE=? WHERE CLSID=? AND LAYOUT=?";
    public final static String INSERT_CL_SQL = "INSERT INTO CL (CLSID,LAYOUT,SQL,ISFAVORITE) VALUES(?,?,?,?)";
    public final static String DELETE_CLFIELDS_SQL = "DELETE FROM CLFIELDS WHERE CLSID=? AND LAYOUT=?";
    public final static String INSERT_CLFIELDS_SQL = "INSERT INTO CLFIELDS (CLSID,LAYOUT,TABLENAME,COLUMNNAME,DISORDINAL,ISSUMMARY,ISCOUNT,FILTERCLAUSE,ORDERBYORDINAL,ORDERBYASCDES) VALUES (?,?,?,?,?,?,?,?,?,?)";    
    public final static String SELECT_MAX_LAYOUT_SQL = "SELECT MAX(LAYOUT) FROM CLFIELDS WHERE CLSID=?";
    
    private static DAO dao;

    public static String getLanguage() {
        
        return LANGUAGE;
        
    }
    
    public static void setLanguage(String language) {
        setGlobalVar("deflang", language);
        LANGUAGE = language;
    }
    
    public static void setGlobalVar(String varId, String varValue) {
        
        dao = DAO.getInstance();
        dao.update(UPDATE_GLOBALVAR_TABLE_SQL);
        dao.setString(1, varValue);
        dao.setString(2, varId);
        dao.executeUpdate();
    }

    public static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
}