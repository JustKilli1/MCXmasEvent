package net.marscraft.xmasevent.quest;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.rewards.request.RewardRequestmanager;
import net.marscraft.xmasevent.quest.task.Taskmanager;
import net.marscraft.xmasevent.quest.task.tasktype.ITaskType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;

import java.sql.ResultSet;

public class Questmanager {

    private  ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;
    private Taskmanager _taskmanager;

    public Questmanager(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
        Taskmanager _taskmanager = new Taskmanager(_logger, _sql, _plugin);
    }

    public boolean CreateNewQuest(String questName, String taskName) {
        Quest quest = new Quest(_logger, _sql, _sql.GetLastQuestId() + 1, questName);

        if(_sql.QuestExists(questName)) return false;

        return _sql.AddNewQuestToDatabase(quest, taskName);
    }

    public boolean FinishQuest(int questId, Player player) {
        String rewardString = _sql.GetRewardCommand(questId);
        RewardRequestmanager requestmanager = new RewardRequestmanager(_logger, _sql, player);
        requestmanager.ExecuteReward(rewardString);
        if(!_sql.ResetProgressValues(questId))return false;
        if(!_sql.SetNextPlayerQuest(player.getUniqueId().toString(), questId))return false;
        return false;
    }

    public Quest GetQuestByQuestId(int questId) {

        try {
            ResultSet questRS = _sql.GetQuest(questId);
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

    /*
     * Updates QuestId From Player in PlayerProgressDatabase TODO --> Anhand QuestId wird Task ermittelt
     * */
    public boolean UpdatePlayerQuestId() {

        return false;
    }

    /*
     * Searches for the Next Quest based on QuestOrder
     * */
    private int findNextQuest(int currentQuestId) {

        return 0;
    }

    public Taskmanager GetTaskManager() { return  _taskmanager; }

}
