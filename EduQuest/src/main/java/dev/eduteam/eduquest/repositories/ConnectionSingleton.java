package dev.eduteam.eduquest.repositories;

import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.cj.jdbc.MysqlDataSource;

public class ConnectionSingleton {
    // Temporaneo
    private static ConnectionSingleton instance;

    public static ConnectionSingleton getInstance() {
        if (instance == null) {
            instance = new ConnectionSingleton();
        }
        return instance;
    }

    private ConnectionSingleton() {
    }

    public Connection getConnection() throws SQLException {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("localhost");

        // -------------------------
        // Parametri modificabili in base al proprio MySQL
        ds.setPortNumber(3306);
        ds.setUser("root");
        ds.setPassword("toor");
        // -------------------------

        ds.setDatabaseName("DB_EduQuest");
        ds.setUseSSL(false);
        ds.setAllowPublicKeyRetrieval(true);

        return ds.getConnection();
    }

}
