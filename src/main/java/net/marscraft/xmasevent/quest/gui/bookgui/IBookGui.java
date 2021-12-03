package net.marscraft.xmasevent.quest.gui.bookgui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IBookGui {

    public ItemStack CreateBookGui(Player player);

    public boolean OpenBookGui(Player player);

}
