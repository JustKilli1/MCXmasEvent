package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.rewards.Rewardmanager;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class InventoryCloseListener implements Listener {
    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;
    private IMessagemanager _messageManager;

    public InventoryCloseListener(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        if (!(event.getView().getTitle().equalsIgnoreCase("§0Quest Belohnungen"))) return;
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();
        String unclaimedRewardIds = "";
        for (int i = 0; i < inv.getContents().length; i++) {
            if(inv.getContents()[i] != null) {
                if (inv.getContents()[i].getItemMeta().hasLocalizedName()) {
                    if (unclaimedRewardIds.length() == 0) {
                        unclaimedRewardIds = "" + inv.getContents()[i].getItemMeta().getLocalizedName();
                    } else {
                        unclaimedRewardIds += "," + inv.getContents()[i].getItemMeta().getLocalizedName();
                    }
                }
            }
        }
        if(unclaimedRewardIds.length() == 0 ){
            _sql.DeleteUnclaimedPlayerReward(player);
            return;
        }
        if (!_sql.SetUnclaimedPlayerRewards(player, unclaimedRewardIds)) {
            _logger.Error("Unclaimed Player Rewards Table could not be Update");
            _logger.Error("PlayerName: " + player.getName());
            _logger.Error("PlayerUUID: " + player.getUniqueId());
            return;
        }
    }
}
