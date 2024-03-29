package net.marscraft.xmasevent.quest.commands.consolecommands;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.Quest;
import net.marscraft.xmasevent.quest.Questmanager;
import net.marscraft.xmasevent.quest.commands.CommandState;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;
import static net.marscraft.xmasevent.quest.commands.CommandState.*;

public class CommandTypeNext extends Commandmanager implements ICommandType {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Player _player;
    private Main _plugin;

    public CommandTypeNext(ILogmanager logger, DatabaseAccessLayer sql, Player player, Main plugin) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _player = player;
        _plugin = plugin;
    }
    /*
    * Command: /setQuest [PlayerName] next
    * Gets Active PlayerQuest, Finish Quest if Task is finished
    */
    @Override
    public CommandState ExecuteCommand(String[] args) {

        int questId = _sql.GetActivePlayerQuestId(_player);
        Questmanager questmanager = new Questmanager(_logger, _sql, _plugin);
        Quest activePlayerQuest = questmanager.GetQuestByQuestId(questId);
        if(activePlayerQuest == null) return NoActiveQuestFound;
        if(activePlayerQuest.GetTaskType().IsTaskFinished(_player)) {
            if(!questmanager.FinishQuest(questId, _player)) return CouldNotFinishQuest;
            if(!questmanager.StartNextQuest(questId, _player)) return CouldNotStartNextQuest;
            return QuestFinished;
        }
        else return TaskNotFinished;
    }
}
