package net.marscraft.xmasevent.shared.Inventorys;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.listener.EventStorage;
import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InvUnclaimedReward extends Inventorymanager implements IInventoryType{

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;

    public InvUnclaimedReward(ILogmanager logger, DatabaseAccessLayer sql) {
        super(logger);
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
    public boolean InventoryClickItem(EventStorage eventStorage) {
        InventoryClickEvent event = eventStorage.GetInventoryClickEvent();
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), "rewardId");
        Inventory eventInv = event.getInventory();
        ItemStack eventItem = event.getCurrentItem();
        if(eventItem == null) return false;
        List<Integer> rewardIds = _sql.GetUnclaimedPlayerRewardIds(player);
        if(eventItem.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            if(!EnoughSpaceInInventory(1, player)) return false;
            eventInv.remove(eventItem);
            int rewardId = eventItem.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
            rewardIds.remove(rewardIds.indexOf(rewardId));
            ItemStack removedReward = RemoveDataFromItemStack(eventItem, key);
            if(rewardIds.size() == 0) {
                player.getInventory().addItem(removedReward);
                return _sql.DeleteUnclaimedPlayerReward(player);
            }
            String rewardStr = null;
            for(int reward : rewardIds) {
                if(rewardStr == null)
                    rewardStr = "" + reward;
                else
                    rewardStr += "," + reward;
            }
            if(!_sql.SetUnclaimedPlayerRewards(player, rewardStr)) return false;

            player.getInventory().addItem(removedReward);
        } else {
            _logger.Error("UnclaimedReward CloseInventory Error: Item has no Persistant Data matching key rewardId");
            return false;
        }
        return true;
    }
}
