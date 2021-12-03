package net.marscraft.xmasevent.quest;

import net.marscraft.xmasevent.quest.task.tasktype.ITaskType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;

public class Quest {

    private ITaskType _taskType;
    private int _questId;
    private String _questName;
    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    public Quest(ILogmanager logger, DatabaseAccessLayer sql, int questId, String questName){
        _logger = logger;
        _sql = sql;
        _questId = questId;
        _questName = questName;
    }
    public Quest (ILogmanager logger, DatabaseAccessLayer sql, int questId, String questName, ITaskType taskType) {
        _logger = logger;
        _sql = sql;
        _questId = questId;
        _questName = questName;
        _taskType = taskType;
    }

    public int GetQuestId() { return _questId; }
    public String GetQuestName() { return _questName; }
    public ITaskType GetTaskType() { return _taskType; }
    public void SetQuestId(int questId) { _questId = questId; }
    public void SetQuestName(String questName) { _questName = questName; }

}
