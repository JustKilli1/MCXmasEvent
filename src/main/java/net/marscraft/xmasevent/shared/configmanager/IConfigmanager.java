package net.marscraft.xmasevent.shared.configmanager;

import org.bukkit.configuration.file.FileConfiguration;

public interface IConfigmanager {

    public FileConfiguration GetConfiguration();

    public void SaveConfig();

    public void ReloadConfig();
}
