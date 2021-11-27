package net.marscraft.xmasevent.shared.Inventorys;

import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.shared.ItemBuilder;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.util.ArrayList;

public class InventoryHandler {

    private final ILogmanager _logger;
    private final DatabaseAccessLayer _sql;
    private final Player _player;

    /*
    * Handles Inventory Creation
    */
    public InventoryHandler(ILogmanager logger, DatabaseAccessLayer sql, Player player) {
        _logger = logger;
        _sql = sql;
        _player = player;
    }
    public Inventory CreateUnclaimedRewardsInventory() {
        ArrayList<Integer> rewardIds = _sql.GetUnclaimedPlayerRewardIds(_player);
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

    public Inventory CreatePlayerQuestsInventory() {
        int lastQuestId = _sql.GetLastQuestId();
        int inventorySize = (lastQuestId / 9) <= 1 ? 2 : lastQuestId + 1;
        Inventory inv = Bukkit.createInventory(null, inventorySize * 9, "§0Quest Fortschritt");
        ResultSet rs = _sql.GetAllQuests();

        try {
            int itemPos = 0;
            while (rs.next()) {
                int activeQuestId = _sql.GetActivePlayerQuestId(_player);
                int activeQuestOrder = _sql.GetQuestOrder(activeQuestId);
                int questId = rs.getInt("QuestId");
                int questOrder = rs.getInt("QuestOrder");
                boolean questSetupFinished = rs.getBoolean("QuestSetupFinished");
                if (questSetupFinished) {
                    if (activeQuestOrder == questOrder) {
                        inv.setItem(itemPos, new ItemBuilder(Material.WRITABLE_BOOK).SetDisplayname("§c" + rs.getString("QuestName")).SetLore("§aAktiv").SetLocalizedName(questId + "").Build());
                    } else if (activeQuestOrder > questOrder) {
                        inv.setItem(itemPos, new ItemBuilder(Material.WRITABLE_BOOK).SetDisplayname("§c" + rs.getString("QuestName")).SetLore("§aAbgeschlossen").SetLocalizedName(questId + "").Build());
                    } else if (activeQuestOrder < questOrder) {
                        inv.setItem(itemPos, new ItemBuilder(Material.WRITABLE_BOOK).SetDisplayname("§c?").SetLore("§aSchließe den vorherigen Quest ab").SetLocalizedName("").Build());
                    }
                    itemPos++;
                }
            }
            return inv;
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }


}
