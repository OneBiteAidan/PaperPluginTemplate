package dev.onebiteaidan.paperplugintemplate.DataManagement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Database {

    void connect() throws SQLException;
    boolean isConnected();
    void disconnect();
    Connection getConnection();
    ResultSet query(String query, Object[] arguments, int[] types);
    void update(String update, Object[] arguments, int[] types);
    void run (String command);
    void createExampleTable();

}

