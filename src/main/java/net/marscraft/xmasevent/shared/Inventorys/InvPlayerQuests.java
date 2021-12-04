package net.marscraft.xmasevent.shared.Inventorys;

import net.marscraft.xmasevent.quest.listener.EventStorage;
import net.marscraft.xmasevent.shared.ItemBuilder;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import java.sql.ResultSet;

public class InvPlayerQuests implements IInventoryType{

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;

    public InvPlayerQuests(ILogmanager logger, DatabaseAccessLayer sql) {
        _logger = logger;
        _sql = sql;
    }

    @Override
    public Inventory CreateInventory(Player player, int questId) {
        int lastQuestId = _sql.GetLastQuestId();
        int inventorySize = (lastQuestId / 9) <= 1 ? 2 : lastQuestId + 1;
        Inventory inv = Bukkit.createInventory(null, inventorySize * 9, "§0Quest Fortschritt");
        ResultSet rs = _sql.GetAllQuests();

        try {
            int itemPos = 0;
            while (rs.next()) {
                int activeQuestId = _sql.GetActivePlayerQuestId(player);
                int activeQuestOrder = _sql.GetQuestOrder(activeQuestId);
                int questIds = rs.getInt("QuestId");
                int questOrder = rs.getInt("QuestOrder");
                boolean questSetupFinished = rs.getBoolean("QuestSetupFinished");
                if (questSetupFinished) {
                    if (activeQuestOrder == questOrder) {
                        inv.setItem(itemPos, new ItemBuilder(Material.WRITABLE_BOOK).SetDisplayname("§c" + rs.getString("QuestName")).SetLore("§aAktiv").SetLocalizedName(questIds + "").Build());
                    } else if (activeQuestOrder > questOrder) {
                        inv.setItem(itemPos, new ItemBuilder(Material.WRITABLE_BOOK).SetDisplayname("§c" + rs.getString("QuestName")).SetLore("§aAbgeschlossen").SetLocalizedName(questIds + "").Build());
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

    @Override
    public boolean OpenInventory(Player player, int questId) {
        Inventory inv = CreateInventory(player, questId);
        if(inv == null) return false;
        player.openInventory(inv);
        return true;
    }

    @Override
    public boolean CloseInventory(EventStorage eventStorage) {
        return true;
    }
}
