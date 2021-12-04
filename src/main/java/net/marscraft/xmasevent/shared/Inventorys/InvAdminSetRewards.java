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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InvAdminSetRewards extends Inventorymanager implements IInventoryType{

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private String _inventoryName = "Quest Rewards";

    public InvAdminSetRewards(ILogmanager logger, DatabaseAccessLayer sql) {
        super(logger);
        _logger = logger;
        _sql = sql;
    }

    @Override
    public Inventory CreateInventory(Player player, int questId) {
        // /mcxmas edit [questID] Rewards
        ItemStackSerializer serializer = new ItemStackSerializer(_logger);
        ResultSet rewards = _sql.GetQuestReward(questId);
        ArrayList<ItemStack> rewardItems = new ArrayList<>();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), "rewardId");
        try {
            while(rewards.next()) {
                int rewardId = rewards.getInt("RewardId");
                String rewardStr = rewards.getString("Reward");
                String rewardName = rewards.getString("RewardName");
                if(rewardName.equalsIgnoreCase("RewardItems")) {
                    ItemStack reward = serializer.ItemStackFromBase64(rewardStr);
                    ItemMeta rewardMeta = reward.getItemMeta();
                    rewardMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, rewardId);
                    reward.setItemMeta(rewardMeta);
                    rewardItems.add(reward);
                }
            }
            int inventoryRows = (rewardItems.size()/9) + 1;
            Inventory inv = Bukkit.createInventory(null, inventoryRows * 9, questId + " Quest Rewards");
            for(int i = 0; i < rewardItems.size(); i++) inv.setItem(i, rewardItems.get(i));
            return inv;
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }

    @Override
    public boolean OpenInventory(Player player, int questId) {
        Inventory inv = CreateInventory(player, questId);
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
        int questId = Integer.parseInt(event.getView().getTitle().split(" ")[0]);
        int newRewardId = _sql.GetLastRewardId() + 1;
        if(eventItem.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            eventInv.remove(eventItem);
            int rewardId = eventItem.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
            ItemStack removedReward = RemoveDataFromItemStack(eventItem, key);
            player.getInventory().addItem(removedReward);
            if(!_sql.DeleteReward(rewardId)) return false;
        } else {
            eventItem = AddDataToItemStack(eventItem, key, newRewardId);
            eventInv.addItem(eventItem);
            player.getInventory().removeItem(eventItem);
            ItemStackSerializer serializer = new ItemStackSerializer(_logger);
            if(!_sql.AddNewReward("RewardItems", serializer.ItemStackToBase64(eventItem), questId)) return false;
        }
        return true;
    }
}
