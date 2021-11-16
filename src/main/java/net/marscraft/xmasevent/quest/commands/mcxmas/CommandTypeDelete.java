package net.marscraft.xmasevent.quest.commands.mcxmas;

import net.marscraft.xmasevent.quest.commands.CommandState;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;

import static net.marscraft.xmasevent.quest.commands.CommandState.*;

public class CommandTypeDelete extends Commandmanager implements ICommandType {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;

    public CommandTypeDelete(ILogmanager logger, DatabaseAccessLayer sql) {
        super(logger);
        _logger = logger;
        _sql = sql;
    }

    @Override
    public CommandState ExecuteCommand(String[] args) {
        if(args.length != 2) return CommandSyntaxErrorDelete;
        int questId = GetIntFromStr(args[1]);
        int questOrder = _sql.GetQuestOrder(questId);
        if(questOrder == 0) return CantFindQuestId;
        if(questOrder == _sql.GetLastQuestOrder()) questOrder -= 1;
        String taskName = _sql.GetTaskNameByQuestId(questId);
        if(!_sql.DeleteTaskByQuestId(questId, taskName)) return CouldNotDeleteTask;
        if(!_sql.DeleteQuestFromQuestsTable(questId)) return CouldNotDeleteQuestFromQuestTable;
        if(!_sql.DeleteQuestRewards(questId)) return CouldNotDeleteQuestRewards;
        if(!_sql.UpdateQuestOrder(0, _sql.GetLastQuestOrder(), questOrder)) return CouldNotUpdateQuestOrder;
        if(!_sql.UpdateALlQuestIds()) return CouldNotUpdateQuestIds;
        return SUCCESS;
    }
}
