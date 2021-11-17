package net.marscraft.xmasevent.quest.commands.mcxmas;

import net.marscraft.xmasevent.quest.commands.CommandState;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.marscraft.xmasevent.quest.commands.CommandState.*;

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
        String commandStr = "";
        for(int i = 3; i < args.length; i++) { commandStr += args[i] + " "; }
        int questId = GetIntFromStr(args[1]);
        if(questId == 0) return CantFindQuestId;
        if(questId > _sql.GetLastQuestId()) return CantFindQuestId;
        if(args[2].equalsIgnoreCase("SetTask")){
            return doActionsBasedOnTask(questId, args);
        } else if(args[2].equalsIgnoreCase("AddReward")){
            // /mcxmas edit [questID] SetReward
            ItemStackSerializer serializer = new ItemStackSerializer(_logger);
            ItemStack reward = _player.getInventory().getItemInMainHand();
            String rewardStr = serializer.ItemStackToBase64(reward);
            if(!_sql.AddNewReward("RewardItems", rewardStr, questId)) return CouldNotSetReward;
            return RewardSet;
        } else if(args[2].equalsIgnoreCase("SetSMessage")) {
            // /mcxmas edit [questID] SetSMessage [Starting Message]
            _sql.UpdateQuestMessage(questId, commandStr, "StartingMessage");
            return StartingMessageSet;
        } else if(args[2].equalsIgnoreCase("SetEMessage")) {
            // /mcxmas edit [questID] SetEMessage [End Message]
            _sql.UpdateQuestMessage(questId, commandStr, "EndMessage");
            return EndMessageSet;
        } else if(args[2].equalsIgnoreCase("SetDescription")) {
            _sql.UpdateQuestMessage(questId, commandStr, "Description");
            return DescriptionSet;
        } else if(args[2].equalsIgnoreCase("SetQOrder")){
            // /mcxmas edit [questID] SetQOrder [new QuestOrder]
            if(args.length != 4)return CommandSyntaxErrorEdit;
            int questOrder = _sql.GetQuestOrder(questId);
            if(!(_sql.UpdateQuestOrder(questId, GetIntFromStr(args[3]), questOrder))) return FAILED;
            return QuestOrderSet;
        } else{
            return CommandSyntaxErrorEdit;
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
        if(!IsValidTaskName(taskName)) return InvalidTaskName;
        String oldTaskName = _sql.GetTaskNameByQuestId(questId);

        int taskId = _sql.GetLastTaskId(taskName) + 1;
        boolean taskExists = _sql.TaskExists(questId, taskName);

        switch (taskName) {
            case "KillMobsTask":
                if(args.length == 7) {
                    int neededMobs = GetIntFromStr(args[4]);
                    if(neededMobs == 0)return InvalidEntityAmount;
                    String entityType = args[5];
                    String entityTypeGer = args[6];
                    if(!IsValidEntityType(entityType)) return InvalidEntityType;
                    if(taskExists) _sql.UpdateKillMobsTask(questId, neededMobs, entityType, entityTypeGer);
                    else _sql.CreateKillMobsTask(taskId, questId, neededMobs, entityType, entityTypeGer);
                    _sql.UpdateQuestTaskName(questId, taskName);
                } else {
                    return CommandSyntaxErrorEdit;
                }
                if(!taskName.equalsIgnoreCase(oldTaskName)) _sql.DeleteTaskByQuestId(questId, oldTaskName);
                return SUCCESS;
            case "PlaceBlockTask":
                if(args.length == 6) {
                    String blockType = args[4];
                    String blockTypeGer = args[5];
                    if(!IsValidBlock(blockType)) return InvalidBlock;
                    if(taskExists) _sql.UpdatePlaceBlockTask(questId, blockType, blockTypeGer, _player.getLocation());
                    else _sql.CreatePlaceBlockTask(taskId, questId, blockType, blockTypeGer, _player.getLocation());
                    _sql.UpdateQuestTaskName(questId, taskName);
                } else {
                    return CommandSyntaxErrorEdit;
                }
                if(!taskName.equalsIgnoreCase(oldTaskName)) _sql.DeleteTaskByQuestId(questId, oldTaskName);
                return SUCCESS;
            default:
                return FAILED;
            }
    }
}
