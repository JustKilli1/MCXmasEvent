package net.marscraft.xmasevent.quest.commands.mcxmas;

import net.marscraft.xmasevent.quest.Quest;
import net.marscraft.xmasevent.quest.Questmanager;
import net.marscraft.xmasevent.quest.commands.CommandState;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;

public class CommandTypeCreate extends Commandmanager implements ICommandType {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private IMessagemanager _messages;
    private Quest _quest;
    private Questmanager _questmanager;

    public CommandTypeCreate(ILogmanager logger, DatabaseAccessLayer sql, IMessagemanager messages) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _messages = messages;
        _questmanager = new Questmanager(_logger, _sql);
    }

    public CommandState ExecuteCommand(String[] args) {
        if(args.length < 3) return CommandState.CommandSyntaxErrorCreate;

        String questName = args[2];
        String taskName = args[1];

        if(!isValidTaskName(taskName)) return CommandState.InvalidTaskName;

        for(int i = 3; i < args.length; i++) {
            questName += " " + args[i];
        }

        boolean state = _sql.QuestExists(questName);
        if(state) return CommandState.QuestAlreadyExists;

        state = _questmanager.CreateNewQuest(questName, taskName);
        CommandState commandState = state ? CommandState.QuestCreated : CommandState.FAILED;
        return commandState;
    }
}
