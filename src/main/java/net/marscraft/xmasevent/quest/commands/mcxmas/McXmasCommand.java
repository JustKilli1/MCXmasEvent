package net.marscraft.xmasevent.quest.commands.mcxmas;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.commands.CommandState;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
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
import java.sql.ResultSet;
import static net.marscraft.xmasevent.quest.commands.CommandState.*;

public class McXmasCommand extends Commandmanager implements CommandExecutor {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;
    private IMessagemanager _messages;
    private ICommandType _commandType;

    public McXmasCommand(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
    }
    /*
     * Command: /mcxmas [args]
     * Handles /mcxmas Command
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        _messages = new Messagemanager(_logger, player);
        int questId = 0;
        if(!args[0].equalsIgnoreCase("create") && !args[0].equalsIgnoreCase("quests")) {
            questId = GetIntFromStr(args[1]);
            if (questId == 0) {
                commandStateActions(CantFindQuestId, args);
                return false;
            }
            if (questId > _sql.GetLastQuestId()) {
                commandStateActions(CantFindQuestId, args);
                return false;
            }
        }
        if(args.length == 0) {
            commandStateActions(CommandState.CommandSyntaxError, args);
            return false;
        }
        if(args[0].equalsIgnoreCase("create")) {
            _commandType = new CommandTypeCreate(_logger, _sql, _plugin, _messages);
            commandStateActions(_commandType.ExecuteCommand(args), args);
            return true;
        } else if(args[0].equalsIgnoreCase("quests")) {
            if(args.length != 2) {
                commandStateActions(CommandSyntaxErrorQuests, args);
                return false;
            }
            if(args[1].equalsIgnoreCase("list")) {
                _commandType = new CommandTypeList(_logger, _sql, _messages);
                _commandType.ExecuteCommand(args);
                return true;
            }
        } else if(args[0].equalsIgnoreCase("edit")) {
            if(args.length < 3){
                commandStateActions(CommandSyntaxErrorEdit, args);
                return false;
            }
            _commandType = new CommandTypeEdit(_logger, _sql, _plugin, player);
            commandStateActions(_commandType.ExecuteCommand(args), args);
            if(questSetupFinished(args)) _sql.QuestSetupFinished(questId);
            return true;
        } else if(args[0].equalsIgnoreCase("delete")){
            _commandType = new CommandTypeDelete(_logger, _sql);
            commandStateActions(_commandType.ExecuteCommand(args), args);
        } else {
            commandStateActions(CommandSyntaxError, args);
            return false;
        }
        return false;
    }

    /*
    * Checks if QuestSetup finished when finished set QuestSetupFinished true in db
    */
    private boolean questSetupFinished(String[] args) {

        Commandmanager cm = new Commandmanager(_logger);
        int questId = cm.GetIntFromStr(args[1]);
        if(questId == 0) return false;
        String taskName = _sql.GetTaskNameByQuestId(questId);
        ResultSet rs = _sql.GetTaskByQuestId(taskName, questId);
        try {
            if (!rs.next()) return false;
        } catch (Exception ex) {
            _logger.Error(ex);
            return false;
        }
        return _sql.GetQuestRewardStr(questId).size() != 0;
    }

    /*
     * Sends Player Message based on commandState
     */
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
                _messages.SendPlayerMessage("Quest §c" + getArgsString(args, 2) + " §awurde erstellt");
                break;
            case QuestAlreadyExists:
                _messages.SendPlayerMessage("Der Quest mit dem Namen §c" + getArgsString(args, 2) + " §aexistiert bereits!");
                break;
            case CommandSyntaxError:
                _messages.SendPlayerMessage("Syntax fehler benutze: §c/mcxmas [create,edit,quests]");
                break;
            case CommandSyntaxErrorCreate:
                _messages.SendPlayerMessage("Syntax fehler benutze: §c/mcxmas create [Task Name] [Quest Name]");
                break;
            case CommandSyntaxErrorEdit:
                _messages.SendPlayerMessage("Syntax fehler benutze: §c/mcxmas edit [Quest Id] [SetTask, SetReward] [Task Id] [Taskspezifische angaben]...");
                break;
            case CommandSyntaxErrorQuests:
                _messages.SendPlayerMessage("Syntax fehler benutze: §c/mcxmas quests list");
                break;
            case CommandSyntaxErrorDelete:
                _messages.SendPlayerMessage("Syntax fehler benutze: §c/mcxmas delete [questId]");
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
            case InvalidEntityAmount:
                _messages.SendPlayerMessage("§c" + args[4] + " §aist keine gültige Zahl");
                break;
            case RewardSet:
                _messages.SendPlayerMessage("Reward §cerfolgreich §agesetzt.");
                break;
            case StartingMessageSet:
                _messages.SendPlayerMessage("Anfangs Nachricht des Quests wurde erfolgreich gesetzt.");
                break;
            case EndMessageSet:
                _messages.SendPlayerMessage("Beendigungs Nachricht des Quests wurde erfolgreich gesetzt.");
                break;
            case QuestOrderSet:
                _messages.SendPlayerMessage("QuestOrder wurde erfolgreich gesetzt.");
                break;
            case CouldNotDeleteTask:
                _messages.SendPlayerMessage("Es ist ein Fehler beim löschen der Quest aufgetreten: Task konnte nicht gelöscht werden." );
                break;
            case CouldNotDeleteQuestFromQuestTable:
                _messages.SendPlayerMessage("Es ist ein Fehler beim löschen der Quest aufgetreten: Quest konnte nicht gelöscht werden." );
                break;
            case CouldNotUpdateQuestIds:
                _messages.SendPlayerMessage("Es ist ein Fehler beim löschen der Quest aufgetreten: QuestIds konnten nicht geupdatet werden." );
                break;
            case CouldNotUpdateQuestOrder:
                _messages.SendPlayerMessage("Es ist ein Fehler beim löschen der Quest aufgetreten: QuestOrder konnte nicht geupdatet werden." );
                break;
            case DescriptionSet:
                _messages.SendPlayerMessage("Beschreibung Gesetzt.");
            case CouldNotSetReward:
                _messages.SendPlayerMessage("Reward konnte nicht gesetzt werden!");
            case QuestNpcNameSet:
                _messages.SendPlayerMessage("Npc Name gesetzt.");
        }
    }
    private String getArgsString(String[] args, int starting) {
        if(starting >= args.length) return null;
        String argsString = args[starting];
        for(int i = starting + 1; i < args.length; i++) { argsString += " " + args[i]; }
        return argsString;
    }
}
