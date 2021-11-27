package net.marscraft.xmasevent.shared.configmanager;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Configmanager implements IConfigmanager{
    private final JavaPlugin _plugin;
    private final String _configName;
    private final ILogmanager _logManager;

    private FileConfiguration _fileConfiguration = null;
    private final File _file;

    public Configmanager(Main plugin, ILogmanager logManager, String configName){
        _plugin = plugin;
        _configName = configName;
        _logManager = logManager;

        _file = new File(_plugin.getDataFolder() + "/", _configName + ".yml");
        if (!_plugin.getDataFolder().exists()) {
            _logManager.Debug("Config Folder is missing, create Folder...");
            _plugin.getDataFolder().mkdir();
        }

        if(!_file.exists()) {
            try {
                _logManager.Debug("Module Config is missing... Creating Folder and File...");
                _file.getParentFile().mkdirs();
                _file.createNewFile();
            } catch (IOException e) {
                _logManager.Error("Error while creating File and Folder", e);
            }
        }
        ReloadConfig();
    }

    @Override
    public FileConfiguration GetConfiguration() {
        return _fileConfiguration;
    }

    @Override
    public void SaveConfig() {
        try {
            _logManager.Info("Saving Configuration...");
            _fileConfiguration.save(_file);
        } catch (IOException e) {
            _logManager.Error("Error while Saving Configurationfile", e);
        }
    }

    @Override
    public void ReloadConfig() {
        _logManager.Info("Loading Configuration...");
        _fileConfiguration = YamlConfiguration.loadConfiguration(_file);
    }
}
