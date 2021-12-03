package net.marscraft.xmasevent.shared.Inventorys;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface IInventoryType {

    public Inventory CreateInventory(Player player);

    public boolean OpenInventory(Player player);

}
