package net.marscraft.xmasevent.quest;

import net.marscraft.xmasevent.quest.task.Task;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;

public class Quest {

    private Task _task;
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
    public Quest (ILogmanager logger, DatabaseAccessLayer sql, int questId, String questName, Task task) {
        _logger = logger;
        _sql = sql;
        _questId = questId;
        _questName = questName;
        _task = task;
    }

    public int GetQuestId() { return _questId; }
    public String GetQuestName() { return _questName; }
    public void SetQuestId(int questId) { _questId = questId; }
    public void SetQuestName(String questName) { _questName = questName; }

}
