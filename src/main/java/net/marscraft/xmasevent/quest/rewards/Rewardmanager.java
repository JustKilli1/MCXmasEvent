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

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Player _player;
    private IMessagemanager _messages;

    public Rewardmanager(ILogmanager logger, DatabaseAccessLayer sql, Player player) {
        _logger = logger;
        _sql = sql;
        _player = player;
        _messages = new Messagemanager(_logger, _player);
    }
    //TODO Use Inventorymanager method
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
    //TODO Use Inventorymanager method
    public ArrayList<ItemStack> GetPlayerInventory() {
        ArrayList<ItemStack> playerInventory = new ArrayList<>();

        for(int i = 0; i < 4*9; i++) { playerInventory.add(_player.getInventory().getItem(i)); }
        return playerInventory;
    }
    public boolean GivePlayerQuestReward(int questId) {
        ResultSet rewards = _sql.GetQuestReward(questId);
        ArrayList<RewardState> rewardResults = new ArrayList<>();
        try {
            while (rewards.next()) {
                int rewardId = rewards.getInt("RewardId");
                String rewardName = rewards.getString("RewardName");
                String rewardString = rewards.getString("Reward");

                IRewardType rewardType = GetRewardType(rewardName, rewardId, rewardString);
                rewardResults.add(rewardType.GivePlayerReward());
            }
        } catch (Exception ex) {
            _logger.Error(ex);
            return false;
        }
        int givenCount = 0;
        int notEnoughSpaceInInvCount = 0;
        for(RewardState rewardState : rewardResults) {
            switch (rewardState) {
                case GIVEN:
                    givenCount++;
                case NotEnoughSpaceInInventory:
                    notEnoughSpaceInInvCount++;
            }
        }
        if(givenCount == 0 && notEnoughSpaceInInvCount == 0) return true;
        if(givenCount > 0) {
            _messages.SendPlayerMessage("§c" + givenCount + " Items §aerhalten.");
        } else if(notEnoughSpaceInInvCount > 0){
            _messages.SendPlayerMessage("Du hast zu wenig platz im Inventar um deine Belohnung zu erhalten. §c" + notEnoughSpaceInInvCount + " Items §akönnen mit §c/quests rewards §aabgeholt werden.");
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
}
