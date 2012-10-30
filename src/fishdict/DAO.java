/*
 * DAO.java
 *
 * Created on 2006年9月4日, 下午4:01
 *
 * RealCix2.0
 * Access DataBase,Singleton
 */

package fishdict;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
import java.util.Properties;
import java.util.InputStream;
 *
 * @author JerryChen
 */
public class DAO {
    
    private static DAO instance;
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    private String dbuser;
    private String dbpass;
    
    public DAO() {
        
        createConnection();
        
    }
    
    public void deleteDAO() {
        
        closeConnection();
        instance = null;
        
    }
    
    //set parameter methods
    public synchronized void setObject(int parameterIndex, Object x) {
        
        try {
            getPstmt().setObject(parameterIndex, x);
        }
        catch (SQLException sqle) {
            System.err.println(" pstmt.setObject(parameterIndex, x) Fail! Error = " + sqle.toString());
        }
        
    }
    
    public synchronized void setBoolean(int parameterIndex, boolean flag) {
        
        try {
            getPstmt().setBoolean(parameterIndex, flag);
        }
        catch (SQLException sqle) {
            System.err.println(" pstmt.setBoolean(parameterIndex, flag) Fail! Error = " + sqle.toString());
        }
        
    }
    
    public synchronized void setDouble(int parameterIndex, double x) {
        
        try {
            getPstmt().setDouble(parameterIndex, x);
        }
        catch (SQLException sqle) {
            System.err.println(" pstmt.setDouble(parameterIndex, x) Fail! Error = " + sqle.toString());
        }
        
    }
    
    public synchronized void setInt(int parameterIndex, int x) {
        
        try {
            getPstmt().setInt(parameterIndex, x);
        }
        catch (SQLException sqle) {
            System.err.println(" pstmt.setInt(parameterIndex, x) Fail! Error = " + sqle.toString());
        }
        
    }
    
    public synchronized void setString(int parameterIndex, String s) {
        
        try {
            getPstmt().setString(parameterIndex, s);
        }
        catch (SQLException sqle) {
            System.err.println(" pstmt.setString(parameterIndex, s) Fail! Error = " + sqle.toString() + " SQL state?" + getPstmt().toString());
        }
        
    }
    
    public synchronized boolean executeUpdate() {
        
        boolean flag = true;
        try {
            getPstmt().executeUpdate();
//            System.err.println("Update informaiton:" + getPstmt().toString());
        }
        catch (SQLException sqle) {
            flag = false;
            System.out.println("executeUpdate() Error!" + sqle.toString() + getPstmt().toString());
        }
        
        return flag;
        
    }
    
    public synchronized void update(String expression) {
        pstmt = null;
        try {
            pstmt = conn.prepareStatement(expression);
        } 
        catch (SQLException sqle) {
            System.err.println("Update DataBase Preparation Fail! Error = " + sqle.toString());
        }
    }
    
    public synchronized ResultSet executeQuery() {
        
        rs = null;
        try {
            rs = getPstmt().executeQuery();
        }
        catch (SQLException sqle) {
            System.err.println("Query DataBase Fail! Error = " + sqle.toString());
        }
        
        return rs;
        
    }
        
    public synchronized void query(String expression) {
        pstmt = null;
        try {
            pstmt = conn.prepareStatement(expression);
        } 
        catch (SQLException sqle) {
            System.err.println("Query DataBase Fail! Error = " + sqle.toString() + " " + expression);
        }
    }
    
    public void closeConnection() {
        
        try {
            conn.close();
            conn = null;
            System.out.println("Close Connect DataBase Success!");
        }
        catch (SQLException e) {
            System.err.println("Close Connect DataBase Fail! Error = " + e.toString());
        }
        
    }
    
    public void commit() {
        
         try {
            conn.commit();
        }
        catch (SQLException sqle) {
            System.err.println(sqle.toString());
        }
        
    }
    
    public void rollback() {
        
        try {
            conn.rollback();
        }
        catch (SQLException sqle) {
            System.err.println(sqle.toString());
        }
        
    }
    
    public void setAutoCommit(boolean flag) {
        try {
            conn.setAutoCommit(flag);
        }
        catch (SQLException sqle) {
            System.err.println(sqle.toString());
        }
        
    }
    
    public void createConnection(){
        FileInputStream fis = null;
         try {
            Class.forName(Resources.DRIVER_NAME);
/*            System.err.println("Resources.DATABASE_ADDRESS = " + Resources.DATABASE_ADDRESS);
            System.err.println("Resources.USER_NAME = " + Resources.USER_NAME);
            System.err.println("Resources.USER_PASSWORD = " + Resources.USER_PASSWORD);*/
	    fis = new FileInputStream("config.properties");
            Properties prop = new Properties();
            prop.load(fis);

            conn = DriverManager.getConnection(Resources.DATABASE_ADDRESS, prop.getProperty("dbusername"),prop.getProperty("dbpassword"));
            
            System.out.println("Connect DataBase Success!" + Resources.DATABASE_ADDRESS);
        } 
        catch (Exception e)
        {
            System.err.println("Connect DataBase Fail! Error = " + e.toString());
        }finally {
            try {
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
         } 
    }
    
    public static DAO getInstance(){

        
        if (instance != null) {
            return instance;
        } else {
            instance = new DAO();
            return instance;
        }
        
    }

    private PreparedStatement getPstmt() {
        return pstmt; 
    }
    
}
