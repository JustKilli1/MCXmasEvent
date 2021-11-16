package net.marscraft.xmasevent.quest.rewards;

import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Rewardmanager {

    private ILogmanager _logger;
    private Player _player;

    public Rewardmanager(ILogmanager logger, Player player) {
        _logger = logger;
        _player = player;
    }

    public boolean EnoughSpaceInInventory(int neededSpace, Player player) {
        int space = 0;
        for(ItemStack iStack : getPlayerInventory()) {
            if(iStack == null) {
                space++;
                if (neededSpace == space) return true;
            }
        }
        return false;
    }
    private ArrayList<ItemStack> getPlayerInventory() {
        ArrayList<ItemStack> playerInventory = new ArrayList<>();

        for(int i = 0; i < 4*9; i++) { playerInventory.add(_player.getInventory().getItem(i)); }
        return playerInventory;
    }
}
