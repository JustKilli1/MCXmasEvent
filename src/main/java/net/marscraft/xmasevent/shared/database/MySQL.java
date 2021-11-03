package net.marscraft.xmasevent.shared.database;

import net.marscraft.xmasevent.shared.configmanager.IConfigmanager;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
    private ILogmanager _logger;
    private IConfigmanager cm;
    private FileConfiguration config;
    private String host = "";
    private String port = "";
    private String database = "";
    private String username = "";
    private String password = "";
    private Connection con;

    public MySQL(ILogmanager logger, IConfigmanager configManager) {
        _logger = logger;
        cm = configManager;
        config = cm.GetConfiguration();
        config.addDefault("sql.host", "SQL.IP");
        config.addDefault("sql.port", "3306");
        config.addDefault("sql.username", "USERNAME");
        config.addDefault("sql.password", "PASSWORD");
        config.addDefault("sql.database", "DATABASE");
        config.options().copyDefaults(true);
        cm.SaveConfig();
    }

    private void reloadDBSettings() {
        cm.ReloadConfig();
        host = config.getString("sql.host");
        port = config.getString("sql.port");
        database = config.getString("sql.database");
        username = config.getString("sql.username");
        //password = config.getString("sql.password");
    }

    // connect
    public void connect() {
        if (!IsConnected()) {
            reloadDBSettings();
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username,
                        password);
            } catch (SQLException e) {
                _logger.Error(e);
            }
        }
    }

    // disconnect
    public void Disconnect() {
        if (IsConnected()) {
            try {
                con.close();
            } catch (SQLException e) {
                _logger.Error(e);
            }
        }
    }

    // isConnected
    public boolean IsConnected() {
        return (con == null ? false : true);
    }

    // getConnection
    public Connection GetConnection() {
        return con;
    }
}
