package net.marscraft.xmasevent.quest.task.tasktype;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.listener.EventStorage;
import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.quest.task.Taskmanager;
import net.marscraft.xmasevent.quest.task.tasktype.taskprogressmessages.TaskProgressMessages;
import net.marscraft.xmasevent.shared.Inventorys.Inventorymanager;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.sql.ResultSet;
import java.util.ArrayList;

public class CollectItemsTask implements ITaskType{

    private String _taskName = "collectitemstask";
    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;
    private ItemStackSerializer _serializer;
    private Taskmanager _taskmanager;
    private int _questId;
    private ArrayList<ItemStack> _neededItems;

    public CollectItemsTask(ILogmanager logger, DatabaseAccessLayer sql, Main plugin, int questId, ArrayList<ItemStack> neededItems) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
        _taskmanager = new Taskmanager(_logger, _sql, _plugin);
        _serializer = new ItemStackSerializer(_logger);
        _questId = questId;
        _neededItems = neededItems;
    }

    public CollectItemsTask(ILogmanager logger, DatabaseAccessLayer sql, Main plugin, int questId) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
        _taskmanager = new Taskmanager(_logger, _sql, _plugin);
        _serializer = new ItemStackSerializer(_logger);
        _questId = questId;
        _neededItems = new ArrayList<>();
        LoadTask();
    }

    @Override
    public boolean CreateTask() {
        boolean taskExists = _sql.TaskExists(_questId, _taskName);
        String neededItemsStr = _taskmanager.ItemStacksToString(_neededItems);
        if(neededItemsStr == null) return false;
        if(taskExists)
            _sql.UpdateCollectItemsTask(_questId, neededItemsStr);
        else
            _sql.CreateCollectItemsTask(_questId, neededItemsStr);

        return _sql.UpdateQuestTaskName(_questId, _taskName);
    }

    @Override
    public boolean LoadTask() {
        _neededItems = _sql.GetCollectItemsTaskNeededItems(_questId);
        return true;
    }
    private ArrayList<ItemStack> getItemsFromIdStr(String target, Player player) {
        if (target == null) return null;
        ArrayList<ItemStack> playerItems = new ArrayList<>();
        String[] playerCollectedItemsIds = _sql.GetPlayerQuestValueString(player).split(",");
/*        if(playerCollectedItemsIds.length == 1) {
            ItemStack iStack = _sql.GetCollectItemByTaskId(Integer.parseInt(playerCollectedItemsIds[0]));
            if(iStack == null) return null;
            playerItems.add(iStack);
            _neededItems.remove(iStack);
        }*/
        for(String itemStr : playerCollectedItemsIds) {
            ItemStack iStack = _sql.GetCollectItemByTaskId(Integer.parseInt(itemStr));
            if(iStack == null) return null;
            playerItems.add(iStack);
            _neededItems.remove(iStack);
        }
        return playerItems;
    }
    @Override
    public boolean ExecuteTask(EventStorage eventStorage, Player player) {
        if(!IsTaskActive(eventStorage)) return false;
        if(IsTaskFinished(player)) return false;
        String playerCollectedItemsIds = _sql.GetPlayerQuestValueString(player);
        TaskProgressMessages taskMessages = new TaskProgressMessages(_logger, _sql, player);
        NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), "taskId");
        ArrayList<ItemStack> playerItems = getItemsFromIdStr(playerCollectedItemsIds, player);
        if(playerItems != null) {
            for (ItemStack iStack : playerItems) {
                if (_neededItems.contains(_neededItems.indexOf(iStack)))
                    _neededItems.remove(iStack);
            }
        }
        String addedItemIds = null;
        for(ItemStack iStack : player.getInventory().getContents()) {
            if(iStack == null || iStack.getType() == Material.AIR) continue;
            for (int i = 0; i < _neededItems.size(); i++) {
                ItemStack neededIstack = _neededItems.get(i);
                if(neededIstack.getType() == iStack.getType()
                        && neededIstack.getItemMeta().getDisplayName() == iStack.getItemMeta().getDisplayName()
                        && neededIstack.getAmount() == iStack.getAmount()) {
                    int taskId = _neededItems.get(i).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
                    if(addedItemIds == null)
                        addedItemIds = "" + taskId;
                    else
                        addedItemIds += "," + taskId;
                    _neededItems.remove(i);
                    player.getInventory().remove(iStack);
                    break;
                } else continue;
            }
        }
        String oldItemStr = _sql.GetPlayerQuestValueString(player);
        if(addedItemIds == null && oldItemStr == null) return false;
        String itemStr;
        if(oldItemStr == null)
            itemStr = addedItemIds;
        else if(addedItemIds == null)
            itemStr = oldItemStr;
        else
            itemStr = oldItemStr + "," + addedItemIds;
        return _sql.SetPlayerQuestValueString(player, itemStr);
/*        if(!IsTaskActive(eventStorage)) return false;
        if(IsTaskFinished(player)) return false;
        int questId = _sql.GetActivePlayerQuestId(player);
        ArrayList<ItemStack> taskNeededItemsData = _sql.GetCollectItemsTaskNeededItems(questId);
        ArrayList<ItemStack> taskNeededItems = new ArrayList<>();
        ArrayList<ItemStack> playerCollectedItems = _taskmanager.GetItemStacksFromStr(_sql.GetPlayerQuestValueString(player));
        TaskProgressMessages taskMessages = new TaskProgressMessages(_logger, _sql, player);
        Inventorymanager inventorymanager = new Inventorymanager(_logger);
        NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), "taskId");
        for(ItemStack iStack : taskNeededItemsData) {
            taskNeededItems.add(inventorymanager.RemoveDataFromItemStack(iStack, key));
        }
        for(ItemStack iStack : playerCollectedItems) {
            if(taskNeededItems.contains(iStack)) {
                taskNeededItems.remove(iStack);
            } else {
                _logger.Error("Database data Error Player " + player.getName() + " has a Collected Item that's not in CollectItemsTask Table");
                return false;
            }
        }
        ItemStack[] playerInvContents = player.getInventory().getContents();

        for(ItemStack playerItem : playerInvContents) {
            if(playerItem == null || playerItem.getType() == Material.AIR) continue;
            for(ItemStack taskItem : taskNeededItems) {
                if(playerItem != taskItem) continue;
                player.getInventory().remove(playerItem);
                taskNeededItems.remove(taskItem);
            }
        }
        if(taskNeededItems.size() == 0)
            taskMessages.SendQuestFinishedMsg();
        else
            _sql.SetPlayerQuestValueString(player, _taskmanager.ItemStacksToString(taskNeededItems));

        return true;*/
    }

    @Override
    public boolean IsTaskFinished(Player player) {
        int questId = _sql.GetActivePlayerQuestId(player);
        ArrayList<ItemStack> taskNeededItems = _sql.GetCollectItemsTaskNeededItems(questId);
        ArrayList<ItemStack> playerCollectedItems = getItemsFromIdStr(_sql.GetPlayerQuestValueString(player), player);
        if(playerCollectedItems == null) return false;
        if (taskNeededItems.size() == playerCollectedItems.size()) return true;
        return false;
    }

    public boolean IsTaskActive(EventStorage eventStorage) {
        if(eventStorage.GetCommandArgs() == null) return false;
        return true;
    }

    @Override
    public String GetTaskName() { return _taskName; }

    @Override
    public int GetTaskId() { return 0; }
}
