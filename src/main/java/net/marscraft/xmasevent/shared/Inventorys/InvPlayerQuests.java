package net.marscraft.xmasevent.shared.Inventorys;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.gui.bookgui.*;
import net.marscraft.xmasevent.quest.listener.EventStorage;
import net.marscraft.xmasevent.shared.ItemBuilder;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.sql.ResultSet;

public class InvPlayerQuests extends Inventorymanager implements IInventoryType{

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;

    public InvPlayerQuests(ILogmanager logger, DatabaseAccessLayer sql) {
        super(logger);
        _logger = logger;
        _sql = sql;
    }

    @Override
    public Inventory CreateInventory(Player player, int questId) {
        int lastQuestId = _sql.GetLastQuestId();
        int inventorySize = (lastQuestId / 9) <= 1 ? 2 : lastQuestId + 1;
        Inventory inv = Bukkit.createInventory(null, inventorySize * 9, "Quest Fortschritt");
        NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), "questId");
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
                    ItemStack questBook;
                    if (activeQuestOrder == questOrder) {
                        questBook = new ItemBuilder(Material.WRITABLE_BOOK).SetDisplayname("§c" + rs.getString("QuestName")).SetLore("§aAktiv").Build();
                        questBook = AddDataToItemStack(questBook, key, questIds);
                        inv.setItem(itemPos, questBook);
                    } else if (activeQuestOrder > questOrder) {
                        questBook = new ItemBuilder(Material.WRITABLE_BOOK).SetDisplayname("§c" + rs.getString("QuestName")).SetLore("§aAbgeschlossen").Build();
                        questBook = AddDataToItemStack(questBook, key, questIds);
                    } else if (activeQuestOrder < questOrder)
                        questBook = new ItemBuilder(Material.WRITABLE_BOOK).SetDisplayname("§c?").SetLore("§aSchließe den vorherigen Quest ab").Build();
                    else
                        return null;
                    inv.setItem(itemPos, questBook);
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
    public boolean InventoryClickItem(EventStorage eventStorage) {
        InventoryClickEvent event = eventStorage.GetInventoryClickEvent();
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), "questId");
        ItemStack eventItem = event.getCurrentItem();
        if(eventItem == null) return false;
        ItemMeta eventItemMeta = eventItem.getItemMeta();
        if(!(eventItemMeta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER))) return false;
        int questId = eventItemMeta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        String taskName = _sql.GetTaskNameByQuestId(questId);
        _logger.Error(taskName + " " + questId);
        IBookGui bookGui;
        switch (taskName.toLowerCase()) {
            case "killmobstask":
                bookGui = new KillMobsTaskBookGui(_logger, _sql);
                break;
            case "placeblocktask":
                bookGui = new PlaceBlockBookGui(_logger, _sql);
                break;
            case "placeblockstask":
                bookGui = new PlaceBlocksBookGui(_logger, _sql);
                break;
            case "breakblockstask":
                bookGui = new BreakBlocksBookGui(_logger, _sql);
                break;
            default:
                return false;
        }
        return bookGui.OpenBookGui(player, questId);
    }
}
