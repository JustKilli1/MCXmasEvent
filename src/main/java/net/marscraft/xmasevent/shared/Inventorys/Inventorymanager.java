package net.marscraft.xmasevent.shared.Inventorys;

import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class Inventorymanager {

    private ILogmanager _logger;

    public Inventorymanager(ILogmanager logger) {
        _logger = logger;
    }

    public ItemStack AddDataToItemStack(ItemStack target, NamespacedKey key, int data) {
        if(target == null) return null;
        ItemMeta iMeta = target.getItemMeta();
        iMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, data);
        target.setItemMeta(iMeta);
        return target;
    }

    public ItemStack RemoveDataFromItemStack(ItemStack target, NamespacedKey key) {
        if(target == null) return null;
        ItemMeta iMeta = target.getItemMeta();
        iMeta.getPersistentDataContainer().remove(key);
        target.setItemMeta(iMeta);
        return target;
    }
    public boolean RemoveItemStackFromInventory(Inventory targetInv, ItemStack targetItem) {
        targetInv.removeItem(targetItem);
        return true;
    }
    public boolean EnoughSpaceInInventory(int neededSpace, Player player) {
        int space = 0;
        for(ItemStack iStack : GetPlayerInventory(player)) {
            if(iStack == null) {
                space++;
                if (neededSpace == space) return true;
            }
        }
        return false;
    }
    public ArrayList<ItemStack> GetPlayerInventory(Player player) {
        ArrayList<ItemStack> playerInventory = new ArrayList<>();

        for(int i = 0; i < 4*9; i++) { playerInventory.add(player.getInventory().getItem(i)); }
        return playerInventory;
    }

}
