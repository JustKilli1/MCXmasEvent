package net.marscraft.xmasevent.quest.commands.mcxmas;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.commands.CommandState;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.quest.task.tasktype.*;
import net.marscraft.xmasevent.shared.Inventorys.IInventoryType;
import net.marscraft.xmasevent.shared.Inventorys.InvAdminSetRewards;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import static net.marscraft.xmasevent.quest.commands.CommandState.*;

public class CommandTypeEdit extends Commandmanager implements ICommandType {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;
    private Player _player;

    public CommandTypeEdit(ILogmanager logger, DatabaseAccessLayer sql, Main plugin, Player player) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
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
        } else if(args[2].equalsIgnoreCase("Rewards")){
            // /mcxmas edit [questID] Rewards
            IInventoryType inventoryType = new InvAdminSetRewards(_logger, _sql);
            inventoryType.OpenInventory(_player, questId);
            return SUCCESS;
        } else if(args[2].equalsIgnoreCase("SetSMessage")) {
            // /mcxmas edit [questID] SetSMessage [Starting Message]
            _sql.UpdateQuestMessage(questId, commandStr, "StartMessage");
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
        } else if(args[2].equalsIgnoreCase("SetNpcName")) {
            if(args.length >= 4) {
                _sql.AddQuestNpcName(questId, commandStr);
                return QuestNpcNameSet;
            } else return CommandSyntaxErrorEdit;
        } else {
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
        ITaskType taskType;

        switch (taskName.toLowerCase()) {
            case "killmobstask":
                if(args.length == 7) {
                    int neededMobs = GetIntFromStr(args[4]);
                    if(neededMobs == 0)return InvalidEntityAmount;
                    String mobType = args[5];
                    String mobTypeGer = args[6];
                    if(!IsValidEntityType(mobType)) return InvalidEntityType;
                    taskType = new KillMobsTask(_logger, _sql, questId, taskId, neededMobs, mobType, mobTypeGer);
                    if(!taskType.CreateTask()) return CouldNotCreateTask;
                } else {
                    return CommandSyntaxErrorEdit;
                }
                if(!taskName.equalsIgnoreCase(oldTaskName)) _sql.DeleteTaskByQuestId(questId, oldTaskName);
                return SUCCESS;
            case "placeblocktask":
                if(args.length == 6) {
                    String blockType = args[4];
                    String blockTypeGer = args[5];
                    if(!IsValidBlock(blockType)) return InvalidBlock;
                    Location playerLoc = _player.getLocation();
                    Location blockLoc = new Location(_player.getWorld(), playerLoc.getBlockX(), playerLoc.getBlockY(), playerLoc.getBlockZ());
                    taskType = new PlaceBlockTask(_logger, _sql, _plugin, questId, blockLoc, blockType, blockTypeGer);
                    if(!taskType.CreateTask()) return CouldNotCreateTask;
                } else {
                    return CommandSyntaxErrorEdit;
                }
                if(!taskName.equalsIgnoreCase(oldTaskName)) _sql.DeleteTaskByQuestId(questId, oldTaskName);
                return SUCCESS;
            case "placeblockstask":
                if(args.length == 7) {
                    // /mcxmas edit [questId] SetTask PlaceBlocksTask [BlockType] [BlockTypeGer] [BlockAmount]
                    String blockType = args[4];
                    String blockTypeGer = args[5];
                    if(!IsValidBlock(blockType));
                    int blockAmount = GetIntFromStr(args[6]);
                    taskType = new PlaceBlocksTask(_logger, _sql, _plugin, questId, blockType, blockTypeGer, blockAmount);
                    if(!taskType.CreateTask()) return CouldNotCreateTask;
                } else return CommandSyntaxErrorEdit;
                if(!taskName.equalsIgnoreCase(oldTaskName)) _sql.DeleteTaskByQuestId(questId, oldTaskName);
                return SUCCESS;
            case "breakblockstask":
                // /mcxmas edit [questId] SetTask BreakBlocksTask [BlockType] [BlockTypeGer] [BlockAmount]
                if(args.length == 7) {
                    String blockType = args[4];
                    String blockTypeGer = args[5];
                    if(!IsValidBlock(blockType));
                    int blockAmount = GetIntFromStr(args[6]);
                    taskType = new BreakBlocksTask(_logger, _sql, _plugin, questId, blockType, blockTypeGer, blockAmount);
                    if(!taskType.CreateTask()) return CouldNotCreateTask;
                } else return CommandSyntaxErrorEdit;
                if(!taskName.equalsIgnoreCase(oldTaskName)) _sql.DeleteTaskByQuestId(questId, oldTaskName);
                return SUCCESS;
            default:
                return FAILED;
            }
    }
}
