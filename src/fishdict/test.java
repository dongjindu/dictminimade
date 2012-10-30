/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishdict;

/**
 *
 * @author rose
 *//*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.sql.*;


public class test {
    public static void main(String[] args) throws Exception {
            if (args.length == 3) {
                Resources.IPADDRESS = args[0];
                Resources.PORT = args[1];
		Resources.DBNAME = args[2];
                Resources.DATABASE_ADDRESS = Resources.DRIVER_NAME + "://" +Resources.IPADDRESS + ":" + Resources.PORT + "/" + Resources.DBNAME 
                    + "?useUnicode=yes&characterEncoding=UTF-8";
	    }
        EventQueue.invokeLater(new Runnable() {
            private DAO dao;
            private ResultSet rs;

            @Override
            public void run() {
                try {
                    dao = DAO.getInstance();
                    dao.query("select word, entry, voice from meaning0");
                    rs = dao.executeQuery();
                    while (rs.next()){
                        System.out.println(rs.getString(1) + "==>" + rs.getString(2) + "==>" + rs.getString(3));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                createAndShowGUI();
            }});
    }
        public static void createAndShowGUI() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.getContentPane().add(new TableProgressBar().makeUI());
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        }
}