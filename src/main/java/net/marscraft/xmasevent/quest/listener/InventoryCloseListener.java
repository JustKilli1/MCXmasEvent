package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.task.Taskmanager;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class InventoryCloseListener implements Listener {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;

    public InventoryCloseListener(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
/*        if(!event.getView().getTitle().contains("CollectItemsTask")) return;
        Inventory eventInv = event.getInventory();
        ItemStack[] invContents = eventInv.getContents();
        int questId = Integer.parseInt(event.getView().getTitle().split(" ")[0]);

        ArrayList<ItemStack> neededItems = new ArrayList<>();
        Taskmanager taskmanager = new Taskmanager(_logger, _sql, _plugin);

        for(ItemStack iStack : invContents) {
            if(iStack == null || iStack.getType() == Material.AIR) continue;
            neededItems.add(iStack);
        }

        String neededItemsStr = taskmanager.ItemStacksToString(neededItems);
        boolean taskExists = _sql.TaskExists(questId, "CollectItemsTask");

        if(taskExists) _sql.UpdateCollectItemsTask(questId, neededItemsStr);
        else _sql.CreateCollectItemsTask(questId, neededItemsStr);*/
    }
}
