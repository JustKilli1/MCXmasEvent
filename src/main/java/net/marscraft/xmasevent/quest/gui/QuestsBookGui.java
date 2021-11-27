package net.marscraft.xmasevent.quest.gui;

import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import java.sql.ResultSet;
import java.util.ArrayList;

public class QuestsBookGui {

    private final ILogmanager _logger;
    private final DatabaseAccessLayer _sql;
    private final int _questId;
    private final Player _player;


    public QuestsBookGui(ILogmanager logger, DatabaseAccessLayer sql, int questId, Player player) {
        _logger = logger;
        _sql = sql;
        _questId = questId;
        _player = player;
        openBookGui();
    }

    private void openBookGui() {

        ResultSet quest = _sql.GetQuest(_questId);
        String taskName = _sql.GetTaskNameByQuestId(_questId);
        ResultSet task = _sql.GetTaskByQuestId(taskName, _questId);

        try {
            quest.next();
            String questName = quest.getString("QuestName");
            String description = quest.getString("Description");
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bookMeta = (BookMeta) book.getItemMeta();
            ArrayList<String> pages = new ArrayList<>();
            bookMeta.setAuthor("Weihnachtsmann");
            bookMeta.setTitle(questName);
            pages.add("§0§l" + questName + "\n\n§0" + description + "\n\n" + getTaskDescription(task));
            bookMeta.setPages(pages);
            book.setItemMeta(bookMeta);
            _player.openBook(book);
        } catch (Exception ex) {
            _logger.Error(ex);
        }
    }
    private String getTaskDescription(ResultSet task) {
        try {
            task.next();
            String taskName = task.getString("TaskName");
            int activeQuestId = _sql.GetActivePlayerQuestId(_player);
            int progressValueInt = _sql.GetPlayerQuestValueInt(_player);
            String description = "";
            switch (taskName) {
                case "KillMobsTask":
                    String mobTypeGer = task.getString("MobTypeGer");
                    int amount = task.getInt("NeededMobs");
                    description = "Aufgabe: Töte " + amount + " " + mobTypeGer + "e\n\nBesiegte " + mobTypeGer + "e: ";
                    if(activeQuestId > _questId) {
                        description += amount + "/" + amount + "\n§a§lAbgeschlossen";
                        return description;
                    } else {
                        description += progressValueInt + "/" + amount + "\n§6§lAktiv";
                        return description;
                    }
                case "PlaceBlockTask":
                    String blockTypeGer = task.getString("BlockTypeGer");
                    description = "Aufgabe: Platziere einen " + blockTypeGer + " block\n\nKoordinaten \nX:" + task.getInt("BlockPositionX") + "\nY:" + task.getInt("BlockPositionY") + "\nZ:" + task.getInt("BlockPositionZ");
                    if(activeQuestId > _questId) {
                        description += "\n§6§lAbgeschlossen";
                        return description;
                    } else {
                        description += "\n§a§lAktiv";
                        return description;
                    }
                default:
                    return null;
            }
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }

}
