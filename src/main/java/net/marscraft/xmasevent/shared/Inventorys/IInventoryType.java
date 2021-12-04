package net.marscraft.xmasevent.shared.Inventorys;

import net.marscraft.xmasevent.quest.listener.EventStorage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface IInventoryType {
    public Inventory CreateInventory(Player player, int questId);
    public boolean OpenInventory(Player player, int questId);
    public boolean CloseInventory(EventStorage eventStorage);
}
