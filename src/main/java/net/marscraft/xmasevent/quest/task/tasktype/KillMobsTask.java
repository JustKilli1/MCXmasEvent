package net.marscraft.xmasevent.quest.task.tasktype;


import net.marscraft.xmasevent.quest.task.tasktype.ITaskType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;

import java.sql.ResultSet;

public class KillMobsTask implements ITaskType{

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private int _questId, _taskId;
    private int _playerMobKills = -1;
    private String _taskName = "killmobstask";
    private String _mobType;


    public KillMobsTask(ILogmanager logger, DatabaseAccessLayer sql, int questId) {
        _logger = logger;
        _sql = sql;
        _questId = questId;
    }

    public KillMobsTask(ILogmanager logger, DatabaseAccessLayer sql, int questId, int playerMobKills, String mobType) {
        _logger = logger;
        _sql = sql;
        _questId = questId;
        _playerMobKills = playerMobKills;
        _mobType = mobType;
    }


    @Override
    public boolean InitTask() {

        if(_playerMobKills == -1) return false;

        int newTaskId = _sql.GetLastTaskId(_taskName) + 1;
        return _sql.CreateKillMobsTask(newTaskId, _questId, _playerMobKills, _mobType);
    }

    @Override
    public boolean IsTaskFinished() {

        if(_playerMobKills == 0) return false;

        ResultSet rs = _sql.GetTaskByQuestId("KillMobsTask", _questId);

        try{
            if(!rs.next())return false; //TODO Fehlerbehandlung Task abbrechen
            if(rs.getInt("NeededMobs") == _playerMobKills) return true;
        } catch(Exception ex) {
            _logger.Error(ex);
        }

        return false;
    }

    @Override
    public String GetTaskName() { return _taskName; }

    @Override
    public int GetTaskId() { return _taskId; }

}
