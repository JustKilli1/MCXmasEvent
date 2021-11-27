package net.marscraft.xmasevent.quest.task.tasktype;

import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlaceBlockTask implements ITaskType {

    private final String _taskName = "placeblocktask";
    private final String _blockType;
    private final String _blockTypeGer;
    private final int _questId;
    private final Location _blockLoc;
    private final ILogmanager _logger;
    private final DatabaseAccessLayer _sql;

    public PlaceBlockTask(ILogmanager logger, DatabaseAccessLayer sql, int questId, String blockType, String blockTypeGer, Location blockLoc) {
        _logger = logger;
        _sql = sql;
        _questId = questId;
        _blockType = blockType;
        _blockTypeGer = blockTypeGer;
        _blockLoc = blockLoc;
    }

    @Override
    public boolean InitTask() {
        int newTaskId = _sql.GetLastTaskId(_taskName) + 1;
        return _sql.CreatePlaceBlockTask(newTaskId, _questId, _blockType, _blockTypeGer, _blockLoc);
    }

    @Override
    public boolean IsTaskFinished(Player player) { return _sql.GetPlayerQuestValueBool(player); }

    @Override
    public String GetTaskName() {
        return null;
    }

    @Override
    public int GetTaskId() {
        return 0;
    }
}
