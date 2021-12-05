package net.marscraft.xmasevent.quest.gui.bookgui;

import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import java.sql.ResultSet;

public class PlaceBlocksBookGui extends BaseQuestsBookGui implements IBookGui{

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;

    public PlaceBlocksBookGui(ILogmanager logger, DatabaseAccessLayer sql) {
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
    * Aufgabe: Platziere 64 Cobblestone
    *
    * Platzierte Cobblestone blöcke: 32/64
    * Aktiv
    * */
    @Override
    public String BuildTaskGui(int questId, Player player) {
        String taskName = _sql.GetTaskNameByQuestId(questId);
        ResultSet task = _sql.GetTaskByQuestId(taskName, questId);
        int activeQuestId = _sql.GetActivePlayerQuestId(player);
        int progressValueInt = _sql.GetPlayerQuestValueInt(player);
        String description = "";
        try {
            if(!task.next()) return null;
            String blockTypeGer = task.getString("BlockTypeGer");
            int amount = task.getInt("BlockAmount");
            description =
                            "Aufgabe: Platziere " + amount + " " + blockTypeGer + "" +
                            "\n\n" +
                            "Platzierte " + blockTypeGer + " blöcke: \n";
            if (activeQuestId > questId) {
                description += amount + "/" + amount + "\n§a§lAbgeschlossen";
                return description;
            } else {
                description += progressValueInt + "/" + amount + "\n§6§lAktiv";
                return description;
            }
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }
}
