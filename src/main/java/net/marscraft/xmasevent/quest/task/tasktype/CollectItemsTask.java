package net.marscraft.xmasevent.quest.task.tasktype;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.listener.EventStorage;
import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.quest.task.Taskmanager;
import net.marscraft.xmasevent.quest.task.tasktype.taskprogressmessages.TaskProgressMessages;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;
import java.util.HashMap;

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
        for(String itemStr : playerCollectedItemsIds) {
            ItemStack iStack = _sql.GetCollectItemByTaskId(Integer.parseInt(itemStr));
            if(iStack == null) return null;
            playerItems.add(iStack);
            _neededItems.remove(iStack);
        }
        return playerItems;
    }
    private boolean removeItemFromPlayerInv(Player player, ItemStack target) {
        ItemMeta targetMeta = target.getItemMeta();
        NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), "taskId");
        if(targetMeta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) targetMeta.getPersistentDataContainer().remove(key);
        ItemStack[] playerInvContents = player.getInventory().getContents();
        ArrayList<Integer> tmpInts = new ArrayList<>();

        for(int i = 0; i < playerInvContents.length; i++) {
            ItemStack playerContent = playerInvContents[i];
            if(playerContent == null || playerContent.getType() == Material.AIR) continue;
            if(playerContent.getType() != target.getType()) continue;
            int playerAmount = playerContent.getAmount();
            int targetAmount = target.getAmount();
            if(playerAmount > targetAmount) {
                int newAmount = playerAmount - targetAmount;
                playerContent.setAmount(newAmount);
                playerInvContents[i] = playerContent;
                player.getInventory().setContents(playerInvContents);
                return true;
            } else if(playerAmount == targetAmount) {
                playerInvContents[i] = new ItemStack(Material.AIR);
                player.getInventory().setContents(playerInvContents);
                return true;
            } else if(playerAmount < targetAmount) {
                tmpInts.add(i);
            }
        }
        if(tmpInts.size() == 0) return false;
        int targetAmount = target.getAmount();
        for(Integer i : tmpInts) {
            ItemStack iStack = playerInvContents[i];
            int playerAmount = iStack.getAmount();
            int differenceAmount = targetAmount - playerAmount;
            if(differenceAmount < 0) {
                iStack.setAmount(playerAmount - targetAmount);
                playerInvContents[i] = iStack;
                targetAmount -= playerAmount;
                break;
            } else if(differenceAmount == 0) {
                playerInvContents[i] = new ItemStack(Material.AIR);
                targetAmount = 0;
            } else {
                targetAmount -= playerAmount;
                playerInvContents[i] = new ItemStack(Material.AIR);
            }
        }
        if(targetAmount <= 0) {
            player.getInventory().setContents(playerInvContents);
            return true;
        }
        return false;
    }
    @Override
    public boolean ExecuteTask(EventStorage eventStorage, Player player) {
        if(!IsTaskActive(eventStorage)) return false;
        if(IsTaskFinished(player)) return false;
        String playerCollectedItemsIds = _sql.GetPlayerQuestValueString(player);
        TaskProgressMessages taskMessages = new TaskProgressMessages(_logger, _sql, player);
        NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), "taskId");
        ArrayList<ItemStack> playerCollectedItems = getItemsFromIdStr(playerCollectedItemsIds, player);
        if(playerCollectedItems != null) {
            for (ItemStack iStack : playerCollectedItems) {
                if (_neededItems.contains(_neededItems.indexOf(iStack)))
                    _neededItems.remove(iStack);
            }
        }
        String addedItemIds = null;
        for(ItemStack iStack : player.getInventory().getContents()) {
            if(iStack == null || iStack.getType() == Material.AIR) continue;
            for (int i = 0; i < _neededItems.size(); i++) {
                ItemStack neededIStack = _neededItems.get(i);
                if(neededIStack.getType() == iStack.getType()
                        && neededIStack.getItemMeta().getDisplayName() == iStack.getItemMeta().getDisplayName()) {
                    int taskId = _neededItems.get(i).getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
                    if(!removeItemFromPlayerInv(player, neededIStack)) continue;
                    if(addedItemIds == null)
                        addedItemIds = "" + taskId;
                    else
                        addedItemIds += "," + taskId;
                    _neededItems.remove(i);
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
