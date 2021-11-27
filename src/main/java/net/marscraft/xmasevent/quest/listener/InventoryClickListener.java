package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.gui.QuestsBookGui;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener{

    private final ILogmanager _logger;
    private final DatabaseAccessLayer _sql;
    private final Main _plugin;

    public InventoryClickListener(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null) return;
        if(!(event.getView().getTitle().equalsIgnoreCase("§0Quest Fortschritt")) || event.getView().getTitle().equalsIgnoreCase("§0Quest Belohnungen")) return;
        Player player = (Player) event.getWhoClicked();
        Commandmanager cm = new Commandmanager(_logger);
        if(event.getView().getTitle().equalsIgnoreCase("§0Quest Fortschritt")) {
            event.setCancelled(true);
            if(!(event.getCurrentItem().getItemMeta().hasLocalizedName())) return;
            int questId = cm.GetIntFromStr(event.getCurrentItem().getItemMeta().getLocalizedName());
            QuestsBookGui gui = new QuestsBookGui(_logger, _sql, questId, player);

        }
    }
}
