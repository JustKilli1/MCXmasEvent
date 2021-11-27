package net.marscraft.xmasevent.quest.task;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.task.tasktype.ITaskType;
import net.marscraft.xmasevent.quest.task.tasktype.KillMobsTask;
import net.marscraft.xmasevent.quest.task.tasktype.PlaceBlockTask;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.sql.ResultSet;
import java.util.HashMap;

public class Taskmanager {

    private final ILogmanager _logger;
    private final DatabaseAccessLayer _sql;
    private final Main _plugin;

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
            switch (taskName) {
                case "KillMobsTask":
                    int neededMobs = rs.getInt("NeededMobs");
                    String mobType = rs.getString("MobType");
                    String mobTypeGer = rs.getString("MobTypeGer");
                    return new KillMobsTask(_logger, _sql, questId, neededMobs, mobType, mobTypeGer);
                case "PlaceBlockTask":
                    String blockType = rs.getString("BlockType");
                    String blockTypeGer = rs.getString("BlockTypeGer");
                    double blockLocX = rs.getDouble("BlockPositionX");
                    double blockLocY = rs.getDouble("BlockPositionY");
                    double blockLocZ = rs.getDouble("BlockPositionZ");
                    String world = rs.getString("WorldName");
                    Location blockLoc = new Location(_plugin.getServer().getWorld(world), blockLocX, blockLocY, blockLocZ);
                    return new PlaceBlockTask(_logger, _sql, questId, blockType, blockTypeGer, blockLoc);
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
    public boolean IsTaskActive(String taskName, int questId) {
        if(_sql.GetTaskNameByQuestId(questId) == null) return false;
        return _sql.GetTaskNameByQuestId(questId).equalsIgnoreCase(taskName);
    }
}
