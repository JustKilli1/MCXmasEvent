package net.marscraft.xmasevent;

import net.marscraft.xmasevent.quest.commands.consolecommands.SetQuestCommand;
import net.marscraft.xmasevent.quest.commands.mcxmas.McXmasCommand;
import net.marscraft.xmasevent.quest.commands.usercommands.QuestsCommand;
import net.marscraft.xmasevent.quest.listener.*;
import net.marscraft.xmasevent.shared.configmanager.Configmanager;
import net.marscraft.xmasevent.shared.configmanager.IConfigmanager;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.logmanager.Logmanager;
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
        createDatabaseTables();
        _logger.Info("All Databases created.");
        registerListener();
        registerCommands();
        _logger.Info("MarsCraft Xmas Event loaded.");
    }

    @Override
    public void onDisable() {
        _sql.disable();
    }

    private boolean createDatabaseTables() {
        _sql.CreatePlayerQuestProgressTable();
        _sql.CreateQuestsTable();
        _sql.CreateKillMobsTaskTable();
        _sql.CreatePlaceBlockTaskTable();
        _sql.CreateRewardsTable();
        _sql.CreateUnclaimedRewardsTable();
        return true;
    }
    private boolean registerListener() {
        getServer().getPluginManager().registerEvents(new KillMobListener(_logger, _sql, this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(_logger, _sql), this);
        getServer().getPluginManager().registerEvents(new PlaceBlockListener(_logger, _sql, this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(_logger, _sql, this), this);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(_logger, _sql, this), this);
        return true;
    }
    private boolean registerCommands() {
        getCommand("mcxmas").setExecutor(new McXmasCommand(_logger, _sql, this));
        getCommand("quests").setExecutor(new QuestsCommand(_logger, _sql, this));
        getCommand("setquest").setExecutor(new SetQuestCommand(_logger, _sql, this));
        return true;
    }

    private void loadConfigs() {
        _mysqlConfig = new Configmanager(this, _logger, "mysql");
    }


}
