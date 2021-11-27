package net.marscraft.xmasevent.quest.rewards;

import net.marscraft.xmasevent.quest.rewards.rewardtype.IRewardType;
import net.marscraft.xmasevent.quest.rewards.rewardtype.RewardItems;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import net.marscraft.xmasevent.shared.messagemanager.Messagemanager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.util.ArrayList;

public class Rewardmanager {

    private final ILogmanager _logger;
    private final DatabaseAccessLayer _sql;
    private final Player _player;
    private final IMessagemanager _messages;

    public Rewardmanager(ILogmanager logger, DatabaseAccessLayer sql, Player player) {
        _logger = logger;
        _sql = sql;
        _player = player;
        _messages = new Messagemanager(_logger, _player);
    }

    public boolean EnoughSpaceInInventory(int neededSpace) {
        int space = 0;
        for(ItemStack iStack : GetPlayerInventory()) {
            if(iStack == null) {
                space++;
                if (neededSpace == space) return true;
            }
        }
        return false;
    }
    public ArrayList<ItemStack> GetPlayerInventory() {
        ArrayList<ItemStack> playerInventory = new ArrayList<>();

        for(int i = 0; i < 4*9; i++) { playerInventory.add(_player.getInventory().getItem(i)); }
        return playerInventory;
    }
    public boolean GivePlayerQuestReward(int questId) {
        ResultSet rewards = _sql.GetQuestReward(questId);
        try {
            while (rewards.next()) {
                int rewardId = rewards.getInt("RewardId");
                String rewardName = rewards.getString("RewardName");
                String rewardString = rewards.getString("Reward");

                IRewardType rewardType = GetRewardType(rewardName, rewardId, rewardString);
                rewardStateActions(rewardType.GivePlayerReward());
            }
        } catch (Exception ex) {
            _logger.Error(ex);
            return false;
        }
        return true;
    }
    public IRewardType GetRewardType(String rewardName,int rewardId, String rewardString) {
        IRewardType rewardType;
        switch (rewardName) {
            case "RewardItems":
                rewardType = new RewardItems(_logger, _sql, _player, rewardId, rewardString);
                return rewardType;
            default:
                return null;
        }
    }
    private boolean rewardStateActions(RewardState rewardState) {
        switch (rewardState) {
            case GIVEN:
                _messages.SendPlayerMessage("Du hast eine §cBelohnung §aerhalten");
                return true;
            case NotEnoughSpaceInInventory:
                _messages.SendPlayerMessage("Du hast zu wenig platz im Inventar. Mit §c/quests rewards §akannst du deine Belohnung abholen");
                return true;
            case CouldNotAddUnclaimedPlayerReward:
                _logger.Error("Could not Give Player QuestReward");
                return false;
            default:
                return false;
        }
    }
}
