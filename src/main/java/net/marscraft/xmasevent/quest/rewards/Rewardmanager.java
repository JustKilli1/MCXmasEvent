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

    public boolean EnoughSpaceInInventory(int neededSpace) {
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
    public boolean IsValidItem(String item) {
        try {
            Material newMaterial = Material.valueOf(item.toUpperCase());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    public int GetIntFromString(String strInt) {
        try {
            return Integer.parseInt(strInt);
        } catch (Exception ex) {
            return 0;
        }
    }

}