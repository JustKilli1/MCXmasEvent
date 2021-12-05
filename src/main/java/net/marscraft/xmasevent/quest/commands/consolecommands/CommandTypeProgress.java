package net.marscraft.xmasevent.quest.commands.consolecommands;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.Quest;
import net.marscraft.xmasevent.quest.Questmanager;
import net.marscraft.xmasevent.quest.commands.CommandState;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.quest.listener.EventStorage;
import net.marscraft.xmasevent.quest.task.tasktype.CollectItemsTask;
import net.marscraft.xmasevent.quest.task.tasktype.ITaskType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;
import static net.marscraft.xmasevent.quest.commands.CommandState.*;

public class CommandTypeProgress extends Commandmanager implements ICommandType {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Player _player;
    private Main _plugin;

    public CommandTypeProgress(ILogmanager logger, DatabaseAccessLayer sql, Player player, Main plugin) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _player = player;
        _plugin = plugin;
    }

    @Override
    public CommandState ExecuteCommand(String[] args) {

        int questId = _sql.GetActivePlayerQuestId(_player);
        if(questId == 0) return NoActiveQuestFound;
        String taskName = _sql.GetTaskNameByQuestId(questId);
        if(taskName.equalsIgnoreCase("CollectItemsTask")) {
            ITaskType taskType = new CollectItemsTask(_logger, _sql, _plugin, questId);
            EventStorage eventStorage = new EventStorage();
            eventStorage.SetCommandArgs(args);
            if (taskType.ExecuteTask(eventStorage, _player)) return TaskExecuted;
        }
        Questmanager questmanager = new Questmanager(_logger, _sql, _plugin);
        Quest activePlayerQuest = questmanager.GetQuestByQuestId(questId);
        if(activePlayerQuest == null) return NoActiveQuestFound;
        if(!(activePlayerQuest.GetTaskType().IsTaskFinished(_player))) return TaskNotFinished;
        if(_sql.PlayerQuestFinished(_player)) {
            int lastQuestId = _sql.GetLastQuestId();
            if(questId == lastQuestId) return NoMoreQuestsFound;
            if(!questmanager.StartNextQuest(questId, _player)) return CouldNotStartNextQuest;
            if(!_sql.SetPlayerQuestFinished(_player, false)) return CouldNotUpdateQuestFinished;
            return NextQuestStarted;
        } else {
            if (!questmanager.FinishQuest(questId, _player)) return CouldNotFinishQuest;
            if(!_sql.SetPlayerQuestFinished(_player, true)) return CouldNotUpdateQuestFinished;
            return QuestFinished;
        }
    }
}
