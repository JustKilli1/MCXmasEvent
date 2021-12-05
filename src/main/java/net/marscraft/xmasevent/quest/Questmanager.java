package net.marscraft.xmasevent.quest;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.rewards.Rewardmanager;
import net.marscraft.xmasevent.quest.task.Taskmanager;
import net.marscraft.xmasevent.quest.task.tasktype.ITaskType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.Messagemanager;
import org.bukkit.entity.Player;
import java.sql.ResultSet;

public class Questmanager {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;
    private Taskmanager _taskmanager;

    public Questmanager(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
        _taskmanager = new Taskmanager(_logger, _sql, _plugin);
    }

    public boolean CreateNewQuest(String questName, String taskName) {
        Quest quest = new Quest(_logger, _sql, _sql.GetLastQuestId() + 1, questName);

        if(_sql.QuestExists(questName)) return false;
        return _sql.AddNewQuestToDatabase(quest, taskName);
    }

    public boolean FinishQuest(int questId, Player player) {
        Rewardmanager rewardmanager = new Rewardmanager(_logger, _sql, player);
        if(!(rewardmanager.GivePlayerQuestReward(questId))) return false;
        if(!_sql.SetPlayerQuestFinished(player, true)) return false;
        String endMessage = _sql.GetQuestMessage(questId, "EndMessage");
        String npcName = _sql.GetQuestNpcName(questId);
        Messagemanager messagemanager = new Messagemanager(_logger, player);
        messagemanager.SendNpcMessage(npcName, endMessage);
        return true;
    }
    public boolean StartNextQuest(int questId, Player player) {
        if(!_sql.ResetProgressValues(questId))return false;
        if(!_sql.SetNextPlayerQuest(player.getUniqueId().toString(), questId)) return false;
        int nextQuestId = _sql.GetActivePlayerQuestId(player);
        String startMessage = _sql.GetQuestMessage(nextQuestId, "StartMessage");
        String npcName = _sql.GetQuestNpcName(questId);
        Messagemanager messagemanager = new Messagemanager(_logger, player);
        messagemanager.SendNpcMessage(npcName, startMessage);
        return true;
    }

    public Quest GetQuestByQuestId(int questId) {

        try {
            ResultSet questRS = _sql.GetQuest(questId);
            if(questRS == null) return null;
            if(!questRS.next())return null;

            String taskName = questRS.getString("TaskName");
            String questName = questRS.getString("QuestName");

            ITaskType taskType = _taskmanager.GetTaskTypeByName(questId, taskName);

            return new Quest(_logger, _sql, questId, questName, taskType);
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }

    }

    public Taskmanager GetTaskManager() { return  _taskmanager; }

}
