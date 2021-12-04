package net.marscraft.xmasevent.quest.gui.bookgui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IBookGui {
    public ItemStack CreateBookGui(Player player, int questId);
    public boolean OpenBookGui(Player player, int questId);
    public String BuildTaskGui(int questId, Player player);
}
