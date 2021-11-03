package net.marscraft.xmasevent.quest.rewards.request;

import net.marscraft.xmasevent.quest.rewards.request.RequestState;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;

public class RewardRequestmanager {

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
        _logger = logger;
        _sql = sql;
        _player = player;
    }

    public RequestState ExecuteReward(String commandStr) {
        String[] commands = commandStr.split("|");
        for(String command : commands) {
            //RewardItems, NormalItem=Diamond_Sword 1, CustomItem=[CustomItemId]
            String[] commandOptions = command.split(",");
            if(!isValidRewardType(commandOptions[0])) return RequestState.FAILED;
            for(int i = 1; i < commandOptions.length; i++) {
                runCommand(commandOptions[i]);
            }
        }
        return RequestState.FAILED;
    }
    private boolean isValidRewardType(String rewardType) {
        switch (rewardType) {
            case "RewardItems":
                return true;
            default:
                return false;
        }
    }
    private RequestState runCommand(String command) {
        String[] commandParts = command.split("=");
        if(commandParts.length != 2) return RequestState.CommandSyntaxError;



        return RequestState.FAILED;
    }

}
