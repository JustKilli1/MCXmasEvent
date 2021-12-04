package net.marscraft.xmasevent.quest.gui.bookgui;

import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import java.sql.ResultSet;

public class PlaceBlockBookGui extends BaseQuestsBookGui implements IBookGui{

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;

    public PlaceBlockBookGui(ILogmanager logger, DatabaseAccessLayer sql) {
        super(logger, sql);
        _logger = logger;
        _sql = sql;
    }

    @Override
    public ItemStack CreateBookGui(Player player, int questId) {
        ItemStack book = CreateBaseQuestsBookGui(player, questId);
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
        return false;
    }

    /*
    * Beispiel TaskGui:
    * Aufgabe: Platziere einen Stein block"
    *
    * Koordinaten
    * X: 100
    * Y:57
    * Z: -1265
    * Aktiv
    * */
    @Override
    public String BuildTaskGui(int questId, Player player) {
        String taskName = _sql.GetTaskNameByQuestId(questId);
        int activeQuestId = _sql.GetActivePlayerQuestId(player);
        int progressValueInt = _sql.GetPlayerQuestValueInt(player);
        ResultSet task = _sql.GetTaskByQuestId(taskName, questId);

        try {
            if(!task.next()) return null;
            String blockTypeGer = task.getString("BlockTypeGer");
            int BlockPosX = task.getInt("BlockPositionX");
            int BlockPosY = task.getInt("BlockPositionY");
            int BlockPosZ = task.getInt("BlockPositionZ");
            String description =
                    "Aufgabe: Platziere einen " + blockTypeGer + " block" +
                    "\n\n" +
                    "Koordinaten \n" +
                    "X:" + BlockPosX + "\n" +
                    "Y:" + BlockPosY + "\n" +
                    "Z:" + BlockPosZ + "\n";
            if (activeQuestId > questId) {
                description += "§6§lAbgeschlossen";
                return description;
            } else {
                description += "§a§lAktiv";
                return description;
            }
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }
}
