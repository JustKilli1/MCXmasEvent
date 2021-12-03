package net.marscraft.xmasevent.shared;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;

public class ItemBuilder {
    private ItemMeta _iMeta;
    private ItemStack _iStack;
    public ItemBuilder(Material mat){
        _iStack = new ItemStack(mat);
        _iMeta = _iStack.getItemMeta();
    }
    public ItemBuilder SetDisplayname(String displayName){
        _iMeta.setDisplayName(displayName);
        return this;
    }
    public ItemBuilder SetLocalizedName(String localizedName){
        _iMeta.setLocalizedName(localizedName);
        return this;
    }
    public ItemBuilder SetLore(String... lore){
        _iMeta.setLore(Arrays.asList(lore));
        return this;
    }
    public ItemBuilder SetUnbreakable(boolean unbreakable){
        _iMeta.setUnbreakable(unbreakable);
        return this;
    }
    public ItemBuilder AddItemFlags(ItemFlag itemFlag){
        _iMeta.addItemFlags(itemFlag);
        return this;
    }
    public ItemStack Build(){
        _iStack.setItemMeta(_iMeta);
        return _iStack;
    }
}
