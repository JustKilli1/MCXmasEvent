package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.shared.Inventorys.IInventoryType;
import net.marscraft.xmasevent.shared.Inventorys.InvAdminSetRewards;
import net.marscraft.xmasevent.shared.Inventorys.InvUnclaimedReward;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import net.marscraft.xmasevent.shared.messagemanager.Messagemanager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
        /*
        * Inventory Close Handler for Inventorys:
        * Quest Belohnungen(User):
        *   command: /quests rewards
        * Quest Rewards(Admin):
        *   command: /mcxmas edit [questId] rewards
        * */

        EventStorage eventStorage = new EventStorage();
        eventStorage.SetInventoryCloseEvent(event);
        String inventoryTitle = event.getView().getTitle();
        IInventoryType inventoryType;
        if(inventoryTitle.equalsIgnoreCase("Quest Belohnungen"))
            inventoryType = new InvUnclaimedReward(_logger, _sql);
        else if(inventoryTitle.contains("Quest Rewards"))
            inventoryType = new InvAdminSetRewards(_logger, _sql);
        else return;

        inventoryType.CloseInventory(eventStorage);

        /*Player player = (Player) event.getPlayer();
        IMessagemanager messagemanager = new Messagemanager(_logger, player);
        Inventory inv = event.getInventory();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), "rewardId");

        if (event.getView().getTitle().equalsIgnoreCase("§0Quest Belohnungen")) {
            ItemStack[] invContents = inv.getContents();
            String unclaimedRewardIds = "";

            for (int i = 0; i < invContents.length; i++) {
                if (invContents[i] == null) continue;
                ItemStack itemStack = invContents[i];
                ItemMeta itemMeta = itemStack.getItemMeta();
                int rewardId = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
                if (unclaimedRewardIds.length() == 0) unclaimedRewardIds = "" + rewardId;
                else unclaimedRewardIds += "," + rewardId;
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
        } else if (event.getView().getTitle().contains("Quest Rewards")){
            ItemStackSerializer serializer = new ItemStackSerializer(_logger);
            int questId = Integer.parseInt(event.getView().getTitle().split(" ")[0]);
            ItemStack[] invItems = inv.getContents();
            ArrayList<Integer> rewardIds = _sql.GetRewardIds(questId);

            for(int i = 0; i < invItems.length; i++) {
                ItemStack invItem = invItems[i];
                if(invItem == null) continue;
                if(invItem.getType() == Material.AIR) continue;
                ItemMeta iMeta = invItem.getItemMeta();
                PersistentDataContainer dataContainer = iMeta.getPersistentDataContainer();
                String rewardStr = serializer.ItemStackToBase64(invItem);
                if(dataContainer.has(key, PersistentDataType.INTEGER)) {
                    int rewardId = dataContainer.get(key, PersistentDataType.INTEGER);
                    if(rewardIds.contains(rewardId)) {
                        _sql.SetReward(rewardId, rewardStr);
                        rewardIds.remove((Integer) rewardId);
                        dataContainer.remove(key);
                        iMeta.getPersistentDataContainer().remove(key);
                        invItem.setItemMeta(iMeta);
                        inv.setItem(i, invItem);
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
        ItemStack[] playerInv = player.getInventory().getContents();
        for (int i = 0; i < playerInv.length; i++) {
            ItemStack iStack = playerInv[i];
            if(iStack == null) continue;
            ItemMeta iMeta = iStack.getItemMeta();
            if(iMeta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
                iMeta.getPersistentDataContainer().remove(key);
                iStack.setItemMeta(iMeta);
                player.getInventory().setItem(i, iStack);
            }
        }*/
    }
}
