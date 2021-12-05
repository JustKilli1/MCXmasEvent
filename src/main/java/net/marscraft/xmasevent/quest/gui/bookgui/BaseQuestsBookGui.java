package net.marscraft.xmasevent.quest.gui.bookgui;

import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.sql.ResultSet;
import java.util.ArrayList;

public class BaseQuestsBookGui {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;

    public BaseQuestsBookGui(ILogmanager logger, DatabaseAccessLayer sql) {
        _logger = logger;
        _sql = sql;
    }

    public ItemStack CreateBaseQuestsBookGui(int questId) {
        ResultSet quest = _sql.GetQuest(questId);

        try {
            quest.next();
            String questName = quest.getString("QuestName");
            String description = quest.getString("Description");
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bookMeta = (BookMeta) book.getItemMeta();
            ArrayList<String> pages = new ArrayList<>();
            bookMeta.setAuthor("Weihnachtsmann");
            bookMeta.setTitle(questName);
            pages.add("§0§l" + questName + "\n\n§0" + description);
            bookMeta.setPages(pages);
            book.setItemMeta(bookMeta);
            return book;
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }

}
