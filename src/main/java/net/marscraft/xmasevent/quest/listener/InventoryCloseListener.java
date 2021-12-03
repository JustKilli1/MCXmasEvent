package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import net.marscraft.xmasevent.shared.messagemanager.Messagemanager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;

public class InventoryCloseListener implements Listener {
    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;

    public InventoryCloseListener(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        IMessagemanager messagemanager = new Messagemanager(_logger, player);
        Inventory inv = event.getInventory();
        if (event.getView().getTitle().equalsIgnoreCase("§0Quest Belohnungen")) {
            ItemStack[] playerInventoryContents = inv.getContents();
            String unclaimedRewardIds = "";
            for (int i = 0; i < playerInventoryContents.length; i++) {
                if (playerInventoryContents[i] == null) continue;
                if (!(playerInventoryContents[i].getItemMeta().hasLocalizedName())) continue;
                if (unclaimedRewardIds.length() == 0) {
                    unclaimedRewardIds = "" + playerInventoryContents[i].getItemMeta().getLocalizedName();
                } else {
                    unclaimedRewardIds += "," + playerInventoryContents[i].getItemMeta().getLocalizedName();
                }
            }
            if (unclaimedRewardIds.length() == 0) {
                _sql.DeleteUnclaimedPlayerReward(player);
                return;
            }
            if (!_sql.SetUnclaimedPlayerRewards(player, unclaimedRewardIds)) {
                _logger.Error("Unclaimed Player Rewards Table could not be Update");
                _logger.Error("PlayerName: " + player.getName());
                _logger.Error("PlayerUUID: " + player.getUniqueId());
                return;
            }
        } else if (event.getView().getTitle().equalsIgnoreCase("Quest Rewards")){
            ItemStackSerializer serializer = new ItemStackSerializer(_logger);
            int questId = Integer.parseInt(event.getView().getTitle().split(" ")[0]);
            ItemStack[] invItems = inv.getContents();
            ArrayList<Integer> rewardIds = _sql.GetRewardIds(questId);

            for(int i = 0; i < invItems.length; i++) {
                ItemStack invItem = invItems[i];
                if(invItem == null) continue;
                if(invItem.getType() == Material.AIR) continue;
                String rewardStr = serializer.ItemStackToBase64(invItem);
                if(invItem.getItemMeta().hasLocalizedName()) {
                    int rewardId = Integer.parseInt(invItem.getItemMeta().getLocalizedName());
                    if(rewardIds.contains(rewardId)) {
                        _sql.SetReward(rewardId, rewardStr);
                        rewardIds.remove((Integer) rewardId);
                    } else {
                        _sql.AddNewReward("RewardItems", rewardStr, questId);
                    }
                } else {
                    _sql.AddNewReward("RewardItems", rewardStr, questId);
                }
            }
            if(rewardIds.size() != 0) {
                for(int rewardId : rewardIds) _sql.DeleteReward(rewardId);
            }
            messagemanager.SendPlayerMessage("Rewards gesetzt");
        } else {
            return;
        }
    }
}
