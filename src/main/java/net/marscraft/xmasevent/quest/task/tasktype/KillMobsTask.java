package net.marscraft.xmasevent.quest.task.tasktype;

import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;

import java.sql.ResultSet;

public class KillMobsTask implements ITaskType{

    private final ILogmanager _logger;
    private final DatabaseAccessLayer _sql;
    private final int _questId;
    private int _taskId;
    private int _mobs = -1;
    private final String _taskName = "killmobstask";
    private final String _mobType;
    private final String _mobTypeGer;

    public KillMobsTask(ILogmanager logger, DatabaseAccessLayer sql, int questId, int mobs, String mobType, String mobTypeGer) {
        _logger = logger;
        _sql = sql;
        _questId = questId;
        _mobs = mobs;
        _mobType = mobType;
        _mobTypeGer = mobTypeGer;
    }


    @Override
    public boolean InitTask() {

        if(_mobs == -1) return false;

        int newTaskId = _sql.GetLastTaskId(_taskName) + 1;
        return _sql.CreateKillMobsTask(newTaskId, _questId, _mobs, _mobType, _mobTypeGer);
    }

    @Override
    public boolean IsTaskFinished(Player player) {

        if(_mobs == 0) return false;

        ResultSet rs = _sql.GetTaskByQuestId("KillMobsTask", _questId);
        int playerProgress = _sql.GetPlayerQuestValueInt(player);

        try{
            if(!rs.next())return false; //TODO Fehlerbehandlung Task abbrechen
            int neededMobs = rs.getInt("NeededMobs");
            if(neededMobs == playerProgress)return true;
            if(neededMobs == playerProgress + 1){
                _sql.AddPlayerMobKill(player, _sql.GetActivePlayerQuestId(player));
                return true;
            }
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
