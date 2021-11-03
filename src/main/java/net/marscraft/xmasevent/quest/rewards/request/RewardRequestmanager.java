package net.marscraft.xmasevent.quest.rewards.request;

import net.marscraft.xmasevent.quest.rewards.RewardCommand;
import net.marscraft.xmasevent.quest.rewards.rewardtype.IRewardType;
import net.marscraft.xmasevent.quest.rewards.rewardtype.RewardItems;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

public class RewardRequestmanager extends Requestmanager{

    /*
     * BelohnungsHandler:
     * An Request Methode wird Befehls String geschickt. Methode vearbeitet Befehls String und führt einzelne Befehle nacheinander aus
     * Verarbeitung Befehle: 1. split nach '|' --> Einzelbefehle --> 2. split nach ',' --> Einzeloptionen des Befehls
     * Syntax Befehl: [BefehlsName], [BefehlsOption], [BefehlsOption],... | [BefehlsName], [BefehlsOption], [BefehlsOption],...|...
     * */

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Player _player;

    public RewardRequestmanager(ILogmanager logger, DatabaseAccessLayer sql, Player player) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _player = player;
    }
    //Splits String to get Single Commands Format: [RewardType], [RewardOptionHeader1]=[RewardOptionValue1], [RewardOptionHeader2]=[RewardOptionValue2]...
    public RequestState ExecuteReward(String commandStr) {
        ArrayList<String> commands = SplitStringByChar(commandStr, '|');
        for(String command : commands) {
            _player.sendMessage(command);//TODO DEBUG
            RewardCommand rewardCommand = GetProcessedCommand(command);
            //RewardItems, NormalItem=Diamond_Sword 1, CustomItem=[CustomItemId]
            runCommand(rewardCommand);
            for(String key : rewardCommand.GetParamOptions().keySet()){//TODO DEBUG
                _player.sendMessage("RewardCommandCommandOption: " + rewardCommand.GetParamOptions().get(key));

            }
        }
        return RequestState.SUCCESS;
    }
    private RequestState runCommand(RewardCommand rewardCommand) {

        RewardCommand command = rewardCommand;
        Map<String, String> paramOptions = command.GetParamOptions();
        IRewardType rewardType;

            switch (command.GetRewardType().toLowerCase()) {
                case "rewarditems":
                    rewardType = new RewardItems(_logger, _player, command.GetParamOptions());
                    _player.sendMessage(command.GetParamOptions().toString() + " runCommand function worked");//TODO DEBUG
                    break;
                default:
                    return RequestState.InvalidRewardType;
            }
            rewardType.GivePlayerReward();
        return RequestState.SUCCESS;
    }
}
