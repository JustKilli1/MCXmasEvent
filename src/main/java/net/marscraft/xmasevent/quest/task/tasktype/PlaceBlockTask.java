package net.marscraft.xmasevent.quest.task.tasktype;

import net.marscraft.xmasevent.quest.rewards.request.RewardRequestmanager;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlaceBlockTask implements ITaskType {

    private String _taskName = "placeblocktask";
    private String _blockType;
    private int _questId;
    private Location _blockLoc;
    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;

    public PlaceBlockTask(ILogmanager logger, DatabaseAccessLayer sql, int questId, String blockType, Location blockLoc) {
        _logger = logger;
        _sql = sql;
        _questId = questId;
        _blockType = blockType;
        _blockLoc = blockLoc;
    }

    @Override
    public boolean InitTask() {
        int newTaskId = _sql.GetLastTaskId(_taskName) + 1;
        return _sql.CreatePlaceBlockTask(newTaskId, _questId, _blockType, _blockLoc);
    }

    @Override
    public boolean IsTaskFinished() {
        return false;
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
