package net.marscraft.xmasevent.quest.rewards;

import java.util.ArrayList;
import java.util.HashMap;

public class RewardCommand {

    //Holds RewardType
    private String _rewardType;
    //Holds Param Options (GetItem(key)=Diamond_Sword 1(value))
    private HashMap<String, String> _paramOptions;

    public RewardCommand(String rewardType, HashMap<String, String> paramOptions) {
        _rewardType = rewardType;
        _paramOptions = paramOptions;
    }

    public String GetRewardType() { return _rewardType; }
    public HashMap<String, String> GetParamOptions() { return _paramOptions; }
    public void AddParamOption(String paramKey, String paramOption) { _paramOptions.put(paramKey, paramOption); }
    public void RemoveParamOption(String paramKey) { _paramOptions.remove(paramKey); }

}
