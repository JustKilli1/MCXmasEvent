package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.shared.Inventorys.IInventoryType;
import net.marscraft.xmasevent.shared.Inventorys.InvAdminSetRewards;
import net.marscraft.xmasevent.shared.Inventorys.InvUnclaimedReward;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import net.marscraft.xmasevent.shared.messagemanager.Messagemanager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class InventoryCloseListener implements Listener {
    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;

    public InventoryCloseListener(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        /*
        * Inventory Close Handler for Inventorys:
        * Quest Belohnungen(User):
        *   command: /quests rewards
        * Quest Rewards(Admin):
        *   command: /mcxmas edit [questId] rewards
        * */

        EventStorage eventStorage = new EventStorage();
        eventStorage.SetInventoryCloseEvent(event);
        String inventoryTitle = event.getView().getTitle();
        IInventoryType inventoryType;
        if(inventoryTitle.equalsIgnoreCase("Quest Belohnungen"))
            inventoryType = new InvUnclaimedReward(_logger, _sql);
        else if(inventoryTitle.contains("Quest Rewards"))
            inventoryType = new InvAdminSetRewards(_logger, _sql);
        else return;
        inventoryType.CloseInventory(eventStorage);
    }
}
