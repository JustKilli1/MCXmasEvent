package net.marscraft.xmasevent.quest.commands.mcxmas;

import net.marscraft.xmasevent.Main;
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
    private Main _plugin;
    private IMessagemanager _messages;
    private Questmanager _questmanager;

    public CommandTypeCreate(ILogmanager logger, DatabaseAccessLayer sql, Main plugin, IMessagemanager messages) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _messages = messages;
        _plugin = plugin;
        _questmanager = new Questmanager(_logger, _sql, plugin);
    }

    /*
    * Command: /mcxmas create [TaskName] [QuestName]
    * Creates New Quest based on User Input
    */
    public CommandState ExecuteCommand(String[] args) {
        if(args.length < 3) return CommandState.CommandSyntaxErrorCreate;

        String questName = args[2];
        String taskName = args[1];

        if(!IsValidTaskName(taskName)) return CommandState.InvalidTaskName;
        if(args.length > 3) {
            for (int i = 3; i < args.length; i++) {
                questName += " " + args[i];
            }
        }
        boolean state = _sql.QuestExists(questName);
        if(state) return CommandState.QuestAlreadyExists;

        state = _questmanager.CreateNewQuest(questName, taskName);
        CommandState commandState = state ? CommandState.QuestCreated : CommandState.FAILED;
        return commandState;
    }
}
