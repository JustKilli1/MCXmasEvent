package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.shared.Inventorys.IInventoryType;
import net.marscraft.xmasevent.shared.Inventorys.InvAdminSetRewards;
import net.marscraft.xmasevent.shared.Inventorys.InvPlayerQuests;
import net.marscraft.xmasevent.shared.Inventorys.InvUnclaimedReward;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener{

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;

    public InventoryClickListener(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {

        if(event.getCurrentItem() == null) return;
        String titleName = event.getView().getTitle();
        IInventoryType inventoryType;

        if(titleName.equalsIgnoreCase("Quest Fortschritt")) {
            inventoryType = new InvPlayerQuests(_logger, _sql);
        } else if(titleName.equalsIgnoreCase("Quest Belohnungen")) {
            inventoryType = new InvUnclaimedReward(_logger, _sql);
        } else if(titleName.contains("Quest Rewards")) {
            inventoryType = new InvAdminSetRewards(_logger, _sql);
        } else {
            return;
        }
        EventStorage eventStorage = new EventStorage();
        eventStorage.SetInventoryClickEvent(event);
        inventoryType.InventoryClickItem(eventStorage);
    }
}
