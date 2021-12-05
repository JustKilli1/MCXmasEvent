package net.marscraft.xmasevent.quest.task.tasktype;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.listener.EventStorage;
import net.marscraft.xmasevent.quest.task.Taskmanager;
import net.marscraft.xmasevent.quest.task.tasktype.taskprogressmessages.TaskProgressMessages;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import java.sql.ResultSet;

public class BreakBlocksTask implements ITaskType{

    private String _taskName = "breakblockstask";
    private String _blockType;
    private String _blockTypeGer;
    private int _questId, _blockAmount;
    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;

    public BreakBlocksTask(ILogmanager logger, DatabaseAccessLayer sql, Main plugin, int questId, String blockType, String blockTypeGer, int blockAmount) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
        _questId = questId;
        _blockType = blockType;
        _blockAmount = blockAmount;
        _blockTypeGer = blockTypeGer;
    }

    public BreakBlocksTask(ILogmanager logger, DatabaseAccessLayer sql, Main plugin, int questId) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
        _questId = questId;
        LoadTask();
    }

    @Override
    public boolean CreateTask() {
        boolean taskExists = _sql.TaskExists(_questId, _taskName);
        if(taskExists)
            _sql.UpdateBreakBlocksTask(_questId, _blockAmount, _blockType, _blockTypeGer);
        else
            _sql.CreateBreakBlocksTask(_questId, _blockAmount, _blockType, _blockTypeGer);
        return _sql.UpdateQuestTaskName(_questId, _taskName);
    }

    @Override
    public boolean LoadTask() {
        ResultSet rs = _sql.GetTaskByQuestId(_taskName, _questId);
        try {
            if(!rs.next()) return false;
            _blockType = rs.getString("BlockType");
            _blockTypeGer = rs.getString("BlockTypeGer");
            _blockAmount = rs.getInt("BlockAmount");
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
        BlockBreakEvent event = eventStorage.GetBlockBreakEvent();
        TaskProgressMessages taskMessages = new TaskProgressMessages(_logger, _sql, player);
        int questId = _sql.GetActivePlayerQuestId(player);
        Taskmanager taskmanager = new Taskmanager(_logger, _sql, _plugin);
        Material eventMat = event.getBlock().getType();
        Material dbMat = taskmanager.GetBlocksBlockType(questId, _taskName);
        if(eventMat != dbMat) return false;
        if(!_sql.AddPlayerMobKill(player, questId)) return false;
        int playerProgressValue = _sql.GetPlayerQuestValueInt(player);
        taskMessages.SendQuestValueIntProgressMsg(playerProgressValue, _blockAmount, "Abgebaute " + _blockTypeGer + " blöcke:");
        return true;
    }

    @Override
    public boolean IsTaskFinished(Player player) {
        int questId = _sql.GetActivePlayerQuestId(player);
        int blockAmount = _sql.GetBlocksTaskBlockAmount(questId, _taskName);
        int playerPlacedBlocks = _sql.GetPlayerQuestValueInt(player);
        TaskProgressMessages taskMessages = new TaskProgressMessages(_logger, _sql, player);
        if (playerPlacedBlocks >= blockAmount){
            taskMessages.SendQuestFinishedMsg();
            return true;
        }
        return false;
    }

    @Override
    public boolean IsTaskActive(EventStorage eventStorage) {
        if(eventStorage.GetBlockBreakEvent() == null) return false;
        return true;
    }

    @Override
    public String GetTaskName() {
        return null;
    }

    @Override
    public int GetTaskId() {
        return 0;
    }
}
