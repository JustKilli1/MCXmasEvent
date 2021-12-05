package net.marscraft.xmasevent.shared.Inventorys;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.listener.EventStorage;
import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.quest.task.Taskmanager;
import net.marscraft.xmasevent.quest.task.tasktype.CollectItemsTask;
import net.marscraft.xmasevent.quest.task.tasktype.ITaskType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class InvAdminCollectItems extends Inventorymanager implements IInventoryType{

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;
    private String _inventoryName = "Quest Rewards";

    public InvAdminCollectItems(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
    }

    @Override
    public Inventory CreateInventory(Player player, int questId) {
        // /mcxmas edit [questID] SetTask CollectItemsTask
        ItemStackSerializer serializer = new ItemStackSerializer(_logger);
        ArrayList<ItemStack> neededItems = new ArrayList<>();
        neededItems = _sql.GetCollectItemsTaskNeededItems(questId);
        Inventory inv = Bukkit.createInventory(null, 6 * 9, questId + " CollectItemsTask");
        if(neededItems == null) return inv;
        for(int i = 0; i < neededItems.size(); i++) {
            inv.setItem(i, neededItems.get(i));
        }
        return inv;
    }

    @Override
    public boolean OpenInventory(Player player, int questId) {
        Inventory inv = CreateInventory(player, questId);
        player.openInventory(inv);
        return true;
    }

    @Override
    public boolean InventoryClickItem(EventStorage eventStorage) {
        InventoryClickEvent event = eventStorage.GetInventoryClickEvent();
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), "taskId");
        Inventory eventInv = event.getInventory();
        ItemStack eventItem = event.getCurrentItem();
        if(eventItem == null) return false;
        int questId = Integer.parseInt(event.getView().getTitle().split(" ")[0]);
        int newTaskId = _sql.GetLastTaskId("CollectItemsTask") + 1;
        if(eventItem.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            eventInv.remove(eventItem);
            int taskId = eventItem.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
            ItemStack removedItem = RemoveDataFromItemStack(eventItem, key);
            player.getInventory().addItem(removedItem);
            if(!_sql.DeleteTaskByTaskId(taskId, "CollectItemsTask")) return false;
        } else {
            eventItem = AddDataToItemStack(eventItem, key, newTaskId);
            eventInv.addItem(eventItem);
            player.getInventory().removeItem(eventItem);
            ItemStackSerializer serializer = new ItemStackSerializer(_logger);
            if(!_sql.CreateCollectItemsTask(questId, serializer.ItemStackToBase64(eventItem))) return false;
        }
        return true;
    }
}
