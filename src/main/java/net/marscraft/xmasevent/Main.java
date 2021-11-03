package net.marscraft.xmasevent;

import net.marscraft.xmasevent.quest.commands.ShowPoint;
import net.marscraft.xmasevent.quest.commands.mcxmas.McXmasCommand;
import net.marscraft.xmasevent.quest.listener.KillMobListener;
import net.marscraft.xmasevent.quest.listener.PlaceBlockListener;
import net.marscraft.xmasevent.quest.listener.PlayerJoinListener;
import net.marscraft.xmasevent.shared.configmanager.Configmanager;
import net.marscraft.xmasevent.shared.configmanager.IConfigmanager;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.logmanager.Logmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import net.marscraft.xmasevent.shared.messagemanager.Messagemanager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private IConfigmanager _mysqlConfig;

    @Override
    public void onEnable() {
        _logger = new Logmanager(this);

        _logger.Info("Loading MySQL Config...");
        loadConfigs();
        _logger.Info("MySQL Config loaded.");

        _logger.Info("Connect to Database...");
        _sql = new DatabaseAccessLayer(_logger, _mysqlConfig);
        _logger.Info("Database Connected");

        _logger.Info("Creating required Databases...");
        createDatabaseTable();
        _logger.Info("All Databases created.");

        _logger.Info("MarsCraft Xmas Event loaded.");

        getServer().getPluginManager().registerEvents(new KillMobListener(_logger, _sql), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(_logger, _sql), this);
        getServer().getPluginManager().registerEvents(new PlaceBlockListener(_logger, _sql), this);
        getCommand("test").setExecutor(new ShowPoint(_logger, _sql));
        getCommand("mcxmas").setExecutor(new McXmasCommand(_logger, _sql));
    }

    @Override
    public void onDisable() {
        _sql.disable();
    }

    private boolean createDatabaseTable() {
        _sql.CreatePlayerQuestProgressTable();
        _sql.CreateQuestsTable();
        _sql.CreateKillMobsTaskTable();
        _sql.CreatePlaceBlockTaskTable();
        return true;
    }

    private void loadConfigs() {
        _mysqlConfig = new Configmanager(this, _logger, "mysql");
    }


}
