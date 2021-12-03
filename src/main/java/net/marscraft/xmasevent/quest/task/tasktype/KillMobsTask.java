package net.marscraft.xmasevent.quest.task.tasktype;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.Questmanager;
import net.marscraft.xmasevent.quest.listener.EventStorage;
import net.marscraft.xmasevent.quest.task.Taskmanager;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import java.sql.ResultSet;

import static net.marscraft.xmasevent.quest.commands.CommandState.InvalidEntityAmount;
import static net.marscraft.xmasevent.quest.commands.CommandState.InvalidEntityType;

public class KillMobsTask implements ITaskType{

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;
    private int _questId, _taskId, _mobs;
    private String _taskName = "killmobstask";
    private String _mobType, _mobTypeGer;

    public KillMobsTask(ILogmanager logger, DatabaseAccessLayer sql, int questId, int taskId, int mobs, String mobType, String mobTypeGer) {
        _logger = logger;
        _sql = sql;
        _questId = questId;
        _taskId = taskId;
        _mobs = mobs;
        _mobType = mobType;
        _mobTypeGer = mobTypeGer;
    }

    public KillMobsTask(ILogmanager logger, DatabaseAccessLayer sql, Main plugin, int questId) {
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
            _sql.UpdateKillMobsTask(_questId, _mobs, _mobType, _mobTypeGer);
        else
            _sql.CreateKillMobsTask(_taskId, _questId, _mobs, _mobType, _mobTypeGer);
        _sql.UpdateQuestTaskName(_questId, _taskName);
        return true;
    }

    @Override
    public boolean LoadTask() {
        ResultSet rs = _sql.GetTaskByQuestId(_taskName, _questId);
        try {
            if(!rs.next()) return false;
            _mobs = rs.getInt("NeededMobs");
            _mobType = rs.getString("MobType");
            _mobTypeGer = rs.getString("MobTypeGer");
            return true;
        } catch (Exception ex) {
            _logger.Error(ex);
            return false;
        }
    }

    @Override
    public boolean ExecuteTask(EventStorage eventStorage, Player player) {
        EntityDeathEvent event = eventStorage.GetEntityDeathEvent();
        Taskmanager taskmanager = new Taskmanager(_logger, _sql, _plugin);
        int questId = _sql.GetActivePlayerQuestId(player);
        EntityType eType = taskmanager.GetKillMobsTaskMobType(questId);

        if(eType == null){
            _logger.Error("EntityType des Task KillMobs konnte nicht geladen werden. Bitte Datenbank überprüfen");
            _logger.Error("QuestId: " + questId);
            return false;
        }
        if(event.getEntityType() == eType){
            if(!IsTaskFinished(player))
                _sql.AddPlayerMobKill(player, questId);
            return true;
        }
        return false;
    }

    @Override
    public boolean IsTaskFinished(Player player) {

        if(_mobs == 0) return false;

        ResultSet rs = _sql.GetTaskByQuestId(_taskName, _questId);
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
