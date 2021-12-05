package net.marscraft.xmasevent.quest.task;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.quest.task.tasktype.*;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class Taskmanager {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;
    private ItemStackSerializer _serializer;

    public Taskmanager(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
        _serializer = new ItemStackSerializer(_logger);
    }
    public ITaskType GetTaskTypeByName(int questId, String taskName) {
        //TODO evtl. mit Reflections arbeiten
        ResultSet rs = _sql.GetTaskByQuestId(taskName, questId);
        try {
            if(!rs.next())return null;
            switch (taskName.toLowerCase()) {
                case "killmobstask":
                    return new KillMobsTask(_logger, _sql, _plugin, questId);
                case "placeblocktask":
                    return new PlaceBlockTask(_logger, _sql, _plugin, questId);
                case "placeblockstask":
                    return new PlaceBlocksTask(_logger, _sql, _plugin, questId);
                case "breakblockstask":
                    return new BreakBlocksTask(_logger, _sql, _plugin, questId);
                case "collectitemstask":
                    return new CollectItemsTask(_logger, _sql, _plugin, questId);
                default:
                    _logger.Error("Task mit dem Namen " + taskName + " existiert nicht");
                    return null;
            }
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }
    public ArrayList<ItemStack> GetItemStacksFromStr(String itemStackStr) {
        String[] itemStrings = itemStackStr.split(",");
        ArrayList<ItemStack> items = new ArrayList<>();
        for (String itemString : itemStrings) {
            ItemStack neededItem = _serializer.ItemStackFromBase64(itemString);
            if(neededItem == null) return null;
            items.add(neededItem);
        }
        return items;
    }

    public String ItemStacksToString(ArrayList<ItemStack> items) {
        String neededItemsStr = null;

        for (ItemStack neededItem : items) {
            if(neededItemsStr == null)
                neededItemsStr = _serializer.ItemStackToBase64(neededItem);
            else
                neededItemsStr = "," + _serializer.ItemStackToBase64(neededItem);
        }
        return neededItemsStr;
    }
    public EntityType GetKillMobsTaskMobType(int questId) {
        ResultSet task = _sql.GetTaskByQuestId("KillMobsTask", questId);
        try {
            if(!task.next()) return null;
            String mobTypeStr = task.getString("MobType").toUpperCase();
            return EntityType.valueOf(mobTypeStr);
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }
    public HashMap<Material, Location> GetPlaceBlockTaskBlockInfo(int questId) {
        ResultSet rs = _sql.GetTaskByQuestId("PlaceBlockTask", questId);
        HashMap<Material, Location> blockInfo = new HashMap<>();
        try {
            if(!rs.next()) return null;
            Material material = Material.valueOf(rs.getString("BlockType").toUpperCase());
            String world = rs.getString("WorldName");
            Location location = new Location(_plugin.getServer().getWorld(world), rs.getDouble("BlockPositionX"), rs.getDouble("BlockPositionY"), rs.getDouble("BlockPositionZ"));
            blockInfo.put(material, location);
            return blockInfo;
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }

    public Material GetBlocksBlockType(int questId, String tableName) {
        ResultSet rs = _sql.GetTaskByQuestId(tableName, questId);
        try {
            if(!rs.next()) return null;
            String blockTypeStr = rs.getString("BlockType");
            return Material.getMaterial(blockTypeStr.toUpperCase());
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }
}
