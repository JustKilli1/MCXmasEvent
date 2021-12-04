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
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InvAdminSetRewards implements IInventoryType{

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private String _inventoryName = "Quest Rewards";

    public InvAdminSetRewards(ILogmanager logger, DatabaseAccessLayer sql) {
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
    public boolean CloseInventory(EventStorage eventStorage) {
        /*
        * RewardIds werden nach quest id aus db geladen und in list gespeichert
        * inv contents werden in itemstack list gespeichert
        * itemstack list wird durchgegangen und geschaut ob item persistent data container mit key enthält
        * wenn ja wird removed
        * reward inv wird item gesetzt ohne persistant data container key
        * */
        InventoryCloseEvent event = eventStorage.GetInventoryCloseEvent();
        Inventory eventInv = event.getInventory();
        InventoryView invView = event.getView();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), "rewardId");
        if(!(invView.getTitle().contains(_inventoryName))) return false;
        int questId = Integer.parseInt(event.getView().getTitle().split(" ")[0]);
        List<Integer> rewardIds = _sql.GetRewardIds(questId);
        ItemStack[] eventInvContents = eventInv.getContents();
        for(int i = 0; i < eventInvContents.length; i++) {
            ItemStack eventInvIStack = eventInvContents[i];
            if(eventInvIStack == null) continue;
            ItemMeta eventInvIMeta = eventInvContents[i].getItemMeta();
            if(eventInvIMeta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
                int rewardId = eventInvIMeta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
                rewardIds.remove(rewardIds.indexOf(rewardId));
                eventInvIMeta.getPersistentDataContainer().remove(key);
                eventInvIStack.setItemMeta(eventInvIMeta);
                ItemStackSerializer serializer = new ItemStackSerializer(_logger);
                _sql.SetReward(rewardId, serializer.ItemStackToBase64(eventInvIStack));
            } else {
                ItemStackSerializer serializer = new ItemStackSerializer(_logger);
                _sql.AddNewReward("RewardItems", serializer.ItemStackToBase64(eventInvIStack), questId);
            }
        }
        for(int rewardId : rewardIds) { _sql.DeleteReward(rewardId); }
        return true;
    }
}
