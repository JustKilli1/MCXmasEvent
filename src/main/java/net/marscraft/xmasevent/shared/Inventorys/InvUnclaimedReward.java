package net.marscraft.xmasevent.shared.Inventorys;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.listener.EventStorage;
import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InvUnclaimedReward implements IInventoryType{

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;

    public InvUnclaimedReward(ILogmanager logger, DatabaseAccessLayer sql) {
        _logger = logger;
        _sql = sql;
    }

    @Override
    public Inventory CreateInventory(Player player, int questId) {

        ArrayList<Integer> rewardIds = _sql.GetUnclaimedPlayerRewardIds(player);
        if(rewardIds == null) return null;
        int inventoryRows = (rewardIds.size() / 9) + 1;
        Inventory inv = Bukkit.createInventory(null, inventoryRows * 9, "Quest Belohnungen");

        for(int i = 0; i < rewardIds.size(); i++) {
            ResultSet rs = _sql.GetQuestRewardByRewardId(rewardIds.get(i));
            try {
                if(!rs.next()) return null;
                String rewardStr = rs.getString("Reward");
                ItemStackSerializer serializer = new ItemStackSerializer(_logger);
                ItemStack reward = serializer.ItemStackFromBase64(rewardStr);
                ItemMeta rewardMeta = reward.getItemMeta();
                NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), "rewardId");
                rewardMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, rewardIds.get(i));
                reward.setItemMeta(rewardMeta);
                inv.setItem(i, reward);
            } catch (Exception ex) {
                _logger.Error(ex);
                return null;
            }
        }
        return inv;
    }

    @Override
    public boolean OpenInventory(Player player, int questId) {
        Inventory inv = CreateInventory(player, questId);
        if(inv == null) return false;
        player.openInventory(inv);
        return true;
    }

    @Override
    public boolean CloseInventory(EventStorage eventStorage) {
        /*
        * UnclaimedRewards String aus db Holen und in List<Integer> rewardIds splitten
        * Event inv holen
        * durch event inv gehen und checken ob nen key hat wenn ja get key, remove key aus list rewardIds, remove key from itemStack,
        * set event inventory ItemStack
        * durch PlayerInventory gehen und persistant data container nach key durchsuchen wenn key hat remove key und set item in player inventory
        * */
        InventoryCloseEvent event = eventStorage.GetInventoryCloseEvent();
        Player player = (Player) event.getPlayer();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), "rewardId");
        List<Integer> unclaimedRewardIds = _sql.GetUnclaimedPlayerRewardIds(player);
        Inventory eventInv = event.getInventory();
        ItemStack[] eventInvContents = eventInv.getContents();
        String rewardStr = "";

        for(int i = 0; i < eventInvContents.length; i++) {
            ItemStack iStack = eventInvContents[i];
            if(iStack == null) continue;
            ItemMeta iMeta = iStack.getItemMeta();
            if(iMeta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)){
                int rewardId = iMeta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
                if(!(unclaimedRewardIds.contains(rewardId))){
                    _logger.Error("UnclaimedReward CloseInventory Error: Item with rewardId " + rewardId + " does not exist in Database");
                    return false;
                }
                if(rewardStr.length() == 0)
                    rewardStr += "" + rewardId;
                else
                    rewardStr += "," + rewardId;

                unclaimedRewardIds.remove(unclaimedRewardIds.indexOf(rewardId));
            } else {
                _logger.Error("UnclaimedReward CloseInventory Error: Item has no Persistant Data matching key rewardId");
                return false;
            }
        }
        if(!_sql.SetUnclaimedPlayerRewards(player, rewardStr)) return false;
        Inventory playerInv = player.getInventory();
        ItemStack[] playerIncContents = playerInv.getContents();

        for(int i = 0; i < playerIncContents.length; i++) {
            ItemStack iStack = playerIncContents[i];
            if(iStack == null) continue;
            ItemMeta iMeta = iStack.getItemMeta();
            if(iMeta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
                iMeta.getPersistentDataContainer().remove(key);
                iStack.setItemMeta(iMeta);
                player.getInventory().setItem(i, iStack);
            }
        }
        return false;
    }
}
