package net.marscraft.xmasevent.shared.configmanager;

import org.bukkit.configuration.file.FileConfiguration;

public interface IConfigmanager {
    FileConfiguration GetConfiguration();
    void SaveConfig();
    void ReloadConfig();
}
