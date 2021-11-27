package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.EventListener;

public class PlayerJoinListener implements Listener {

    private final DatabaseAccessLayer _sql;
    private final ILogmanager _logger;

    public PlayerJoinListener(ILogmanager logger, DatabaseAccessLayer sql) {
        _sql = sql;
        _logger = logger;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        _sql.AddNewPlayerToDatabase(event.getPlayer());
        _logger.Info("Player Added: " + event.getPlayer().getName());
    }

}
