package net.marscraft.xmasevent.shared.Inventorys;

import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.sql.ResultSet;
import java.util.ArrayList;

public class InvUnclaimedReward implements IInventoryType{

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;

    public InvUnclaimedReward(ILogmanager logger, DatabaseAccessLayer sql) {
        _logger = logger;
        _sql = sql;
    }

    @Override
    public Inventory CreateInventory(Player player) {

        ArrayList<Integer> rewardIds = _sql.GetUnclaimedPlayerRewardIds(player);
        if(rewardIds == null) return null;
        int inventorySize = (rewardIds.size() / 9) + 1;
        Inventory inv = Bukkit.createInventory(null, inventorySize * 9, "Quest Belohnungen");

        for(int i = 0; i < rewardIds.size(); i++) {
            ResultSet rs = _sql.GetQuestRewardByRewardId(rewardIds.get(i));
            try {
                if(!rs.next()) return null;
                String rewardStr = rs.getString("Reward");
                ItemStackSerializer serializer = new ItemStackSerializer(_logger);
                ItemStack reward = serializer.ItemStackFromBase64(rewardStr);
                ItemMeta rewardMeta = reward.getItemMeta();
                rewardMeta.setLocalizedName(rewardIds.get(i) + "");
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
    public boolean OpenInventory(Player player) {
        Inventory inv = CreateInventory(player);
        player.openInventory(inv);
        return true;
    }
}
