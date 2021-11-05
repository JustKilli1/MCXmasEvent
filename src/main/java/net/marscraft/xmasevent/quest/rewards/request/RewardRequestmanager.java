package net.marscraft.xmasevent.quest.rewards.request;

import net.marscraft.xmasevent.quest.rewards.RewardCommand;
import net.marscraft.xmasevent.quest.rewards.RewardState;
import net.marscraft.xmasevent.quest.rewards.rewardtype.IRewardType;
import net.marscraft.xmasevent.quest.rewards.rewardtype.RewardItems;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import net.marscraft.xmasevent.shared.messagemanager.Messagemanager;
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
    private IMessagemanager _messages;

    public RewardRequestmanager(ILogmanager logger, DatabaseAccessLayer sql, Player player) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _player = player;
        _messages = new Messagemanager(_logger, _player);
    }
    //Splits String to get Single Commands Format: [RewardType], [RewardOptionHeader1]=[RewardOptionValue1], [RewardOptionHeader2]=[RewardOptionValue2]...
    public RequestState ExecuteReward(String commandStr) {
        ArrayList<String> commands = SplitStringByChar(commandStr, '|');
        for(String command : commands) {
            RewardCommand rewardCommand = GetProcessedCommand(command);
            runCommand(rewardCommand);
        }
        return RequestState.SUCCESS;
    }
    private RequestState runCommand(RewardCommand rewardCommand) {

        RewardCommand command = rewardCommand;
        Map<String, String> paramOptions = command.GetParamOptions();
        IRewardType rewardType;

            switch (command.GetRewardType().replace(" ", "").toLowerCase()) {
                case "rewarditems":
                    rewardType = new RewardItems(_logger, _player, command.GetParamOptions());
                    break;
                default:
                    return RequestState.InvalidRewardType;
            }
        sendMessageBasedOnRewardState(rewardType.GivePlayerReward());
        return RequestState.SUCCESS;
    }
    private void sendMessageBasedOnRewardState(RewardState rewardState) {

        switch (rewardState) {
            case GIVEN:
                _messages.SendPlayerMessage("Belohnung erfolgreich verteilt");
                break;
            case NotEnoughSpaceInInventory:
                _messages.SendPlayerMessage("Du hast nicht genug Platz im Inventar um die Belohnung zu erhalten.");
                break;
        }
    }
}
