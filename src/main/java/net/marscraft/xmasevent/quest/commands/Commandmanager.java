package net.marscraft.xmasevent.quest.commands;

import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class Commandmanager {

    private ILogmanager _logger;

    public Commandmanager (ILogmanager logger) {
        _logger = logger;
    }

    public boolean isValidTaskName(String taskName) {
        switch (taskName.toLowerCase()) {
            case "killmobstask":
                return true;
            case "placeblocktask":
                return true;
            default:
                return false;
        }
    }
    public boolean isValidBlock(String block) {
        try {
            Material material = Material.valueOf(block.toUpperCase());
            if(!material.isBlock()) return false;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    public boolean isValidEntityType(String entityType) {
        try {
            EntityType.valueOf(entityType.toUpperCase());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    public int getIntFromStr(String target) {
        try {
            return Integer.parseInt(target);
        } catch (Exception ex){
            return 0;
        }
    }
}
