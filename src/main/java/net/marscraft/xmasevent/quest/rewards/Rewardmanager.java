package net.marscraft.xmasevent.quest.rewards;

import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Rewardmanager {

    private ILogmanager _logger;
    private Player _player;

    public Rewardmanager(ILogmanager logger, Player player) {
        _logger = logger;
        _player = player;
    }

    public boolean EnoughSpaceInInventory(int neededSpace) {
        int space = 0;
        for(ItemStack iStack : _player.getInventory().getContents()) {
            if(iStack.getItemMeta().getLocalizedName() == "Air") space++;
            if(neededSpace == space) return true;
        }
        return false;
    }

}
