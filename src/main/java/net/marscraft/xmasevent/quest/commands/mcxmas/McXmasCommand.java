package net.marscraft.xmasevent.quest.commands.mcxmas;

import net.marscraft.xmasevent.quest.commands.CommandState;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import net.marscraft.xmasevent.shared.messagemanager.Messagemanager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class McXmasCommand implements CommandExecutor {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private IMessagemanager _messages;
    private ICommandType _commandType;

    public McXmasCommand(ILogmanager logger, DatabaseAccessLayer sql) {
        _logger = logger;
        _sql = sql;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        _messages = new Messagemanager(_logger, player);

        if(args[0].equalsIgnoreCase("create")) {
            _commandType = new CommandTypeCreate(_logger, _sql, _messages);
            commandStateActions(_commandType.ExecuteCommand(args), args);
        } else if(args[0].equalsIgnoreCase("quests")) {
            if(args[1].equalsIgnoreCase("list")) {
                _commandType = new CommandTypeList(_logger, _sql, _messages);
                _commandType.ExecuteCommand(args);
            }
        } else if(args[0].equalsIgnoreCase("edit")) {
            _commandType = new CommandTypeEdit(_logger, _sql, player);
            CommandState cState = _commandType.ExecuteCommand(args);
            commandStateActions(cState, args);
        }
        return false;
    }
    private void commandStateActions(CommandState commandState, String[] args) {

        String command = "";
        for(int i = 0; i < args.length; i++){ command += args[i] + " "; }

        switch (commandState) {
            case SUCCESS:
                _messages.SendPlayerMessage("Command §c" + command + " §aerfolgreich ausgeführt!");
                break;
            case FAILED:
                _messages.SendErrorMessage("Command: §c" + command + " §akonnte nicht ausgeführt werden!");
                break;
            case QuestCreated:
                _messages.SendPlayerMessage("Quest §c" + getQuestName(args) + " §awurde erstellt");
                break;
            case QuestAlreadyExists:
                _messages.SendPlayerMessage("Der Quest mit dem Namen §c" + getQuestName(args) + " §aexistiert bereits!");
                break;
            case CommandSyntaxErrorCreate:
                _messages.SendPlayerMessage("Syntax fehler benutze: §c/mcxmas create [Task Name] [Quest Name]");
                break;
            case CommandSyntaxErrorEdit:
                _messages.SendPlayerMessage("Syntax fehler benutze: §c/mcxmas edit [Quest Id] [SetTask] [Task Id] [Taskspezifische angaben]...");
                break;
            case CommandSyntaxErrorQuests:
                _messages.SendPlayerMessage("Syntax fehler benutze: §c/mcxmas quests list");
                break;
            case QuestIdWrongFormat:
                _messages.SendPlayerMessage("Bitte gebe eine gültige §cQuestId§a an.");
            case CantFindQuestId:
                _messages.SendPlayerMessage("Die Quest Id §c" + args[1] + " §aexistiert nicht.");
                break;
            case InvalidTaskName:
                _messages.SendPlayerMessage("Der Task mit dem Namen §c" + args[1] + " §aexistiert nicht!");
                break;
            case InvalidBlock:
                _messages.SendPlayerMessage("Der Block §c" + args[4] + " §aist kein gültiger Block");
                break;
            case InvalidEntityType:
                _messages.SendPlayerMessage("Der Mob §c" + args[5] + " §aist kein gültiger Mob");
                break;
        }
    }
    private String getQuestName(String[] args) {
        String questName = args[2];
        for(int i = 3; i < args.length; i++) { questName += " " + args[i]; }
        return questName;
    }
}
