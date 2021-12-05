package net.marscraft.xmasevent.quest.task;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.task.tasktype.*;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.sql.ResultSet;
import java.util.HashMap;

public class Taskmanager {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;

    public Taskmanager(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
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
                default:
                    _logger.Error("Task mit dem Namen " + taskName + " existiert nicht");
                    return null;
            }
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
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
