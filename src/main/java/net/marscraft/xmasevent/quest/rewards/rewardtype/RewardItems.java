package net.marscraft.xmasevent.quest.rewards.rewardtype;

import net.marscraft.xmasevent.quest.rewards.RewardState;
import net.marscraft.xmasevent.quest.rewards.Rewardmanager;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class RewardItems extends Rewardmanager implements IRewardType{

    private ILogmanager _logger;
    private Player _player;
    private ArrayList<ItemStack> _rewards;

    public RewardItems(ILogmanager logger, Player player, ArrayList<ItemStack> rewards) {
        super(logger, player);
        _logger = logger;
        _player = player;
        _rewards = rewards;
    }

    @Override
    public RewardState GivePlayerReward() {
        if(!EnoughSpaceInInventory(_rewards.size())) return RewardState.NotEnoughSpaceInInventory;
        for(ItemStack reward : _rewards) { _player.getInventory().addItem(reward); }

        return RewardState.GIVEN;
    }
}
