package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.gui.QuestsBookGui;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;

public class InventoryClickListener implements Listener{

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;
    private IMessagemanager _messageManager;

    public InventoryClickListener(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null) return;
        if(!(event.getView().getTitle().equalsIgnoreCase("§0Quest Fortschritt"))) return;
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        if(!(event.getCurrentItem().getItemMeta().hasLocalizedName())) return;
        Commandmanager cm = new Commandmanager(_logger);
        int questId = cm.GetIntFromStr(event.getCurrentItem().getItemMeta().getLocalizedName());
        QuestsBookGui gui = new QuestsBookGui(_logger, _sql, questId, player);
/*        player.sendMessage(questId + "");
        ArrayList<String> pages = new ArrayList<>();
        ItemStack iStack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) iStack.getItemMeta();
        bookMeta.setAuthor("Weihnachtsmann");
        bookMeta.setTitle("Test");
        pages.add("Test\nTESTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT\nTest");
        bookMeta.setPages(pages);
        iStack.setItemMeta(bookMeta);
        player.openBook(iStack);*/
    }

}
