package net.marscraft.xmasevent.quest.task.tasktype;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.Questmanager;
import net.marscraft.xmasevent.quest.listener.EventStorage;
import net.marscraft.xmasevent.quest.task.Taskmanager;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import java.sql.ResultSet;
import java.util.HashMap;

public class PlaceBlockTask implements ITaskType {

    private String _taskName = "placeblocktask";
    private String _blockType, _blockTypeGer;
    private int _questId;
    private Location _blockLoc;
    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;

    public PlaceBlockTask(ILogmanager logger, DatabaseAccessLayer sql, Main plugin, int questId, Location blockLoc, String blockType, String blockTypeGer) {
        _logger = logger;
        _sql = sql;
        _questId = questId;
        _plugin = plugin;
        _blockLoc = blockLoc;
        _blockType = blockType;
        _blockTypeGer = blockTypeGer;
    }

    public PlaceBlockTask(ILogmanager logger, DatabaseAccessLayer sql, Main plugin, int questId) {
        _logger = logger;
        _sql = sql;
        _questId = questId;
        _plugin = plugin;
        LoadTask();
    }

    @Override
    public boolean CreateTask() {
        int taskId = _sql.GetLastTaskId(_taskName);
        boolean taskExists = _sql.TaskExists(_questId, _taskName);
        if(taskExists) _sql.UpdatePlaceBlockTask(_questId, _blockType, _blockTypeGer, _blockLoc);
        else _sql.CreatePlaceBlockTask(taskId, _questId, _blockType, _blockTypeGer, _blockLoc);
        _sql.UpdateQuestTaskName(_questId, _taskName);
        return true;
    }

    @Override
    public boolean LoadTask() {
        ResultSet rs = _sql.GetTaskByQuestId(_taskName, _questId);
        try {
            if(!rs.next()) return false;
            _blockType = rs.getString("BlockType");
            _blockTypeGer = rs.getString("BlockTypeGer");
            double blockLocX = rs.getDouble("BlockPositionX");
            double blockLocY = rs.getDouble("BlockPositionY");
            double blockLocZ = rs.getDouble("BlockPositionZ");
            String world = rs.getString("WorldName");
            _blockLoc = new Location(_plugin.getServer().getWorld(world), blockLocX, blockLocY, blockLocZ);
            return true;
        } catch (Exception ex) {
            _logger.Error(ex);
            return false;
        }
    }

    @Override
    public boolean ExecuteTask(EventStorage eventStorage, Player player) {
        if(!IsTaskActive(eventStorage)) return false;
        if(IsTaskFinished(player)) return false;
        BlockPlaceEvent event = eventStorage.GetBlockPlaceEvent();
        int questId = _sql.GetActivePlayerQuestId(player);

        Taskmanager taskmanager = new Taskmanager(_logger, _sql, _plugin);
        HashMap<Material, Location> blockInfo = taskmanager.GetPlaceBlockTaskBlockInfo(questId);
        if(blockInfo == null)return false;
        if(blockInfo.keySet().size() != 1)return false;

        Material blockType = null;
        Location blockLoc = null;
        for(Material key : blockInfo.keySet()) {
            blockType = key;
            blockLoc = blockInfo.get(key);

        }
        Block eventBlock = event.getBlock();
        if(blockType == eventBlock.getType()) {
            if(eventBlock.getWorld() == blockLoc.getWorld()
                    && eventBlock.getLocation().getX() == blockLoc.getX()
                    && eventBlock.getLocation().getY() == blockLoc.getY()
                    && eventBlock.getLocation().getZ() == blockLoc.getZ()) {
                if(_sql.UpdateTaskPlayerBlockPlaced(player)) return true;
                else _logger.Error("PlaceBlockTask von Spieler " + player.getName() + " konnte nicht geupdatet werden!");
            }
        } else return false;

        return false;
    }

    @Override
    public boolean IsTaskFinished(Player player) { return _sql.GetPlayerQuestValueBool(player); }

    public boolean IsTaskActive(EventStorage eventStorage) {
        if(eventStorage.GetBlockPlaceEvent() == null) return false;
        return true;
    }

    @Override
    public String GetTaskName() {
        return _taskName;
    }

    @Override
    public int GetTaskId() {
        return 0;
    }
}
