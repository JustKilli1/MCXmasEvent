package net.marscraft.xmasevent.quest.gui.bookgui;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.ResultSet;
import java.util.ArrayList;

public class CollectItemsBookGui extends BaseQuestsBookGui implements IBookGui{

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;

    public CollectItemsBookGui(ILogmanager logger, DatabaseAccessLayer sql) {
        super(logger, sql);
        _logger = logger;
        _sql = sql;
    }
    @Override
    public ItemStack CreateBookGui(Player player, int questId) {
        ItemStack book = CreateBaseQuestsBookGui(questId);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        String page = BuildTaskGui(questId, player);
        bookMeta.addPage(page);
        book.setItemMeta(bookMeta);
        return book;
    }

    @Override
    public boolean OpenBookGui(Player player, int questId) {
        ItemStack bookGui = CreateBookGui(player, questId);
        if(bookGui == null) return false;
        player.openBook(bookGui);
        return true;
    }

    /*
    * Beispiel TaskGui:
    * Trixi möchte folgende Items von dir:
    *
    * Cobblestone 64/64
    * RedstoneBlock 0/32
    * GoldBlock 0/32
    * Aktiv
    * */
    @Override
    public String BuildTaskGui(int questId, Player player) {
        ArrayList<ItemStack> neededItems = _sql.GetCollectItemsTaskNeededItems(questId);
        ArrayList<Integer> taskIds = new ArrayList<>();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), "taskId");

        String questValueString = _sql.GetPlayerQuestValueString(player);
        if(questValueString != null) {
            String[] taskIdsStr = questValueString.split(",");

            for (String taskIdStr : taskIdsStr) {
                taskIds.add(Integer.parseInt(taskIdStr));
            }
        }
        String npcName = _sql.GetQuestNpcName(questId);
        String taskGui = npcName + " möchte folgende Items von dir:\n\n";

        for(ItemStack iStack : neededItems) {
            int amount = iStack.getAmount();
            if(iStack.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {

                int taskId = iStack.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
                if(taskIds.contains(taskId))
                    taskGui += "§a" + iStack.getI18NDisplayName() + " " + amount + "/" + amount + "\n";
                else
                    taskGui += "§0" + iStack.getI18NDisplayName() + " 0/" + amount + "\n";
            } else {
                _logger.Error("ItemStack has no persistant data container matching key");
                return null;
            }
        }
        return taskGui;
    }
}
