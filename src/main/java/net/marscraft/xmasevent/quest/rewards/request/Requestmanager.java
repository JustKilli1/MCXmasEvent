package net.marscraft.xmasevent.quest.rewards.request;

import net.marscraft.xmasevent.quest.rewards.RewardCommand;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;

import java.util.ArrayList;
import java.util.HashMap;

public class Requestmanager {

    private ILogmanager _logger;
    private String _command, _rewardType;

    public Requestmanager(ILogmanager logger) {
        _logger = logger;
    }

    //RewardItems, NormalItem=Diamond_Sword 1, CustomItem=[CustomItemId]
    public RewardCommand GetProcessedCommand(String command) {
        _command = command;
        ArrayList<String> commandOptions = SplitStringByChar(_command, ',');
        HashMap<String, String> processedCommand = new HashMap<>();

        for(String commandOption : commandOptions) {
            if(!commandOption.contains("=")) {
                    _rewardType = commandOption;
            } else {
                ArrayList<String> commandOptionParts = SplitStringByChar(commandOption, '=');
                if(commandOptionParts.size() != 2) return null;
                else processedCommand.put(commandOptionParts.get(0).replace(" ", ""), commandOptionParts.get(1));
            }
        }
        return new RewardCommand(_rewardType, processedCommand);
    }

    public ArrayList<String> SplitStringByChar(String target, char splitter) {
        char[] targetChars = target.toCharArray();
        ArrayList<String> splitedStr = new ArrayList<>();
        String tmpString = "";

        for(char targetChar : targetChars) {
            if(targetChar == splitter) {
                splitedStr.add(tmpString);
                tmpString = "";
            } else {
                tmpString += targetChar;
            }
        }
        if(tmpString != "") splitedStr.add(tmpString);
        return splitedStr;
    }

}
