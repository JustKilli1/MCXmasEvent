package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private DatabaseAccessLayer _sql;
    private ILogmanager _logger;

    public PlayerJoinListener(ILogmanager logger, DatabaseAccessLayer sql) {
        _sql = sql;
        _logger = logger;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(!event.getPlayer().hasPermission("mcxmasevent.user.participate") || event.getPlayer().isOp()) {
            if (!_sql.AddNewPlayerToDatabase(event.getPlayer())) return;
            _logger.Info("Player Added: " + event.getPlayer().getName());
        }
    }
}
