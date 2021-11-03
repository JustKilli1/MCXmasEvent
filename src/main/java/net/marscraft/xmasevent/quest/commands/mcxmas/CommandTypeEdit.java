package net.marscraft.xmasevent.quest.commands.mcxmas;

import net.marscraft.xmasevent.quest.commands.CommandState;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;

import java.sql.ResultSet;

public class CommandTypeEdit extends Commandmanager implements ICommandType {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Player _player;

    public CommandTypeEdit(ILogmanager logger, DatabaseAccessLayer sql, Player player) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _player = player;
    }

    @Override
    public CommandState ExecuteCommand(String[] args) {
        // /mcxmas edit [questID] SetTask [TaskName] [taskspezifische Values]
        int questId = getIntFromStr(args[1]);
        if(questId == 0) return CommandState.CantFindQuestId;
        if(args[2].equalsIgnoreCase("SetTask")){
            return doActionsBasedOnTask(questId, args);
        } else if(args[2].equalsIgnoreCase("SetReward")){
            // /mcxmas edit [questID] SetReward [RewardCommandString]
            String rewardCommandStr = "";
            for(int i = 3; i < args.length; i++) { rewardCommandStr += args[i] + " "; }
            _sql.UpdateRewardCommandString(questId, rewardCommandStr);
            return CommandState.RewardSet;
        } else {
            return CommandState.CommandSyntaxErrorEdit;
        }
    }
    /*
    * KillMobsTask Command:
    * /mcxmas edit [questId] SetTask [TaskName] [neededMobs] [MobType]
    * PlaceBlockTask Command:
    * /mcxmas edit [questId] SetTask [TaskName] [blockType]
    * */



    private CommandState doActionsBasedOnTask(int questId, String[] args) {
        String taskName = args[3];
        if(!isValidTaskName(taskName)) return CommandState.InvalidTaskName;
        String oldTaskName = _sql.GetTaskNameByQuestId(questId);

        int taskId = _sql.GetLastTaskId(taskName) + 1;
        boolean taskExists = _sql.TaskExists(questId, taskName);

        switch (taskName) {
            case "KillMobsTask":
                if(args.length == 6) {
                    int neededMobs = getIntFromStr(args[4]);
                    String entityType = args[5];
                    if(!isValidEntityType(entityType)) return CommandState.InvalidEntityType;
                    if(taskExists) _sql.UpdateKillMobsTask(questId, neededMobs, entityType);
                    else _sql.CreateKillMobsTask(taskId, questId, neededMobs, entityType);
                    _sql.UpdateQuestTaskName(questId, taskName);
                } else {
                    return CommandState.CommandSyntaxErrorEdit;
                }
                if(!taskName.equalsIgnoreCase(oldTaskName)) _sql.DeleteTaskByQuestId(questId, oldTaskName);
                return CommandState.SUCCESS;
            case "PlaceBlockTask":
                if(args.length == 5) {
                    String blockType = args[4];
                    if(!isValidBlock(blockType)) return CommandState.InvalidBlock;
                    if(taskExists) _sql.UpdatePlaceBlockTask(questId, blockType, _player.getLocation());
                    else _sql.CreatePlaceBlockTask(taskId, questId, blockType, _player.getLocation());
                    _sql.UpdateQuestTaskName(questId, taskName);
                } else {
                    return CommandState.CommandSyntaxErrorEdit;
                }
                if(!taskName.equalsIgnoreCase(oldTaskName)) _sql.DeleteTaskByQuestId(questId, oldTaskName);
                return CommandState.SUCCESS;
            default:
                return CommandState.FAILED;
            }
    }
}