package net.marscraft.xmasevent.quest.commands.mcxmas;

import net.marscraft.xmasevent.quest.commands.CommandState;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import java.sql.ResultSet;

public class CommandTypeList extends Commandmanager implements ICommandType {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private IMessagemanager _messages;

    public CommandTypeList(ILogmanager logger, DatabaseAccessLayer sql, IMessagemanager messages) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _messages = messages;
    }
    /*
    * Command: /mcxmas quests list
    * Gets all quests and displays them
    */
    @Override
    public CommandState ExecuteCommand(String[] args) {

        ResultSet rs = _sql.GetAllQuests();
        _messages.SendQuestList(rs);

        return CommandState.SUCCESS;
    }
}
